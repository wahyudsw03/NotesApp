package four.saudagar.notesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
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
//        val btnProfile = findViewById<FloatingActionButton>(R.id.fab)
        val btnLogout = findViewById<TextView>(R.id.btnLogout)
        val btnCreate = findViewById<FloatingActionButton>(R.id.fab)
//        val btnMath = findViewById<TextView>(R.id.btnMath)
        btnLogout.setOnClickListener {
            auth.signOut()
            Intent(this@MainActivity, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
//        btnProfile.setOnClickListener {
//            Intent(this@MainActivity, ProfilesActivity::class.java).also {
//                startActivity(it)
//            }
//        }
        btnCreate.setOnClickListener {
            Intent(this, CreateNotes::class.java).also {
                startActivity(it)
            }
        }

//        btnMath.setOnClickListener {
//            Intent(this@MainActivity, MathChallengeActivity::class.java).also {
//                startActivity(it)
//            }
//        }

        }

    override fun onResume() {
        super.onResume()
        // GET DB
        val data = ArrayList<ItemsViewModel>()

        val db = Firebase.firestore
        db.collection("notes").whereEqualTo("uid", auth.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
//                    Log.d("FIREBASE SUCCESS HOME : ", "${document.id} => ${document.data}")
                    data.add(ItemsViewModel(document.id ,R.drawable.aklogo, document.data.getValue("title") as String,
                        document.data.getValue("content") as String, document.data.getValue("start") as Long,
                        document.data.getValue("end") as Long, document.data.getValue("date") as Long))

                }
                // getting the recyclerview by its id
                val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

                // this creates a vertical layout Manager
                recyclerview.layoutManager = LinearLayoutManager(this)

                // This will pass the ArrayList to our Adapter
                val adapter = CustomAdapter(data)

                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter

                // Remove placeholder text on home screen
                val textView3 = findViewById<TextView>(R.id.textView3)
                if(data.size > 0){
                    textView3.text = ""
                } else {
                    textView3.text = "@string/notesEmpty"
                }

                adapter.onItemClick = {
                    val intent = Intent(this, DetailNotes::class.java)
                    intent.putExtra("note", it)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FIREBASE FAILED HOME : ", "Error getting documents.", exception)
            }
    }
    }

