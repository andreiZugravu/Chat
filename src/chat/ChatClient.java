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
    
    //clasa interna prin care clientul citeste datele primite de la server,
    //folosind un fir de executare separat
    private static class FirCitire extends Thread 
    {
        private Socket socket;
        private BufferedReader in;

        public FirCitire(Socket socket)
        {
            this.socket = socket;
        }

        //actiunile efectuate de server in momentul in care un client se conecteaza:
        //1. server-ul solicita clientului un nume pana cand acesta trimite unul neutilizat in acel moment
        //2. server-ul comunica clientului faptul ca a fost acceptat numele respectiv
        //3. server-ul inregistreaza clientul
        //4. server-ul preia mesajele clientului si le transmite tuturor celorlalti clienti
        @Override
        public void run()
        {
            try
            {
                //se creeaza fluxurile de comunicare cu clientul, partea de citire
                in =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                //4. server-ul preia mesajele clientului si le transmite tuturor celorlalti clienti 
                while (true)
                {
                    String response = in.readLine();
                    
                    System.out.println("\n" + response);
                    if (response.startsWith("STOP"))
                        break;
                        
                    //if(!response.equals("Nume utilizator?"))
                        //ChatClient.nameAccepted();
                    
                    // prints the username after displaying the server's message
                    //asta ajuta la claritate
                    if (ChatClient.getUserName() != null) {
                        System.out.print("[" + ChatClient.getUserName() + "]: ");
                    }
                }
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
            finally
            {
                //clientul s-a deconectat, deci trebuie sa fie eliminat 
                //tb sa inchidem acest thread
                System.out.println("deconectat");
            }
        }
    }
    
    //clasa interna prin care clientul citeste datele primite de la server,
    //folosind un fir de executare separat
    private static class FirScriere extends Thread 
    {
        private Socket socket;
        private PrintWriter out;

        public FirScriere(Socket socket)
        {
            this.socket = socket;
        }

        //actiunile efectuate de server in momentul in care un client se conecteaza:
        //1. server-ul solicita clientului un nume pana cand acesta trimite unul neutilizat in acel moment
        //2. server-ul comunica clientului faptul ca a fost acceptat numele respectiv
        //3. server-ul inregistreaza clientul
        //4. server-ul preia mesajele clientului si le transmite tuturor celorlalti clienti
        @Override
        public void run()
        {
            try
            {
                //se creeaza fluxurile de comunicare cu clientul, partea de scriere
                out = new PrintWriter(socket.getOutputStream() , true);
                
                Scanner sc = new Scanner (System.in);
 
                //System.out.println("\nusername : ");
                String userName;
                //do {
                    userName = sc.nextLine();
                    ChatClient.setUserName(userName);
                    out.println(userName);
                //}
                //while(!ChatClient.isNameAccepted());
                String text;

                do {
                    text = sc.nextLine();
                    out.println(text);

                } while (!text.equals("bye"));

                //try {
                    socket.close(); //o prindem mai jos
                //} catch (IOException ex) {

                    //System.out.println("Error writing to server: " + ex.getMessage());
                //}
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
            finally
            {
                //clientul s-a deconectat, deci trebuie sa fie eliminat 
                //tb sa inchidem acest thread
                System.out.println("deconectat");
            }
        }
    }
}

/*
while(true)
            {
                //clientul citeste mesajul de la server
				String mesajServer = in.readLine();

				//clientul citeste de la tastatura si transmite server-ului un nume de utilizator 
				//pana cand numele transmis este diferit de numele tuturor utilizatorilor conectati in acel moment
                if (mesajServer.startsWith("Nume utilizator?"))
                {
                    System.out.print("Nume utilizator: ");
                    String nume = sc.nextLine();
                    out.println(nume);
                }
                //server-ul ii transmite clientului faptul ca numele de utilizator a fost acceptat
				else if (mesajServer.startsWith("Nume acceptat!"))
                {
                    System.out.println("Conectare reusita la server!");
                    break;
                }
            }
			
			//comunicarea cu server-ul (la fel ca in cazul chat-ului simplu)
            while (true)
            {
                System.out.print("Mesaj: ");
                String mesajClient = sc.nextLine();
                out.println(mesajClient);

                if (mesajClient.startsWith("STOP"))
                    break;

                String mesajServer = in.readLine();
                System.out.println(mesajServer);
            }
*/