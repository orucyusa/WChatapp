/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

/**
 *
 * @author INSECT
 */

public class Message implements java.io.Serializable {
    //mesaj tipleri enum 
    public static enum Message_Type {None, NameServer, NameClient, Disconnect,RivalConnected, Text,
    Selected, Bitis,Start, UserName,Users,RefreshListServer,RefreshListClient
    ,SendMsgServer,SendMsgClient,OpenOzelChatServer,OpenOzelChatClient, 
    SendOzelChatMsgClient, SendOzelChatMsgServer, OdalarRefreshListServer, 
    OdalarRefreshListClient, OdalarOpenChatServer, OdalarOpenChatClient,
    OdalarJoinChatServerMsg, OdalarJoinChatClientMsg , 
    SendOdaChatMsgServer, SendOdaChatMsgClient, SendFileMessageBroadCast, SendFileMessageOzelChat, SendFileMessageOdaChat}
    //mesajın tipi
    public Message_Type type;
    //mesajın içeriği obje tipinde ki istenilen tip içerik yüklenebilsin
    public Object content;
    public byte [] dosya;
    public String dosyaName;
    public Message(Message_Type t)
    {
        this.type=t;
    }
    public Message(Message_Type t, byte[] b,String name)
    {
        this.type=t;
        dosya=b;
        dosyaName=name;
        
    }
    
    
 

    
    
}
