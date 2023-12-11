package four.saudagar.notesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import four.saudagar.notesapp.user.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: AppCompatActivity

    private lateinit var auth: FirebaseAuth

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val btnProfile = findViewById<FloatingActionButton>(R.id.fab)
        val btnLogout = findViewById<TextView>(R.id.btnLogout)
//        val btnMath = findViewById<TextView>(R.id.btnMath)
        btnLogout.setOnClickListener {
            auth.signOut()
            Intent(this@MainActivity, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
        btnProfile.setOnClickListener {
            Intent(this@MainActivity, ProfilesActivity::class.java).also {
                startActivity(it)
            }
        }

//        btnMath.setOnClickListener {
//            Intent(this@MainActivity, MathChallengeActivity::class.java).also {
//                startActivity(it)
//            }
//        }

        }
    }

