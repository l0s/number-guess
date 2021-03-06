package com.macasaet.numberguess;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.macasaet.numberguess.speechlet.NumberGuessSpeechlet;

/**
 * Entry point into the Number Guessing Lambda function.
 *
 * <p>Copyright &copy; 2016 Carlos Macasaet.</p>
 *
 * @author Carlos Macasaet
 */
public class NumberGuessHandler extends SpeechletRequestStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(NumberGuessHandler.class);
    private static final Set<String> supportedApplicationIds;

    static {
        final Properties secrets = new Properties();
        try {
            try (final InputStream inputStream = NumberGuessHandler.class.getResourceAsStream("/secrets.properties")) {
                secrets.load(inputStream);
                final String appIdsString = String.valueOf(secrets.get("applicationId"));
                supportedApplicationIds = unmodifiableSet(new HashSet<>(asList(appIdsString.split(","))));
            }
        } catch (final IOException ioe) {
            final String message = "Error loading secrets: " + ioe.getMessage();
            logger.error(message, ioe);
            throw new RuntimeException(message, ioe);
        }
    }

    public NumberGuessHandler() {
        super(new NumberGuessSpeechlet(), supportedApplicationIds);
    }

}