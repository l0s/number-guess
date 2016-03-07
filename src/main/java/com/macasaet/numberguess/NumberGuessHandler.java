package com.macasaet.numberguess;

import static java.util.Collections.singleton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

/**
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
                supportedApplicationIds = singleton(String.valueOf(secrets.get("applicationId")));
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