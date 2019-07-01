package client_server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client_server.ObservableMap.ChangeListener;




// TODO Singleton machen


public class Nutzerverwaltung {
    
    private ObservableMap aktiveNutzer;
    private Map<String, String> nutzer;
    private static Nutzerverwaltung nutzerverw = new Nutzerverwaltung();
    
    private Nutzerverwaltung(){

        this.aktiveNutzer = new ObservableMap();
        this.nutzer = new HashMap<String,String>();

    }

    public static Nutzerverwaltung getInstance(){
        return nutzerverw;
    }

    public void addActiveUser(String key, Socket socket){
        aktiveNutzer.addUser(key,socket);
        //TODO Sag Teilserver er soll senden
    
    }

    public void addUser(String user, String pass){
        nutzer.put(user, pass);
        
    }

    /**
     * Gibt Socket des Nutzers wieder oder null, wenn kein Nutzer mit dem Key gefunden wird 
     * 
     * @param key
     * @return Socket des Nutzers oder null
     */
    public Socket getActiveUserSocket(String key){
        
        return aktiveNutzer.getSingleUser(key);

    }

    public void removeActiveUser(String key){
        aktiveNutzer.deleteUser(key);
    }

    public void removeUser(String user, String pass){

        if(comparePass(user,pass)){
            nutzer.remove(user);
        }
    }

    public boolean comparePass(String user, String pass) {

        if(nutzer.get(user).equals(pass)){
            return true;
        }
        return false;

    }

    /**
     * Nutzerliste, bei der der zugreifende Nutzer entfernt wird
     * 
     * @param zugreifenderNutzer Nutzername des zugreifenden Clients
     * @return Namen aller User als List<String> Objekt
     */
    public synchronized List<String> getActiveUserlist(String zugreifenderNutzer){

        List<String> list = Arrays.asList(aktiveNutzer.getKeySet().toArray(new String[0]));
        list.remove(zugreifenderNutzer);
        Collections.sort(list);

        return list;

    }
    /**
     * Getter für Nutzerliste
     * @return Namen aller User als List<String> Objekt
     */
    public synchronized List<String> getActiveUserlist(){

        List<String> list = Arrays.asList(aktiveNutzer.getKeySet().toArray(new String[0]));
        Collections.sort(list);

        return list;

    }

	public boolean compareUser(String user) {
		try{
            return nutzer.containsKey(user);
        } catch(NullPointerException e){
            return false;
        }
    }
    
    public boolean isUserActive(String user){
        try{
            return aktiveNutzer.inMap(user);
        } catch(NullPointerException e){
            return false;
        }
        
    }

    public ObservableMap getMap(){
        return aktiveNutzer.getSelf();
    }

    
}