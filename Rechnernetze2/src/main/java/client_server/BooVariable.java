package client_server;

import java.util.ArrayList;

public class BooVariable {
    
    private boolean boo = false;
    private ArrayList<String> vonListe = new ArrayList<>();
    private ChangeListener listener;

    public BooVariable(boolean boo){
        this.boo = boo;
    }

    public boolean isBoo() {
        return this.boo;
    }

    public void setVon(String v){
        this.vonListe.add(v);
    }

    /**
     * @return the von
     */
    public String getVon() {
        return vonListe.get(vonListe.size()-1);
    }

    public void deleteVon(String s){
        this.vonListe.remove(s);
    }

    public void setBoo(boolean boo) {
        this.boo = boo;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}