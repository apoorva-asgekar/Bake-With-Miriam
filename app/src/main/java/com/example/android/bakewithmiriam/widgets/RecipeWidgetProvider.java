package com.example.android.bakewithmiriam.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.bakewithmiriam.R;
import com.example.android.bakewithmiriam.RecipeDetailActivity;
import com.example.android.bakewithmiriam.dataobjects.Recipe;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String recipeWidgetDetails =
                WidgetConfigureActivity.getRecipeDetailsPref(context, appWidgetId);
        Recipe currentRecipe =
                WidgetConfigureActivity.getCurrentRecipePref(context, appWidgetId);

        if(recipeWidgetDetails == null) {
            return;
        }
        String recipeName = null;
        String recipeIngList = null;

        if (recipeWidgetDetails.contains(":")) {
            // Split it.
            String[] parts = recipeWidgetDetails.split(":");
            recipeName = parts[0];
            recipeIngList = parts[1];
        } else {
            throw new IllegalArgumentException(
                    "String " + recipeWidgetDetails + " does not contain :");
        }
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
        views.setTextViewText(R.id.widget_recipe_name, recipeName);
        views.setTextViewText(R.id.widget_recipe_ing_list, recipeIngList);

        String imageDrawableResourceName = (recipeName.replaceAll("\\s+", "")).toLowerCase();
        int imageResourceId = context.getResources().getIdentifier(
                imageDrawableResourceName,
                "drawable",
                context.getPackageName());
        views.setImageViewResource(R.id.widget_recipe_image, imageResourceId);

        Bundle extras = new Bundle();
        extras.putParcelable("RECIPE", currentRecipe);
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtras(extras);
        PendingIntent pending =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_ll_recipe_card, pending);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WidgetConfigureActivity.deleteRecipePref(context, appWidgetId);
        }
    }
}

