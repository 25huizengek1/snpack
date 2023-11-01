package me.huizengek.snpack.util

import android.net.Uri
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope

private val chars = ('0'..'9') + ('a'..'z') + ('A'..'Z')
fun randomString(length: Int = 32) = Array(length) { chars.random() }.joinToString(separator = "")

val uriSaver = object : Saver<Uri?, String> {
    override fun restore(value: String) = if (value != "null") Uri.parse(value) else null
    override fun SaverScope.save(value: Uri?) = value?.toString() ?: "null"
}