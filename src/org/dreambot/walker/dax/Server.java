package org.dreambot.walker.dax;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.dreambot.walker.dax.models.BulkBankPathRequest;
import org.dreambot.walker.dax.models.BulkPathRequest;
import org.dreambot.walker.dax.models.PathResult;
import org.dreambot.walker.dax.models.exceptions.AuthorizationException;
import org.dreambot.walker.dax.models.exceptions.RateLimitException;
import org.dreambot.walker.dax.models.exceptions.UnknownException;
import org.dreambot.api.methods.MethodProvider;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Server {
    private static final String BASE_URL = "https://api.dax.cloud/walker";
    private final Gson gson;
    private final String key;
    private final String secret;
    private long rateLimit;

    public Server(String key, String secret) {
        this.gson = new Gson();
        this.key = key;
        this.secret = secret;
        this.rateLimit = 0L;
    }

    public List<PathResult> getPaths(BulkPathRequest bulkPathRequest) {
        return makePathRequest("https://api.dax.cloud/walker/generatePaths", gson.toJson(bulkPathRequest));
    }

    public List<PathResult> getBankPaths(BulkBankPathRequest bulkBankPathRequest) {
        return makePathRequest("https://api.dax.cloud/walker/generateBankPaths", gson.toJson(bulkBankPathRequest));
    }

    private List<PathResult> makePathRequest(String url, String jsonPayload) {
        if (System.currentTimeMillis() - rateLimit < 5000L) throw new RateLimitException("Throttling requests because key rate limit.");

        long start = System.currentTimeMillis();
        try {
            URL myurl = new URL(url);
            HttpURLConnection connection = (HttpsURLConnection) myurl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Method", "POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("key", this.key);
            connection.setRequestProperty("secret", this.secret);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            try {
                outputStream.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            } catch (Throwable e) {
                throw e;
            } finally {
                outputStream.close();
            }

            int responseCode = connection.getResponseCode();
            switch (responseCode) {
                case 429:
                    MethodProvider.logError("[Server] Rate limit hit");
                    this.rateLimit = System.currentTimeMillis();
                    throw new RateLimitException("RATE LIMIT HIT");
                case 401:
                    throw new AuthorizationException("Invalid API Key");
                case 200:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String responseBody = br.lines().collect(Collectors.joining());
                    if (responseBody.length() == 0) throw new IllegalStateException("Illegal response returned from server.");
                    MethodProvider.logInfo("[DaxWalker]: " + String.format("Generated path in %dms", System.currentTimeMillis() - start));
                    return this.gson.fromJson(responseBody, new TypeToken<List<PathResult>>() {}.getType());
            }
        } catch (IOException e) {
            MethodProvider.logError("[" + Level.SEVERE + "] Server: " + e);
        }
        throw new UnknownException("Error connecting to server.");
    }
}
