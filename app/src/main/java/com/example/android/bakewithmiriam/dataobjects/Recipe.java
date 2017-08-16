package com.example.android.bakewithmiriam.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apoorva on 7/19/17.
 */
//Java Object class to hold all the recipe information
public class Recipe implements Parcelable {
    public static final Parcelable.Creator<Recipe> CREATOR =
            new Parcelable.Creator<Recipe>() {
                @Override
                public Recipe createFromParcel(Parcel source) {
                    return new Recipe(source);
                }

                @Override
                public Recipe[] newArray(int size) {
                    return new Recipe[size];
                }
            };
    private String mRecipeId;
    private String mRecipeName;
    private List<String> mIngredients;
    private List<RecipeStep> mSteps;
    private String mServings;
    private String mImage;

    public Recipe(String recipeId, String recipeName, List<String> ingredients, List<RecipeStep> steps,
                  String servings, String image) {
        this.mRecipeId = recipeId;
        this.mRecipeName = recipeName;
        this.mIngredients = ingredients;
        this.mSteps = steps;
        this.mServings = servings;
        this.mImage = image;
    }

    //Constructor to use when re-constructing object from Parcel.
    public Recipe(Parcel in) {
        mRecipeId = in.readString();
        mRecipeName = in.readString();
        mIngredients = new ArrayList<String>();
        in.readStringList(mIngredients);
        mSteps = new ArrayList<RecipeStep>();
        in.readTypedList(mSteps, RecipeStep.CREATOR);
        mServings = in.readString();
        mImage = in.readString();
    }


    public String getRecipeId() {
        return this.mRecipeId;
    }

    public String getRecipeName() {
        return this.mRecipeName;
    }

    public List<String> getIngredients() {
        return this.mIngredients;
    }

    public List<RecipeStep> getSteps() {
        return this.mSteps;
    }

    public String getServings() {
        return this.mServings;
    }

    public String getImage() {
        return this.mImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mRecipeId);
        parcel.writeString(mRecipeName);
        parcel.writeStringList(mIngredients);
        parcel.writeTypedList(mSteps);
        parcel.writeString(mServings);
        parcel.writeString(mImage);
    }
}
