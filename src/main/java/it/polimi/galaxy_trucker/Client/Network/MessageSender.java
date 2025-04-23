package it.polimi.galaxy_trucker.Client.Network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Server.VirtualView;

import java.io.PrintWriter;
import java.util.List;

public class MessageSender {
    private final PrintWriter writer;
    private final ObjectMapper mapper;

    public MessageSender(PrintWriter writer) {
        this.writer = writer;
        mapper = new ObjectMapper();
    }

    public void send(Object message) {

        String json;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        writer.println(json);

    }



}
