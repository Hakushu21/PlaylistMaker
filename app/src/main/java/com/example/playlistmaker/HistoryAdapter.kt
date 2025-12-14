package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class HistoryAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onClearHistoryClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val TYPE_TRACK = 0
        const val TYPE_CLEAR_BUTTON = 1
        const val CLEAR_BUTTON_COUNT = 1
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    fun clearHistory() {
        tracks = emptyList()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < tracks.size) TYPE_TRACK else TYPE_CLEAR_BUTTON
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TRACK -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
                TrackViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.clear_history_button_item, parent, false)
                ClearButtonViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrackViewHolder -> {
                val track = tracks[position]
                holder.bind(track)
                holder.itemView.setOnClickListener {
                    onTrackClick(track)
                }
            }
            is ClearButtonViewHolder -> {
                holder.clearButton.setOnClickListener {
                    onClearHistoryClick()
                }

                holder.itemView.setOnClickListener {
                    println("DEBUG: Clear history item clicked (not button)")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (tracks.isEmpty()) 0 else tracks.size + CLEAR_BUTTON_COUNT
    }

    class ClearButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clearButton: MaterialButton = itemView.findViewById(R.id.clear_history_button_item)
    }
}