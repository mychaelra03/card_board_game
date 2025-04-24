package it.polimi.galaxy_trucker.Client;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface UpdateUICallBack {
    void process(String cmd) throws JsonProcessingException;

    void disconnect();
}
