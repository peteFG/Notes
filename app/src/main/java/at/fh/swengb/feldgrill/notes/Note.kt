package at.fh.swengb.feldgrill.notes

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity
class Note(@PrimaryKey val id: String,
           val title: String,
           val text: String,
           val toUpload: Boolean)
{
}