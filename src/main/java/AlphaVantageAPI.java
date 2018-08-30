package main.java;

import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

/**
 * Simple REST demo on getting data
 *
 * Probably better to use a library if this was a real world thing, but for a demo this is fine
 */
@SuppressWarnings("MagicConstant")
public class AlphaVantageAPI {
    private static final String API_KEY = "";


    /*public String getStockOpeningPrice(String stockName) throws Exception {
        getStockInfo(stockName);
        // parse this detail to get opening price and return it

    }*/


    /**
     * Get the top article from a specific new york times stockSymbol using REST (GET)
     * @param stockName The stockSymbol
     * @return Title of the article
     * @throws Exception On errors w/ REST
     */
    public String getStockInfo(String stockName, String priceType) throws Exception {

        Map<String, String> stockNameToSymbolMap = new HashMap<>();
        String priceToReturn= "";
        stockNameToSymbolMap.put("amazon", "AMZN");
        stockNameToSymbolMap.put("microsoft", "MSFT");
        stockNameToSymbolMap.put("faceboook", "FB");
        stockNameToSymbolMap.put("apple", "AAPL");
        stockNameToSymbolMap.put("tesla", "TSLA");
        stockNameToSymbolMap.put("walmart", "WMT");
        stockNameToSymbolMap.put("wells fargo", "WFC");
        stockNameToSymbolMap.put("a t and t", "T");
        stockNameToSymbolMap.put("verizon", "VZ");


        String inputSymbol = stockNameToSymbolMap.get(stockName.toLowerCase());


        if(inputSymbol == null || inputSymbol.isEmpty()){
            return null;
        }


        String endpointUrl =
                String.format("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                        inputSymbol,
                        API_KEY);

        URL url = new URL(endpointUrl);
        URLConnection connection = url.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String tmp;
        while((tmp = br.readLine()) != null) {
            sb.append(tmp);
        }

        System.out.println("From alphavantage got entire json" + sb.toString());

        // Create a JsonObject from the JSON the API gives back
        // Basically just a map of either maps or lists
        JSONObject jsonObject = new JSONObject(sb.toString());

     /*   // parse jsonObject

        String lastClosingPrice = ((JSONObject)jsonObject.getJSONObject("Meta Data")).toString();
       // String lastClosingPrice = "";*/
        JSONObject dateObject = jsonObject.getJSONObject("Time Series (Daily)")
                .getJSONObject(LocalDate.now().toString());
        //System.out.println("From alphavantage got metadata" + lastClosingPrice);
        switch (priceType) {
            case "close":
                priceToReturn = dateObject.getString("4. close");
                System.out.println("From alphavantage got close price" +
                        priceToReturn);
                break;
            case "open":
                priceToReturn = dateObject.getString("1. open");
                System.out.println("From alphavantage got open price" +
                        priceToReturn);
                break;
            case "high":
                priceToReturn = dateObject.getString("2. high");
                System.out.println("From alphavantage got high price" +
                        priceToReturn);
                break;
            case "low":
                priceToReturn = dateObject.getString("3. low");
                System.out.println("From alphavantage got low price" +
                        priceToReturn);
            case "volume":
                priceToReturn = dateObject.getString("5. volume");
                System.out.println("From alphavantage got volume price" +
                        priceToReturn);
                break;
            default:
                break;


        }
        String closePrice = jsonObject.getJSONObject("Time Series (Daily)")
                .getJSONObject("2018-08-27").getString("4. close");



        return priceToReturn;
    }
}
