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
    val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        if(sharedPreferences.getString(KEY_ACCESS_TOKEN,null)!= null){

            val intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
            finish()
        }

        save_login.setOnClickListener {

            val usernameString = main_username.text.toString()
            val passwordString = main_password.text.toString()

            if (usernameString.isEmpty() || passwordString.isEmpty()) {

                Toast.makeText(this, getString(R.string.login_username_password_empty) , Toast.LENGTH_SHORT).show()

            }
            else {

                val auth = AuthRequest(usernameString, passwordString)

                loginProcess(auth,
                    success = {
                        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, it.token).apply()
                        val intent = Intent(this, NoteListActivity::class.java)
                        startActivity(intent)
                        finish()

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
