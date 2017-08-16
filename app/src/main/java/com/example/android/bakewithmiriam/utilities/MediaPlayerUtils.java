package com.example.android.bakewithmiriam.utilities;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static com.example.android.bakewithmiriam.R.id.playerView;


/**
 * Created by apoorva on 7/27/17.
 */

public class MediaPlayerUtils {

    private static final String LOG_TAG = MediaPlayerUtils.class.getSimpleName();
    private static MediaSessionCompat mMediaSession;
    private static PlaybackStateCompat.Builder mStateBuilder;

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    public static void initializeMediaSession(Context context, SimpleExoPlayer exoPlayer) {
        mMediaSession = new MediaSessionCompat(context, LOG_TAG);

        //Enable callbacks from MediaButtons and Transport Controls.
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
        );

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback(exoPlayer));

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    public static SimpleExoPlayer initializePlayer(Uri mediaUri, Context context,
                                                   SimpleExoPlayer exoplayer) {
        if (exoplayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoplayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            //playerView.setPlayer(exoplayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(context, "BakingWithMiriam");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    context, userAgent), new DefaultExtractorsFactory(), null, null);
            exoplayer.prepare(mediaSource);
        }
        return exoplayer;
    }

    /**
     * Release ExoPlayer.
     */
    public static SimpleExoPlayer releasePlayer(SimpleExoPlayer exoPlayer) {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
        return exoPlayer;
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private static class MySessionCallback extends MediaSessionCompat.Callback {
        SimpleExoPlayer mExoPlayer;

        public MySessionCallback(SimpleExoPlayer exoPlayer) {
            mExoPlayer = exoPlayer;
        }

        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
