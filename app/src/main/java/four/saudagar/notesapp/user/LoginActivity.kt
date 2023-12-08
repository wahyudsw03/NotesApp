package four.saudagar.notesapp.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import four.saudagar.notesapp.MainActivity
import four.saudagar.notesapp.R

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val btnRegister = findViewById<Button>(R.id.btnRegisterOnLogin)
        btnRegister.setOnClickListener {
            Intent(this@LoginActivity, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }

        val btnForgotPassword = findViewById<TextView>(R.id.btnForgotPassword)
        btnForgotPassword.setOnClickListener {
            Intent(this@LoginActivity, ForgotPwActivity::class.java).also {
                startActivity(it)
            }
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val loginEmail = findViewById<EditText>(R.id.etEmail)
        val loginPassword = findViewById<EditText>(R.id.etPassword)
        btnLogin.setOnClickListener {
            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if(email.isEmpty()){
                loginEmail.error = "Please insert your Email"
                loginEmail.requestFocus()
                return@setOnClickListener
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                loginEmail.error = "Please insert a valid Email"
                loginEmail.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty() || password.length < 6) {
                loginPassword.error = "Password needs to be at least 6 characters"
                loginPassword.requestFocus()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful) {
                    Intent(this@LoginActivity, MainActivity::class.java).also {intent ->
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            Intent(this@LoginActivity, MainActivity::class.java).also {intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}