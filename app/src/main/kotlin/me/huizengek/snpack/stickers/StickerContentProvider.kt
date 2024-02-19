package me.huizengek.snpack.stickers

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.huizengek.snpack.Database
import me.huizengek.snpack.DependencyGraph
import me.huizengek.snpack.models.Sticker
import me.huizengek.snpack.models.StickerPack
import me.huizengek.snpack.models.StickerPackWithStickers
import me.huizengek.snpack.stickers.whatsapp.METADATA
import me.huizengek.snpack.stickers.whatsapp.METADATA_CODE
import me.huizengek.snpack.stickers.whatsapp.METADATA_CODE_FOR_SINGLE_PACK
import me.huizengek.snpack.stickers.whatsapp.STICKERS
import me.huizengek.snpack.stickers.whatsapp.STICKERS_ASSET
import me.huizengek.snpack.stickers.whatsapp.STICKERS_ASSET_CODE
import me.huizengek.snpack.stickers.whatsapp.STICKERS_CODE
import me.huizengek.snpack.stickers.whatsapp.STICKERS_FOLDER
import me.huizengek.snpack.stickers.whatsapp.STICKER_APP_AUTHORITY
import me.huizengek.snpack.stickers.whatsapp.STICKER_PACK_TRAY_ICON_CODE

private val contentProviderScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

val stickerPacks by lazy {
    Database.getPacks()
        .cancellable()
        .distinctUntilChanged()
        .stateIn(
            scope = contentProviderScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )
}

class StickerContentProvider : ContentProvider() {
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate() = runCatching {
        if (!STICKER_APP_AUTHORITY.startsWith(context!!.packageName))
            error("Invalid authority $STICKER_APP_AUTHORITY")

        with(context!!.applicationContext as Application) { DependencyGraph.postInit() }

        uriMatcher.addURI(STICKER_APP_AUTHORITY, METADATA, METADATA_CODE)
        uriMatcher.addURI(
            STICKER_APP_AUTHORITY,
            "$METADATA/*",
            METADATA_CODE_FOR_SINGLE_PACK
        )
        uriMatcher.addURI(STICKER_APP_AUTHORITY, "$STICKERS/*", STICKERS_CODE)

        contentProviderScope.launch {
            DependencyGraph.awaitInitialization()
            stickerPacks.collectLatest { packs ->
                packs.forEach { uriMatcher.addStickerPack(it) }
            }
        }

        runBlocking { DependencyGraph.awaitInitialization() }
    }.isSuccess

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor = when (uriMatcher.match(uri)) {
        METADATA_CODE -> uri.asPackList
        METADATA_CODE_FOR_SINGLE_PACK -> uri.asPack
        STICKERS_CODE -> uri.asStickers
        else -> throw IllegalArgumentException("Unknown URI: $uri")
    }

    private fun UriMatcher.addStickerPack(pack: StickerPackWithStickers) {
        addURI(
            /* authority = */ STICKER_APP_AUTHORITY,
            /* path = */ "$STICKERS_ASSET/${pack.pack.id}/${pack.pack.trayImageFile}",
            /* code = */ STICKER_PACK_TRAY_ICON_CODE
        )
        pack.stickers.forEach {
            addURI(
                /* authority = */ STICKER_APP_AUTHORITY,
                /* path = */ "$STICKERS_ASSET/${pack.pack.id}/${it.imageFileName}",
                /* code = */ STICKERS_ASSET_CODE
            )
        }
    }

    @JvmName("packsToCursor")
    private fun List<StickerPack>.toCursor(uri: Uri): Cursor {
        val matrix = MatrixCursor(
            arrayOf(
                "sticker_pack_identifier",
                "sticker_pack_name",
                "sticker_pack_publisher",
                "sticker_pack_icon",
                "android_play_store_link",
                "ios_app_download_link",
                "sticker_pack_publisher_email",
                "sticker_pack_publisher_website",
                "sticker_pack_privacy_policy_website",
                "sticker_pack_license_agreement_website",
                "image_data_version",
                "whatsapp_will_not_cache_stickers",
                "animated_sticker_pack"
            )
        )

        forEach { pack ->
            matrix.newRow().apply {
                add(pack.id.toString())
                add(pack.name)
                add(pack.publisher)
                add(pack.trayImageFile)
                add(pack.androidPlayStoreLink)
                add(pack.iosAppStoreLink)
                add(pack.publisherEmail)
                add(pack.publisherWebsite)
                add(pack.privacyPolicyWebsite)
                add(pack.licenseAgreementWebsite)
                add(pack.imageDataVersion.toString())
                add(if (pack.avoidCache) 1 else 0)
                add(if (pack.animatedStickerPack) 1 else 0)
            }
        }
        matrix.setNotificationUri(context!!.contentResolver, uri)
        return matrix
    }

    @JvmName("stickersToCursor")
    private fun List<Sticker>.toCursor(uri: Uri): Cursor {
        val matrix = MatrixCursor(arrayOf("sticker_file_name", "sticker_emoji"))
        forEach {
            matrix.addRow(arrayOf(it.imageFileName, it.emojis.joinToString(separator = ",")))
        }
        matrix.setNotificationUri(context!!.contentResolver, uri)
        return matrix
    }

    private val Uri.asPackList
        get() = stickerPacks.value
            .map { it.pack }
            .toCursor(this)

    private val Uri.asPack
        get() = stickerPacks.value
            .firstOrNull { it.pack.id.toString() == lastPathSegment }
            ?.let { listOf(it.pack) }
            .orEmpty()
            .toCursor(this)

    private val Uri.asStickers
        get() = stickerPacks.value
            .firstOrNull { it.pack.id.toString() == lastPathSegment }?.stickers?.toCursor(this)
            ?: listOf<Sticker>().toCursor(this)

    override fun getType(uri: Uri) = when (uriMatcher.match(uri)) {
        METADATA_CODE -> "vnd.android.cursor.dir/vnd.$STICKER_APP_AUTHORITY.$METADATA"
        METADATA_CODE_FOR_SINGLE_PACK -> "vnd.android.cursor.item/vnd.$STICKER_APP_AUTHORITY.$METADATA"
        STICKERS_CODE -> "vnd.android.cursor.dir/vnd.$STICKER_APP_AUTHORITY.$STICKERS"
        STICKERS_ASSET_CODE, STICKER_PACK_TRAY_ICON_CODE -> "image/webp"
        else -> throw IllegalArgumentException("Unknown URI: $uri")
    }

    @Suppress("ReturnCount")
    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        val code = uriMatcher.match(uri)
        if (code != STICKERS_ASSET_CODE && code != STICKER_PACK_TRAY_ICON_CODE) return null
        val (fileName, identifier) = uri.pathSegments.reversed()

        val id = identifier.toLongOrNull() ?: return null
        if (fileName.isEmpty()) return null

        return if (
            Database.getPacksBlocking()
                .any { pack -> pack.pack.id == id || pack.stickers.any { it.id == id } }
        ) AssetFileDescriptor(
            /* fd = */ ParcelFileDescriptor.open(
                /* file = */ context!!.filesDir.resolve(STICKERS_FOLDER).resolve(fileName),
                /* mode = */ ParcelFileDescriptor.MODE_READ_ONLY
            ),
            /* startOffset = */ 0,
            /* length = */ AssetFileDescriptor.UNKNOWN_LENGTH
        ) else null
    }

    override fun insert(uri: Uri, values: ContentValues?) = error("Unsupported")
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) =
        error("Unsupported")

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = error("Unsupported")
}
