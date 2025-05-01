package it.polimi.galaxy_trucker.Client;

import java.io.IOException;

public interface VirtualServerAware {
    void setVirtualServer(VirtualServer virtualServer) throws IOException;
}
