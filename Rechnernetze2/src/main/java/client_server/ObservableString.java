package client_server;



public class ObservableString {


    private String string = "";
    private ChangeListener listener;
   
    public ObservableString(){
        
    }

    public void setString(String string) {
        this.string = string;
        if (listener != null) listener.onChange();
    }

    /**
     * @return the string
     */
    public String getString() {
        return string;
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