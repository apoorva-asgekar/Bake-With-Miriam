package com.example.android.bakewithmiriam;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakewithmiriam.dataobjects.RecipeStep;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = StepDetailActivity.class.getSimpleName();
    @Nullable
    @BindView(R.id.button_next_step)
    Button nextButton;
    @Nullable
    @BindView(R.id.tv_step_description)
    TextView stepDescription;
    private RecipeStep mCurrentStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        mCurrentStep = extras.getParcelable("STEP");

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            stepDescription.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        } else {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nextStepId = String.valueOf(Integer.parseInt(mCurrentStep.getStepId())
                            + 1);
                    Log.d(LOG_TAG, "Next step id is: " + nextStepId);

                    Intent newStepDetailActivityIntent =
                            new Intent(StepDetailActivity.this, StepDetailActivity.class);
                    RecipeStep nextStep = RecipeDetailActivity.getNextStep(nextStepId);
                    if (nextStep != null) {
                        Log.d(LOG_TAG, "Next Step in fragment:" + nextStep.getShortDescription());
                        Bundle extras = new Bundle();
                        extras.putParcelable("STEP", nextStep);
                        Intent stepDetailActivityIntent = new Intent(StepDetailActivity.this,
                                StepDetailActivity.class);
                        stepDetailActivityIntent.putExtras(extras);
                        startActivity(stepDetailActivityIntent);
                    } else {
                        Toast.makeText(StepDetailActivity.this, getString(R.string.no_steps),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public RecipeStep getCurrentStep() {
        return getIntent().getExtras().getParcelable("STEP");
    }

}
