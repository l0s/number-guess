package com.macasaet.numberguess.speechlet;

import static org.apache.commons.lang3.Validate.notEmpty;

import com.amazon.speech.slu.Intent;

/**
 * Type-safe enumeration of the supported user intents.
 *
 * <p>Copyright &copy; 2016 Carlos Macasaet.</p>
 *
 * @author Carlos Macasaet
 */
public enum Intents {

    /**
     * Amazon built-in help intent
     */
    HELP_INTENT("AMAZON.HelpIntent"),
    /**
     * Amazon built-in stop intent
     */
    STOP_INTENT("AMAZON.StopIntent"),
    /**
     * Amazon built-in cancel intent
     */
    CANCEL_INTENT("AMAZON.CancelIntent"),
    /**
     * Indication that the user wants to guess a number.
     */
    START_GAME("StartGameIntent"),
    /**
     * User's guess as to what the number is.
     */
    PROVIDE_GUESS("ProvideGuessIntent"),
    /**
     * Indication that the user wants the system to guess a number.
     */
    GUESS_NUMBER_INTENT("GuessNumberIntent"),
    /**
     * User's feedback about whether the system's guess is too high or low.
     */
    PROVIDE_FEEDBACK_INTENT("ProvideFeedbackIntent"),
    /**
     * User's indication that the system guessed the correct number.
     */
    CONFIRM_GUESS_INTENT("ConfirmGuessIntent");

    private final String name;

    private Intents(final String name) {
        notEmpty(name, "name must be specified");
        this.name = name;
    }

    protected String getName() {
        return name;
    }

    public boolean matches(final Intent intent) {
        return intent != null && getName().equals(intent.getName());
    }

}