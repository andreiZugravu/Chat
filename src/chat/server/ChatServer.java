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
import java.util.ArrayList;
import java.util.Arrays;

public class ChatServer
{

    //port-ul server-ului
    private static final int PORT = 9999;

    private static HashMap<String, PrintWriter> fluxuriAsociate = new HashMap<String, PrintWriter>();

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
        
    private static HashMap<PrivateChatKey, String> privateChatColours = new HashMap<PrivateChatKey, String>();
    private static ArrayList<String> colours = new ArrayList<String>(Arrays.asList(
            ANSI_RED,
            ANSI_GREEN,
            ANSI_YELLOW,
            ANSI_BLUE,
            ANSI_PURPLE,
            ANSI_CYAN
    ));
            
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
    
    static HashMap<PrivateChatKey, String> getPrivateChatColours()
    {
        return privateChatColours;
    }
    
    static String getAnsiReset()
    {
        return ANSI_RESET;
    }
    
    static ArrayList<String> getColours()
    {
        return colours;
    }
}
