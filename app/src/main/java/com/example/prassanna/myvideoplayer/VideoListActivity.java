package com.example.prassanna.myvideoplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoListActivity extends AppCompatActivity {
	private static final String TAG = "LOG";
	RecyclerView mRecyclerView;
	private ProgressDialog pDialog;
	String url;
	AdapterVideoList mAdapterVideoList;
	ArrayList<Video> mVideoDataList = new ArrayList<>();
	private Context mContext = this;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_list);
		
		Intent intent = getIntent();
		if (intent != null) {
			String username = intent.getStringExtra("username");
			if (!TextUtils.isEmpty(username)) {
				Toast.makeText(mContext, username, Toast.LENGTH_SHORT).show();
			}
		}
		
		// URL to get Video JSON
		url = "https://interview-e18de.firebaseio.com/media.json?print=pretty";
		
		mRecyclerView = findViewById(R.id.mRecyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
		
		new getVideoListData().execute();
		
	}
	
	private class getVideoListData extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(VideoListActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			HttpHandler sh = new HttpHandler();
			
			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url);
			
			Log.e(TAG, "Response from url: " + jsonStr);
			
			if (jsonStr != null) {
				try {
					
					mVideoDataList = new GsonBuilder().create().
						fromJson(
							String.valueOf(new JSONArray(jsonStr)),
							new TypeToken<ArrayList<Video>>() {
							}.getType());
					
				} catch (final JSONException e) {
					Log.e(TAG, "Json parsing error: " + e.getMessage());
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(
								getApplicationContext(), "Json parsing error: " + e.getMessage(),
								Toast.LENGTH_LONG)
							     .show();
						}
					});
					
				}
			} else {
				Log.e("issues", "Couldn't get json from server.");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
							getApplicationContext(),
							"Couldn't get json from server. Check LogCat for possible errors!",
							Toast.LENGTH_LONG)
						     .show();
					}
				});
				
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing()) {
				pDialog.dismiss();
			}
			/**
			 * Updating parsed JSON data into ListView
			 * */
			
			mAdapterVideoList = new AdapterVideoList(mContext, mVideoDataList);
			mRecyclerView.setAdapter(mAdapterVideoList);
			
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
	}
}
