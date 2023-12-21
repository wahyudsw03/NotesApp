package four.saudagar.notesapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateNotes : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_notes)

        val today = Calendar.getInstance()
        val cal = Calendar.getInstance()
        var startTime = Calendar.getInstance()
        var endTime = Calendar.getInstance()
        endTime.add(Calendar.MINUTE, 1)

        val inputTitle = findViewById<EditText>(R.id.inputTitle)
        val inputContent = findViewById<EditText>(R.id.inputContent)
        val inputDate = findViewById<TextView>(R.id.inputDate)
//        set date to current date
        inputDate.text = SimpleDateFormat("MMMM d, yyyy").format(today.time)
        val inputTime = findViewById<TextView>(R.id.inputTime)
//        set time to current time
        inputTime.text = SimpleDateFormat("HH:mm").format(startTime.time) +
                " - " + SimpleDateFormat("HH:mm").format(endTime.time)
        val submitNoteButton = findViewById<Button>(R.id.submitNoteButton)
        val btnBackNote = findViewById<Button>(R.id.btnBackNote)
        val btnDelete = findViewById<ImageView>(R.id.btnDeleteNote)
        btnDelete.visibility = View.INVISIBLE

//        DATE
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.US)
            inputDate.text = sdf.format(cal.time)
        }

        inputDate.setOnClickListener {
            val dateDialog = DatePickerDialog(this@CreateNotes, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
//            set minimum date
            dateDialog.datePicker.minDate = today.timeInMillis
            dateDialog.show()
        }

//        TIME
        val endTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val temp = Calendar.getInstance()
            temp.set(Calendar.HOUR_OF_DAY, hourOfDay)
            temp.set(Calendar.MINUTE, minute)
            endTime = temp

            inputTime.text = SimpleDateFormat("HH:mm").format(startTime.time) +
                    " - " + SimpleDateFormat("HH:mm").format(endTime.time)
        }

        val startTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val temp = Calendar.getInstance()
            temp.set(Calendar.HOUR_OF_DAY, hourOfDay)
            temp.set(Calendar.MINUTE, minute)
            startTime = temp

            val endTimeDialog = TimePickerDialog(this@CreateNotes, endTimeSetListener,
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE), true)
//            set minimum date
            endTimeDialog.setTitle("END TIME")
            endTimeDialog.show()
        }

        inputTime.setOnClickListener {
            val startTimeDialog = TimePickerDialog(this@CreateNotes, startTimeSetListener,
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE), true)
//            set minimum date
            startTimeDialog.setTitle("START TIME")
            startTimeDialog.show()
        }

        submitNoteButton.setOnClickListener {
            val title = inputTitle.text.toString().trim()
            val content = inputContent.text.toString().trim()
            //for easier reading and writing
            val calInMillis = cal.timeInMillis
            val startInMillis = startTime.timeInMillis
            val endInMillis = endTime.timeInMillis
            submitNote(title, calInMillis, startInMillis, endInMillis, content, auth.uid as String)
        }

        btnBackNote.setOnClickListener{
            finish()
        }
    }

    private fun submitNote(title: String, cal: Long, start: Long,
                           end: Long, content: String, uid: String) {
        // Create a new user with a first and last name
        val note = hashMapOf(
            "title" to title,
            "date" to cal,
            "start" to start,
            "end" to end,
            "content" to content,
            "uid" to uid
        )

// Add a new document with a generated ID
        db.collection("notes")
            .add(note)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore: ", "DocumentSnapshot added with ID: ${documentReference.id}")
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Something Went Wrong, Please Try Again in a While", Toast.LENGTH_SHORT).show()
            }
    }
}