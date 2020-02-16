package at.fh.swengb.feldgrill.notes


import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object NoteRepository {

    fun getNotes(token: String, lastSync: Long, success:(noteResponse: NotesResponse)->Unit,error:(errorMessage:String)->Unit){
        NoteApi.retrofitService.notes(token, lastSync).enqueue(object : Callback<NotesResponse> {

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                error("The call failed")
            }

            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {

                    success(responseBody)
                } else {

                    error("Something went wrong")
                }
            }
        })
    }

    fun uploadNote (token: String, noteToUpload: Note, success: (note: Note)->Unit, error: (errorMessage: String)->Unit){
        NoteApi.retrofitService.addOrUpdateNote(token, noteToUpload).enqueue(object :
            Callback<Note> {

            override fun onFailure(call: Call<Note>, t: Throwable) {
                error("The call failed! " + t.localizedMessage)
            }

            override fun onResponse(call: Call<Note>, response: Response<Note>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {

                    success(responseBody)
                } else {

                    error("Something went wrong " + response.message())
                }
            }
        })
    }

    fun addNote(context: Context, newNote: Note) {
        val db = NoteDB.getDatabase(context)
        db.noteDao.insert(newNote)
    }

    fun getNoteById (context: Context, id: String):Note {
        val db = NoteDB.getDatabase(context)
        return db.noteDao.findNoteById(id)
    }

    fun getAllNotes (context: Context):List<Note> {
        val db = NoteDB.getDatabase(context)
        return db.noteDao.getAllNotes()
    }

    fun clearDb (context: Context) {
        val db = NoteDB.getDatabase(context)
        db.noteDao.deleteAllNotes()
    }
}