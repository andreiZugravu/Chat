/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.client;

import chat.client.ChatClient;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author azusr16
 */
//clasa interna prin care clientul citeste datele primite de la server,
    //folosind un fir de executare separat
    public class FirScriere extends Thread 
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
                    System.out.print("[" + userName + "]: ");
                    out.println(text);

                } while (!text.equals("/stop"));

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