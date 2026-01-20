package org.example.app.model

/**
 * Represents a TV channel in EPG.
 */
data class Channel(
    val id: String,
    val name: String,
    val number: Int,
)

/**
 * Represents one EPG program (a block on the timeline).
 */
data class ProgramItem(
    val id: String,
    val channelId: String,
    val title: String,
    val startMinutes: Int,
    val durationMinutes: Int,
    val description: String,
)

/**
 * Represents a VOD item.
 */
data class VodItem(
    val id: String,
    val title: String,
    val category: String,
    val thumbnailText: String,
    val videoUrl: String,
)

/**
 * Represents a favorite item (can point to either VOD or Program).
 */
data class FavoriteItem(
    val id: String,
    val type: FavoriteType,
    val title: String,
    val subtitle: String,
)

enum class FavoriteType {
    VOD,
    PROGRAM,
}
