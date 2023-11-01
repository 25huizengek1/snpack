package me.huizengek.snpack.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

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
    val iosAppStoreLink: String = "https://apps.apple.com/nl/app/whatsapp-messenger/id310633997",
    val androidPlayStoreLink: String = "https://play.google.com/store/apps/details?id=com.whatsapp"
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = StickerPack::class,
        parentColumns = ["id"],
        childColumns = ["packId"],
        onDelete = ForeignKey.CASCADE
    )],
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