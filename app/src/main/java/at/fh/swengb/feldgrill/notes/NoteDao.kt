package at.fh.swengb.feldgrill.notes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Note: Note)

    @Query("SELECT * FROM Note WHERE id = :note_id")
    fun findNoteById(note_id: String):Note

    @Query("SELECT * FROM Note")
    fun getAllNotes():List<Note>

    @Query("DELETE FROM Note")
    fun deleteAllNotes()
}