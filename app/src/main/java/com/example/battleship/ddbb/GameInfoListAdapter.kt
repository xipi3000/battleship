package com.example.battleship.ddbb

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class GameInfoListAdapter : ListAdapter<GameInfo, GameInfoListAdapter.GameInfoViewHolder>(GameInfoComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameInfoViewHolder {
        return GameInfoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GameInfoViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.alias)
    }

    class GameInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //aqui no se que ficar perque ells ho fan per actualitzar un TextView :/
        //potser si ells el que actualitzen son TextViews, naltres ho hauriem
        //d'actualitzar aqui?
        lateinit var message: String
        fun bind(text: String?) {
            if (text != null) {
                message = text
            }
        }

        companion object {
            fun create(parent: ViewGroup): GameInfoViewHolder {
                val view= View(parent.context)
                return GameInfoViewHolder(view)
            }
        }
    }

    class GameInfoComparator : DiffUtil.ItemCallback<GameInfo>() {
        override fun areItemsTheSame(oldItem: GameInfo, newItem: GameInfo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: GameInfo, newItem: GameInfo): Boolean {
            return oldItem.alias == newItem.alias &&
                    oldItem.result == newItem.result &&
                    oldItem.shots == newItem.shots &&
                    oldItem.hit == newItem.hit &&
                    oldItem.miss == newItem.miss &&
                    oldItem.accuracy == newItem.accuracy
        }
    }
}