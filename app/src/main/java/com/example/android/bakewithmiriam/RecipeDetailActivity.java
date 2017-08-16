package com.example.android.bakewithmiriam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.bakewithmiriam.dataobjects.Recipe;
import com.example.android.bakewithmiriam.dataobjects.RecipeStep;

import java.util.List;

import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements
        RecipeStepsFragment.OnStepSelectedListener {

    private static final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();

    private static boolean mTwoPane;
    private static Recipe currentRecipe;
    private RecipeStep mSelectedStep;

    public static RecipeStep getNextStep(String stepId) {
        RecipeStep nextStep = null;
        List<RecipeStep> currentRecipeSteps = currentRecipe.getSteps();

        int cnt = 0;
        for (RecipeStep step : currentRecipeSteps) {
            cnt += 1;
            if (stepId.equals(step.getStepId())) {
                nextStep = step;
                break;
            }
            //This returns the next highest stepId available in case some stepId is missing from the JSON
            else if (Integer.parseInt(step.getStepId()) == cnt && Integer.parseInt(stepId) < cnt) {
                nextStep = step;
                break;
            }
        }
        return nextStep;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ButterKnife.bind(this);

        //Getting the Recipe from the Bundle
        Bundle extras = getIntent().getExtras();
        currentRecipe = extras.getParcelable("RECIPE");
        Log.d(LOG_TAG, "Recipe selected name from Detail Activity is: "
                + currentRecipe.getRecipeName());
        Log.d(LOG_TAG, "Ingredients: " + currentRecipe.getIngredients().size());
        Log.d(LOG_TAG, "Steps: " + currentRecipe.getSteps().size());

        if (savedInstanceState != null) {
            mSelectedStep = savedInstanceState.getParcelable("selectedStep");
            mTwoPane = savedInstanceState.getBoolean("twoPane");
        } else {

            if (findViewById(R.id.step_detail_container) != null) {
                mTwoPane = true;
                mSelectedStep = currentRecipe.getSteps().get(0);
            } else {
                mTwoPane = false;
            }
        }

        if (mTwoPane) {
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        }
    }

    @Override
    public void onStepSelected(RecipeStep currentStep) {
        if (mTwoPane) {
            mSelectedStep = currentStep;
            Log.d(LOG_TAG, "Current step is: " + currentStep.getShortDescription());
            StepDetailFragment newStepFragment = new StepDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, newStepFragment)
                    .commit();
        } else {
            Bundle extras = new Bundle();
            extras.putParcelable("STEP", currentStep);
            Intent stepDetailActivityIntent = new Intent(this, StepDetailActivity.class);
            stepDetailActivityIntent.putExtras(extras);

            startActivity(stepDetailActivityIntent);
        }
    }

    public RecipeStep getCurrentStep() {
        return mSelectedStep;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("selectedStep", mSelectedStep);
        outState.putBoolean("twoPane", mTwoPane);
    }
}
