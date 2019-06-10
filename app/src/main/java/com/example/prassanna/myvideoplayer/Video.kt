package com.example.prassanna.myvideoplayer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Video(
		@SerializedName("id") var videoId: Int = 0,
		@SerializedName("title") var title: String? = null,
		@SerializedName("description") var description: String? = null,
		@SerializedName("thumb") var thumb: String? = null,
		@SerializedName("url") var url: String? = null,
		@SerializedName("time") var seektime: Long = 0) :  Parcelable {
	constructor(parcel: Parcel) : this(
			parcel.readInt(),
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.readLong())
	
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(videoId)
		parcel.writeString(title)
		parcel.writeString(description)
		parcel.writeString(thumb)
		parcel.writeString(url)
		parcel.writeLong(seektime)
	}
	
	override fun describeContents(): Int {
		return 0
	}
	
	companion object CREATOR : Parcelable.Creator<Video> {
		override fun createFromParcel(parcel: Parcel): Video {
			return Video(parcel)
		}
		
		override fun newArray(size: Int): Array<Video?> {
			return arrayOfNulls(size)
		}
	}
}