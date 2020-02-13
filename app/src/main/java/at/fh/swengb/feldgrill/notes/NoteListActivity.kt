package at.fh.swengb.feldgrill.notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_note_list.*
import java.util.*


class NoteListActivity : AppCompatActivity() {
    companion object{
        val ACCESS_TOKEN = "ACCESS_TOKEN"
        val LAST_SYNC = "LAST_SYNC"
        val NOTE_ID = "NOTE_ID"
        val NEW_NOTE_RESULT = 0
    }

    private val noteAdapter = NoteAdapter{
        val intent = Intent(this, NewNote::class.java)
        intent.putExtra(NOTE_ID, it.id)
        startActivityForResult(intent, NEW_NOTE_RESULT)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        val lastSync = sharedPreferences.getLong(LAST_SYNC, 0)
        if (accessToken != null){

            NoteRepository.getNotes(
                accessToken,
                lastSync,
                success = {
                    it.notes.map { NoteRepository.addNote(this, it) }
                    sharedPreferences.edit().putLong(LAST_SYNC, it.lastSync).apply()
                    noteAdapter.updateList(NoteRepository.getNotesAll(this))
                },
                error = {
                    Log.e("Error", it)
                    noteAdapter.updateList(NoteRepository.getNotesAll(this))
                })
            note_recycler_view.layoutManager = StaggeredGridLayoutManager(2,1)
            note_recycler_view.adapter = noteAdapter

        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_NOTE_RESULT  && resultCode == Activity.RESULT_OK){

            noteAdapter.updateList(NoteRepository.getNotesAll(this))
            note_recycler_view.layoutManager = StaggeredGridLayoutManager(2,1)
            note_recycler_view.adapter = noteAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {

            R.id.logout -> {
                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                NoteRepository.clearDb(this)
                finish()
                true}
            R.id.new_note -> {
                val uuidString = UUID.randomUUID().toString()
                val intent = Intent(this, NewNote::class.java)
                intent.putExtra(NOTE_ID, uuidString)
                startActivityForResult(intent, NEW_NOTE_RESULT)
                true}
            else -> super.onOptionsItemSelected(item)
        }
    }
}
