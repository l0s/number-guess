package com.macasaet.numberguess.speechlet;

import static com.macasaet.numberguess.speechlet.Intents.GUESS_NUMBER_INTENT;
import static com.macasaet.numberguess.speechlet.Intents.PROVIDE_FEEDBACK_INTENT;
import static com.macasaet.numberguess.speechlet.Intents.PROVIDE_GUESS;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;

/**
 * Type-safe enumeration of the valid intent parameters.
 *
 * <p>Copyright &copy; 2016 Carlos Macasaet</p>
 *
 * @author Carlos Macasaet
 */
public enum Slots {

    /**
     * User's guess during a game.
     */
    GUESS("Guess", PROVIDE_GUESS),
    /**
     * Lower bound specified when starting a game.
     */
    LOWER("Lower", GUESS_NUMBER_INTENT),
    /**
     * Upper bound specified when starting a game.
     */
    UPPER("Upper", GUESS_NUMBER_INTENT),
    /**
     * User's feedback on why the system's guess is incorrect.
     */
    RELATION("Relation", PROVIDE_FEEDBACK_INTENT);

    private final String name;
    private final Intents[] matchingIntents;

    private Slots(final String name, final Intents... matchingIntents) {
        notEmpty(name, "name must be specified");
        notNull(matchingIntents, "matchingIntents cannot be null");
        this.name = name;
        this.matchingIntents = matchingIntents;
    }

    public String getValue(final Intent intent) {
        final Slot slot = intent.getSlot(getName());
        if (slot != null) {
            return slot.getValue();
        }
        return null;
    }

    public int getInt(final Intent intent) {
        return parseInt(getValue(intent));
    }

    protected String getName() {
        return name;
    }

    protected Intents[] getMatchingIntents() {
        return matchingIntents;
    }

}