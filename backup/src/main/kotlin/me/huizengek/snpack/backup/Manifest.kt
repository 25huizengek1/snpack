package me.huizengek.snpack.backup

import kotlinx.serialization.Serializable

internal const val CURRENT_VERSION = 1
internal const val FILE_NAME = "manifest.json"

@Serializable
data class Manifest internal constructor(
    val version: Int = CURRENT_VERSION,
    val meta: Meta,
    val packs: List<Pack>
) {
    @Serializable
    data class Meta(
        val author: String,
        val date: Long
    )
}

@Serializable
data class Pack internal constructor(
    val meta: Meta,
    val stickers: List<Sticker>
) {
    @Serializable
    data class Meta(
        val id: SerializableUUID,
        val name: String,
        val author: String,
        val email: String,
        val website: String,
        val privacyPolicy: String,
        val licenseAgreement: String,
        val iconFile: String,
        val version: Long,
        val avoidCache: Boolean,
        val animated: Boolean,
        val appStoreLink: String,
        val playStoreLink: String
    )
}

@Serializable
data class Sticker internal constructor(
    val meta: Meta
) {
    @Serializable
    data class Meta(
        val id: SerializableUUID,
        val packId: SerializableUUID,
        val iconFile: String,
        val emojis: List<String>
    )
}
