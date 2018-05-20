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
public class PrivateChatKey {
    private final String userName1;
    private final String userName2;
    
    public PrivateChatKey(String userName1, String userName2) {
        if(userName1.compareTo(userName2) < 0)
        {
            this.userName1 = userName1;
            this.userName2 = userName2;
        }
        else
        {
            this.userName1 = userName2;
            this.userName2 = userName1;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrivateChatKey)) return false;
        PrivateChatKey key = (PrivateChatKey) o;
        return userName1.equals(key.userName1) && userName2.equals(key.userName2);
    }

    @Override
    public int hashCode() {
        int result = userName1.hashCode();
        result = 31 * result + userName2.hashCode();
        return result;
    }
}
