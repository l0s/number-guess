package com.macasaet.numberguess;

import static com.amazon.speech.speechlet.SpeechletResponse.newAskResponse;
import static com.amazon.speech.speechlet.SpeechletResponse.newTellResponse;
import static com.macasaet.numberguess.Intents.CONFIRM_GUESS_INTENT;
import static com.macasaet.numberguess.Intents.GUESS_NUMBER_INTENT;
import static com.macasaet.numberguess.Intents.PROVIDE_FEEDBACK_INTENT;
import static com.macasaet.numberguess.Intents.PROVIDE_GUESS;
import static com.macasaet.numberguess.Intents.START_GAME;
import static com.macasaet.numberguess.Slots.GUESS;
import static com.macasaet.numberguess.Slots.LOWER;
import static com.macasaet.numberguess.Slots.RELATION;
import static com.macasaet.numberguess.Slots.UPPER;
import static com.macasaet.numberguess.speechlet.Attributes.EFFECTIVE_RANGE;
import static com.macasaet.numberguess.speechlet.Attributes.GUESSES;
import static com.macasaet.numberguess.speechlet.Attributes.LAST_GUESS;
import static com.macasaet.numberguess.speechlet.Attributes.SPECIFIED_RANGE;
import static com.macasaet.numberguess.speechlet.Attributes.TARGET_NUMBER;
import static java.lang.Integer.parseInt;
import static java.lang.Math.round;
import static org.apache.commons.lang3.RandomUtils.nextInt;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 
 * <p>Copyright &copy; 2016 Carlos Macasaet.</p>
 *
 * @author Carlos Macasaet
 */
public class NumberGuessSpeechlet implements Speechlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
        // TODO Auto-generated method stub
        logger.info( "( onSessionStarted: {}, {} )", request, session );
    }

    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
        // TODO Auto-generated method stub
        logger.info( "( onLaunch: {}, {} )", request, session );
        final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Welcome!");
        final Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);
        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }

    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        logger.info( "( onIntent: {}, {} )", request, session );
        final Intent intent = request.getIntent();
        if (START_GAME.matches(intent)) {
            final int targetNumber = nextInt(1, 10);
            TARGET_NUMBER.setInt(session, targetNumber);
            return createAskResponse("I'm thinking of a number between one and ten inclusive. To guess, say I think it's");
        } else if (PROVIDE_GUESS.matches(intent)) {
            final String guessString = GUESS.getValue(intent);
            // TODO validation
            final int guess = parseInt(guessString);
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
        throw new SpeechletException("Invalid intent: " + intent.getName());
    }

    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        // TODO Auto-generated method stub
        logger.info( "( onSessionEnded: {}, {} )", request, session );
    }

    protected int guess(final Range<Integer> range) {
        return round((range.getMaximum() - range.getMinimum()) / 2.0f) + range.getMinimum();
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