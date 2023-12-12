package me.huizengek.snpack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.huizengek.snpack.R
import kotlin.math.ceil

private val categories @Composable get() = mapOf(
    stringResource(R.string.happy) to listOf(
        "😀",
        "😃",
        "😄",
        "😁",
        "😆",
        "😅",
        "😂",
        "🤣",
        "🙂",
        "😛",
        "😝",
        "😜",
        "🤪",
        "🤗",
        "😺",
        "😸",
        "😹",
        "☺",
        "😌",
        "😉",
        "🤗",
        "😊",
        "👋"
    ),
    stringResource(R.string.love) to listOf(
        "❤",
        "😍",
        "😘",
        "💕",
        "😻",
        "💑",
        "👩‍❤‍👩",
        "👨‍❤‍👨",
        "💏",
        "👩‍❤‍💋‍👩",
        "👨‍❤‍💋‍👨",
        "🧡",
        "💛",
        "💚",
        "💙",
        "💜",
        "🖤",
        "💔",
        "❣",
        "💞",
        "💓",
        "💗",
        "💖",
        "💘",
        "💝",
        "💟",
        "♥",
        "💌",
        "💋",
        "👩‍❤️‍💋‍👩",
        "👨‍❤️‍💋‍👨",
        "👩‍❤️‍👨",
        "👩‍❤️‍👩",
        "👨‍❤️‍👨",
        "👩‍❤️‍💋‍👨",
        "👬",
        "👭",
        "👫",
        "🥰",
        "😚",
        "😙",
        "👄",
        "🌹",
        "😽",
        "❣️"
    ),
    stringResource(R.string.sad) to listOf(
        "☹",
        "😣",
        "😖",
        "😫",
        "😩",
        "😢",
        "😭",
        "😞",
        "😔",
        "😟",
        "😕",
        "😤",
        "😠",
        "😥",
        "😰",
        "😨",
        "😿",
        "😾",
        "😓",
        "🙍‍♂",
        "🙍‍♀",
        "💔",
        "🙁",
        "🥺",
        "🤕",
        "☔️",
        "⛈",
        "🌩",
        "🌧"
    ),
    stringResource(R.string.angry) to listOf(
        "😯",
        "😦",
        "😧",
        "😮",
        "😲",
        "🙀",
        "😱",
        "🤯",
        "😳",
        "❗",
        "❕",
        "🤬",
        "😡",
        "😠",
        "🙄",
        "👿",
        "😾",
        "😤",
        "💢",
        "👺",
        "🗯️",
        "😒",
        "🥵"
    ),
    stringResource(R.string.party) to listOf(
        "🎊",
        "🎉",
        "🎁",
        "🎈",
        "👯‍♂️",
        "👯",
        "👯‍♀️",
        "💃",
        "🕺",
        "🔥",
        "⭐️",
        "✨",
        "💫",
        "🎇",
        "🎆",
        "🍻",
        "🥂",
        "🍾",
        "🎂",
        "🍰"
    )
)

@Composable
fun StickerEmojiPicker(
    onPicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    gridCells: Int = 8
) = BoxWithConstraints(modifier = modifier) {
    val config = LocalConfiguration.current
    val categories = categories

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(config.screenHeightDp.dp)
    ) {
        categories.forEach { (name, emojis) ->
            item {
                Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridCells),
                        modifier = Modifier
                            .size(
                                width = this@BoxWithConstraints.maxWidth,
                                height = (this@BoxWithConstraints.maxWidth - 32.dp) / gridCells *
                                        ceil(emojis.size / gridCells.toFloat())
                            )
                            .padding(horizontal = 16.dp),
                        userScrollEnabled = false
                    ) {
                        items(emojis) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clickable { onPicked(it) }
                            ) {
                                Text(
                                    text = it,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
