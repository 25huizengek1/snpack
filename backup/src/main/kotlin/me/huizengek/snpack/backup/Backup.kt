package me.huizengek.snpack.backup

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream

internal val json = Json {
    isLenient = true
    prettyPrint = true
    encodeDefaults = true
    ignoreUnknownKeys = true
}

data class BackupSnapshot internal constructor(
    val meta: Manifest.Meta,
    val packs: Map<Pack.Meta, ByteArray>,
    val stickers: Map<Sticker.Meta, ByteArray>
)

object Backup {
    fun backup(
        meta: Manifest.Meta,
        packs: Map<Pack.Meta, File>,
        stickers: Map<Sticker.Meta, File>
    ): ByteArray {
        require(stickers.all { (sticker, file) ->
            sticker.iconFile == file.name &&
                    !file.isDirectory && file.exists() &&
                    packs.any { (pack, _) -> pack.id == sticker.packId }
        })
        require(packs.all { (pack, file) ->
            pack.iconFile == file.name && !file.isDirectory && file.exists()
        })

        val files = stickers
            .asSequence()
            .map { (_, file) -> file.name to file.inputStream() } + packs
            .asSequence()
            .map { (_, file) -> file.name to file.inputStream() }
        val stickersByPack = stickers.keys.groupBy { it.packId }
        val manifest = Manifest(
            meta = meta,
            packs = packs.keys.map { pack ->
                Pack(
                    meta = pack,
                    stickers = stickersByPack[pack.id]?.map { Sticker(meta = it) }.orEmpty()
                )
            }
        )
        val archive = files + sequenceOf(
            FILE_NAME to json.encodeToString(manifest).byteInputStream()
        )
        return archive.zip()
    }

    fun restore(zip: InputStream): BackupSnapshot {
        val archive = zip.unzip().toList()
        val manifest = json.decodeFromString<Manifest>(
            archive
                .firstNotNullOf { (name, file) -> file.takeIf { name == FILE_NAME } }
                .decodeToString()
        )
        val stickers = manifest.packs.flatMap { pack ->
            pack.stickers.map { sticker ->
                sticker.meta to archive.firstNotNullOf { (name, file) ->
                    file.takeIf { name == sticker.meta.iconFile }
                }
            }
        }.toMap()
        val packs = manifest.packs.associate { pack ->
            pack.meta to archive.firstNotNullOf { (name, file) ->
                file.takeIf { name == pack.meta.iconFile }
            }
        }

        return BackupSnapshot(
            meta = manifest.meta,
            packs = packs,
            stickers = stickers
        )
    }
}
