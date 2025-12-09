package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onClearHistoryClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_TRACK = 0
        private const val TYPE_CLEAR_BUTTON = 1
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
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
            TYPE_CLEAR_BUTTON -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.clear_history_button_item, parent, false)
                ClearButtonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
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
                holder.bind()
                holder.clearButton.setOnClickListener {
                    onClearHistoryClick()
                }
            }
        }
    }

    override fun getItemCount(): Int = tracks.size + 1 // +1 для кнопки

    class ClearButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clearButton: Button = itemView.findViewById(R.id.clear_history_button_item)

        fun bind() {
            // Можно настроить что-то дополнительно, если нужно
        }
    }
}