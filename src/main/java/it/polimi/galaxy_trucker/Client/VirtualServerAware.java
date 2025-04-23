package it.polimi.galaxy_trucker.Client;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface VirtualServerAware {
    void setVirtualServer(VirtualServer virtualServer) throws JsonProcessingException;
}
