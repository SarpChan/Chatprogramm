package client_server;

import java.util.ArrayList;
import java.util.List;

public class ObservableListe {
    
    private List<String> liste = new ArrayList<>();
    private ChangeListener listener;

    
    public void setListe(String[] s) {
        liste.clear();
        for (String x : s) {
            liste.add(x);
        }
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    /**
     * @return the liste
     */
    public List<String> getListe() {
        return liste;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}