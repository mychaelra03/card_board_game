package it.polimi.galaxy_trucker.Client;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public interface VirtualServerAware {
    void setVirtualServer(VirtualServer virtualServer) throws IOException;
}
