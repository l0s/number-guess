package com.macasaet.numberguess.speechlet;

import static com.amazon.speech.speechlet.SpeechletResponse.newAskResponse;
import static com.amazon.speech.speechlet.SpeechletResponse.newTellResponse;
import static com.macasaet.numberguess.speechlet.Intents.CONFIRM_GUESS_INTENT;
import static com.macasaet.numberguess.speechlet.Intents.*;
import static com.macasaet.numberguess.speechlet.Intents.PROVIDE_FEEDBACK_INTENT;
import static com.macasaet.numberguess.speechlet.Intents.PROVIDE_GUESS;
import static com.macasaet.numberguess.speechlet.Intents.START_GAME;
import static com.macasaet.numberguess.speechlet.SessionAttribute.EFFECTIVE_RANGE;
import static com.macasaet.numberguess.speechlet.SessionAttribute.GUESSES;
import static com.macasaet.numberguess.speechlet.SessionAttribute.LAST_GUESS;
import static com.macasaet.numberguess.speechlet.SessionAttribute.SPECIFIED_RANGE;
import static com.macasaet.numberguess.speechlet.SessionAttribute.TARGET_NUMBER;
import static com.macasaet.numberguess.speechlet.Slots.GUESS;
import static com.macasaet.numberguess.speechlet.Slots.LOWER;
import static com.macasaet.numberguess.speechlet.Slots.RELATION;
import static com.macasaet.numberguess.speechlet.Slots.UPPER;
import static java.lang.Math.round;
import static org.apache.commons.lang3.RandomUtils.nextInt;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Range;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

/**
 * Main logic for both game modes.
 *
 * <p>Copyright &copy; 2016 Carlos Macasaet.</p>
 *
 * @author Carlos Macasaet
 */
public class NumberGuessSpeechlet implements Speechlet {

    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
    }

    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
        return createHelpResponse();
    }

    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        final Intent intent = request.getIntent();
        if (HELP_INTENT.matches(intent)) {
            return createAskResponse("I have two modes. "
                    + "I can think of a number for you to guess or you can think of a number for me to guess. "
                    + "If you want to guess, say \"I want to guess a number\". "
                    + "If you want me to guess, think of a number, then say, \"I'm thinking of a number between X and Y\" where X is less than the number and Y is greater than the number.");
        } else if (STOP_INTENT.matches(intent) || CANCEL_INTENT.matches(intent)) {
            return createTellResponse("Goodbye!");
        } else if (START_GAME.matches(intent)) {
            final int targetNumber = nextInt(1, 10);
            TARGET_NUMBER.setInt(session, targetNumber);
            return createAskResponse("I'm thinking of a number between one and ten inclusive. To guess, say I think it's");
        } else if (PROVIDE_GUESS.matches(intent)) {
            final int guess = GUESS.getInt(intent);
            final int targetNumber = TARGET_NUMBER.getInt(session);
            if (guess == targetNumber) {
                return createTellResponse("You guessed correctly. I was thinking of " + targetNumber);
            } else if (guess > targetNumber) {
                return createAskResponse("Sorry, " + guess + " is too high. Try again.");
            }
            return createAskResponse("Sorry, " + guess + " is too low. Try again.");
        } else if (GUESS_NUMBER_INTENT.matches(intent)) {
            // TODO validation: { integers | valid range }
            final int lower = LOWER.getInt(intent);
            final int upper = UPPER.getInt(intent);
            final Range<Integer> effectiveRange = Range.between(lower, upper);

            final int guess = guess(effectiveRange);
            final List<Integer> guesses = new LinkedList<>();
            guesses.add(guess);
            GUESSES.setIntList(session, guesses);

            SPECIFIED_RANGE.setIntRange(session, effectiveRange);

            EFFECTIVE_RANGE.setIntRange(session, effectiveRange);
            LAST_GUESS.setInt(session, guess);
            return createAskResponse("Is it " + guess + "?");
        } else if (PROVIDE_FEEDBACK_INTENT.matches(intent)) {
            final int lastGuess = LAST_GUESS.getInt(session);
            final String relation = RELATION.getValue(intent);
            final Range<Integer> effectiveRange =
                    "high".equalsIgnoreCase(relation)
                    ? Range.between(EFFECTIVE_RANGE.getIntRange(session).getMinimum(), lastGuess)
                    : Range.between(lastGuess, EFFECTIVE_RANGE.getIntRange(session).getMaximum());

            final int guess = guess(effectiveRange);

            GUESSES.getIntList(session).add(guess);

            EFFECTIVE_RANGE.setIntRange(session, effectiveRange);            
            LAST_GUESS.setInt(session, guess);
            return createAskResponse("Is it " + guess + "?");
        } else if (CONFIRM_GUESS_INTENT.matches(intent)) {
            return createTellResponse("Thank you, that was fun!");
        }
        throw new SpeechletException("Unrecognised intent: " + intent.getName());
    }

    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
    }

    protected int guess(final Range<Integer> range) {
        return round((range.getMaximum() - range.getMinimum()) / 2.0f) + range.getMinimum();
    }

    protected SpeechletResponse createHelpResponse() {
        return createAskResponse("Hello! If you want me to think of a number for you to guess, say \"I want to guess a number\". If you want me to guess a number say, \"I'm thinking of a number between\".");
    }

    protected SpeechletResponse createTellResponse(final String text) {
        final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(text);
        return newTellResponse(outputSpeech);
    }

    protected SpeechletResponse createAskResponse(final String text) {
        final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(text);
        final Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);
        return newAskResponse(outputSpeech, reprompt);
    }

}