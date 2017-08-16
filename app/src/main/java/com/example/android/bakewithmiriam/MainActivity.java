package com.example.android.bakewithmiriam;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakewithmiriam.dataobjects.Recipe;
import com.example.android.bakewithmiriam.dataobjects.RecipeStep;
import com.example.android.bakewithmiriam.utilities.NetworkUtils;
import com.example.android.bakewithmiriam.utilities.RecipeJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity
        implements RecipeAdapter.RecipeAdapterOnClickHander {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int NETWORK_RECIPE_LOADER_ID = 111;

    @BindView(R.id.recipes_recycler_view)
    RecyclerView mRecyclerViewRecipe;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessage;

    RecipeAdapter mRecipeAdapter;

    LoaderManager.LoaderCallbacks<List<Recipe>> networkLoadListener =
            new LoaderManager.LoaderCallbacks<List<Recipe>>() {

                @Override
                public Loader<List<Recipe>> onCreateLoader(int id, Bundle args) {
                    return new AsyncTaskLoader<List<Recipe>>(MainActivity.this) {

                        List<Recipe> mListOfRecipes;

                        @Override
                        protected void onStartLoading() {
                            if (mListOfRecipes != null) {
                                deliverResult(mListOfRecipes);
                            } else {
                                forceLoad();
                            }
                        }

                        @Override
                        public List<Recipe> loadInBackground() {
                            List<Recipe> listOfRecipes = null;

                            try {
                                if(NetworkUtils.isNetworkConnected(getContext())) {
                                    listOfRecipes = RecipeJsonUtils
                                            .getRecipes(RecipeJsonUtils.RECIPE_JSON_URL);
                                }
                            } catch (JSONException e) {
                                Log.e(LOG_TAG, "JSON Exception while fetching data from the network.", e);
                            }
                            return listOfRecipes;
                        }

                        @Override
                        public void deliverResult(List<Recipe> data) {
                            mListOfRecipes = data;
                            super.deliverResult(data);
                        }
                    };
                }

                @Override
                public void onLoadFinished(Loader<List<Recipe>> loader, List<Recipe> data) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    mRecipeAdapter.setRecipeData(data);
                    if (data == null) {
                        showErrorMessage();
                    } else {
                        showMovies();
                    }

                }

                @Override
                public void onLoaderReset(Loader<List<Recipe>> loader) {

                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        int orientation = GridLayout.VERTICAL;
        int span = getResources().getInteger(R.integer.gridlayout_span);
        boolean reverseLayout = false;

        GridLayoutManager layoutManager = new GridLayoutManager(this, span, orientation, reverseLayout);

        mRecyclerViewRecipe.setHasFixedSize(true);
        mRecyclerViewRecipe.setLayoutManager(layoutManager);

        mRecipeAdapter = new RecipeAdapter(this, this);
        mRecyclerViewRecipe.setAdapter(mRecipeAdapter);

        getSupportLoaderManager().initLoader(NETWORK_RECIPE_LOADER_ID, null, networkLoadListener);
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent recipeDetailActivityIntent = new Intent(this, RecipeDetailActivity.class);
        Bundle recipeBundle = new Bundle();
        recipeBundle.putParcelable("RECIPE", recipe);
        recipeDetailActivityIntent.putExtras(recipeBundle);

        startActivity(recipeDetailActivityIntent);
    }

    /**
     * This method will make the movies visible and the error message invisible.
     */
    private void showMovies() {
        mRecyclerViewRecipe.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie data.
     */
    private void showErrorMessage() {
        mRecyclerViewRecipe.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }
}
