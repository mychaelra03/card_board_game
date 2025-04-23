package it.polimi.galaxy_trucker.Common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage implements GameMessage {
    private String sender;
    private String message;

    public ChatMessage() {}

    public ChatMessage(String message) {
        this.message = message;
        this.sender = null;
    }

    public ChatMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }



    public String getMessage() {
        return message;
    }

    @Override
    public String getType() {
        return "chatMessage";
    }

    public String getSender() {
        return sender;
    }

}
