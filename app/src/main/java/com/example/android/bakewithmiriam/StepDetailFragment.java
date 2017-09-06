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
    private static final String PLAYER_STATE = "buffered_position";
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
    private Long currentPosition;
    private Uri videoUri = null;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            currentPosition = savedInstanceState.getLong(PLAYER_STATE);
        }
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
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        mExoPlayer = MediaPlayerUtils.releasePlayer(mExoPlayer);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        if(mExoPlayer == null) {
            // Initialize the Media Session.
            MediaPlayerUtils.initializeMediaSession(getActivity(), mExoPlayer);
            // Initialize the player.
            if (videoUri != null) {
                mExoPlayer = MediaPlayerUtils.initializePlayer(videoUri, getActivity(), mExoPlayer);
                Log.d(LOG_TAG, "Player initialized");
            } else {
                mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                        (getResources(), R.drawable.no_video));
            }
        }
        mPlayerView.setPlayer(mExoPlayer);
        if (mExoPlayer != null) {
            if(currentPosition != null) {
                mExoPlayer.seekTo(currentPosition);
            } else {
                mExoPlayer.seekTo(0);
            }
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void setVideoForStep(RecipeStep currentStep) {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        long lastPosition = mExoPlayer.getCurrentPosition();
        outState.putLong(PLAYER_STATE, lastPosition);
    }
}
