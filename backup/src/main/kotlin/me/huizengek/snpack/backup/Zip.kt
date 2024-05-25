package me.huizengek.snpack.backup

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal fun Sequence<Pair<String, InputStream>>.zip(charset: Charset = Charsets.UTF_8): ByteArray =
    ByteArrayOutputStream().use { output ->
        ZipOutputStream(output, charset).use { zip ->
            forEach { (name, stream) ->
                zip.putNextEntry(ZipEntry(name))
                stream.use { it.copyTo(zip) }
            }
        }
        output.toByteArray()
    }

internal fun InputStream.unzip(charset: Charset = Charsets.UTF_8) = sequence<Pair<String, ByteArray>> {
    ZipInputStream(this@unzip, charset).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            ByteArrayOutputStream().use { bytes ->
                zip.copyTo(bytes)
                yield(entry?.name.orEmpty() to bytes.toByteArray())
            }
            entry = zip.nextEntry
        }
    }
}
