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
                int MAX_OUTPUT_LENGTH = 50;
                while (true)
                {
                    String response = in.readLine();
                    
                    if(response.contains("Choose a username"))
                        response = response.replaceAll("\n", "").replaceAll("\r", "");
                    else if(response.contains("Current users"))
                    {
                        String[] userNames = response.split(", ");
                        String output = "";
                        boolean lastAddedEndLine = false;
                        boolean atLeastOnce = false;
                        int len = 0;
                        for(int i = 0 ; i < userNames.length ; i++)
                        {
                            output = output + userNames[i] + ", ";
                            len = len + userNames[i].length() + 2;
                            if(len > MAX_OUTPUT_LENGTH)
                            {
                                len = len - 50;
                                output = output + "\n";
                                lastAddedEndLine = true;
                                atLeastOnce = true;
                            }
                            else
                                lastAddedEndLine = false;
                        }
                        
                        output = output.substring(0, output.length() - 2);
                        
                        if(lastAddedEndLine == false || atLeastOnce == false)
                            output = output + "\n";
                        response = output;
                    }
                    else
                        response = response + "\n";
                    
                    System.out.print("\n" + response);
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
                //System.out.println(ex);
            }
            finally
            {
                //clientul s-a deconectat, deci trebuie sa fie eliminat 
                //tb sa inchidem acest thread
                System.out.println("deconectat");
            }
        }
    }
