package com.example.android.bakewithmiriam.utilities;

/**
 * Created by apoorva on 7/20/17.
 */


import android.util.Log;

import com.example.android.bakewithmiriam.dataobjects.Recipe;
import com.example.android.bakewithmiriam.dataobjects.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class containing all methods which require a maniputalition of the Json received from
 * the network.
 */
public class RecipeJsonUtils {

    public static final String RECIPE_JSON_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String LOG_TAG = RecipeJsonUtils.class.getSimpleName();
    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_SERVINGS = "servings";
    private static final String JSON_IMAGE = "image";
    private static final String JSON_INGREDIENTS = "ingredients";
    private static final String JSON_STEPS = "steps";
    private static final String JSON_INGREDIENT_QUANTITY = "quantity";
    private static final String JSON_INGREDIENT_MEASURE = "measure";
    private static final String JSON_INGREDIENT_NAME = "ingredient";
    private static final String JSON_STEPS_ID = "id";
    private static final String JSON_STEPS_SHORT_DESCRIPTION = "shortDescription";
    private static final String JSON_STEPS_DESCRIPTION = "description";
    private static final String JSON_STEPS_VIDEO_URL = "videoURL";
    private static final String JSON_STEPS_THUMBNAIL_URL = "thumbnailURL";

    public static List<Recipe> getRecipes(String requestUrl) throws JSONException {
        List<Recipe> recipesArraylist = new ArrayList<Recipe>();

        URL recipeRequestUrl = null;
        try {
            recipeRequestUrl = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "MalformedURLException in getRecipes. ", e);
        }
        if (recipeRequestUrl == null) {
            return null;
        }
        try {
            String jsonResponse = NetworkUtils.getResponseFromUrl(recipeRequestUrl);
            JSONArray externalJsonArray = new JSONArray(jsonResponse.trim());

            for (int i = 0; i < externalJsonArray.length(); i++) {
                List<String> recipeIngredients = new ArrayList<String>();
                ;
                List<RecipeStep> listOfRecipeSteps = new ArrayList<RecipeStep>();
                ;
                JSONObject recipeObject = (JSONObject) externalJsonArray.get(i);
                String id = recipeObject.getString(JSON_ID);
                String name = recipeObject.getString(JSON_NAME);
                String servings = recipeObject.getString(JSON_SERVINGS);
                String image = recipeObject.getString(JSON_IMAGE);
                JSONArray ingredients = recipeObject.getJSONArray(JSON_INGREDIENTS);
                JSONArray steps = recipeObject.getJSONArray(JSON_STEPS);
                Log.d(LOG_TAG, "id: " + id);
                Log.d(LOG_TAG, "name: " + name);
                Log.d(LOG_TAG, "servings: " + servings);
                Log.d(LOG_TAG, "image: " + image);
                Log.d(LOG_TAG, "ingredients size: " + ingredients.length());
                Log.d(LOG_TAG, "Steps size: " + steps.length());
                for (int index = 0; index < ingredients.length(); index++) {
                    JSONObject ingredientObject = (JSONObject) ingredients.get(index);
                    String quantity = ingredientObject.getString(JSON_INGREDIENT_QUANTITY);
                    String measure = ingredientObject.getString(JSON_INGREDIENT_MEASURE);
                    String ingredient = ingredientObject.getString(JSON_INGREDIENT_NAME);
                    String ingredientInfo = quantity + " " + measure + " - " + ingredient;
                    recipeIngredients.add(ingredientInfo);
                }
                for (int index = 0; index < steps.length(); index++) {
                    JSONObject recipeStepsObject = (JSONObject) steps.get(index);
                    String stepID = recipeStepsObject.getString(JSON_STEPS_ID);
                    String shortDescription = recipeStepsObject
                            .getString(JSON_STEPS_SHORT_DESCRIPTION);
                    String description = recipeStepsObject.getString(JSON_STEPS_DESCRIPTION);
                    String videoUrl = recipeStepsObject.getString(JSON_STEPS_VIDEO_URL);
                    String thumbnailUrl = recipeStepsObject.getString(JSON_STEPS_THUMBNAIL_URL);
                    RecipeStep newRecipeStep = new RecipeStep(stepID, shortDescription, description,
                            videoUrl, thumbnailUrl);
                    listOfRecipeSteps.add(newRecipeStep);
                }
                Recipe newRecipe =
                        new Recipe(id, name, recipeIngredients, listOfRecipeSteps, servings, image);
                recipesArraylist.add(newRecipe);
            }

        } catch (IOException ioEx) {
            Log.e(LOG_TAG, "IOException in getRecipes. ", ioEx);
        }
        return recipesArraylist;
    }
}
