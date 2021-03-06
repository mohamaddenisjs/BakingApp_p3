package net.denis.sundevs.bakingapp.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import net.denis.sundevs.bakingapp.App;
import net.denis.sundevs.bakingapp.R;
import net.denis.sundevs.bakingapp.event.RecipeStepEvent;
import net.denis.sundevs.bakingapp.model.Steps;
import net.denis.sundevs.bakingapp.util.Constant;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_STEP;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_STEP_FIRST;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_STEP_LAST;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_STEP_NUMBER;

/**
 * Created by moham on 10/09/17.
 */

public class RecipeStepDetailFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.detail_step_instruction)
    TextView mDetailStepInstruction;

    @BindView(R.id.detail_step_video)
    SimpleExoPlayerView mDetailStepVideo;

    @BindView(R.id.detail_step_nav_prev)
    Button mDetailStepPrev;

    @BindView(R.id.detail_step_nav_next)
    Button mDetailStepNext;

    @BindView(R.id.detail_step_image)
    ImageView mDetailStepImage;

    private SimpleExoPlayer mPlayer;
    private Steps mStep;
    private long mPlaybackPosition;
    private int mCurrentWindow;
    private boolean mPlayWhenReady;
    private int mNumber;
    private boolean mFirst;
    private boolean mLast;
    private static long position = 0;

    public RecipeStepDetailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);

        initView(rootView);

        String strStepJson = getArguments().getString(EXTRA_STEP);
        mStep = App.getInstance().getGson().fromJson(strStepJson, Steps.class);
        mNumber = getArguments().getInt(EXTRA_STEP_NUMBER);
        mFirst = getArguments().getBoolean(EXTRA_STEP_FIRST);
        mLast = getArguments().getBoolean(EXTRA_STEP_LAST);

        mDetailStepInstruction.setText(mStep.getDescription());

        mDetailStepImage.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(mStep.getThumbnailUrl()) && !mStep.getThumbnailUrl().substring(mStep.getThumbnailUrl().length() - 4, mStep.getThumbnailUrl().length()).equals(".mp4")) {
            mDetailStepImage.setVisibility(View.VISIBLE);
            Constant.Function.setImageResource(getContext(), mStep.getThumbnailUrl(), mDetailStepImage);
        }


        mDetailStepPrev.setVisibility(View.VISIBLE);
        mDetailStepNext.setVisibility(View.VISIBLE);

        if (mFirst) mDetailStepPrev.setVisibility(View.GONE);
        if (mLast) mDetailStepNext.setVisibility(View.GONE);
        //step3
        position = C.TIME_UNSET;

        if (savedInstanceState != null) {
//            mStep = savedInstanceState.getParcelable(EXTRA_STEP);
            //step 4
            position = savedInstanceState.getLong(EXTRA_STEP);
            mNumber = savedInstanceState.getInt(EXTRA_STEP_NUMBER);
            mFirst = savedInstanceState.getBoolean(EXTRA_STEP_FIRST);
            mLast = savedInstanceState.getBoolean(EXTRA_STEP_LAST);
        }

        return rootView;
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void initView(View rootView) {
        ButterKnife.bind(this, rootView);

        mDetailStepPrev.setOnClickListener(this);
        mDetailStepNext.setOnClickListener(this);
    }

    private void initializePlayer() {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        mDetailStepVideo.setPlayer(mPlayer);

        mPlayer.setPlayWhenReady(mPlayWhenReady);
        mPlayer.seekTo(mCurrentWindow, mPlaybackPosition);

        if (TextUtils.isEmpty(mStep.getVideoUrl()) && TextUtils.isEmpty(mStep.getThumbnailUrl())) {
            mDetailStepVideo.setVisibility(View.GONE);
        } else {
            mDetailStepVideo.setVisibility(View.VISIBLE);
            Uri uri = null;
            if (!TextUtils.isEmpty(mStep.getVideoUrl())) {
                uri = Uri.parse(mStep.getVideoUrl());
            } else if (!TextUtils.isEmpty(mStep.getThumbnailUrl()) && mStep.getThumbnailUrl().substring(mStep.getThumbnailUrl().length() - 4, mStep.getThumbnailUrl().length()).equals(".mp4")) {
                uri = Uri.parse(mStep.getThumbnailUrl());
            } else if (!TextUtils.isEmpty(mStep.getThumbnailUrl()) && !mStep.getThumbnailUrl().substring(mStep.getThumbnailUrl().length() - 4, mStep.getThumbnailUrl().length()).equals(".mp4")) {
                mDetailStepImage.setVisibility(View.VISIBLE);
                Constant.Function.setImageResource(getContext(), mStep.getThumbnailUrl(), mDetailStepImage);
            }

            MediaSource mediaSource = buildMediaSource(uri);
            //step 5
            if (position != C.TIME_UNSET) mPlayer.seekTo(position);
            mPlayer.prepare(mediaSource, true, false);
        }
    }
//
//    private void updateResumePosition(long position, boolean playWhenReady) {
//        this.position = position;
//        mPlayer.seekTo(position);
//        mPlayer.setPlayWhenReady(playWhenReady);
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable(EXTRA_STEP, mStep);
        outState.putInt(EXTRA_STEP_NUMBER, mNumber);
        outState.putBoolean(EXTRA_STEP_FIRST, mFirst);
        outState.putBoolean(EXTRA_STEP_LAST, mLast);


    }

    @Override
    public void onStart() {
        super.onStart();
        hideSystemUI();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUI();
        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
//            mPlayer.setPlayWhenReady(true);
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        if (Util.SDK_INT > 23) {
//            releasePlayer();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();

    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getCurrentPosition();
            mCurrentWindow = mPlayer.getCurrentWindowIndex();
            mPlayWhenReady = mPlayer.getPlayWhenReady();
//            updateResumePosition(position, true);
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void hideSystemUI() {
        mDetailStepVideo.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_step_nav_prev:
                previousStep();
                break;
            case R.id.detail_step_nav_next:
                nextStep();
                break;
            default:
                break;
        }
    }

    private void previousStep() {
        EventBus eventBus = App.getInstance().getEventBus();
        RecipeStepEvent event = new RecipeStepEvent();
        event.setSelectedPosition(mNumber - 1);
        eventBus.post(event);
    }

    private void nextStep() {
        EventBus eventBus = App.getInstance().getEventBus();
        RecipeStepEvent event = new RecipeStepEvent();
        event.setSelectedPosition(mNumber + 1);
        eventBus.post(event);
    }
}