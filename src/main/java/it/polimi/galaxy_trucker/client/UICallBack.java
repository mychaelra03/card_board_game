package it.polimi.galaxy_trucker.client;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface UICallBack {
    void process(String cmd) throws JsonProcessingException;
}
