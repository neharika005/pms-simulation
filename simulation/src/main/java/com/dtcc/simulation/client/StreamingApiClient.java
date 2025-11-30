package com.dtcc.simulation.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StreamingApiClient {

    @Value("${fmp.api.key}")
    private String apiKey;

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    String apiUrl =
                            "https://financialmodelingprep.com/stable/aftermarket-trade"
                                    + "?symbol=AAPL&apikey=" + apiKey;

                    URL url = new URL(apiUrl);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(url.openStream(), "UTF-8")
                    );

                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }

                    
                    Thread.sleep(200);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
