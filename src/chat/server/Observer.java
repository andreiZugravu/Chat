/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

/**
 *
 * @author azusr16
 */
public abstract class Observer {
    protected FirUtilizator subject;
    public abstract void update();
}
