package me.huizengek.snpack.stickers.whatsapp

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import me.huizengek.snpack.R
import me.huizengek.snpack.models.StickerPack

context(Activity)
fun StickerPack.addToWhatsapp() = runCatching {
    val intent = Intent.createChooser(
        /* target = */ Intent().apply {
            action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
            putExtra("sticker_pack_id", id.toString())
            putExtra("sticker_pack_authority", STICKER_APP_AUTHORITY)
            putExtra("sticker_pack_name", name)
        },
        /* title = */ getString(R.string.add_to_title)
    )
    startActivityForResult(intent, 200)
}.onFailure {
    Toast.makeText(
        this@Activity,
        getString(R.string.error_whatsapp_not_installed),
        Toast.LENGTH_SHORT
    ).show()
}
