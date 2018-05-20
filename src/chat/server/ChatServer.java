/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

/**
 *
 * @author azusr16
 */
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer
{

    //port-ul server-ului
    private static final int PORT = 9999;

    private static HashMap<String, PrintWriter> fluxuriAsociate = new HashMap<String, PrintWriter>();

    public static void main(String[] args) throws Exception
    {
        //se creeaza server-ul folosind port-ul indicat
        try(ServerSocket ServerChat = new ServerSocket(PORT)) 
        {
            System.out.println("Server-ul a pornit!");            
            
            //server-ul asteapta ca un client sa se conecteze si apoi
            //creeaza o conexiune cu acesta pe un fir de executare separat
            while (true)
            {
               //astept conexiune
               Socket cs = ServerChat.accept();
               //am stabilit o conexiune, instantiez un mediator care se ocupe de primirea si transmiterea mesajelor
               MediatorUserThread fu = new MediatorUserThread(cs);
               //pornesc thread-ul
               fu.start();
            }
        }
    }

    static HashMap<String, PrintWriter> getFluxuriAsociate()
    {
        return fluxuriAsociate;
    }
    
    static void addFluxAsociat(String nume, PrintWriter flux)
    {
        fluxuriAsociate.put(nume, flux);
    }
    
    static void removeFluxAsociat(String nume)
    {
        fluxuriAsociate.remove(nume);
    }
}
