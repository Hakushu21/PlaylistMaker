package com.example.playlistmaker

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)
    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    private val dotTextView: TextView = itemView.findViewById(R.id.dotTextView)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = track.trackTime

        val cornerRadiusPx = dpToPx(itemView.context, 2)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_album_placeholder_45)
            .error(R.drawable.ic_album_placeholder_45)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadiusPx)))
            .into(artworkImageView)
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}