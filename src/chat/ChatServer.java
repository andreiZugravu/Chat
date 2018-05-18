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

    //clasa interna prin care server-ul realizeaza conexiunea cu un client,
    //folosind un fir de executare separat
    private static class FirUtilizator extends Thread 
    {
        private String nume;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";

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
            try
            {
                //se creeaza fluxurile de comunicare cu clientul
                in =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream() , true);

                //1. server-ul solicita clientului un nume pana cand acesta trimite unul neutilizat in acel moment
                while (true)
                {
                    out.println("Nume utilizator?");
                    nume = in.readLine();

                    synchronized(this)
                    {
                        //server-ul verifica daca numele respectiv mai este utilizat sau nu
                        if (!numeUtilizatori.contains(nume))
                        {
                            numeUtilizatori.add(nume);
                            break;
                        }
                    }
                }

                //2. server-ul comunica clientului faptul ca a fost acceptat numele respectiv
                out.println("Nume acceptat!");
                
                //3. server-ul inregistreaza clientul
                fluxuriCatreUtilizatori.add(out);
                
                //asociez
                fluxuriAsociate.put(nume, out);
                
                System.out.println("Utilizatorul " + nume + " s-a conectat la server!");
                
                //4. server-ul preia mesajele clientului si le transmite tuturor celorlalti clienti 
                while (true)
                {
                    String input = in.readLine();
                    if (input.startsWith("STOP"))
                        break;
                        
                    synchronized(this)
                    {
                        boolean sentToSpecificUser = false;
                        if(input.charAt(0) == '/') //este posibil sa vrea sa trimita unui user
                        {
                            if(input.contains(" "))
                            {
                                String firstWord = input.substring(1, input.indexOf(" ")); //1 ca sa scapam de '/'
                                if(numeUtilizatori.contains(firstWord))
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
                            for (PrintWriter pw : fluxuriCatreUtilizatori)
                            if(pw != out)
                                pw.println("[" + nume + "]: " + input);
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
                
                if (nume != null)
                {
                    numeUtilizatori.remove(nume);
                }
                if (out != null)
                {
                    fluxuriCatreUtilizatori.remove(out);
                }
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
}
