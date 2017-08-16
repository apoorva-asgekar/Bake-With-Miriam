package com.example.android.bakewithmiriam.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakewithmiriam.R;
import com.example.android.bakewithmiriam.dataobjects.Recipe;
import com.example.android.bakewithmiriam.utilities.RecipeJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by apoorva on 8/3/17.
 */

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String LOG_TAG = GridRemoteViewsFactory.class.getSimpleName();
    Context mContext;
    List<Recipe> mListOfRecipes;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        try {
            mListOfRecipes = RecipeJsonUtils.getRecipes(RecipeJsonUtils.RECIPE_JSON_URL);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JsonException while getting data for widget.", e);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mListOfRecipes == null) {
            return 0;
        }
        return mListOfRecipes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Recipe currentRecipe = mListOfRecipes.get(position);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_recipe_card);

        String recipeName = currentRecipe.getRecipeName();
        views.setTextViewText(R.id.widget_recipe_name, recipeName);
        ArrayList<String> ingredientList = (ArrayList) currentRecipe.getIngredients();
        String ingredients = null;
        for (String ing : ingredientList) {
            if (ingredients == null) {
                ingredients = ing + "\n";
            } else {
                ingredients = ingredients + ing + '\n';
            }
        }
        views.setTextViewText(R.id.widget_recipe_ing_list, ingredients);

        String recipeImageUrl = currentRecipe.getImage();
        if (recipeImageUrl == null || recipeImageUrl.isEmpty()) {
            String imageDrawableResourceName = (recipeName.replaceAll("\\s+", "")).toLowerCase();
            int imageResourceId = mContext.getResources().getIdentifier(
                    imageDrawableResourceName,
                    "drawable",
                    mContext.getPackageName());
            Log.d(LOG_TAG, "Resource Id: " + imageResourceId);
            views.setImageViewResource(R.id.widget_recipe_image, imageResourceId);
        } else {
            try {
                Bitmap bitmap = Picasso.with(mContext).load(recipeImageUrl).get();
                views.setImageViewBitmap(R.id.widget_recipe_image, bitmap);
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOEXception while loading recipe image", e);
            }
        }

        Bundle extras = new Bundle();
        extras.putParcelable("RECIPE", currentRecipe);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_ll_recipe_card, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
