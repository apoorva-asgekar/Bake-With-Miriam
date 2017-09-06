package com.example.android.bakewithmiriam.widgets;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Spinner;

import com.example.android.bakewithmiriam.R;
import com.example.android.bakewithmiriam.RecipeDetailActivity;
import com.example.android.bakewithmiriam.dataobjects.Recipe;
import com.example.android.bakewithmiriam.utilities.RecipeJsonUtils;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apoorva on 9/1/17.
 */

public class WidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    @BindView(R.id.spinner_recipe_name)
    Spinner mRecipeNameSpinner;
    @BindView(R.id.btn_add)
    Button mAddWidgetButton;

    private List<Recipe> mListOfRecipes;

    private static final String PREFS_NAME = "AppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget";
    private static final String PREF_RECIPE_KEY = "currentRecipe";
    private static final String LOG_TAG = WidgetConfigureActivity.class.getSimpleName();

    public WidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_widget_configure);
        ButterKnife.bind(this);

        // Set layout size of activity
        getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final HashMap<String, String> recipeDetailsMap = new HashMap<>();
        final ArrayList<String> spnOptions = new ArrayList<>();

        //set adapter for the spinner
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spnOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        mRecipeNameSpinner.setAdapter(adapter);

        new AsyncTask<Void, Void, List<Recipe>>() {
            @Override
            protected List<Recipe> doInBackground(Void... params) {
                List<Recipe> listOfRecipes = null;
                try {
                    listOfRecipes = RecipeJsonUtils.getRecipes(RecipeJsonUtils.RECIPE_JSON_URL);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JsonException while getting data for widget.", e);
                }
                return listOfRecipes;
            }

            @Override
            protected void onPostExecute(List<Recipe> recipes) {
                 mListOfRecipes = recipes;
                for (Recipe recipe : mListOfRecipes) {
                    String recipeIngList = null;
                    spnOptions.add(recipe.getRecipeName());
                    for (String ing : recipe.getIngredients()) {
                        if (recipeIngList == null) {
                            recipeIngList = ing;
                        } else {
                            recipeIngList = recipeIngList + "," + "\n" + ing;
                        }
                    }
                    recipeDetailsMap.put(recipe.getRecipeName(), recipeIngList);
                }
                adapter.notifyDataSetChanged();
            }
        }.execute();



        //initializing RemoteViews and AppWidgetManager
        final AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        final RemoteViews remoteViews =
                new RemoteViews(this.getPackageName(), R.layout.recipe_widget);

        // Find the widget id from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAddWidgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe currentRecipe = null;
                String selectedRecipe = mRecipeNameSpinner.getSelectedItem().toString();
                for(Recipe recipe : mListOfRecipes) {
                    if (recipe.getRecipeName().equals(selectedRecipe)) {
                        currentRecipe = recipe;
                    }
                }

                remoteViews.setTextViewText(R.id.widget_recipe_name, selectedRecipe);
                remoteViews.setTextViewText(R.id.widget_recipe_ing_list,
                        recipeDetailsMap.get(selectedRecipe));
                String imageDrawableResourceName =
                        (selectedRecipe.replaceAll("\\s+", "")).toLowerCase();
                int imageResourceId = getApplicationContext().getResources().getIdentifier(
                        imageDrawableResourceName,
                        "drawable",
                        getApplicationContext().getPackageName());
                remoteViews.setImageViewResource(R.id.widget_recipe_image, imageResourceId);

                //Saving the selected Recipe as a Shared Preference.
                saveRecipePref(getApplicationContext(),
                        mAppWidgetId,
                        selectedRecipe + ":" + recipeDetailsMap.get(selectedRecipe),
                        currentRecipe);

                Bundle extras = new Bundle();
                extras.putParcelable("RECIPE", currentRecipe);
                Intent intent = new Intent(getApplicationContext(), RecipeDetailActivity.class);
                intent.putExtras(extras);
                PendingIntent pending =
                        PendingIntent.getActivity(getApplicationContext(), 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.widget_ll_recipe_card, pending);

                widgetManager.updateAppWidget(mAppWidgetId, remoteViews);
                Intent resultValue = new Intent();

                // Set the results as expected from a 'configure activity'.
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
    }


    // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipePref(Context context, int appWidgetId, String text, Recipe currentRecipe) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        Gson gson = new Gson();
        String json = gson.toJson(currentRecipe);
        prefs.putString(PREF_RECIPE_KEY + appWidgetId, json);
        prefs.apply();
        Log.d(LOG_TAG, "Recipe Prefs saved! " + text);
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String getRecipeDetailsPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String recipeDetails = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        Log.d(LOG_TAG, "Recipe details preference fetched: " + recipeDetails);
        return recipeDetails;
    }

    static Recipe getCurrentRecipePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String json = prefs.getString(PREF_RECIPE_KEY + appWidgetId, null);
        Gson gson = new Gson();
        Recipe currentRecipe = gson.fromJson(json, Recipe.class);
        Log.d(LOG_TAG, "Current Recipe preference fetched: " + json);
        return currentRecipe;
    }

    static void deleteRecipePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
        Log.d(LOG_TAG, "Recipe preference removed");
    }

}
