/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 *
 * @author azusr16
 */
public class ObserverUserNew extends Observer {
    public ObserverUserNew(FirUtilizator subject) {
        this.subject = subject;
        this.subject.attach(this);
    }
    
    @Override
    public void update() {
        HashMap<String, PrintWriter> fluxuriAsociate = ChatServer.getFluxuriAsociate();
        PrintWriter out = subject.getOutputStream();
        String nume = subject.getUserName();
        
        for(HashMap.Entry<String, PrintWriter> pw : fluxuriAsociate.entrySet())
            if(pw.getValue() != out)
                pw.getValue().println("User " + nume + " has joined the chat");
    }
}
