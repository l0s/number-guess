package com.macasaet.numberguess;

import static com.macasaet.numberguess.Intents.GUESS_NUMBER_INTENT;
import static com.macasaet.numberguess.Intents.PROVIDE_FEEDBACK_INTENT;
import static com.macasaet.numberguess.Intents.PROVIDE_GUESS;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;

public enum Slots {

    GUESS("Guess", PROVIDE_GUESS),
    LOWER("Lower", GUESS_NUMBER_INTENT),
    UPPER("Upper", GUESS_NUMBER_INTENT),
    RELATION("Relation", PROVIDE_FEEDBACK_INTENT);

    private final String name;
    private final Intents[] matchingIntents;

    private Slots(final String name, final Intents... matchingIntents) {
        notEmpty(name, "name must be specified");
        notNull(matchingIntents, "matchingIntents cannot be null");
        this.name = name;
        this.matchingIntents = matchingIntents;
    }

    public String getName() {
        return name;
    }

    public Intents[] getMatchingIntents() {
        return matchingIntents;
    }

    public String getValue(final Intent intent) {
        final Slot slot = intent.getSlot(getName());
        if (slot != null) {
            return slot.getValue();
        }
        return null;
    }

}