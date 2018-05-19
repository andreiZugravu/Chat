/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

/**
 *
 * @author azusr16
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.HashMap;

public class ChatServer
{

    //port-ul server-ului
    private static final int PORT = 9999;

    //multimea numelor clientilor activi, pastrata sub forma unui HashSet 
    //deoarece numele acestora trebuie sa fie unice si 
    //pentru a verifica rapid daca un anumit nume este disponibil sau nu
    private static HashSet<String> numeUtilizatori = new HashSet<String>();

    //multimea fluxurilor de iesire ale server-ului catre clienti, 
    //folosita pentru a transmite mai usor un mesaj catre toti clientii
    private static HashSet<PrintWriter> fluxuriCatreUtilizatori = new HashSet<PrintWriter>();
    
    //asociere nume utilizator - hashset
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
               Socket cs = ServerChat.accept();
               new FirUtilizator(cs).start();
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
