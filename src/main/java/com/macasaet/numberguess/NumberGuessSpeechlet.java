package com.macasaet.numberguess;

import static org.apache.commons.lang3.RandomUtils.*;
import static com.amazon.speech.speechlet.SpeechletResponse.*;
import static com.macasaet.numberguess.Intents.*;
import static com.macasaet.numberguess.Slots.GUESS;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isNumeric;

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
        }
        throw new SpeechletException("Invalid intent: " + intent.getName());
    }

    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        // TODO Auto-generated method stub
        logger.info( "( onSessionEnded: {}, {} )", request, session );
    }

}