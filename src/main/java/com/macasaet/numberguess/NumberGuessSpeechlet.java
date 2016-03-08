package com.macasaet.numberguess;

import static java.lang.Math.*;
import static org.apache.commons.lang3.RandomUtils.*;
import static com.amazon.speech.speechlet.SpeechletResponse.*;
import static com.macasaet.numberguess.Intents.*;
import static com.macasaet.numberguess.Slots.*;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.util.LinkedList;
import java.util.List;

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

    @SuppressWarnings("unchecked")
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        // TODO Auto-generated method stub
        logger.info( "( onIntent: {}, {} )", request, session );
        final Intent intent = request.getIntent();
        if (START_GAME.matches(intent)) {
            final int targetNumber = nextInt(1, 10);
            session.setAttribute("targetNumber", targetNumber);
            final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("I'm thinking of a number between one and ten inclusive. To guess, say I think it's");
            final Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(outputSpeech);
            return newAskResponse(outputSpeech, reprompt);
        } else if (PROVIDE_GUESS.matches(intent)) {
            final String guessString = GUESS.getValue(intent);
            if (!isNumeric(guessString)) {
                // FIXME: we really should support numbers that have non-numeric
                // characters
                throw new SpeechletException("Invalid guess: " + guessString);
            }
            final int guess = parseInt(guessString);
            final int targetNumber = (int)session.getAttribute("targetNumber");
            if (guess == targetNumber) {
                final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
                outputSpeech.setText("You guessed correctly. I was thinking of " + targetNumber);
                return newTellResponse(outputSpeech);
            } else if (guess > targetNumber) {
                final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
                outputSpeech.setText("Sorry, " + guess + " is too high. Try again.");
                final Reprompt reprompt = new Reprompt();
                reprompt.setOutputSpeech(outputSpeech);
                return newAskResponse(outputSpeech, reprompt);
            }
            final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Sorry, " + guess + " is too low. Try again.");
            final Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(outputSpeech);
            return newAskResponse(outputSpeech, reprompt);
        } else if (GUESS_NUMBER_INTENT.matches(intent)) {
            final String lowerString = LOWER.getValue(intent);
            final String upperString = UPPER.getValue(intent);
            // TODO validation
            final int lower = parseInt(lowerString);
            final int upper = parseInt(upperString);

            final int guess = round( ( upper - lower ) / 2.0f ) + lower;
            final List<Integer> guesses = new LinkedList<>();
            guesses.add(guess);

            session.setAttribute("specifiedLower", lower); // FIXME use Range
            session.setAttribute("specifiedUpper", upper);
            session.setAttribute("effectiveLower", lower); // FIXME use Range
            session.setAttribute("effectiveUpper", upper);
            session.setAttribute("guesses", guesses);
            session.setAttribute("lastGuess", guess);

            final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Is it " + guess + "?");
            final Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(outputSpeech);
            return newAskResponse(outputSpeech, reprompt);
        } else if (PROVIDE_FEEDBACK_INTENT.matches(intent)) {
            // FIXME
            final int lastGuess = (int)session.getAttribute("lastGuess");
            final String relation = RELATION.getValue(intent); // FIXME enum
            if ("high".equalsIgnoreCase(relation)) {
                final int effectiveLower = (int) session.getAttribute("effectiveLower");
                final int effectiveUpper = lastGuess;

                final int guess = round((effectiveUpper - effectiveLower) / 2.0f) + effectiveLower;
                session.setAttribute("effectiveLower", effectiveLower);
                session.setAttribute("effectiveUpper", effectiveUpper);
                ((List<Integer>) session.getAttribute("guesses")).add(guess);
                session.setAttribute("lastGuess", guess);
                final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
                outputSpeech.setText("Is it " + guess + "?");
                final Reprompt reprompt = new Reprompt();
                reprompt.setOutputSpeech(outputSpeech);
                return newAskResponse(outputSpeech, reprompt);
            } else if ("low".equalsIgnoreCase(relation)) {
                final int effectiveLower = lastGuess;
                final int effectiveUpper = (int) session.getAttribute("effectiveUpper");

                final int guess = round((effectiveUpper - effectiveLower) / 2.0f) + effectiveLower;
                session.setAttribute("effectiveLower", effectiveLower);
                session.setAttribute("effectiveUpper", effectiveUpper);
                ((List<Integer>) session.getAttribute("guesses")).add(guess);
                session.setAttribute("lastGuess", guess);
                final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
                outputSpeech.setText("Is it " + guess + "?");
                final Reprompt reprompt = new Reprompt();
                reprompt.setOutputSpeech(outputSpeech);
                return newAskResponse(outputSpeech, reprompt);
            }
            throw new SpeechletException("Invalid relation: " + relation);
        } else if (CONFIRM_GUESS_INTENT.matches(intent)) {
            final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Thank you, that was fun!");
            return newTellResponse(outputSpeech);
        }
        throw new SpeechletException("Invalid intent: " + intent.getName());
    }

    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        // TODO Auto-generated method stub
        logger.info( "( onSessionEnded: {}, {} )", request, session );
    }

}