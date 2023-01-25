package de.presti.wrapper;

import com.google.gson.*;
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

    public static String[] KEYS = new String[] {
            "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
            "AIzaSyCtkvNIR1HCEwzsqK6JuE6KqpyjusIRI30",
            "AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w",
            "AIzaSyC8UYZpvA2eknNex0Pjid0_eTLJoDu6los",
            "AIzaSyCjc_pVEDi4qsv5MtC2dMXzpIaDoRFLsxw",
            "AIzaSyDHQ9ipnphqTzDqZsbtd8_Ru4_kiKVQe2k"
    };

    public static JsonObject CONTEXT;

    public static List<SearchResult> search(String query) throws IOException, InterruptedException {
        List<SearchResult> results = new ArrayList<>();
        query = URLEncoder.encode(query, StandardCharsets.UTF_8);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("query", query);

        JsonElement jsonElement = send("/search", jsonObject);
        JsonArray actualContent = jsonElement.getAsJsonObject().getAsJsonObject("contents")
                .getAsJsonObject("twoColumnSearchResultsRenderer").getAsJsonObject("primaryContents")
                .getAsJsonObject("sectionListRenderer").getAsJsonArray("contents").get(0).getAsJsonObject()
                .getAsJsonObject("itemSectionRenderer").getAsJsonArray("contents");

        for (int i = 0; i < actualContent.size(); i++) {
            JsonObject result = actualContent.get(i).getAsJsonObject();
            if (result.has("videoRenderer")) {
                results.add(GSON.fromJson(result.getAsJsonObject("videoRenderer"), VideoSearchResult.class));
            } else if (result.has("channelRenderer")) {
                results.add(GSON.fromJson(result.getAsJsonObject("channelRenderer"), SearchResult.class));
            } else if (result.has("shelfRenderer")) {
                // Mostly not actual stuff related to the search query.
                break;
            }
            /*if (result.has("videoRenderer")) {
                JsonObject videoRenderer = result.getAsJsonObject("videoRenderer");

                String title = videoRenderer.getAsJsonObject("title").getAsJsonArray("runs").get(0).getAsJsonObject()
                        .getAsJsonPrimitive("text").getAsString();

                long viewCount = Long.parseLong(videoRenderer.getAsJsonObject("viewCountText")
                        .getAsJsonPrimitive("simpleText").getAsString().replaceAll("[^0-9.]", ""));

                results.add(new VideoSearchResult(videoRenderer.getAsJsonPrimitive("videoId").getAsString(), title, viewCount));
            }*/
        }

        return results;
    }

    private static JsonElement send(String path, JsonObject requestObject) throws IOException, InterruptedException {

        if (CONTEXT == null) {
            CONTEXT = createContext();
        }

        requestObject.add("context", CONTEXT);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path + "?key=" + KEYS[ThreadLocalRandom.current().nextInt(KEYS.length)] + "&prettyPrint=true"))
                .POST(HttpRequest.BodyPublishers.ofString(requestObject.toString())).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return JsonParser.parseString(httpResponse.body());
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
