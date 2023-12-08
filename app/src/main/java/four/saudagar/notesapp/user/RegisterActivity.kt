package four.saudagar.notesapp.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import four.saudagar.notesapp.MainActivity
import four.saudagar.notesapp.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val btnSignUp = findViewById<Button>(R.id.btnRegister)
        val regEmail = findViewById<EditText>(R.id.regEmail)
        val regPassword = findViewById<EditText>(R.id.regPassword)
        btnSignUp.setOnClickListener {
            val email = regEmail.text.toString().trim()
            val password = regPassword.text.toString().trim()

            if(email.isEmpty()){
                regEmail.error = "Please insert your Email"
                regEmail.requestFocus()
                return@setOnClickListener
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                regEmail.error = "Pleas insert a valid Email"
                regEmail.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty() || password.length < 6) {
                regPassword.error = "Password needs to be at least 6 characters"
                regPassword.requestFocus()
                return@setOnClickListener
            }

            registerUser(email, password)
        }

        val btnLogin = findViewById<Button>(R.id.btnAlrHaveAnAccount)
        btnLogin.setOnClickListener {
            Intent(this@RegisterActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    Intent(this@RegisterActivity, MainActivity::class.java).also {intent ->
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null) {
            Intent(this@RegisterActivity, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
}