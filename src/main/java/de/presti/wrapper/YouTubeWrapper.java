package de.presti.wrapper;

import com.google.gson.*;
import de.presti.wrapper.entities.VideoResult;
import de.presti.wrapper.entities.channel.ChannelResult;
import de.presti.wrapper.entities.channel.ChannelShortResult;
import de.presti.wrapper.entities.channel.ChannelVideoResult;
import de.presti.wrapper.entities.search.ChannelSearchResult;
import de.presti.wrapper.entities.search.SearchResult;
import de.presti.wrapper.entities.search.VideoSearchResult;
import io.sentry.Sentry;
import io.sentry.SentryEvent;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains all the constants for the YouTubeWrapper.
 * And it also functions as a directory for all actions.
 *
 * @author Presti
 * @since 1.0.0
 */
public class YouTubeWrapper {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * The base URL to the InnerTube API.
     */
    public static String BASE_URL = "https://www.youtube.com/youtubei/v1";

    /**
     * All public InnerTube Tokens
     */
    public static String[] KEYS = new String[]{
            "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
            "AIzaSyCtkvNIR1HCEwzsqK6JuE6KqpyjusIRI30",
            "AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w",
            "AIzaSyCjc_pVEDi4qsv5MtC2dMXzpIaDoRFLsxw"
    };

    /**
     * Hashmap containing a CallId and its retries.
     */
    public static Map<String, Long> actionRetries = new HashMap<>();

    /**
     * Pre-generated Context JSON for api calls.
     */
    public static JsonObject CONTEXT;

    //region None Future

    /**
     * Search for something based on the query and filter on YouTube.
     * @param query The Query String.
     * @param filter the {@link SearchResult.FILTER}.
     * @return a List of {@link SearchResult}.
     * @throws IllegalAccessException If a API-Token is invalid.
     * @throws IOException If there was an error while trying to process the request.
     * @throws InterruptedException If the HTTP request was interrupted.
     */
    public static List<SearchResult> search(String query, SearchResult.FILTER filter) throws IllegalAccessException, IOException, InterruptedException {
        List<SearchResult> results = new ArrayList<>();
        query = URLEncoder.encode(query, StandardCharsets.UTF_8);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("query", query);

        if (filter != null && filter != SearchResult.FILTER.NONE) {
            jsonObject.addProperty("params", filter.getParams());
        }

        JsonElement jsonElement = send("/search", jsonObject);

        try {
            JsonArray actualContent = jsonElement.getAsJsonObject().getAsJsonObject("contents")
                    .getAsJsonObject("twoColumnSearchResultsRenderer").getAsJsonObject("primaryContents")
                    .getAsJsonObject("sectionListRenderer").getAsJsonArray("contents").get(0).getAsJsonObject()
                    .getAsJsonObject("itemSectionRenderer").getAsJsonArray("contents");

            for (int i = 0; i < actualContent.size(); i++) {
                JsonObject result = actualContent.get(i).getAsJsonObject();
                if (result.has("videoRenderer")) {
                    jsonElement = result.getAsJsonObject("videoRenderer");
                    VideoSearchResult videoSearchResult = new VideoSearchResult(jsonElement.getAsJsonObject());
                    results.add(videoSearchResult);
                } else if (result.has("channelRenderer")) {
                    jsonElement = result.getAsJsonObject("channelRenderer");
                    ChannelSearchResult channelSearchResult = new ChannelSearchResult(jsonElement.getAsJsonObject());
                    results.add(channelSearchResult);
                }
            }

        } catch (Exception exception) {
            SentryEvent event = new SentryEvent(exception);
            event.setExtra("internalObject", jsonElement.toString());
            Sentry.captureEvent(event);
            throw exception;
        }

        return results;
    }

    /**
     * The a channel via its ID.
     * @param channelId The ID of the channel.
     * @return a {@link ChannelResult}
     * @throws IllegalAccessException If a API-Token is invalid.
     * @throws IOException If there was an error while trying to process the request.
     * @throws InterruptedException If the HTTP request was interrupted.
     */
    public static ChannelResult getChannel(String channelId) throws IllegalAccessException, IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browseId", channelId);

