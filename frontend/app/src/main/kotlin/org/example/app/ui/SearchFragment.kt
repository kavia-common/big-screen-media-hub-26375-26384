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
import org.example.app.model.ProgramItem
import org.example.app.model.VodItem

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var queryText: TextView
    private lateinit var resultsList: RecyclerView

    private var initialQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialQuery = arguments?.getString(ARG_QUERY).orEmpty()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        queryText = view.findViewById(R.id.queryText)
        resultsList = view.findViewById(R.id.resultsList)

        resultsList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        render(initialQuery)
    }

    private fun render(query: String) {
        val results = DummyRepositories.searchAll(query)
        queryText.text = getString(R.string.search_results_for, results.query)

        val rows = mutableListOf<SearchRow>()

        if (results.programs.isNotEmpty()) {
            rows.add(SearchRow.Header(getString(R.string.search_section_epg)))
            results.programs.forEach { rows.add(SearchRow.Program(it)) }
        }
        if (results.vod.isNotEmpty()) {
            rows.add(SearchRow.Header(getString(R.string.search_section_vod)))
            results.vod.forEach { rows.add(SearchRow.Vod(it)) }
        }
        if (results.favorites.isNotEmpty()) {
            rows.add(SearchRow.Header(getString(R.string.search_section_favorites)))
            results.favorites.forEach { rows.add(SearchRow.Favorite(it)) }
        }
        if (rows.isEmpty()) {
            rows.add(SearchRow.Empty(getString(R.string.search_no_results)))
        }

        resultsList.adapter = SearchAdapter(
            rows = rows,
            onPlayVod = { vod ->
                startActivity(PlayerActivity.createIntent(requireContext(), vod.title, vod.videoUrl))
            },
            onPlayProgram = { program ->
                val fallbackUrl = DummyRepositories.getVodCatalog().first().videoUrl
                startActivity(PlayerActivity.createIntent(requireContext(), program.title, fallbackUrl))
            },
            onToggleFavoriteVod = { vod ->
                DummyRepositories.toggleFavorite(
                    FavoriteItem(vod.id, FavoriteType.VOD, vod.title, vod.category),
                )
            },
            onToggleFavoriteProgram = { program ->
                DummyRepositories.toggleFavorite(
                    FavoriteItem(program.id, FavoriteType.PROGRAM, program.title, "EPG Program"),
                )
            },
        )
    }

    companion object {
        private const val ARG_QUERY = "arg_query"

        // PUBLIC_INTERFACE
        fun newInstance(query: String): SearchFragment {
            val f = SearchFragment()
            val b = Bundle()
            b.putString(ARG_QUERY, query)
            f.arguments = b
            return f
        }
    }
}

private sealed class SearchRow {
    data class Header(val title: String) : SearchRow()
    data class Vod(val item: VodItem) : SearchRow()
    data class Program(val item: ProgramItem) : SearchRow()
    data class Favorite(val item: FavoriteItem) : SearchRow()
    data class Empty(val message: String) : SearchRow()
}

private class SearchAdapter(
    private val rows: List<SearchRow>,
    private val onPlayVod: (VodItem) -> Unit,
    private val onPlayProgram: (ProgramItem) -> Unit,
    private val onToggleFavoriteVod: (VodItem) -> Unit,
    private val onToggleFavoriteProgram: (ProgramItem) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (rows[position]) {
            is SearchRow.Header -> 0
            is SearchRow.Vod -> 1
            is SearchRow.Program -> 2
            is SearchRow.Favorite -> 3
            is SearchRow.Empty -> 4
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = android.view.LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> SimpleTextVH(inflater.inflate(R.layout.item_search_header, parent, false))
            1 -> VodRowVH(inflater.inflate(R.layout.item_search_row, parent, false))
            2 -> ProgramRowVH(inflater.inflate(R.layout.item_search_row, parent, false))
            3 -> FavoriteRowVH(inflater.inflate(R.layout.item_search_row, parent, false))
            else -> SimpleTextVH(inflater.inflate(R.layout.item_search_empty, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = rows[position]) {
            is SearchRow.Header -> (holder as SimpleTextVH).bind(row.title)
            is SearchRow.Empty -> (holder as SimpleTextVH).bind(row.message)
            is SearchRow.Vod -> (holder as VodRowVH).bind(row.item, onPlayVod, onToggleFavoriteVod)
            is SearchRow.Program -> (holder as ProgramRowVH).bind(row.item, onPlayProgram, onToggleFavoriteProgram)
            is SearchRow.Favorite -> (holder as FavoriteRowVH).bind(row.item)
        }
    }

    override fun getItemCount(): Int = rows.size
}

private class SimpleTextVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val text: TextView = itemView.findViewById(R.id.simpleText)
    fun bind(value: String) {
        text.text = value
        itemView.isFocusable = false
        itemView.isFocusableInTouchMode = false
    }
}

private class VodRowVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.rowTitle)
    private val subtitle: TextView = itemView.findViewById(R.id.rowSubtitle)
    private val action1: TextView = itemView.findViewById(R.id.rowAction1)
    private val action2: TextView = itemView.findViewById(R.id.rowAction2)

    fun bind(item: VodItem, onPlay: (VodItem) -> Unit, onFavorite: (VodItem) -> Unit) {
        title.text = item.title
        subtitle.text = item.category
        action1.text = itemView.context.getString(R.string.action_play)
        action2.text = itemView.context.getString(R.string.action_favorite)

        action1.setOnClickListener { onPlay(item) }
        action2.setOnClickListener { onFavorite(item) }
    }
}

private class ProgramRowVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.rowTitle)
    private val subtitle: TextView = itemView.findViewById(R.id.rowSubtitle)
    private val action1: TextView = itemView.findViewById(R.id.rowAction1)
    private val action2: TextView = itemView.findViewById(R.id.rowAction2)

    fun bind(item: ProgramItem, onPlay: (ProgramItem) -> Unit, onFavorite: (ProgramItem) -> Unit) {
        title.text = item.title
        subtitle.text = item.description
        action1.text = itemView.context.getString(R.string.action_play)
        action2.text = itemView.context.getString(R.string.action_favorite)

        action1.setOnClickListener { onPlay(item) }
        action2.setOnClickListener { onFavorite(item) }
    }
}

private class FavoriteRowVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.rowTitle)
    private val subtitle: TextView = itemView.findViewById(R.id.rowSubtitle)
    private val action1: TextView = itemView.findViewById(R.id.rowAction1)
    private val action2: TextView = itemView.findViewById(R.id.rowAction2)

    fun bind(item: FavoriteItem) {
        title.text = item.title
        subtitle.text = item.subtitle
        action1.text = itemView.context.getString(R.string.action_open)
        action2.text = itemView.context.getString(R.string.action_remove)

        // For simplicity in search list: actions are informational only.
        // Favorites are managed in Favorites screen.
        action1.setOnClickListener(null)
        action2.setOnClickListener(null)
        action1.isFocusable = false
        action2.isFocusable = false
    }
}
