package main.java;

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

import java.util.Optional;

public class SpeechletMain implements Speechlet {

    /** ny times api wrapper */
    private AlphaVantageAPI stockAPI = new AlphaVantageAPI();

    /**
     * Initialize values for the skill here
     * @param sessionStartedRequest
     * @param session
     * @throws SpeechletException
     */
    @Override
    public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
        // This is said by the device when you first open the skill
        // Not going to do anything here
        System.out.println("Session started with session id=" + session.getSessionId());
    }

    /**
     * Run when the skill is opened
     * @param launchRequest
     * @param session
     * @return
     * @throws SpeechletException
     */
    @Override
    public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
        System.out.println("Got launch request " + launchRequest.toString());
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("Smart stock is open");

        System.out.println("Sending text to user: " + speech.getText());

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }

    /**
     * Called to perform some kind of action
     * @param request The request (contains slots, intent, etc)
     * @param session Session info (session id, attributes, etc)
     * @return Response sent to device, alexa will say this.
     */
    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) {
        Optional<String> intentNameOpt = getIntentName(request);
        if(intentNameOpt.isPresent()) {
            String intentName = intentNameOpt.get();
            System.out.println("Got intent name " + intentName);
            if ("ListIntent".equals(intentName)) {
                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText("Valid categories are open, home, arts, automobiles, books, business, fashion, " +
                        "food, health, insider, magazine, movies, national, nyregion, obituaries, opinion, " +
                        "politics, realestate, science, sports, sundayreview, technology, theater, tmagazine, " +
                        "travel, upshot, and world");
                return SpeechletResponse.newTellResponse(speech);
            } else if("ClosePrice".equals(intentName)) {
                try {
                    String priceName = request.getIntent().getSlot("priceType").getValue();
                    String stockName = request.getIntent().getSlot("stockname").getValue();
                    // Get top story in a New York Times category provided by the user.
                   String lastClosingPrice = stockAPI.getStockInfo(stockName, priceName);
                    PlainTextOutputSpeech out = new PlainTextOutputSpeech();

                    System.out.println("request is " + request.getIntent().getSlot("stockname").getValue()
                    + " lastClosing price = " + lastClosingPrice);

                    if(lastClosingPrice != null) {
                        out.setText("the " + priceName + " price of " + request.getIntent().getSlot("stockname").getValue() + " is "
                                + lastClosingPrice);
                    } else {
                        out.setText("stock value of " + request.getIntent().getSlot("stockname").getValue() + " is unavailable.");
                    }
                    return SpeechletResponse.newTellResponse(out);
                } catch(Exception e) {
                    // Tell the user that something went wrong
                    System.err.println("Error getting stock data");
                    e.printStackTrace();
                    PlainTextOutputSpeech errorOut = new PlainTextOutputSpeech();
                    errorOut.setText("An error happened, please try again");
                    return SpeechletResponse.newTellResponse(errorOut);
                }
            }
        }

        System.out.println("Intent isn't present");
        PlainTextOutputSpeech error = new PlainTextOutputSpeech();
        error.setText("Unknown intent, exiting");
        return SpeechletResponse.newTellResponse(error);
    }

    /**
     * Handles intent or intent name being null
     * @param request Request to extract entent name from
     * @return Optional of the intent name
     */
    private Optional<String> getIntentName(IntentRequest request) {
        Intent intent = request.getIntent();
        Optional<String> intentName;
        if(null == intent.getName()) {
            return Optional.empty();
        } else {
            return Optional.of(intent.getName());
        }
    }

    /**
     * Perform cleanup here
     * @param sessionEndedRequest
     * @param session
     * @throws SpeechletException
     */
    @Override
    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
        System.out.println("Session ended with session id=" + session.getSessionId());

    }
}
