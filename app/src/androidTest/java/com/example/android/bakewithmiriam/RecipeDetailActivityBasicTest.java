package com.example.android.bakewithmiriam;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.example.android.bakewithmiriam.dataobjects.Recipe;
import com.example.android.bakewithmiriam.dataobjects.RecipeStep;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by apoorva on 8/14/17.
 */

public class RecipeDetailActivityBasicTest {

    private final static String STEP_DESC = "Step description";

    private RecipeStep step1 = new RecipeStep("1", "Intent step", STEP_DESC,
            null,null);
    private ArrayList<RecipeStep> listOfSteps = new ArrayList<RecipeStep>(Arrays.asList(step1));

    private Recipe intentRecipe = new Recipe("1", "Brownies", null, listOfSteps, "4", null);

    @Rule
    public ActivityTestRule<RecipeDetailActivity> mActivityTestRule
            = new ActivityTestRule<RecipeDetailActivity>(RecipeDetailActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Intent intent =
                    new Intent(InstrumentationRegistry.getContext(),RecipeDetailActivity.class);
            Bundle intentBundle = new Bundle();
            intentBundle.putParcelable("intentStub", intentRecipe);
            intent.putExtra("RECIPE",intentRecipe);
            return intent;
        }
    };

    @Test
    public void clickRecipeStep_displaysCorrectStepDescription() {
        //Find the view
        //Perform Action
        onView(withId(R.id.recyclerview_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.tv_step_description))
                .check(matches(withText(STEP_DESC)));

    }
}
