import com.google.gson.*;
import de.presti.wrapper.YouTubeWrapper;
import de.presti.wrapper.entities.VideoResult;
import de.presti.wrapper.entities.channel.ChannelResult;
import de.presti.wrapper.entities.channel.ChannelShortResult;
import de.presti.wrapper.entities.channel.ChannelVideoResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] ids = new String[] { "UCXuqSBlHAE6Xw-yeJA0Tunw", "UCxLD4Sqi6CQ1uscjQrzMIPQ" };
        for (String id : ids) {
            ChannelResult channel = YouTubeWrapper.getChannel(id);
            System.out.println(channel.getSubscriber());
            System.out.println(channel);

            ChannelShortResult shorts = YouTubeWrapper.getChannelShort(id);
            System.out.println(shorts.getShorts().size());
            System.out.println(shorts.getShorts());

            ChannelVideoResult videos = YouTubeWrapper.getChannelVideo(id);
            System.out.println(videos.getVideos().size());
            System.out.println(videos.getVideos());
        }
    }

    public void testStuff() throws Exception {
        JsonObject requestJSON = new JsonObject();
        requestJSON.addProperty("query", "memerinoto");

        JsonObject context = new JsonObject();
        JsonObject client = new JsonObject();
        JsonObject mainAppWebInfo = new JsonObject();

        client.addProperty("hl", "en");
        client.addProperty("gl", "US");
        client.addProperty("clientName", "WEB");
        client.addProperty("clientVersion", "2.20210721.00.00");
        client.addProperty("clientFormFactor", "UNKNOWN_FORM_FACTOR");

        mainAppWebInfo.addProperty("graftUrl", "https://www.youtube.com/results?search_query=memerinoto");

        client.add("mainAppWebInfo", mainAppWebInfo);

        context.add("client", client);

        requestJSON.add("context", context);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://www.youtube.com/youtubei/v1/search?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8&prettyPrint=true"))
                .POST(HttpRequest.BodyPublishers.ofString(requestJSON.toString())).build();

        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        JsonElement jsonElement = JsonParser.parseString(httpResponse.body());

        JsonArray actualContent = jsonElement.getAsJsonObject().getAsJsonObject("contents").getAsJsonObject("twoColumnSearchResultsRenderer").getAsJsonObject("primaryContents").getAsJsonObject("sectionListRenderer").getAsJsonArray("contents").get(0).getAsJsonObject().getAsJsonObject("itemSectionRenderer").getAsJsonArray("contents");

        System.out.println(actualContent);
    }
}