package me.huizengek.snpack

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import me.huizengek.snpack.models.Sticker
import me.huizengek.snpack.models.StickerPack
import me.huizengek.snpack.models.StickerPackWithStickers

@Database(
    entities = [StickerPack::class, Sticker::class],
    version = 1
)
@TypeConverters(StringListTypeConverter::class)
abstract class DatabaseCreator : RoomDatabase() {
    abstract val dao: DatabaseAccessor
}

@Suppress("TooManyFunctions")
@Dao
interface DatabaseAccessor {
    companion object {
        lateinit var instance: DatabaseCreator
        private var isInitialized = false

        context(Context)
        fun init() {
            if (isInitialized) return
            instance = Room.databaseBuilder(
                context = applicationContext,
                klass = DatabaseCreator::class.java,
                name = "database.db"
            ).build()
            isInitialized = true
        }
    }

    @Transaction
    @Query("SELECT * FROM StickerPack")
    fun getPacks(): Flow<List<StickerPackWithStickers>>

    @Transaction
    @Query("SELECT * FROM StickerPack")
    fun getPacksBlocking(): List<StickerPackWithStickers>

    @Transaction
    @Query("SELECT * FROM StickerPack WHERE id = :id")
    fun pack(id: Long): Flow<StickerPackWithStickers>

    @Transaction
    @Query("SELECT * FROM StickerPack WHERE id = :id")
    fun packBlocking(id: Long): StickerPackWithStickers

    @Transaction
    @Query("SELECT * FROM Sticker WHERE id = :id")
    fun sticker(id: Long): Flow<Sticker>

    @Query("UPDATE StickerPack SET imageDataVersion = imageDataVersion + 1 WHERE id = :id")
    fun incrementPack(id: Long)

    @Query(
        """
        UPDATE StickerPack 
        SET imageDataVersion = imageDataVersion + 1
        WHERE id = (SELECT packId FROM Sticker WHERE id = :id)
        """
    )
    fun incrementPackBySticker(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stickerPack: StickerPack): Long

    @Update
    fun update(stickerPack: StickerPack)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sticker: Sticker): Long

    @Delete
    fun delete(stickerPack: StickerPack)

    @Delete
    fun delete(sticker: Sticker)
}

object Database : DatabaseAccessor by DatabaseAccessor.instance.dao {
    fun transaction(block: suspend me.huizengek.snpack.Database.() -> Unit) =
        DatabaseAccessor.instance.transactionExecutor.execute {
            runBlocking { this@Database.block() }
        }
}

@TypeConverters
class StringListTypeConverter {
    @TypeConverter
    fun toList(string: String) = string.split(", ")

    @TypeConverter
    fun toString(list: List<String>) = list.joinToString(separator = ", ")
}
