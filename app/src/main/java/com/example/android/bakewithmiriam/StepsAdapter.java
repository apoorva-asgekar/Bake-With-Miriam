package com.example.android.bakewithmiriam;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakewithmiriam.dataobjects.RecipeStep;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apoorva on 7/25/17.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    Context mContext;
    List<RecipeStep> mListOfSteps;
    OnClickStepListener mOnClickHandler;

    public StepsAdapter(Context context, List<RecipeStep> recipeStepList,
                        OnClickStepListener listener) {
        mContext = context;
        mListOfSteps = recipeStepList;
        mOnClickHandler = listener;
    }

    public interface OnClickStepListener{
        void onClickStep(RecipeStep currentStep);
    }

    @Override
    public StepsAdapter.StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        int layoutId = R.layout.item_recipe_step;
        boolean shouldAttachToParentImmediately = false;
        View v = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);

        return new StepsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StepsAdapter.StepsViewHolder holder, int position) {
        RecipeStep currentStep = mListOfSteps.get(position);

        holder.stepId.setText("Step " + (position + 1));
        holder.stepDescription.setText(currentStep.getShortDescription());
    }

    @Override
    public int getItemCount() {
        if(mListOfSteps == null) {
            return 0;
        }
        return mListOfSteps.size();
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_step_id)
        TextView stepId;

        @BindView(R.id.tv_step)
        TextView stepDescription;

        public StepsViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            RecipeStep currentStep = mListOfSteps.get(position);
            mOnClickHandler.onClickStep(currentStep);
        }
    }
}
