package com.example.prassanna.myvideoplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "videoplayer";
	private static final String TABLE_VIDEO_PLAYER = "video";
	private static final String KEY_VIDEO_ID = "video_id";
	private static final String KEY_VIDEO_TITLE = "video_title";
	private static final String KEY_VIDEO_DES = "video_des";
	private static final String KEY_VIDEO_THUMP = "video_thumb";
	private static final String KEY_VIDEO_URL = "video_url";
	private static final String KEY_VIDEO_CURRENT_TIME = "video_time";
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//3rd argument to be passed is CursorFactory instance
	}
	
	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_VIDEO_PLAYER + "("
			+ KEY_VIDEO_ID + " INTEGER," + KEY_VIDEO_TITLE + " TEXT,"
			+ KEY_VIDEO_DES + " TEXT," + KEY_VIDEO_THUMP + " TEXT," + KEY_VIDEO_URL + " TEXT,"
			+ KEY_VIDEO_CURRENT_TIME + " INTEGER" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}
	
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO_PLAYER);
		
		// Create tables again
		onCreate(db);
	}
	
	// code to add the new contact
	void addVideo(Video video) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_VIDEO_ID, video.getVideoId()); // video id
		values.put(KEY_VIDEO_TITLE, video.getTitle()); // video title
		values.put(KEY_VIDEO_DES, video.getDescription()); // video des
		values.put(KEY_VIDEO_THUMP, video.getThumb()); // video image
		values.put(KEY_VIDEO_URL, video.getUrl()); // video url
		values.put(KEY_VIDEO_CURRENT_TIME, video.getSeektime()); // video seektime
		
		// Inserting Row
		db.insert(TABLE_VIDEO_PLAYER, null, values);
		//2nd argument is String containing nullColumnHack
		db.close(); // Closing database connection
	}
	
	// code to get the single contact
	Video getVideo(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_VIDEO_PLAYER,
		                         new String[]{KEY_VIDEO_ID, KEY_VIDEO_TITLE, KEY_VIDEO_DES,
			                         KEY_VIDEO_THUMP, KEY_VIDEO_URL, KEY_VIDEO_CURRENT_TIME},
		                         KEY_VIDEO_ID + "=?",
		                         new String[]{String.valueOf(id)}, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		
		Video video = new Video(Integer.parseInt(cursor.getString(0)),
		                        cursor.getString(1), cursor.getString(2), cursor.getString(3),
		                        cursor.getString(4), Integer.parseInt(cursor.getString(5)));
		// return video
		return video;
	}
	
	// code to get all video in a list view
	public List<Video> getAllVideos() {
		List<Video> videoList = new ArrayList<>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_VIDEO_PLAYER;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Video videoData = new Video();
				videoData.setVideoId(Integer.parseInt(cursor.getString(0)));
				videoData.setTitle(cursor.getString(1));
				videoData.setDescription(cursor.getString(2));
				videoData.setThumb(cursor.getString(3));
				videoData.setUrl(cursor.getString(4));
				videoData.setSeektime(cursor.getLong(5));
				// Adding video to list
				videoList.add(videoData);
			} while (cursor.moveToNext());
		}
		
		// return video list
		return videoList;
	}
	
	// code to update the single video
	public int updateVideo(Video video) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_VIDEO_ID, video.getVideoId());
		values.put(KEY_VIDEO_TITLE, video.getTitle());
		values.put(KEY_VIDEO_DES, video.getDescription());
		values.put(KEY_VIDEO_THUMP, video.getThumb());
		values.put(KEY_VIDEO_URL, video.getUrl());
		values.put(KEY_VIDEO_CURRENT_TIME, video.getSeektime());
		
		// updating row
		return db.update(TABLE_VIDEO_PLAYER, values, KEY_VIDEO_ID + " = ?",
		                 new String[]{String.valueOf(video.getVideoId())});
	}
	
	// Deleting single video
	public void deleteVideo(Video video) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_VIDEO_PLAYER, KEY_VIDEO_ID + " = ?",
		          new String[]{String.valueOf(video.getVideoId())});
		db.close();
	}
	
	// Getting video Count
	public int getVideoCount() {
		String countQuery = "SELECT  * FROM " + TABLE_VIDEO_PLAYER;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		
		// return count
		return cursor.getCount();
	}
	
}