/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankenponserver;

import game.Message;
import jankenponserver.SClient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusa
 */
public class Oda  implements java.io.Serializable{
    String isim;
    ArrayList <String> mesajlar;
    public ArrayList kullanicilar = new ArrayList<>();
    int OdaId;

    public Oda (String isim) {
        this.isim = isim;
    }
    public void MesajEkle(String mesaj){
        mesajlar.add(mesaj);
    }
    
    
    
}

