package client_server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObservableMap {
    
    private Map<String, Socket> map = new HashMap<>();
    private ArrayList <ChangeListener> listener = new ArrayList<>();

    public ObservableMap(){
        
    }

    
    public void addUser(String s, Socket socket) {
        map.put(s, socket);
        if (!listener.isEmpty()) {
            for (ChangeListener var : listener) {
                var.onChange();
            }
            
        }
    }

    public void deleteUser(String s) {
        map.remove(s);
        if (!listener.isEmpty()) {
            for (ChangeListener var : listener) {
                var.onChange();
            }
            
        }
    }

    public Socket getSingleUser(String s){
       return map.get(s);
       
    }


    public Set<String> getKeySet(){
        return this.map.keySet();
        }

    /**
     * @return the liste
     */
    public List<Socket> getListe() {
        return (List<Socket>) map.values();
    }

    public void setListener(ChangeListener listener) {
        this.listener.add(listener);
        
        
    }

    public interface ChangeListener {
        void onChange();
    }

    public boolean inMap(String s){
        return map.containsKey(s);
    }

    public ObservableMap getSelf(){
        return this;
    }
}