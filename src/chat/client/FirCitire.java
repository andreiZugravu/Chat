/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.client;

import chat.client.ChatClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author azusr16
 */
//clasa interna prin care clientul citeste datele primite de la server,
    //folosind un fir de executare separat
    public class FirCitire extends Thread 
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
