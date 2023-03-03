package de.presti.wrapper;

import com.google.gson.*;
import de.presti.wrapper.entities.VideoResult;
import de.presti.wrapper.entities.channel.ChannelResult;
import de.presti.wrapper.entities.channel.ChannelShortResult;
import de.presti.wrapper.entities.channel.ChannelVideoResult;
import de.presti.wrapper.entities.search.ChannelSearchResult;
import de.presti.wrapper.entities.search.SearchResult;
import de.presti.wrapper.entities.search.VideoSearchResult;

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

    public static String BASE_URL = "https://www.youtube.com/youtubei/v1";

    public static String[] KEYS = new String[]{
            "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
            "AIzaSyCtkvNIR1HCEwzsqK6JuE6KqpyjusIRI30",
            "AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w",
            "AIzaSyCjc_pVEDi4qsv5MtC2dMXzpIaDoRFLsxw"
    };

    public static Map<String, Long> actionRetries = new HashMap<>();

    public static JsonObject CONTEXT;

    //region None Future

    public static List<SearchResult> search(String query, SearchResult.FILTER filter) throws IllegalAccessException, IOException, InterruptedException {
        List<SearchResult> results = new ArrayList<>();
        query = URLEncoder.encode(query, StandardCharsets.UTF_8);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("query", query);

        if (filter != null && filter != SearchResult.FILTER.NONE) {
            jsonObject.addProperty("params", filter.getParams());
        }

        JsonElement jsonElement = send("/search", jsonObject);

        JsonArray actualContent = jsonElement.getAsJsonObject().getAsJsonObject("contents")
                .getAsJsonObject("twoColumnSearchResultsRenderer").getAsJsonObject("primaryContents")
                .getAsJsonObject("sectionListRenderer").getAsJsonArray("contents").get(0).getAsJsonObject()
                .getAsJsonObject("itemSectionRenderer").getAsJsonArray("contents");

        for (int i = 0; i < actualContent.size(); i++) {
            JsonObject result = actualContent.get(i).getAsJsonObject();
            if (result.has("videoRenderer")) {
                VideoSearchResult videoSearchResult = new VideoSearchResult(result.getAsJsonObject("videoRenderer"));
                results.add(videoSearchResult);
            } else if (result.has("channelRenderer")) {
                ChannelSearchResult channelSearchResult = new ChannelSearchResult(result.getAsJsonObject("channelRenderer"));
                results.add(channelSearchResult);
            }
        }

        return results;
    }

    public static ChannelResult getChannel(String channelId) throws IllegalAccessException, IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browseId", channelId);

        return new ChannelResult(send("/browse", jsonObject).getAsJsonObject());
    }

    public static ChannelVideoResult getChannelVideo(String channelId) throws IllegalAccessException, IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browseId", channelId);
        jsonObject.addProperty("params", "EgZ2aWRlb3PyBgQKAjoA");

        return new ChannelVideoResult(send("/browse", jsonObject).getAsJsonObject());
    }

    public static ChannelShortResult getChannelShort(String channelId) throws IllegalAccessException, IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browseId", channelId);
        jsonObject.addProperty("params", "EgZzaG9ydHPyBgUKA5oBAA%3D%3D");

        return new ChannelShortResult(send("/browse", jsonObject).getAsJsonObject());
    }

    public static VideoResult getVideo(String videoId, boolean isShort) throws IllegalAccessException, IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("videoId", videoId);
        if (isShort)
            jsonObject.addProperty("params", "8AEByAMkuAQ0");

        return new VideoResult(send("/player", jsonObject).getAsJsonObject(), false, false);
    }

    private static JsonElement send(String path, JsonObject requestObject) throws IllegalAccessException, IOException, InterruptedException {
        return send(path, requestObject, getRandomId(8));
    }

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

            JsonElement jsonElement = JsonParser.parseString(httpResponse.body());

            if (jsonElement.getAsJsonObject().has("error")) {
                if (jsonElement.getAsJsonObject()
                        .getAsJsonObject("error").getAsJsonPrimitive("code").getAsInt() == 403) {
                    throw new IllegalAccessException("Invalid API key: " + currentKey);
                } else {
                    throw new IOException("Error while sending request: " + jsonElement);
                }
            }

            return jsonElement;
        } catch (ConnectException connectException) {
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
    }

    //endregion

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
