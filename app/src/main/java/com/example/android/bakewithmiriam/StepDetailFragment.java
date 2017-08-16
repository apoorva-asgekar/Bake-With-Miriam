package com.example.android.bakewithmiriam;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakewithmiriam.dataobjects.RecipeStep;
import com.example.android.bakewithmiriam.utilities.MediaPlayerUtils;
import com.example.android.bakewithmiriam.utilities.NetworkUtils;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bakewithmiriam.R.id.playerView;

/**
 * Created by apoorva on 7/26/17.
 */

public class StepDetailFragment extends Fragment {

    private static final String LOG_TAG = StepDetailFragment.class.getSimpleName();
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    @Nullable
    @BindView(R.id.tv_step_description)
    TextView mStepDescription;
    @Nullable
    @BindView(R.id.button_next_step)
    Button mNextButton;
    private RecipeStep mCurrentStep = null;
    private SimpleExoPlayer mExoPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String activityName = null;


        final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (isAdded()) {
            activityName = getActivity().getClass().getSimpleName();
        }
        if (activityName != null && !activityName.isEmpty()) {
            if (activityName.equals(RecipeDetailActivity.class.getSimpleName())) {
                mNextButton.setVisibility(View.GONE);
                mCurrentStep = ((RecipeDetailActivity) getActivity()).getCurrentStep();
            } else {
                mCurrentStep = ((StepDetailActivity) getActivity()).getCurrentStep();
            }
        }

        mStepDescription.setText(mCurrentStep.getDescription());

        // Initialize the player view.
        mPlayerView =
                (SimpleExoPlayerView) rootView.findViewById(playerView);
        setVideoForStep(mCurrentStep);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "onDestroyView");
        MediaPlayerUtils.releasePlayer(mExoPlayer);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        mPlayerView.setPlayer(mExoPlayer);
        if (mExoPlayer != null) {
            mExoPlayer.seekTo(0);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void setVideoForStep(RecipeStep currentStep) {
        Uri videoUri = null;
        String thumbnailUrl = null;
        if (currentStep.getVideoUrl() != null) {
            videoUri = Uri.parse(currentStep.getVideoUrl());
        }
        if (currentStep.getThumbnailUrl() != null) {
            thumbnailUrl = currentStep.getThumbnailUrl().trim();
        }

        if (thumbnailUrl == null || thumbnailUrl.isEmpty()) {
            mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.no_video));
        } else {
            Log.d(LOG_TAG, "Content Type: " + NetworkUtils.getUrlContentType(thumbnailUrl));
            String urlContentType = NetworkUtils.getUrlContentType(thumbnailUrl);
            if (urlContentType != null && !urlContentType.isEmpty()) {
                if (urlContentType.startsWith("image/")) {
                    mPlayerView.setDefaultArtwork(NetworkUtils.getBitmapFromURL(thumbnailUrl));
                } else if (urlContentType.startsWith("video/")
                        && (videoUri == null)) {
                    videoUri = Uri.parse(thumbnailUrl);
                }
            }
            //In case thumbnail Url is provided but is of unidentifiable content type -
            // display the default art work.
            else {
                mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                        (getResources(), R.drawable.no_video));
            }
        }
        // Initialize the Media Session.
        MediaPlayerUtils.initializeMediaSession(getActivity(), mExoPlayer);
        // Initialize the player.
        if (videoUri != null) {
            mExoPlayer = MediaPlayerUtils.initializePlayer(videoUri, getActivity(), mExoPlayer);
            mPlayerView.setPlayer(mExoPlayer);
            Log.d(LOG_TAG, "Player initialized");
        }
    }

}
