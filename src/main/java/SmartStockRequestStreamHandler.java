package main.java;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.Arrays;
import java.util.HashSet;

/**
 * For the purposes of this project, this class is just boiler plate.
 * This is the class given to lambda for the alexa skill (full package - main.java.SmartStockRequestStreamHandler)
 *
 * Look at SpeechletMain for logic.
 */
public class SmartStockRequestStreamHandler extends SpeechletRequestStreamHandler {

    public SmartStockRequestStreamHandler() {
        super(new SpeechletMain(),
                new HashSet<>(Arrays.asList("amzn1.ask.skill.c097b6bf-5fb4-4c29-9177-41c6f3a2e2ab")));
    }
}
