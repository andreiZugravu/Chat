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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient
{
    //le punem aici pt simplitate
    private static final String adresaServer = "localhost";
    private static final int portServer = 9999;
    private static String userName;
    private static boolean nameAccepted = false;
    
    public static void main(String[] args)
    {
        //Scanner sc = new Scanner(System.in);

        //System.out.print("Adresa server: ");
        //String adresaServer = sc.nextLine();

        //System.out.print("Port server: ");
        //int portServer = sc.nextInt();
        
        //sc.nextLine();
        
        //clientul incearca sa se conecteze la server
        try
        {
            Socket socket = new Socket(adresaServer, portServer);
            //daca am ajuns aici, ne-am conectat cu success la server
            //instantiem un firele de citire si scriere
            new FirCitire(socket).start();
            new FirScriere(socket).start();
        }
        catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    static void setUserName(String userName) {
        ChatClient.userName = userName;
    }
 
    static String getUserName() {
        return ChatClient.userName;
    }
    
    static void nameAccepted()
    {
        nameAccepted = true;
    }
    
    static boolean isNameAccepted()
    {
        return nameAccepted;
    }
}