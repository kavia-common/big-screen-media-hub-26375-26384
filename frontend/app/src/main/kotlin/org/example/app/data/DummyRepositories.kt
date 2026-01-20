package org.example.app.data

import org.example.app.model.Channel
import org.example.app.model.FavoriteItem
import org.example.app.model.FavoriteType
import org.example.app.model.ProgramItem
import org.example.app.model.VodItem
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Dummy data for EPG/VOD and an in-memory favorites store.
 *
 * Note: This is intentionally simple; replace with a real datasource later.
 */
object DummyRepositories {

    private val favorites = CopyOnWriteArrayList<FavoriteItem>()

    // PUBLIC_INTERFACE
    fun getChannels(): List<Channel> {
        return listOf(
            Channel(id = "c1", name = "Ocean News", number = 101),
            Channel(id = "c2", name = "Blue Sports", number = 102),
            Channel(id = "c3", name = "Amber Movies", number = 103),
            Channel(id = "c4", name = "Documentary+", number = 104),
        )
    }

    // PUBLIC_INTERFACE
    fun getProgramsForToday(): List<ProgramItem> {
        // Times are in minutes from 00:00 (simple for dummy EPG).
        return listOf(
            ProgramItem("p1", "c1", "Morning Headlines", 8 * 60, 60, "Top stories from around the world."),
            ProgramItem("p2", "c1", "Market Watch", 9 * 60, 60, "Daily finance and market recap."),
            ProgramItem("p3", "c2", "Live Football", 8 * 60 + 30, 120, "Premier matchup of the week."),
            ProgramItem("p4", "c2", "Sports Desk", 10 * 60 + 30, 60, "Highlights and analysis."),
            ProgramItem("p5", "c3", "Amber Classic: The Voyage", 9 * 60, 120, "A timeless adventure film."),
            ProgramItem("p6", "c4", "Deep Sea Wonders", 8 * 60, 90, "Exploring the ocean depths."),
        )
    }

    // PUBLIC_INTERFACE
    fun getVodCatalog(): List<VodItem> {
        // Use publicly accessible sample videos (Big Buck Bunny / Sintel).
        val bunny = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val sintel = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        val tears = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

        return listOf(
            VodItem("v1", "Big Buck Bunny", "Family", "BUNNY", bunny),
            VodItem("v2", "Sintel", "Action", "SINTEL", sintel),
            VodItem("v3", "Tears of Steel", "Sci-Fi", "STEEL", tears),
            VodItem("v4", "Ocean Documentary: Blue Planet", "Documentary", "OCEAN", bunny),
            VodItem("v5", "Amber Nights: Noir Collection", "Movies", "NOIR", sintel),
        )
    }

    // PUBLIC_INTERFACE
    fun getFavorites(): List<FavoriteItem> = favorites.toList()

    // PUBLIC_INTERFACE
    fun isFavorite(id: String): Boolean = favorites.any { it.id == id }

    // PUBLIC_INTERFACE
    fun toggleFavorite(item: FavoriteItem) {
        val existingIdx = favorites.indexOfFirst { it.id == item.id }
        if (existingIdx >= 0) {
            favorites.removeAt(existingIdx)
        } else {
            favorites.add(item)
        }
    }

    // PUBLIC_INTERFACE
    fun searchAll(queryRaw: String): SearchResults {
        val query = queryRaw.trim()
        if (query.isEmpty()) {
            return SearchResults(query, emptyList(), emptyList(), getFavorites())
        }

        val epgMatches = getProgramsForToday().filter {
            it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
        }
        val vodMatches = getVodCatalog().filter {
            it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
        val favMatches = getFavorites().filter {
            it.title.contains(query, ignoreCase = true) || it.subtitle.contains(query, ignoreCase = true)
        }

        return SearchResults(query, epgMatches, vodMatches, favMatches)
    }
}

/**
 * Aggregate search results across app content.
 */
data class SearchResults(
    val query: String,
    val programs: List<ProgramItem>,
    val vod: List<VodItem>,
    val favorites: List<FavoriteItem>,
)
