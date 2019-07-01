package client_server;

public class BooVariable {
    
    private boolean boo = false;
    private ChangeListener listener;

    public BooVariable(boolean boo){
        this.boo = boo;
    }

    public boolean isBoo() {
        return this.boo;
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