        return new ChannelResult(send("/browse", jsonObject).getAsJsonObject());
    }

    /**
     * Get all the videos of a channel via its ID.
     * @param channelId The ID of the channel.
     * @return a {@link ChannelVideoResult}
     * @throws IllegalAccessException If a API-Token is invalid.
     * @throws IOException If there was an error while trying to process the request.
     * @throws InterruptedException If the HTTP request was interrupted.
     */
    public static ChannelVideoResult getChannelVideo(String channelId) throws IllegalAccessException, IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browseId", channelId);
        jsonObject.addProperty("params", "EgZ2aWRlb3PyBgQKAjoA");

        return new ChannelVideoResult(send("/browse", jsonObject).getAsJsonObject());
    }

    /**
     * Get all the shorts of a channel via its ID.
     * @param channelId The ID of the channel.
     * @return a {@link ChannelShortResult}
     * @throws IllegalAccessException If a API-Token is invalid.
     * @throws IOException If there was an error while trying to process the request.
     * @throws InterruptedException If the HTTP request was interrupted.
     */
    public static ChannelShortResult getChannelShort(String channelId) throws IllegalAccessException, IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browseId", channelId);
        jsonObject.addProperty("params", "EgZzaG9ydHPyBgUKA5oBAA%3D%3D");

        return new ChannelShortResult(send("/browse", jsonObject).getAsJsonObject());
    }

    /**
     * Get a video via its ID.
     * @param videoId The ID of the Video.
     * @param isShort If the video is a short or not.
     * @return a {@link VideoResult}
     * @throws IllegalAccessException If a API-Token is invalid.
     * @throws IOException If there was an error while trying to process the request.
     * @throws InterruptedException If the HTTP request was interrupted.
     */
    public static VideoResult getVideo(String videoId, boolean isShort) throws IllegalAccessException, IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("videoId", videoId);
        if (isShort)
            jsonObject.addProperty("params", "8AEByAMkuAQ0");

        return new VideoResult(send("/player", jsonObject).getAsJsonObject(), false, false);
    }

    /**
     * Method to send request to the InnerTube API without manually doing anything related to API Issues or context creation.
     * @param path The URL path.
     * @param requestObject The extra request Object.
     * @return a {@link JsonElement} responds from the InnerTube API.
     * @throws IllegalAccessException If a API-Token is invalid.
     * @throws IOException If there was an error while trying to process the request.
     * @throws InterruptedException If the HTTP request was interrupted.
     */
    private static JsonElement send(String path, JsonObject requestObject) throws IllegalAccessException, IOException, InterruptedException {
        return send(path, requestObject, getRandomId(8));
    }

    /**
     * Method to send request to the InnerTube API without manually doing anything related to API Issues or context creation.
     * @param path The URL path.
     * @param requestObject The extra request Object.
     * @param callId Its call ID to use for retries.
     * @return a {@link JsonElement} responds from the InnerTube API.
     * @throws IllegalAccessException If a API-Token is invalid.
     * @throws IOException If there was an error while trying to process the request.
     * @throws InterruptedException If the HTTP request was interrupted.
     */
    private static JsonElement send(String path, JsonObject requestObject, String callId) throws IllegalAccessException, IOException, InterruptedException {

        if (CONTEXT == null) {
            CONTEXT = createContext();
        }

        requestObject.add("context", CONTEXT);

        String currentKey = KEYS[ThreadLocalRandom.current().nextInt(KEYS.length)];

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path + "?key=" + currentKey + "&prettyPrint=true"))
                .POST(HttpRequest.BodyPublishers.ofString(requestObject.toString())).build();

        try {
            HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JsonElement jsonElement;
            String responseBody = httpResponse.body();
            try {
                jsonElement = JsonParser.parseString(responseBody);
            } catch (Exception exception) {
                String trimmedBody = responseBody.trim().toLowerCase();
                if (trimmedBody.startsWith("<!doctype html>") && trimmedBody.contains("<title>error 5")) {
                    return retryAction(callId, path, requestObject);
                }

                throw new IOException("Invalid JSONElement: " + responseBody);
            }

            if (jsonElement.getAsJsonObject().has("error")) {
                int errorCode = jsonElement.getAsJsonObject()
                        .getAsJsonObject("error").getAsJsonPrimitive("code").getAsInt();
                if (errorCode == 403) {
                    throw new IllegalAccessException("Invalid API key: " + currentKey);
                } else if (errorCode == 429 || (errorCode >= 500 && errorCode <= 599)) {
                    return retryAction(callId, path, requestObject);
                } else {
                    throw new IOException("Error while sending request: " + jsonElement);
                }
            }

            return jsonElement;
        } catch (ConnectException connectException) {
            return retryAction(callId, path, requestObject);
        }
    }

    //endregion

    private static JsonElement retryAction(String callId, String path, JsonObject requestObject) throws IOException, InterruptedException, IllegalAccessException {
        long tries = (actionRetries.containsKey(callId) ? actionRetries.get(callId) : 0);

        if (tries >= 3) {
            JsonObject errorObject = new JsonObject();
            errorObject.addProperty("failed", true);
            actionRetries.remove(callId);
            return errorObject;
        } else {
            actionRetries.put(callId, tries + 1);
            return send(path, requestObject, callId);
        }
    }

    private static JsonObject createContext() {
        JsonObject context = new JsonObject();
        JsonObject client = new JsonObject();
        JsonObject mainAppWebInfo = new JsonObject();

        client.addProperty("hl", "en");
        client.addProperty("gl", "US");
        client.addProperty("clientName", "WEB");
        client.addProperty("clientVersion", "2.20210721.00.00");
        client.addProperty("clientFormFactor", "UNKNOWN_FORM_FACTOR");

        mainAppWebInfo.addProperty("graftUrl", "https://www.youtube.com/");

        client.add("mainAppWebInfo", mainAppWebInfo);

        context.add("client", client);

        return context;
    }

    /**
     * Get a String fully of random Number by the given length.
     *
     * @param length the wanted Length.
     * @return the {@link String} with the wanted Length.
     */
    public static String getRandomId(int length) {
        StringBuilder end = new StringBuilder();

        Random random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            end.append(random.nextInt(9));
        }

        return end.toString();
    }
}
