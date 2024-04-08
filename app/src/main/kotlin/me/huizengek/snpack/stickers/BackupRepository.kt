package me.huizengek.snpack.stickers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import me.huizengek.snpack.Database
import me.huizengek.snpack.R
import me.huizengek.snpack.backup.Backup
import me.huizengek.snpack.backup.Manifest
import me.huizengek.snpack.backup.Pack
import me.huizengek.snpack.backup.Sticker
import me.huizengek.snpack.util.resolveStickerImage
import me.huizengek.snpack.util.useAsInput
import me.huizengek.snpack.util.useAsOutput
import java.util.UUID
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

object BackupRepository {
    private val backupScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()

    const val MIME_TYPE = "application/zip"

    class BackupScope internal constructor()

    @OptIn(ExperimentalContracts::class)
    fun <T> obtain(block: suspend BackupScope.() -> T): Job {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        return backupScope.launch {
            try {
                mutex.withLock {
                    BackupScope().block()
                }
            } catch (e: Throwable) {
                if (e is CancellationException) throw e

                Log.e("BackupRepository", "A backup error was caught:")
                e.printStackTrace()
            }
        }
    }

    context(Context)
    suspend fun BackupScope.backup(
        destination: Uri,
        author: String
    ) = withContext(Dispatchers.IO) {
        runCatching {
            destination.useAsOutput(MIME_TYPE) { output ->
                val dbPacks = StickerRepository.packs.value
                val packIndices = dbPacks.associate { it.pack.id to UUID.randomUUID() }

                Backup.backup(
                    meta = Manifest.Meta(
                        author = author,
                        date = System.currentTimeMillis()
                    ),
                    packs = dbPacks.associate {
                        Pack.Meta(
                            id = packIndices[it.pack.id]!!,
                            name = it.pack.name,
                            author = it.pack.publisher,
                            iconFile = it.pack.trayImageFile,
                            email = it.pack.publisherEmail,
                            website = it.pack.publisherWebsite,
                            privacyPolicy = it.pack.privacyPolicyWebsite,
                            licenseAgreement = it.pack.licenseAgreementWebsite,
                            version = it.pack.imageDataVersion,
                            avoidCache = it.pack.avoidCache,
                            animated = it.pack.animatedStickerPack,
                            appStoreLink = it.pack.iosAppStoreLink,
                            playStoreLink = it.pack.androidPlayStoreLink,
                        ) to resolveStickerImage(it.pack.trayImageFile)
                    },
                    stickers = dbPacks.flatMap { pack ->
                        pack.stickers.map {
                            Sticker.Meta(
                                id = UUID.randomUUID(),
                                packId = packIndices[it.packId]!!,
                                iconFile = it.imageFileName,
                                emojis = it.emojis
                            ) to resolveStickerImage(it.imageFileName)
                        }
                    }.toMap()
                ).inputStream().copyTo(output)
            }
        }.exceptionOrNull()?.printStackTrace()
    }

    context(Context)
    suspend fun BackupScope.restore(
        source: Uri
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val backup = source.useAsInput(MIME_TYPE) { input -> Backup.restore(input) }
                ?: return@runCatching
            val stickers = backup.stickers.entries.groupBy { it.key.packId }

            backup.packs.map { (pack, bytes) ->
                launch {
                    val dbPack = StickerRepository.insertPack(
                        name = pack.name,
                        publisher = pack.author,
                        trayImage = bytes.toBitmap()
                    )?.copy(
                        publisherEmail = pack.email,
                        publisherWebsite = pack.website,
                        privacyPolicyWebsite = pack.privacyPolicy,
                        licenseAgreementWebsite = pack.licenseAgreement,
                        imageDataVersion = pack.version,
                        avoidCache = pack.avoidCache,
                        animatedStickerPack = pack.animated
                    ) ?: return@launch
                    Database.insert(dbPack)

                    stickers[pack.id]?.map { (sticker, bytes) ->
                        launch {
                            StickerRepository.insertSticker(
                                pack = dbPack,
                                image = bytes.toBitmap(),
                                emojis = sticker.emojis
                            )
                        }
                    }?.joinAll()
                }
            }.joinAll()

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@Context,
                    getString(R.string.backup_restored, backup.meta.author),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.exceptionOrNull()?.printStackTrace()
    }
}

fun ByteArray.toBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)
