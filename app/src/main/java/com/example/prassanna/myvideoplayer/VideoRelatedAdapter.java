package com.example.prassanna.myvideoplayer;

import android.content.Context;
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

class VideoRelatedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int TYPE_ITEM = 1;
	VideoClickListener mVideoClickListener;
	private ArrayList<Video> mDataList;
	private Context mContext;
	
	public VideoRelatedAdapter(Context con1, ArrayList<Video> mDataList, VideoClickListener mVideoClickListener) {
		this.mContext = con1;
		this.mDataList = mDataList;
		this.mVideoClickListener = mVideoClickListener;
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == TYPE_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.adapter_video_related, parent, false);
			return new ItemViewHolder(v);
		}
		return null;
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
		final Video mData = mDataList.get(position);
		if (holder instanceof ItemViewHolder) {
			ItemViewHolder VItem = (ItemViewHolder) holder;
			
			Picasso.get()
			       .load(mData.getThumb())
			       .placeholder(R.drawable.img_video)
			       .error(R.drawable.img_video)
			       .into(VItem.videoBanner);
			
			VItem.adapterTitle.setText(URLDecoder.decode(mData.getTitle()));
			VItem.adapterSubTitle.setText(URLDecoder.decode(mData.getDescription()));
			
			VItem.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mVideoClickListener.onItemClicked(position);
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
	
	public interface VideoClickListener {
		void onItemClicked(int pos);
	}
	
	public class ItemViewHolder extends RecyclerView.ViewHolder {
		ImageView videoBanner;
		TextView adapterTitle, adapterSubTitle;
		
		public ItemViewHolder(View view) {
			super(view);
			videoBanner = view.findViewById(R.id.adapter_related_video_image);
			adapterTitle = view.findViewById(R.id.adapter_related_video_title);
			adapterSubTitle = view.findViewById(R.id.adapter_related_video_des);
		}
	}
	
}
