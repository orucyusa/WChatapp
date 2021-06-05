/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankenponserver;

import game.Message;
import static game.Message.Message_Type.Selected;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import game.Message;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
/**
 *
 * @author INSECT
 */
public class SClient {
     public static SClient thisSClient;
    int id;
    public String name = "NoName";
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    //clientten gelenleri dinleme threadi
    Listen listenThread;
    //cilent eşleştirme thredi
    //rakip client
    SClient rival;
    //eşleşme durumu
    public boolean paired = false;

    public SClient(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        //thread nesneleri
        this.listenThread = new Listen(this);

    }

    //client mesaj gönderme
    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //client dinleme threadi
    //her clientin ayrı bir dinleme thredi var
    class Listen extends Thread {

        SClient TheClient;

        //thread nesne alması için yapıcı metod
        Listen(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            //client bağlı olduğu sürece dönsün
            while (TheClient.soket.isConnected()) {
                try {
                    //mesajı bekleyen kod satırı
                    Message received = (Message) (TheClient.sInput.readObject());
                    //mesaj gelirse bu satıra geçer
                    //mesaj tipine göre işlemlere ayır
                    switch (received.type) {
                        case UserName:
                            TheClient.name = received.content.toString();
                            for (SClient sc: Server.Clients) {
                                Message msg = new Message(Message.Message_Type.Users);
                                msg.content=TheClient.name;
                                Server.Send(sc,msg);
                            }
                           
                            break;
                        case NameServer:
                            TheClient.name=received.content.toString();
                            
                            break;
                        case RefreshListServer:
                            Message msg1= new Message(Message.Message_Type.RefreshListClient);
                            Server.BroadCastSendList(msg1);
                            break;
                            
                        case SendMsgServer:
                            Message msg2 = new Message(Message.Message_Type.SendMsgClient);
                            msg2.content=received.content;
                            Server.BroadCastSendMsg(msg2);
                            break;
                            
                        case OpenOzelChatServer:
                            Message msg3 = new Message(Message.Message_Type.OpenOzelChatClient);
                            msg3.content=received.content;
                            Server.SendOzelChat(msg3);
                            break;
                            
                        case SendOzelChatMsgServer:
                            Message msg4 = new Message(Message.Message_Type.SendOzelChatMsgClient);
                            msg4.content=received.content;
                            Server.SendOzelChatMsg(msg4);
                            break;
                            
                        case OdalarRefreshListServer:
                            Message msg5 = new Message(Message.Message_Type.OdalarRefreshListClient);
                            Server.BroadCastSendOdalarList(msg5);
                            break;
                            
                        case OdalarOpenChatServer: // myName ve odaName
                           ArrayList gelen = (ArrayList)received.content;
                           Message msg6 = new Message(Message.Message_Type.OdalarOpenChatClient);
                           Oda o = new Oda(gelen.get(1).toString());
                           o.kullanicilar.add(gelen.get(0).toString());
                           Server.Odalar.add(o);
                           msg6.content=gelen.get(1).toString();
                           Server.BroadCastSendMsg(msg6);
                           break;
                        case OdalarJoinChatServerMsg: // myName ve secilenoda
                           ArrayList gelenJoin = (ArrayList)received.content;
                            for (Oda odaJoin : Server.Odalar) {
                                if(odaJoin.isim.equals(gelenJoin.get(1).toString())){
                                    odaJoin.kullanicilar.add(gelenJoin.get(0).toString());
                                }
                            }
                            Message msg7 = new Message(Message.Message_Type.OdalarJoinChatClientMsg);
                            msg7.content=received.content;
                            Server.OdalarJoinList(msg7);
                           break;
                        case SendOdaChatMsgServer: // name, text ve roomName
                            Message msg8 = new Message(Message.Message_Type.SendOdaChatMsgClient);
                            msg8.content=received.content;
                            Server.BroadCastSendMsg(msg8);
                            break;
                        case SendFileMessageBroadCast:
                            Server.BroadCastSendMsg(received);
                            
                            break;
                        case SendFileMessageOzelChat:
                            Server.SendOzelChatMsg(received);
                            break;
                            
                        case SendFileMessageOdaChat:
                            Server.BroadCastSendMsg(received);
                            break;
                    }

                } catch (IOException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    //client bağlantısı koparsa listeden sil
                    Server.Clients.remove(TheClient);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    //client bağlantısı koparsa listeden sil
                    Server.Clients.remove(TheClient);
                }
            }

        }
    }

    //eşleştirme threadi
    //her clientin ayrı bir eşleştirme thredi var
   

}
