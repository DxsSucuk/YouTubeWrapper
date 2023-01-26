package de.presti.wrapper;

import com.google.gson.*;
import de.presti.wrapper.entities.VideoResult;
import de.presti.wrapper.entities.channel.ChannelResult;
import de.presti.wrapper.entities.channel.ChannelVideoResult;
import de.presti.wrapper.entities.search.ChannelSearchResult;
import de.presti.wrapper.entities.search.SearchResult;
import de.presti.wrapper.entities.search.VideoSearchResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains all the constants for the YouTubeWrapper.
 * And it also functions as a directory for all actions.
 *
 * @author Presti
 * @since 1.0.0
 */
@Slf4j
public class YouTubeWrapper {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String BASE_URL = "https://www.youtube.com/youtubei/v1";

    public static String[] KEYS = new String[]{
            "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
            "AIzaSyCtkvNIR1HCEwzsqK6JuE6KqpyjusIRI30",
            "AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w",
            "AIzaSyCjc_pVEDi4qsv5MtC2dMXzpIaDoRFLsxw"
    };

    public static JsonObject CONTEXT;

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

    public static VideoResult getVideo(String videoId) throws IllegalAccessException, IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("videoId", videoId);

        return new VideoResult(send("/player", jsonObject).getAsJsonObject(), false);
    }

    private static JsonElement send(String path, JsonObject requestObject) throws IllegalAccessException, IOException, InterruptedException {

        if (CONTEXT == null) {
            CONTEXT = createContext();
        }

        requestObject.add("context", CONTEXT);

        String currentKey = KEYS[ThreadLocalRandom.current().nextInt(KEYS.length)];

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path + "?key=" + currentKey + "&prettyPrint=true"))
                .POST(HttpRequest.BodyPublishers.ofString(requestObject.toString())).build();

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

}
