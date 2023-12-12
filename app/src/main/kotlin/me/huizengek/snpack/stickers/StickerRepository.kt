package me.huizengek.snpack.stickers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import me.huizengek.snpack.Database
import me.huizengek.snpack.models.Sticker
import me.huizengek.snpack.models.StickerPack
import me.huizengek.snpack.models.StickerPackWithStickers
import me.huizengek.snpack.stickers.whatsapp.STICKERS_FOLDER
import me.huizengek.snpack.util.randomString
import me.huizengek.snpack.util.resolveStickerImage
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.minutes

private suspend fun RequestBuilder<Bitmap>.await(onCleared: () -> Unit): Bitmap? =
    withTimeout(1.minutes) {
        suspendCoroutine { continuation ->
            into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) =
                    continuation.resume(resource)

                override fun onLoadCleared(placeholder: Drawable?) = onCleared()
            })
        }
    }

context(Context)
suspend fun Bitmap.processAndWrite(
    file: File,
    processing: RequestBuilder<Bitmap>.() -> RequestBuilder<Bitmap>
) = withContext(Dispatchers.IO) {
    if (file.parentFile?.mkdirs() == true && file.exists()) return@withContext null

    var trayImageProcessed: Bitmap? = null
    trayImageProcessed = Glide
        .with(applicationContext)
        .asBitmap()
        .load(this@processAndWrite)
        .processing()
        .await {
            // shouldn't happen, but at least we throw instead of crash when the processed image gets cleared
            trayImageProcessed?.recycle()
            trayImageProcessed = null
        } ?: return@withContext null

    val out = file.outputStream()
    @Suppress("DEPRECATION") // I know what I'm doing, at least this one has better backwards compatibility
    trayImageProcessed?.compress(Bitmap.CompressFormat.WEBP, 40, out)
    out.flush()
    out.close()
}

object StickerRepository {
    val packs get() = stickerPacks

    context(Context)
    suspend fun insertPack(
        name: String,
        publisher: String,
        trayImage: Bitmap
    ): StickerPack? = withContext(Dispatchers.IO) {
        val fileName = "${randomString()}.webp"

        val pack = runCatching {
            trayImage.processAndWrite(resolveStickerImage(fileName)) {
                override(96).centerCrop()
            }

            StickerPack(
                name = name,
                publisher = publisher,
                trayImageFile = fileName
            )
        }.getOrNull()

        pack?.copy(id = Database.insert(pack))
    }

    context(Context)
    suspend fun updatePack(id: Long, name: String, publisher: String, trayImage: Bitmap) = withContext(Dispatchers.IO) {
        runCatching {
            Database.transaction {
                val pack = packBlocking(id).pack
                val fileName = "${randomString()}.webp"

                launch { resolveStickerImage(pack.trayImageFile).delete() }

                trayImage.processAndWrite(resolveStickerImage(fileName)) {
                    override(96).centerCrop()
                }
                update(
                    pack.copy(
                        name = name,
                        publisher = publisher,
                        trayImageFile = fileName
                    )
                )
                incrementPack(id)
            }
        }
    }

    context(Context)
    suspend fun insertSticker(
        pack: StickerPack,
        image: Bitmap,
        emojis: List<String>
    ): Sticker? = withContext(Dispatchers.IO) {
        require(emojis.size in (1..3)) { "You need 1 to 3 (inclusive) emojis for the sticker" }

        val fileName = "${randomString()}.webp"
        val imageFile = filesDir.resolve(STICKERS_FOLDER).resolve(fileName)

        runCatching {
            image.processAndWrite(imageFile) {
                override(512).centerCrop()
            }

            val sticker = Sticker(
                packId = pack.id,
                imageFileName = fileName,
                emojis = emojis,
                size = imageFile.length()
            )

            Database.transaction {
                incrementPack(pack.id)
            }

            sticker.copy(id = Database.insert(sticker))
        }.getOrNull()
    }

    context(Context)
    fun deleteSticker(sticker: Sticker) = Database.transaction {
        incrementPackBySticker(sticker.id)
        delete(sticker)
        resolveStickerImage(sticker.imageFileName).delete()
    }

    context(Context)
    fun deletePack(stickerPack: StickerPackWithStickers) = Database.transaction {
        delete(stickerPack.pack)
        stickerPack.stickers.forEach { deleteSticker(it) }
        resolveStickerImage(stickerPack.pack.trayImageFile).delete()
    }
}
