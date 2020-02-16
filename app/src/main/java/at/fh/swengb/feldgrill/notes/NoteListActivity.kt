package at.fh.swengb.feldgrill.notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_note_list.*
import java.util.*


class NoteListActivity : AppCompatActivity() {
    companion object{
        val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
        val LAST_SYNC = "LAST_SYNC"
        val NOTE_ID = "NOTE_ID"
        val NEW_NOTE_RESULT = 1
    }

    private val noteAdapter = NoteAdapter{
        val intent = Intent(this, NewNote::class.java)
        intent.putExtra(NOTE_ID, it.id)
        startActivityForResult(intent, NEW_NOTE_RESULT)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list) //auslagern

        noteSync()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_NOTE_RESULT  && resultCode == Activity.RESULT_OK){

            noteSync()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {

            R.id.sync_notes -> { //in order to synchronise Notes after regaining internet connection

                noteSync()

                true
            }
            R.id.logout -> {
                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                NoteRepository.clearDb(this)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

                true
            }
            R.id.new_note -> {
                val uuidString = UUID.randomUUID().toString()
                val intent = Intent(this, NewNote::class.java)
                intent.putExtra(NOTE_ID, uuidString)
                startActivityForResult(intent, NEW_NOTE_RESULT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    private fun noteSync() {

        note_recycler_view.layoutManager = StaggeredGridLayoutManager(2,1)
        note_recycler_view.adapter = noteAdapter

        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        val lastSync = sharedPreferences.getLong(LAST_SYNC, 0)

        if (accessToken != null){

            NoteRepository.getNotes(
                accessToken,
                lastSync,
                success = {

                    it.notes.map {NoteRepository.addNote(this, it) }
                    sharedPreferences.edit().putLong(LAST_SYNC, it.lastSync).apply()
                    noteAdapter.updateList(NoteRepository.getAllNotes(this))
                },
                error = {
                    noteAdapter.updateList(NoteRepository.getAllNotes(this))
                    Toast.makeText(this, getString(R.string.note_up_download_failed) , Toast.LENGTH_LONG).show()
                }
            )
        }
    }

}
