package com.example.android.bakewithmiriam;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakewithmiriam.dataobjects.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apoorva on 7/19/17.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private static final String LOG_TAG = RecipeAdapter.class.getSimpleName();

    List<Recipe> mListOfRecipes;
    RecipeAdapterOnClickHander mOnClickHandler;
    Context mContext;

    public RecipeAdapter(Context context, RecipeAdapterOnClickHander handler) {
        this.mContext = context;
        this.mOnClickHandler = handler;
    }

    @Override
    public RecipeAdapter.RecipeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idForListItem = R.layout.item_recipe_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(idForListItem, parent, shouldAttachToParentImmediately);

        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeAdapterViewHolder holder, int position) {
        String recipeName = mListOfRecipes.get(position).getRecipeName();
        String recipeImageUrl = mListOfRecipes.get(position).getImage();

        holder.recipeNameTextview.setText(recipeName);
        if (recipeImageUrl == null || recipeImageUrl.isEmpty()) {
            String imageDrawableResourceName = (recipeName.replaceAll("\\s+", "")).toLowerCase();
            int imageResourceId = mContext.getResources().getIdentifier(
                    imageDrawableResourceName,
                    "drawable",
                    mContext.getPackageName());
            Picasso.with(mContext)
                    .load(imageResourceId)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(holder.recipeImageview);
        } else {
            Picasso.with(mContext)
                    .load(recipeImageUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(holder.recipeImageview);
        }
    }

    @Override
    public int getItemCount() {
        if (mListOfRecipes == null) {
            return 0;
        }
        return mListOfRecipes.size();
    }

    public void setRecipeData(List<Recipe> listOfRecipes) {
        mListOfRecipes = listOfRecipes;
        notifyDataSetChanged();
    }

    public interface RecipeAdapterOnClickHander {
        void onClick(Recipe recipe);
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.recipe_imageview)
        ImageView recipeImageview;

        @BindView(R.id.recipe_name_textview)
        TextView recipeNameTextview;

        public RecipeAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Recipe currentRecipe = mListOfRecipes.get(position);
            mOnClickHandler.onClick(currentRecipe);
        }
    }
}
