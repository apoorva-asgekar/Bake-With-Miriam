package com.example.android.bakewithmiriam;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakewithmiriam.dataobjects.Recipe;
import com.example.android.bakewithmiriam.dataobjects.RecipeStep;

import java.util.List;

/**
 * Created by apoorva on 7/25/17.
 */

public class RecipeStepsFragment extends Fragment implements StepsAdapter.OnClickStepListener {

    private static final String LOG_TAG = RecipeStepsFragment.class.getSimpleName();
    private OnStepSelectedListener mCallback;
    private Recipe mCurrentRecipe;

    //Mandatory empty constructor
    public RecipeStepsFragment() {
    }

    /**
     * This method makes sure that the container activity has implemented the callback.
     * If not it throws an Exception.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnStepSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_steps, container, false);

        // Get a reference to the RecyclerViews in the fragment_recipe_steps xml layout file
        RecyclerView ingredientsRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.recyclerview_ingredients);
        RecyclerView stepsRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.recyclerview_steps);

        //Get reference to the textview and set its value;
        TextView recipeNameTextView = (TextView) rootView.findViewById(R.id.tv_recipe_name);

        Bundle extras = getActivity().getIntent().getExtras();
        mCurrentRecipe = extras.getParcelable("RECIPE");
        List<RecipeStep> listOfRecipeSteps = mCurrentRecipe.getSteps();
        List<String> ingredientList = mCurrentRecipe.getIngredients();

        recipeNameTextView.setText(mCurrentRecipe.getRecipeName());


        // Create the adapters.
        // These adapters takes in the context and an ArrayList of all the recipe ingredietns
        // and steps to display.
        StepsAdapter mRecipeStepAdapter =
                new StepsAdapter(getContext(), listOfRecipeSteps, this);
        IngredientsAdapter mIngredientsAdapter =
                new IngredientsAdapter(getContext(), ingredientList);

        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set the adapters on the respective RecyclerViews
        ingredientsRecyclerView.setAdapter(mIngredientsAdapter);
        stepsRecyclerView.setAdapter(mRecipeStepAdapter);

        // Return the root view
        return rootView;
    }

    @Override
    public void onClickStep(RecipeStep currentStep) {
        mCallback.onStepSelected(currentStep);
    }

    //OnStepClickListener interface calls a method in the host activity named onStepClick.
    public interface OnStepSelectedListener {
        void onStepSelected(RecipeStep currentStep);
    }
}
