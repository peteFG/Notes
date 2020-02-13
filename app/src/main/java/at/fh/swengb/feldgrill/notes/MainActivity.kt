package at.fh.swengb.feldgrill.notes

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
    val ACCESS_TOKEN = "ACCESS_TOKEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        if(sharedPreferences.getString(ACCESS_TOKEN,null)!= null){

            val intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)

        }

        save_login.setOnClickListener {

            if(username_login.text != null && password_login.text != null) {

                val auth = AuthRequest(username_login.text.toString(), password_login.text.toString())

                loginProcess(auth,
                    success = {
                        sharedPreferences.edit().putString(ACCESS_TOKEN, it.token).apply()
                        val intent = Intent(this, NoteListActivity::class.java)
                        startActivity(intent)

                    },
                    error = {
                        Log.e("Error", it)
                        Toast.makeText(this, getString(R.string.login_username_password_required) , Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun loginProcess (
        request: AuthRequest,
        success: (response: AuthResponse) -> Unit,
        error: (errorMessage: String) -> Unit) {
        NoteApi.retrofitService.login(request).enqueue(object: retrofit2.Callback<AuthResponse>{
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                error("Login Error")
            }

            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                }
                else {
                    error("There was an error logging in")

                }

            }
        })
    }
}
