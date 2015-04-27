package se.greatbrain.sats.json;

import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.concurrent.atomic.AtomicInteger;

public final class JsonParser
{
    public static JsonArray refactorClassTypes(JsonArray classTypes)
    {
        JsonArray refactoredClassTypes = new JsonArray();

        for (JsonElement element : classTypes)
        {
            JsonObject classType = element.getAsJsonObject();
            JsonArray classCategories = classType.get("classCategories")
                    .getAsJsonArray();
            JsonArray classCategoriesAsObjects = new JsonArray();

            for (JsonElement id : classCategories)
            {
                JsonObject jsonCategory = new JsonObject();
                jsonCategory.add("id", id.getAsJsonPrimitive());
                classCategoriesAsObjects.add(jsonCategory);
            }

            classType.remove("classCategories");
            classType.add("classCategories", classCategoriesAsObjects);
            refactoredClassTypes.add(classType);
        }

        return refactoredClassTypes;
    }

    public static JsonArray refactorCenters(JsonArray regions)
    {
        for (JsonElement element : regions)
        {
            JsonObject region = element.getAsJsonObject();
            JsonArray centers = region.get("centers").getAsJsonArray();
            JsonArray centersWithLng = new JsonArray();

            for (JsonElement jsonElement : centers)
            {
                JsonObject center = jsonElement.getAsJsonObject();
                JsonPrimitive lng = center.get("long").getAsJsonPrimitive();
                center.remove("long");
                center.add("lng", lng);
                centersWithLng.add(center);
            }
            region.remove("centers");
            region.add("centers", centersWithLng);
        }

        return regions;
    }
}
