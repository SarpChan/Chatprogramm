package client_server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class MessageListe {
    
    @Expose
    private List<Message> liste = new ArrayList<>();
    private ChangeListener listener;
    @Expose
    private String user, otherUser;

    public MessageListe(){
        
    }

    
    public void addMessage(String sender, String message){
        liste.add(new Message(LocalDateTime.now(), sender, message));
        if (listener != null) listener.onChange();
    }

    public void deleteVerlauf(){
        liste.clear();
    }

    public ChangeListener getListener() {
        return listener;
    }

    /**
     * @return the liste
     */
    public List<Message> getListe() {
        return liste;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(String otherUser) {
        this.otherUser = otherUser;
    }

    
}