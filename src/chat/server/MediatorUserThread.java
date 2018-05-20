/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author azusr16
 */
//clasa interna prin care server-ul realizeaza conexiunea cu un client,
    //folosind un fir de executare separat
    public class MediatorUserThread extends Thread 
    {
        private String nume;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        private List<Observer> observers = new ArrayList<Observer>();
        
        public MediatorUserThread(Socket socket)
        {
            this.socket = socket;
            //ii atasez 2 observeri care anunta cand un user intra in sistem si cand iese
            new ObserverUserNew(this);
            new ObserverUserLeft(this);
        }
        
        PrintWriter getOutputStream()
        {
            return out;
        }
        
        String getUserName()
        {
            return nume;
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
                    out.println("Choose a username : ");
                    
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
                out.println("Username accepted!");
                
                //notify observers that a new user has entered the system
                this.notifyUserNew();
                
                //3. server-ul inregistreaza clientul
                //fluxuriCatreUtilizatori.add(out);
                
                //asociez
                //fluxuriAsociate.put(nume, out);
                
                System.out.println("Utilizatorul " + nume + " s-a conectat la server!");
                
                //4. server-ul preia mesajele clientului si le transmite tuturor celorlalti clienti 
                while (true)
                {
                    String input = in.readLine();
                    if (input.equals("/stop"))
                        break;
                        
                    if(input.equals("/users"))
                    {
                        this.showUsers();
                    }
                    else if(input.charAt(0) == '/' && input.contains(" ")) //este posibil sa vrea sa trimita unui user
                    {
                        String firstWord = input.substring(1, input.indexOf(" ")); //1 ca sa scapam de '/'
                        if(fluxuriAsociate.containsKey(firstWord)) //daca user-ul exista, trimite mesaj acelui user
                        {
                            //trimite mesaj doar acelui user
                            String restOfMessage = input.substring(input.indexOf(" ") + 1, input.length());
                            this.sendToUser(firstWord, restOfMessage);
                        }
                        else //nu exista acel user, trimit inapoi eroare user-ului
                        {
                            this.sendError("User " + firstWord + " does not exist!");
                        }
                    }
                    else
                    {
                        this.broadcastMessage(input);
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
                //din lista clientilor activi
                
                System.out.println("Utilizatorul " + nume + " s-a deconectat de la server!\n");
                
                //notify observers that the user has left the system
                this.notifyUserLeft();
                
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
                    //System.out.println(ex);
                }
            }
        }
        
        public void attach(Observer observer) {
            observers.add(observer);
        }
        
        public void notifyUserNew() {
            for(Observer observer : observers) {
                if(observer instanceof ObserverUserNew)
                    observer.update();
            }
        }
        
        public void notifyUserLeft() {
            for(Observer observer : observers) {
                if(observer instanceof ObserverUserLeft)
                    observer.update();
            }
        }
        
        public void broadcastMessage(String message) {
            HashMap<String, PrintWriter> fluxuriAsociate = ChatServer.getFluxuriAsociate();
            synchronized(this) {
                for(HashMap.Entry<String, PrintWriter> pw : fluxuriAsociate.entrySet())
                    if(pw.getValue() != out)
                        pw.getValue().println("[" + nume + "]: " + message);
            }
        }
        
        public void sendToUser(String userName, String message) {
            HashMap<String, PrintWriter> fluxuriAsociate = ChatServer.getFluxuriAsociate();
            HashMap<PrivateChatKey, String> privateChatColours = ChatServer.getPrivateChatColours();
            
            String ANSI_RESET = ChatServer.getAnsiReset();
            PrivateChatKey key = new PrivateChatKey(userName, nume);
            if(!privateChatColours.containsKey(key))
            {
                ArrayList<String> colours = ChatServer.getColours();
                Random rand = new Random();
                int index = rand.nextInt(colours.size());
                String colour = colours.get(index);
                privateChatColours.put(key, colour);
                
            }
            String ANSI_COLOUR = privateChatColours.get(key);
            
            synchronized(this) {
                fluxuriAsociate.get(userName).println(ANSI_COLOUR + "[" + nume + "] whispers: " + message + ANSI_RESET);
            }
        }
        
        public void sendError(String error) {
            synchronized(this) {
                out.println(error);
            }
        }
        
        public void showUsers()
        {
            HashMap<String, PrintWriter> fluxuriAsociate = ChatServer.getFluxuriAsociate();
            String message = "Current users : ";
            int contor = 0;
            int MAX_NR_PER_LINE = 5;
            boolean lastEndline = false;
            for(HashMap.Entry<String, PrintWriter> pw : fluxuriAsociate.entrySet())
            {
                message = message + pw.getKey() + ", ";
            }
            
            message = message.substring(0, message.length() - 2);
            
            synchronized(this) {
                out.println(message);
            }
        }
    }