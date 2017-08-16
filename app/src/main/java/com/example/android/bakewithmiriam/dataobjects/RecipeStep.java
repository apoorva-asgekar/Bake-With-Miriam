package com.example.android.bakewithmiriam.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by apoorva on 7/19/17.
 */
//Java object class which holds a single recipe step.
public class RecipeStep implements Parcelable {

    public static final Parcelable.Creator<RecipeStep> CREATOR =
            new Parcelable.Creator<RecipeStep>() {
                @Override
                public RecipeStep createFromParcel(Parcel source) {
                    return new RecipeStep(source);
                }

                @Override
                public RecipeStep[] newArray(int size) {
                    return new RecipeStep[size];
                }
            };
    private String mStepId;
    private String mShortDescription;
    private String mDescription;
    private String mVideoUrl;
    private String mThumbnailUrl;

    public RecipeStep(String stepId, String shortDescription, String description, String videoUrl,
                      String thumbnailUrl) {
        this.mStepId = stepId;
        this.mShortDescription = shortDescription;
        this.mDescription = description;
        this.mVideoUrl = videoUrl;
        this.mThumbnailUrl = thumbnailUrl;
    }

    //Constructor used while re-scontructing the object from a Parcel.
    public RecipeStep(Parcel in) {
        this.mStepId = in.readString();
        this.mShortDescription = in.readString();
        this.mDescription = in.readString();
        this.mVideoUrl = in.readString();
        this.mThumbnailUrl = in.readString();
    }

    public String getStepId() {
        return this.mStepId;
    }

    public String getShortDescription() {
        return this.mShortDescription;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getVideoUrl() {
        return this.mVideoUrl;
    }

    public String getThumbnailUrl() {
        return this.mThumbnailUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mStepId);
        parcel.writeString(this.mShortDescription);
        parcel.writeString(this.mDescription);
        parcel.writeString(this.mVideoUrl);
        parcel.writeString(this.mThumbnailUrl);
    }
}
