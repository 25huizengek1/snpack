package me.huizengek.snpack.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import me.huizengek.snpack.stickers.whatsapp.STICKERS_FOLDER
import java.io.InputStream
import java.io.OutputStream

val isAtLeastAndroid12 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

context(Context)
fun Uri.isVirtual(): Boolean {
    if (!DocumentsContract.isDocumentUri(this@Context, this)) return false

    val flags = contentResolver.query(
        /* uri           = */ this,
        /* projection    = */ arrayOf(DocumentsContract.Document.COLUMN_FLAGS),
        /* selection     = */ null,
        /* selectionArgs = */ null,
        /* sortOrder     = */ null
    )?.use {
        if (it.moveToFirst()) it.getInt(0) else 0
    } ?: 0

    return flags and DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT != 0
}

context(Context)
@PublishedApi
internal fun Uri.openVirtualFile(mimeTypeFilter: String): AssetFileDescriptor? {
    val openableTypes = contentResolver.getStreamTypes(this, mimeTypeFilter)
    return if (openableTypes?.isNotEmpty() == true) contentResolver
        .openTypedAssetFileDescriptor(this, openableTypes.first(), null) else null
}

context(Context)
inline fun <T> Uri.useAsInput(mimeTypeFilter: String, block: (InputStream) -> T) = (
        if (isVirtual()) openVirtualFile(mimeTypeFilter)?.createInputStream()
        else contentResolver.openInputStream(this@useAsInput)
        ).use { stream -> stream?.let { block(it) } }

context(Context)
inline fun <T> Uri.useAsOutput(mimeTypeFilter: String, block: (OutputStream) -> T) = (
        if (isVirtual()) openVirtualFile(mimeTypeFilter)?.createOutputStream()
        else contentResolver.openOutputStream(this@useAsOutput)
        ).use { stream -> stream?.let { block(it) } }

fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper && current !is Activity) {
        current = current.baseContext
    }
    return current as? Activity
}

fun Context.resolveStickerImage(path: String) = filesDir.resolve(STICKERS_FOLDER).resolve(path)
