package org.example.app.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.DummyRepositories
import org.example.app.model.FavoriteItem
import org.example.app.model.FavoriteType
import org.example.app.model.VodItem

class VodFragment : Fragment(R.layout.fragment_vod) {

    private lateinit var vodGrid: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vodGrid = view.findViewById(R.id.vodGrid)

        val vodItems = DummyRepositories.getVodCatalog()

        vodGrid.layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        vodGrid.adapter = VodAdapter(
            items = vodItems,
            onPlay = { item ->
                startActivity(PlayerActivity.createIntent(requireContext(), item.title, item.videoUrl))
            },
            onToggleFavorite = { item ->
                DummyRepositories.toggleFavorite(
                    FavoriteItem(
                        id = item.id,
                        type = FavoriteType.VOD,
                        title = item.title,
                        subtitle = item.category,
                    ),
                )
            },
        )
    }

    companion object {
        // PUBLIC_INTERFACE
        fun newInstance(): VodFragment = VodFragment()
    }
}

private class VodAdapter(
    private val items: List<VodItem>,
    private val onPlay: (VodItem) -> Unit,
    private val onToggleFavorite: (VodItem) -> Unit,
) : RecyclerView.Adapter<VodViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VodViewHolder {
        val v = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_vod, parent, false)
        return VodViewHolder(v)
    }

    override fun onBindViewHolder(holder: VodViewHolder, position: Int) {
        holder.bind(items[position], onPlay, onToggleFavorite)
    }

    override fun getItemCount(): Int = items.size
}

private class VodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val thumbnailText: TextView = itemView.findViewById(R.id.vodThumbText)
    private val title: TextView = itemView.findViewById(R.id.vodTitle)
    private val category: TextView = itemView.findViewById(R.id.vodCategory)
    private val actionPlay: TextView = itemView.findViewById(R.id.actionPlay)
    private val actionFav: TextView = itemView.findViewById(R.id.actionFavorite)

    fun bind(item: VodItem, onPlay: (VodItem) -> Unit, onToggleFavorite: (VodItem) -> Unit) {
        thumbnailText.text = item.thumbnailText
        title.text = item.title
        category.text = item.category

        actionPlay.setOnClickListener { onPlay(item) }
        actionFav.setOnClickListener { onToggleFavorite(item) }
    }
}
