/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankenponserver;


import game.Message;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author INSECT
 */
//client gelişini dinleme threadi


public class Server {
    
    //server soketi eklemeliyiz
    public static ServerSocket serverSocket;
    public static int IdClient = 0;
    public static int odaId=0;
    // Serverın dileyeceği port
    public static int port = 0;
    //Serverı sürekli dinlemede tutacak thread nesnesi
    public static ServerThread runThread;
    //public static PairingThread pairThread;

    public static ArrayList<SClient> Clients = new ArrayList<>();
    public static ArrayList<Oda> Odalar = new ArrayList<>();
    public static ArrayList <ArrayList> Kullanicilar = new ArrayList<>();
    //   a        b         c 
    // 1 2 3    4 5 6      2 4 5
    //semafor nesnesi
    public static Semaphore pairTwo = new Semaphore(1, true);

    // başlaşmak için sadece port numarası veriyoruz
    public static void Start(int openport) {
        try {
            Server.port = openport;
            Server.serverSocket = new ServerSocket(Server.port);

            Server.runThread = new ServerThread();
            Server.runThread.start();

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    // serverdan clietlara mesaj gönderme
    //clieti alıyor ve mesaj olluyor
    public static void Send(SClient cl, Message msg) {

        try {
            cl.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public static void BroadCastSendList(Message msg) {
        String [] clientName= new String[Clients.size()];
        int a= 0;
        for (SClient cl : Clients) {
            clientName[a]=cl.name;
            a++;
            
        }
        msg.content=clientName;
        for (SClient cl : Clients) {
            try {
            cl.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
     public static void BroadCastSendOdalarList(Message msg) {
        String [] odalarName= new String[Odalar.size()];
        int a= 0;
        for (Oda o1 : Odalar) {
            odalarName[a]=o1.isim;
            a++;
        }
        msg.content=odalarName;
        for (SClient c1 : Clients) {
            try {
            c1.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
     public static void OpenOdalar(Message msg){
         //odaName,kendiIsmi
         ArrayList gelen =(ArrayList) msg.content;
         String odaName = gelen.get(0).toString();
         String myName = gelen.get(1).toString();
         int i = 0;
         for (Object oda : Odalar) {
                if(oda.toString().equals(odaName)){
                    
                    break;
                   
                }
                i++;
         }
     }
    public static void BroadCastSendMsg(Message msg) {
        
        for (SClient cl : Clients) {
            try {
            cl.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
       

    }
    public static void OdalarJoinList(Message msg)// my name ile oda geliyor // 
    {
        ArrayList info = (ArrayList) msg.content;
        ArrayList send = new ArrayList<>();
        ArrayList kullanicilar = new ArrayList<>();
        for (Oda o1 : Odalar) {
            if (o1.isim.equals(info.get(1).toString())) {
                for (Object object : o1.kullanicilar) {
                    kullanicilar.add(object.toString());
                }
                break;
            }
        }
        send.add(info.get(1).toString());
        send.add(kullanicilar);
        msg.content = send;
        for (SClient cl : Clients) {
            try {
                cl.sOutput.writeObject(msg);
            } catch (IOException ex) {
                Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public static void BroadCastOdalar(Message msg) {
        
        for (SClient cl : Clients) {
            try {
            cl.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
       

    }
    
    public static void SendOzelChat(Message msg) {
        ArrayList arrayl = (ArrayList) msg.content;

        for (SClient cl : Clients) {
            if (cl.name.equals(arrayl.get(1).toString())) {
                System.out.println("Gonderilen client : " + cl.name);
                try {
                    cl.sOutput.writeObject(msg);
                } catch (IOException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }
    public static void SendOdalarChat(Message msg){
        ArrayList arrayl = (ArrayList) msg.content;
        for (Object o1: Odalar) {
            if (o1.equals(arrayl.get(0))) {
                try {
                   SClient.thisSClient.sOutput.writeObject(msg);    
                } catch (IOException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
     public static void SendOzelChatMsg(Message msg) {
        ArrayList arrayl = (ArrayList) msg.content;
        
        for (SClient cl : Clients) {
            if (cl.name.equals(arrayl.get(1).toString()) || cl.name.equals(arrayl.get(0).toString())) {
                System.out.println("Gonderilen client : " + cl.name);
                try {
                    cl.sOutput.writeObject(msg);
                } catch (IOException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }
    


}
class ServerThread extends Thread {

    public void run() {
        //server kapanana kadar dinle
        while (!Server.serverSocket.isClosed()) {
            try {
                Server.Display("Client Bekleniyor...");
                // clienti bekleyen satır
                //bir client gelene kadar bekler
                Socket clientSocket = Server.serverSocket.accept();
                //client gelirse bu satıra geçer
                Server.Display("Client Geldi...");
                //gelen client soketinden bir sclient nesnesi oluştur
                //bir adet id de kendimiz verdik
                SClient nclient = new SClient(clientSocket, Server.IdClient);
                
                Server.IdClient++;
                //clienti listeye ekle.
                Server.Clients.add(nclient);
                //client mesaj dinlemesini başlat
                nclient.listenThread.start();

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
