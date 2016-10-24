package com.hanson.pintu.util;
import android.content.Context;
import android.media.MediaPlayer;

import com.hanson.pintu.R;

public class SoundPlayer {

	private final static int SOUND_NEXT = R.raw.next;
	private final static int SOUND_STEP = R.raw.step;
	private final static int SOUND_STEP_2 = R.raw.tip;
	private final static int SOUND_COMPLETE_LEVEL_1 = R.raw.yes1;
	private final static int SOUND_COMPLETE_LEVEL_2 = R.raw.yes2;
	private final static int SOUND_COMPLETE_LEVEL_3 = R.raw.yes3;
	private static MediaPlayer mMediaPlayer = null;
	/**
	 * 
	 */
	public SoundPlayer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public final static void sound_step(Context context) {
		if(mMediaPlayer != null){
			mMediaPlayer.release();
		}
		mMediaPlayer = MediaPlayer.create(context, SOUND_STEP);
        mMediaPlayer.start();
	}
	
	public final static void sound_step2(Context context) {
		if(mMediaPlayer != null){
			mMediaPlayer.release();
		}
		mMediaPlayer = MediaPlayer.create(context, SOUND_STEP_2);
        mMediaPlayer.start();
	}
	
	public final static void sound_next(Context context) {
		if(mMediaPlayer != null){
			mMediaPlayer.release();
		}
		mMediaPlayer = MediaPlayer.create(context, SOUND_NEXT);
        mMediaPlayer.start();
	}
	
	public final static void sound_complete_level_1(Context context) {
		if(mMediaPlayer != null){
			mMediaPlayer.release();
		}
		mMediaPlayer = MediaPlayer.create(context, SOUND_COMPLETE_LEVEL_1);
        mMediaPlayer.start();
	}
	
	public final static void sound_complete_level_2(Context context) {
		if(mMediaPlayer != null){
			mMediaPlayer.release();
		}
		mMediaPlayer = MediaPlayer.create(context, SOUND_COMPLETE_LEVEL_2);
        mMediaPlayer.start();
	}
	
	public final static void sound_complete_level_3(Context context) {
		if(mMediaPlayer != null){
			mMediaPlayer.release();
		}
		mMediaPlayer = MediaPlayer.create(context, SOUND_COMPLETE_LEVEL_3);
        mMediaPlayer.start();
	}
}
