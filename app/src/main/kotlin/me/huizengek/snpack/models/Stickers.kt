package me.huizengek.snpack.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import me.huizengek.snpack.stickers.whatsapp.WHATSAPP_ANDROID_LINK
import me.huizengek.snpack.stickers.whatsapp.WHATSAPP_APPLE_LINK

@Entity
data class StickerPack(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val publisher: String,
    val trayImageFile: String,
    val publisherEmail: String = "",
    val publisherWebsite: String = "",
    val privacyPolicyWebsite: String = "",
    val licenseAgreementWebsite: String = "",
    val imageDataVersion: Long = 1,
    val avoidCache: Boolean = false,
    val animatedStickerPack: Boolean = false,
    val iosAppStoreLink: String = WHATSAPP_APPLE_LINK,
    val androidPlayStoreLink: String = WHATSAPP_ANDROID_LINK
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = StickerPack::class,
            parentColumns = ["id"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["packId"])]
)
data class Sticker(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packId: Long,
    val imageFileName: String,
    val emojis: List<String>,
    val size: Long
)

data class StickerPackWithStickers(
    @Embedded val pack: StickerPack,
    @Relation(
        parentColumn = "id",
        entityColumn = "packId"
    )
    val stickers: List<Sticker>
) {
    val totalSize get() = stickers.sumOf { it.size }
}
