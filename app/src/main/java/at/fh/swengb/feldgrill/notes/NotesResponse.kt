package at.fh.swengb.feldgrill.notes

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class NotesResponse(val lastSync: Long,
                    val notes: List<Note>) {
}