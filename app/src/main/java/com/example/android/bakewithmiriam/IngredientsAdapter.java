package com.example.android.bakewithmiriam;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apoorva on 7/25/17.
 */

public class IngredientsAdapter extends
        RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private static final String LOG_TAG = IngredientsAdapter.class.getSimpleName();
    Context mContext;
    List<String> mListOfIngredients;

    public IngredientsAdapter(Context context, List<String> listOfIngredients) {
        mContext = context;
        mListOfIngredients = listOfIngredients;
    }

    @Override
    public IngredientsAdapter.IngredientViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        int layoutId = R.layout.item_ingredient;
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsAdapter.IngredientViewHolder holder, int position) {
        String ingredientInfo = mListOfIngredients.get(position);
        holder.ingredient.setText(ingredientInfo);
    }

    @Override
    public int getItemCount() {
        if (mListOfIngredients == null) {
            return 0;
        }
        return mListOfIngredients.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ingredient)
        TextView ingredient;

        public IngredientViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
