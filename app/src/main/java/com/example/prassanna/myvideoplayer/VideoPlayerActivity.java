package com.example.prassanna.myvideoplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity
	implements VideoRelatedAdapter.VideoClickListener {
	private SimpleExoPlayer player;
	private int mPlayIndex = 0;
	private ArrayList<Video> mVideoList = new ArrayList<>();
	private TextView tvVideoTitle, tvVideoDes;
	private RecyclerView mRelatedRecyclerView;
	Context mContext;
	private VideoRelatedAdapter mVideoRelatedAdapter;
	private boolean shouldLoadNext = false;
	long currentDuration = 0;
	private boolean isPlaying;
	
	
	private Player.EventListener playerEventListener = new Player.EventListener() {
		
		@Override
		public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
		
		}
		
		@Override
		public void onTracksChanged(
			TrackGroupArray trackGroups,
			TrackSelectionArray trackSelections) {
			
		}
		
		@Override
		public void onLoadingChanged(boolean isLoading) {
		}
		
		@Override
		public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
			switch (playbackState) {
				case Player.STATE_BUFFERING:
					break;
				case Player.STATE_READY:
					
					break;
				case Player.STATE_ENDED:
					currentDuration = 0;
					if (mPlayIndex >= mVideoList.size()) {
						mPlayIndex = 0;
					} else {
						mPlayIndex += 1;
					}
					
					isPlaying = false;
					initializePlayer();
					
					break;
			}
		}
		
		@Override
		public void onRepeatModeChanged(int repeatMode) {
		
		}
		
		@Override
		public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
		
		}
		
		@Override
		public void onPlayerError(ExoPlaybackException error) {
		
		}
		
		@Override
		public void onPositionDiscontinuity(int reason) {
		
		}
		
		@Override
		public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
		
		}
		
		@Override
		public void onSeekProcessed() {
		
		}
	};
	
	private void setPlayPause() {
		try {
			player.seekTo(currentDuration);
			player.setPlayWhenReady(isPlaying);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);
		
		initViews();
		prepareObjects();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	private void initViews() {
		tvVideoTitle = findViewById(R.id.tv_video_title);
		tvVideoDes = findViewById(R.id.tv_video_des);
		mRelatedRecyclerView = findViewById(R.id.mRecyclerView);
	}
	
	private void prepareObjects() {
		Intent intent = getIntent();
		mPlayIndex = intent.getIntExtra("position", 0);
		mVideoList = (ArrayList<Video>) intent.getSerializableExtra("videoData");
		
		mVideoRelatedAdapter = new VideoRelatedAdapter(mContext, mVideoList, this);
		mRelatedRecyclerView.setLayoutManager(
			new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
		mRelatedRecyclerView.setAdapter(mVideoRelatedAdapter);
		
		initializePlayer();
		
		DatabaseHandler db = new DatabaseHandler(this);
		Video selectedVideo = db.getVideo(mVideoList.get(mPlayIndex).getVideoId());
		
		Log.e("selectedVideo id", String.valueOf(selectedVideo.getVideoId()));
		Log.e("selectedVideo title", selectedVideo.getTitle());
		Log.e("selectedVideo des", selectedVideo.getDescription());
		Log.e("selectedVideo seek", String.valueOf(selectedVideo.getSeektime()));
		currentDuration = selectedVideo.getSeektime();
		if (player != null) {
			player.seekTo(currentDuration);
			Log.e("selectedVideo seek done", String.valueOf(currentDuration));
		}
	}
	
	private void setTextForVideo() {
		tvVideoTitle.setText(mVideoList.get(mPlayIndex).getTitle());
		tvVideoDes.setText(mVideoList.get(mPlayIndex).getDescription());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		releasePlayer();
	}
	
	private void initializePlayer() {
		// Create a default TrackSelector
		BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
		TrackSelection.Factory videoTrackSelectionFactory =
			new AdaptiveTrackSelection.Factory(bandwidthMeter);
		TrackSelector trackSelector =
			new DefaultTrackSelector(videoTrackSelectionFactory);
		
		//Initialize the player
		player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
		
		//Initialize simpleExoPlayerView
		SimpleExoPlayerView simpleExoPlayerView = findViewById(R.id.exoplayer);
		simpleExoPlayerView.setPlayer(player);
		
		// Produces DataSource instances through which media data is loaded.
		DataSource.Factory dataSourceFactory =
			new DefaultDataSourceFactory(this, Util.getUserAgent(this, "MyVideoPlayer"));
		
		// Produces Extractor instances for parsing the media data.
		ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
		MediaSource videoSource = null;
		
		try {
			// This is the MediaSource representing the media to be played.
			Uri videoUri = Uri.parse(mVideoList.get(mPlayIndex).getUrl());
			videoSource = new ExtractorMediaSource(videoUri,
			                                       dataSourceFactory, extractorsFactory,
			                                       null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Prepare the player with the source.
		player.prepare(videoSource);
		player.addListener(playerEventListener);
		isPlaying = true;
		setPlayPause();
		setTextForVideo();
	}
	
	
	@Override
	public void onItemClicked(int pos) {
		mPlayIndex = pos;
		shouldLoadNext = false;
		initializePlayer();
	}
	
	private void releasePlayer() {
		if (player != null) {
			
			long seekTime = player.getCurrentPosition();
			Log.e("releasePlayer seekTime", String.valueOf(seekTime));
			
			Video mVideoData = mVideoList.get(mPlayIndex);
			DatabaseHandler db = new DatabaseHandler(this);
			db.deleteVideo(
				new Video(mVideoData.getVideoId(), mVideoData.getTitle(),
				          mVideoData.getDescription(),
				          mVideoData.getThumb(), mVideoData.getUrl(), seekTime));
			db.addVideo(
				new Video(mVideoData.getVideoId(), mVideoData.getTitle(),
				          mVideoData.getDescription(),
				          mVideoData.getThumb(), mVideoData.getUrl(), seekTime));
			db.close();
			
			player.release();
			player = null;
		}
	}
}
