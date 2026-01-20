package org.example.app.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.DummyRepositories
import org.example.app.model.Channel
import org.example.app.model.FavoriteItem
import org.example.app.model.FavoriteType
import org.example.app.model.ProgramItem

class EpgFragment : Fragment(R.layout.fragment_epg) {

    private lateinit var channelList: RecyclerView
    private lateinit var programList: RecyclerView

    private val channels = DummyRepositories.getChannels()
    private val allPrograms = DummyRepositories.getProgramsForToday()

    private var selectedChannel: Channel = channels.first()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        channelList = view.findViewById(R.id.channelList)
        programList = view.findViewById(R.id.programList)

        channelList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        programList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val channelAdapter = ChannelAdapter(channels) { channel ->
            selectedChannel = channel
            renderPrograms()
        }
        channelList.adapter = channelAdapter

        renderPrograms()
    }

    private fun renderPrograms() {
        val programs = allPrograms.filter { it.channelId == selectedChannel.id }
        programList.adapter = ProgramAdapter(
            programs = programs,
            onPlay = { program ->
                // For demo playback, reuse a VOD url (player expects URL).
                // In a real app this would map to a live/OTT stream URL.
                val vodUrl = DummyRepositories.getVodCatalog().first().videoUrl
                startActivity(PlayerActivity.createIntent(requireContext(), program.title, vodUrl))
            },
            onToggleFavorite = { program ->
                DummyRepositories.toggleFavorite(
                    FavoriteItem(
                        id = program.id,
                        type = FavoriteType.PROGRAM,
                        title = program.title,
                        subtitle = "Ch ${selectedChannel.number} • ${selectedChannel.name}",
                    ),
                )
            },
        )
    }

    companion object {
        // PUBLIC_INTERFACE
        fun newInstance(): EpgFragment = EpgFragment()
    }
}

private class ChannelAdapter(
    private val items: List<Channel>,
    private val onSelected: (Channel) -> Unit,
) : RecyclerView.Adapter<ChannelViewHolder>() {

    private var selectedPos: Int = 0

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ChannelViewHolder {
        val v = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(v)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = items[position]
        holder.bind(channel, position == selectedPos)

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                selectedPos = pos
                notifyDataSetChanged()
                onSelected(items[pos])
            }
        }
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    selectedPos = pos
                    notifyDataSetChanged()
                    onSelected(items[pos])
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

private class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.channelTitle)

    fun bind(channel: Channel, selected: Boolean) {
        title.text = "${channel.number}  ${channel.name}"
        itemView.isSelected = selected
    }
}

private class ProgramAdapter(
    private val programs: List<ProgramItem>,
    private val onPlay: (ProgramItem) -> Unit,
    private val onToggleFavorite: (ProgramItem) -> Unit,
) : RecyclerView.Adapter<ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ProgramViewHolder {
        val v = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_program, parent, false)
        return ProgramViewHolder(v)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val item = programs[position]
        holder.bind(item, onPlay, onToggleFavorite)
    }

    override fun getItemCount(): Int = programs.size
}

private class ProgramViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.programTitle)
    private val subtitle: TextView = itemView.findViewById(R.id.programSubtitle)
    private val actionPlay: TextView = itemView.findViewById(R.id.actionPlay)
    private val actionFav: TextView = itemView.findViewById(R.id.actionFavorite)

    fun bind(program: ProgramItem, onPlay: (ProgramItem) -> Unit, onToggleFavorite: (ProgramItem) -> Unit) {
        title.text = program.title
        subtitle.text = "${formatTime(program.startMinutes)} • ${program.durationMinutes} min"

        actionPlay.setOnClickListener { onPlay(program) }
        actionFav.setOnClickListener { onToggleFavorite(program) }
    }

    private fun formatTime(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return String.format("%02d:%02d", h, m)
    }
}
