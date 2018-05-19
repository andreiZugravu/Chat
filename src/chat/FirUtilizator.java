/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.HashMap;

/**
 *
 * @author azusr16
 */
//clasa interna prin care server-ul realizeaza conexiunea cu un client,
    //folosind un fir de executare separat
    public class FirUtilizator extends Thread 
    {
        private String nume;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        private static final String ANSI_RESET = "\u001B[0m";
        private static final String ANSI_BLACK = "\u001B[30m";
        private static final String ANSI_RED = "\u001B[31m";
        private static final String ANSI_GREEN = "\u001B[32m";
        private static final String ANSI_YELLOW = "\u001B[33m";
        private static final String ANSI_BLUE = "\u001B[34m";
        private static final String ANSI_PURPLE = "\u001B[35m";
        private static final String ANSI_CYAN = "\u001B[36m";
        private static final String ANSI_WHITE = "\u001B[37m";

        public FirUtilizator(Socket socket)
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
            HashMap<String, PrintWriter> fluxuriAsociate = ChatServer.getFluxuriAsociate();
            try
            {
                //se creeaza fluxurile de comunicare cu clientul
                in =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream() , true);
                
                //1. server-ul solicita clientului un nume pana cand acesta trimite unul neutilizat in acel moment
                while (true)
                {
                    out.println("Nume utilizator? : ");
                    
                    nume = in.readLine();
                    synchronized(this)
                    {
                        //server-ul verifica daca numele respectiv mai este utilizat sau nu
                        if (!fluxuriAsociate.containsKey(nume) && nume != null)
                        {
                            fluxuriAsociate.put(nume, out);
                            break;
                        }
                    }
                }

                //2. server-ul comunica clientului faptul ca a fost acceptat numele respectiv
                out.println("Nume acceptat!");
                
                //3. server-ul inregistreaza clientul
                //fluxuriCatreUtilizatori.add(out);
                
                //asociez
                //fluxuriAsociate.put(nume, out);
                
                System.out.println("Utilizatorul " + nume + " s-a conectat la server!");
                
                //4. server-ul preia mesajele clientului si le transmite tuturor celorlalti clienti 
                while (true)
                {
                    String input = in.readLine();
                    if (input.equals("STOP_CHAT"))
                        break;
                        
                    synchronized(this)
                    {
                        boolean sentToSpecificUser = false;
                        if(input.charAt(0) == '/') //este posibil sa vrea sa trimita unui user
                        {
                            if(input.contains(" "))
                            {
                                String firstWord = input.substring(1, input.indexOf(" ")); //1 ca sa scapam de '/'
                                if(fluxuriAsociate.containsKey(firstWord))
                                {
                                    //trimite mesaj doar acelui user
                                    sentToSpecificUser = true;
                                    String restOfMessage = input.substring(input.indexOf(" ") + 1, input.length());
                                    fluxuriAsociate.get(firstWord).println(ANSI_RED + "[" + nume + "] whispers: " + restOfMessage + ANSI_RESET);
                                }
                            }
                        }
                        
                        if(!sentToSpecificUser)
                        {
                            for(HashMap.Entry<String, PrintWriter> pw : fluxuriAsociate.entrySet())
                                if(pw.getValue() != out)
                                    pw.getValue().println("[" + nume + "]: " + input);
                        }
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
                //din lista clientilor activi
                
                System.out.println("Utilizatorul " + nume + " s-a deconectat de la server!");
                
                if (nume != null && out != null)
                {
                    fluxuriAsociate.remove(nume, out);
                    //numeUtilizatori.remove(nume);
                }
                //if (out != null)
                //{
                    //fluxuriCatreUtilizatori.remove(out);
                //}
                try
                {
                    socket.close();
                }
                catch (IOException ex)
                {
                    System.out.println(ex);
                }
            }
        }
    }