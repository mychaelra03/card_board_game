package it.polimi.galaxy_trucker.server.controller.listeners;

import it.polimi.galaxy_trucker.server.events.EventModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handles {
    Class<? extends EventModel> value();
}