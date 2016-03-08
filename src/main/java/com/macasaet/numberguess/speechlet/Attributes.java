package com.macasaet.numberguess.speechlet;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;

import com.amazon.speech.speechlet.Session;

public enum Attributes {

    TARGET_NUMBER("targetNumber"),
    SPECIFIED_RANGE("specifiedRange"),
    EFFECTIVE_RANGE("effectiveRange"),
    GUESSES("guesses"),
    LAST_GUESS("lastGuess");

    private final String name;

    private Attributes(final String name) {
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