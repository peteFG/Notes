package at.fh.swengb.feldgrill.notes

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_note.*

class NewNote : AppCompatActivity() {

    companion object {
        val ACCESS_TOKEN = "ACCESS_TOKEN"
        val NEW_NOTE_RESULT = "NEW_NOTE_RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        val noteId: String? = intent.getStringExtra(NoteListActivity.NOTE_ID)

        if(noteId != null){
            // val note:Note?
            val note:Note? = NoteRepository.getNoteById(this, noteId)

                new_content.setText(note?.text)
                new_title.setText(note?.title)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.save_note -> {

                val extra: String? = intent.getStringExtra(NoteListActivity.NOTE_ID)
                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)

                if ((new_title.text.toString().isNotEmpty()|| new_content.text.toString().isNotEmpty()) &&
                    (extra != null) &&
                    (accessToken != null))
                {
                    val note = Note(extra, new_title.text.toString(), new_content.text.toString(), true)
                    NoteRepository.addNote(this, note)
                    NoteRepository.uploadNote(
                        accessToken,
                        note,
                        success = {
                            NoteRepository.addNote(this, it)
                        },
                        error = {
                            Log.e("Upload", it)
                        })

                    val resultIntent = intent
                    resultIntent.putExtra(NEW_NOTE_RESULT, "ADDED")
                    Log.e("NEW_NOTE", "Note has been added")
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                else {
                    Toast.makeText(this, this.getString(R.string.text_required) , Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu,menu)
        return true
    }
}
