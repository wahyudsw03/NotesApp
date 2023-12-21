package four.saudagar.notesapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailNotes : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_notes)

        val selectedNote = intent.getParcelableExtra<ItemsViewModel>("note")

        if(selectedNote != null){
            val today = Calendar.getInstance()
            today.setTimeInMillis(selectedNote.date)
            val cal = Calendar.getInstance()
            cal.setTimeInMillis(selectedNote.date)
            var startTime = Calendar.getInstance()
            startTime.setTimeInMillis(selectedNote.start)
            var endTime = Calendar.getInstance()
            endTime.setTimeInMillis(selectedNote.end)

            val inputTitle = findViewById<EditText>(R.id.inputTitle)
            val inputContent = findViewById<EditText>(R.id.inputContent)
            val inputDate = findViewById<TextView>(R.id.inputDate)
            val inputTime = findViewById<TextView>(R.id.inputTime)

            val submitNoteButton = findViewById<Button>(R.id.submitNoteButton)
            val btnBackNote = findViewById<Button>(R.id.btnBackNote)
            val btnDeleteNote = findViewById<ImageView>(R.id.btnDeleteNote)

            btnDeleteNote.setOnClickListener {
                val noteId = selectedNote.id
                deleteNote(noteId)
            }

            // sets the layout to selected note data's and edit mode
            inputTitle.setText(selectedNote.title)
            inputContent.setText(selectedNote.text)
            //        set date to selected date
            inputDate.text = SimpleDateFormat("MMMM d, yyyy").format(today.time)
            //        set time to selected time
            inputTime.text = SimpleDateFormat("HH:mm").format(startTime.time) +
                    " - " + SimpleDateFormat("HH:mm").format(endTime.time)
            submitNoteButton.setText("Save")
            submitNoteButton.visibility = View.INVISIBLE


//        DATE
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.US)
                inputDate.text = sdf.format(cal.time)
            }

            inputDate.setOnClickListener {
                submitNoteButton.visibility = View.VISIBLE
                val dateDialog = DatePickerDialog(this@DetailNotes, dateSetListener,
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

                val endTimeDialog = TimePickerDialog(this@DetailNotes, endTimeSetListener,
                    startTime.get(Calendar.HOUR_OF_DAY),
                    startTime.get(Calendar.MINUTE), true)
//            set minimum date
                endTimeDialog.setTitle("END TIME")
                endTimeDialog.show()
            }

            inputTime.setOnClickListener {
                submitNoteButton.visibility = View.VISIBLE
                val startTimeDialog = TimePickerDialog(this@DetailNotes, startTimeSetListener,
                    startTime.get(Calendar.HOUR_OF_DAY),
                    startTime.get(Calendar.MINUTE), true)
//            set minimum date
                startTimeDialog.setTitle("START TIME")
                startTimeDialog.show()
            }

            inputTitle.setOnFocusChangeListener { _, hasFocus ->  submitNoteButton.visibility = View.VISIBLE}

            inputContent.setOnFocusChangeListener { _, hasFocus ->  submitNoteButton.visibility = View.VISIBLE}

            submitNoteButton.setOnClickListener {
                val id = selectedNote.id
                val title = inputTitle.text.toString().trim()
                val content = inputContent.text.toString().trim()
                //for easier reading and writing
                val calInMillis = cal.timeInMillis
                val startInMillis = startTime.timeInMillis
                val endInMillis = endTime.timeInMillis
                submitNote(id, title, calInMillis, startInMillis, endInMillis, content, auth.uid as String)
            }

            btnBackNote.setOnClickListener{
                finish()
            }
        }
    }

    private fun submitNote(id: String, title: String, cal: Long, start: Long,
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
            .document(id).set(note)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore: ", "DocumentSnapshot edited with ID: ${id}")
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Something Went Wrong, Please Try Again in a While", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNote(id: String){
        db.collection("notes")
            .document(id).delete()
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore: ", "Document ${id} is deleted")
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Something Went Wrong, Please Try Again in a While", Toast.LENGTH_SHORT).show()
            }
    }
}