package org.example.app.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.DummyRepositories
import org.example.app.model.FavoriteItem
import org.example.app.model.FavoriteType

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var favoritesList: RecyclerView
    private lateinit var emptyText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritesList = view.findViewById(R.id.favoritesList)
        emptyText = view.findViewById(R.id.emptyText)

        favoritesList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val items = DummyRepositories.getFavorites()
        emptyText.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        favoritesList.adapter = FavoritesAdapter(
            items = items,
            onOpen = { fav ->
                if (fav.type == FavoriteType.VOD) {
                    val vod = DummyRepositories.getVodCatalog().find { it.id == fav.id }
                    if (vod != null) {
                        startActivity(PlayerActivity.createIntent(requireContext(), vod.title, vod.videoUrl))
                    }
                } else {
                    val fallbackUrl = DummyRepositories.getVodCatalog().first().videoUrl
                    startActivity(PlayerActivity.createIntent(requireContext(), fav.title, fallbackUrl))
                }
            },
            onRemove = { fav ->
                DummyRepositories.toggleFavorite(fav)
                render()
            },
        )
    }

    companion object {
        // PUBLIC_INTERFACE
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}

private class FavoritesAdapter(
    private val items: List<FavoriteItem>,
    private val onOpen: (FavoriteItem) -> Unit,
    private val onRemove: (FavoriteItem) -> Unit,
) : RecyclerView.Adapter<FavoritesViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): FavoritesViewHolder {
        val v = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoritesViewHolder(v)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(items[position], onOpen, onRemove)
    }

    override fun getItemCount(): Int = items.size
}

private class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.favoriteTitle)
    private val subtitle: TextView = itemView.findViewById(R.id.favoriteSubtitle)
    private val actionOpen: TextView = itemView.findViewById(R.id.actionOpen)
    private val actionRemove: TextView = itemView.findViewById(R.id.actionRemove)

    fun bind(item: FavoriteItem, onOpen: (FavoriteItem) -> Unit, onRemove: (FavoriteItem) -> Unit) {
        title.text = item.title
        subtitle.text = item.subtitle
        actionOpen.setOnClickListener { onOpen(item) }
        actionRemove.setOnClickListener { onRemove(item) }
    }
}
