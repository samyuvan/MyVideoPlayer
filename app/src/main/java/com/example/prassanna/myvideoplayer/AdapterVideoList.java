package com.example.prassanna.myvideoplayer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;


public class AdapterVideoList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int TYPE_ITEM = 1;
	private ArrayList<Video> mDataList;
	private Context mContext;
	
	public AdapterVideoList(Context con1, ArrayList<Video> mDataList) {
		mContext = con1;
		this.mDataList = mDataList;
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(
			R.layout.adapter_video_list_items, parent, false);
		return new ItemViewHolder(v);
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
		final Video mData = mDataList.get(position);
		
		if (holder instanceof ItemViewHolder) {
			ItemViewHolder vItem = (ItemViewHolder) holder;
			
			Picasso.get()
			       .load(mData.getThumb())
			       .placeholder(R.drawable.img_video)
			       .error(R.drawable.img_video)
			       .into(vItem.videoBanner);
			
			vItem.adapterTitle.setText(URLDecoder.decode(mData.getTitle()));
			
			vItem.adapterSubTitle.setText(mData.getDescription());
			
			vItem.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					
					DatabaseHandler db = new DatabaseHandler(mContext);
					db.addVideo(mData);
					db.close();
					
					Intent intent = new Intent(mContext, VideoPlayerActivity.class);
					intent.putExtra("position", position);
					intent.putExtra("videoData", mDataList);
					mContext.startActivity(intent);
				}
			});
			
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		return TYPE_ITEM;
	}
	
	@Override
	public int getItemCount() {
		return (mDataList.size());
	}
	
	public void setData(ArrayList<Video> mVideos) {
		if (mDataList != mVideos) {
			mDataList = mVideos;
			notifyItemRangeInserted(0, mDataList.size());
		}
	}
	
	public class ItemViewHolder extends RecyclerView.ViewHolder {
		ImageView videoBanner;
		TextView adapterTitle, adapterSubTitle, adapterLabelTitle;
		
		public ItemViewHolder(View view) {
			super(view);
			videoBanner = view.findViewById(R.id.adapter_video_banner_image);
			adapterTitle = view.findViewById(R.id.adapter_video_title);
			adapterSubTitle = view.findViewById(R.id.adapter_sub_title);
			adapterLabelTitle = view.findViewById(R.id.adapter_label_title);
		}
	}
}
