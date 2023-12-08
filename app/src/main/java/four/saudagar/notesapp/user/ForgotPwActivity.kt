package four.saudagar.notesapp.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import four.saudagar.notesapp.R

class ForgotPwActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pw)

        auth = FirebaseAuth.getInstance()

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            Intent(this@ForgotPwActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        val btnNewPassword = findViewById<Button>(R.id.btnNewPassword)
        val userEmail = findViewById<EditText>(R.id.userEmail)
        btnNewPassword.setOnClickListener {
            val email = userEmail.text.toString().trim()

            if(email.isEmpty()){
                userEmail.error = "Please insert your Email"
                userEmail.requestFocus()
                return@setOnClickListener
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                userEmail.error = "Please insert a valid Email"
                userEmail.requestFocus()
                return@setOnClickListener
            }

            updatePassword(email)
        }
    }

    private fun updatePassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this){
                if(it.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_SHORT).show()
                    Intent(this@ForgotPwActivity, LoginActivity::class.java).also {intent ->
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}