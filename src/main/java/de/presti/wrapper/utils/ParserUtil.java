package de.presti.wrapper.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Class used to parse the simplest parts such as "run" based arrays or simple text objects.
 * Priorities are:
 * - Simple Text
 * - Content
 * - Text
 * - Url
 * - Runs
 * - Sources
 */
public class ParserUtil {

    /**
     * Determine the internal structure and try to extract a string.
     * @param jsonObject the parent.
     * @param path the path to the object.
     * @return the content or null.
     */
    public static String extractSimpleText(JsonObject jsonObject, String path) {
        if (jsonObject.has(path)) {
            JsonElement jsonElement = jsonObject.get(path);
            if (jsonElement.isJsonObject() || jsonElement.isJsonArray()) {
                return extractSimpleText(jsonElement);
            } else if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsJsonPrimitive().getAsString();
            }
        }

        return null;
    }

    /**
     * Determine the internal structure and try to extract a string.
     * @param jsonElement the object.
     * @return the content or null.
     */
    public static String extractSimpleText(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject pathObject = jsonElement.getAsJsonObject();
            if (pathObject.has("simpleText") && pathObject.get("simpleText").isJsonPrimitive()) {
                return pathObject.get("simpleText").getAsString();
            }

            if (pathObject.has("content") && pathObject.get("content").isJsonPrimitive()) {
                return pathObject.get("content").getAsString();
            }

            if (pathObject.has("text") && pathObject.get("text").isJsonPrimitive()) {
                return pathObject.get("text").getAsString();
            }

            if (pathObject.has("url") && pathObject.get("url").isJsonPrimitive()) {
                return pathObject.get("url").getAsString();
            }

            if (pathObject.has("runs") && pathObject.get("runs").isJsonArray()) {
                return extractSimpleText(pathObject.get("runs").getAsJsonArray());
            }

            if (pathObject.has("sources") && pathObject.get("sources").isJsonArray()) {
                return extractSimpleText(pathObject.get("sources").getAsJsonArray());
            }
        } else if (jsonElement.isJsonArray()) {
            if (!jsonElement.getAsJsonArray().isEmpty()) {
                return extractSimpleText(jsonElement.getAsJsonArray().get(0));
            }
        }

        return null;
    }

    public static JsonElement stripJson(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // Remove useless information.

            if (jsonObject.has("responseContext")) {
                jsonObject.remove("responseContext");
            }

            if (jsonObject.has("topbar")) {
                jsonObject.remove("topbar");
            }

            if (jsonObject.has("trackingParams")) {
                jsonObject.remove("trackingParams");
            }

            if (jsonObject.has("microformat")) {
                JsonObject microformat = jsonObject.getAsJsonObject("microformat");

                if (microformat.has("microformatDataRenderer")) {
                    JsonObject microformatRender = microformat.getAsJsonObject("microformatDataRenderer");

                    if (microformatRender.has("availableCountries")) {
                        microformatRender.remove("availableCountries");
                    }

                    if (microformatRender.has("linkAlternates")) {
                        microformatRender.remove("linkAlternates");
                    }

                    microformat.add("microformatDataRenderer", microformatRender);
                }

                jsonObject.add("microformat", microformat);
            }

            if (jsonObject.has("metadata")) {
                JsonObject metadata = jsonObject.getAsJsonObject("metadata");

                if (metadata.has("channelMetadataRenderer")) {
                    JsonObject channelMetadataRenderer = metadata.getAsJsonObject("channelMetadataRenderer");

                    if (channelMetadataRenderer.has("availableCountryCodes")) {
                        channelMetadataRenderer.remove("availableCountryCodes");
                    }

                    if (channelMetadataRenderer.has("androidAppindexingLink")) {
                        channelMetadataRenderer.remove("androidAppindexingLink");
                    }

                    if (channelMetadataRenderer.has("iosAppindexingLink")) {
                        channelMetadataRenderer.remove("iosAppindexingLink");
                    }

                    if (channelMetadataRenderer.has("androidDeepLink")) {
                        channelMetadataRenderer.remove("androidDeepLink");
                    }

                    metadata.add("channelMetadataRenderer", channelMetadataRenderer);
                }

                jsonObject.add("metadata", metadata);
            }

            jsonElement = jsonObject;
        }
        return jsonElement;
    }
}
