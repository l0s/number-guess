package com.macasaet.numberguess.speechlet;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;

import com.amazon.speech.speechlet.Session;

/**
 * Type-safe enumeration of session attributes.
 *
 * <p>Copyright &copy; 2016 Carlos Macasaet.</p>
 *
 * @author Carlos Macasaet
 */
public enum SessionAttribute {

    /**
     * The number the system chose and the user is trying to guess
     */
    TARGET_NUMBER("targetNumber"),
    /**
     * The user-specified bounds for the game
     */
    SPECIFIED_RANGE("specifiedRange"),
    /**
     * The narrowed search range determined by the system
     */
    EFFECTIVE_RANGE("effectiveRange"),
    /**
     * The history of guesses the system made
     */
    GUESSES("guesses"),
    /**
     * The last guess the system made
     */
    LAST_GUESS("lastGuess");

    private final String name;

    private SessionAttribute(final String name) {
        notEmpty(name, "name must be specified");
        this.name = name;
    }

    public int getInt(final Session session) {
        return (int) session.getAttribute(getName());
    }

    public void setInt(final Session session, final int value) {
        session.setAttribute(getName(), value);
    }

    @SuppressWarnings("unchecked")
    public Range<Integer> getIntRange(final Session session) {
        final Map<String, Object> map = (Map<String, Object>)session.getAttribute(getName());
        final int minimum = (int)map.get("minimum");
        final int maximum = (int)map.get("maximum");
        return Range.between(minimum, maximum);
    }

    public void setIntRange(final Session session, final Range<Integer> value) {
        session.setAttribute(getName(), value);
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getIntList(final Session session) {
        return (List<Integer>)session.getAttribute(getName());
    }

    public void setIntList(final Session session, final List<Integer> value) {
        session.setAttribute(getName(), value);
    }

    protected String getName() {
        return name;
    }

}