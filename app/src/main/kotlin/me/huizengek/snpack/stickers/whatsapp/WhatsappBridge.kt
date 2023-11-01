package me.huizengek.snpack.stickers.whatsapp

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import me.huizengek.snpack.models.StickerPack

context(Activity)
fun StickerPack.addToWhatsapp() = runCatching {
    val intent = Intent.createChooser(Intent().apply {
        action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
        putExtra("sticker_pack_id", id.toString())
        putExtra("sticker_pack_authority", STICKER_APP_AUTHORITY)
        putExtra("sticker_pack_name", name)
    }, "Toevoegen aan...")
    startActivityForResult(intent, 200)
}.onFailure {
    Toast.makeText(
        this@Activity,
        "Kon Whatsapp niet openen, controleer of Whatsapp juist geïnstalleerd is...",
        Toast.LENGTH_SHORT
    ).show()
}