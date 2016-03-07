package com.macasaet.numberguess;

import static org.apache.commons.lang3.Validate.notEmpty;

import com.amazon.speech.slu.Intent;

/**
 * 
 * <p>Copyright &copy; 2016 Carlos Macasaet.</p>
 *
 * @author Carlos Macasaet
 */
public enum Intents {

    START_GAME("StartGameIntent"),
    PROVIDE_GUESS("ProvideGuessIntent");

    private final String name;

    private Intents(final String name) {
        notEmpty(name, "name must be specified");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean matches(final Intent intent) {
        return intent != null && getName().equals(intent.getName());
    }

}