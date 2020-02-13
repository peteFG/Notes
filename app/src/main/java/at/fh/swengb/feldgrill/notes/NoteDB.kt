package at.fh.swengb.feldgrill.notes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1)
abstract class NoteDB : RoomDatabase() {
    abstract val noteDao: NoteDao

    companion object {
        private var INSTANCE: NoteDB? = null
        fun getDatabase(context: Context): NoteDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): NoteDB {
            return Room.databaseBuilder(
                context,
                NoteDB::class.java, "note-db"
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()

        }
    }
}