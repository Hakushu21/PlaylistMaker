package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)
    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
    private val artistTimeTextView: TextView = itemView.findViewById(R.id.artistTimeTextView)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistTimeTextView.text = "${track.artistName} • ${track.trackTime}"

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2)))
            .into(artworkImageView)
    }
}