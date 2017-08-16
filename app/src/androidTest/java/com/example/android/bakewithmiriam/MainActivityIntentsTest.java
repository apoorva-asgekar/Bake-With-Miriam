package com.example.android.bakewithmiriam;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.IsNot.not;

/**
 * Created by apoorva on 8/14/17.
 */

public class MainActivityIntentsTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule
            = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(
                new Instrumentation.ActivityResult(
                        Activity.RESULT_OK, null));
    }

    @Test
    public void checkIntent_RecipeDetailActivity() {
        onView(ViewMatchers.withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0,click()));
        intended(hasComponent(
                RecipeDetailActivity.class.getName()));
    }
}
