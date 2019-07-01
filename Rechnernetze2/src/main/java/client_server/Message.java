package client_server;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Message{

    private LocalDateTime dateTime;
    private String message;
    private String sender;

    public Message(LocalDateTime dateTime, String sender, String message){
        this.dateTime = dateTime;
        this.sender = sender;
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    
}