package at.fh.swengb.feldgrill.notes

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AuthResponse(val token: String) {
}