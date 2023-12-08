package four.saudagar.notesapp

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream


class ProfilesActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CAMERA = 100
    }

    private lateinit var imageUri: String
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var icVerified: ImageView
    private lateinit var icUnverified: ImageView
    private lateinit var btnUpdate: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles)

        auth = FirebaseAuth.getInstance()
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        icVerified = findViewById(R.id.icVerified)
        icUnverified = findViewById(R.id.icUnverified)
        btnUpdate = findViewById(R.id.btnUpdate)


        val ivProfile = findViewById<CircleImageView>(R.id.ivProfile)
        val user = auth.currentUser
        if(user != null){
            if(user.photoUrl != null){
                Picasso.get().load(user.photoUrl.toString()).into(ivProfile)
            } else {
                Picasso.get().load("https://picsum.photos/id/316/200").into(ivProfile)
            }

            etName.setText(user.displayName)
            etEmail.setText(user.email)

            if(user.isEmailVerified){
                icVerified.visibility = View.VISIBLE
                icUnverified.visibility = View.GONE // Hide the unverified icon when email is verified
            } else {
                icVerified.visibility = View.GONE // Hide the verified icon when email is not verified
                icUnverified.visibility = View.VISIBLE
            }
        }
        ivProfile.setOnClickListener {
            showImagePickerDialog()

        }

        btnUpdate.setOnClickListener{
            val image = when{
                ::imageUri.isInitialized -> Uri.parse(imageUri)
                user?.photoUrl == null -> Uri.parse("https://picsum.photos/id/316/200")
                else -> user.photoUrl
            }

            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "Please enter your name"
                etName.requestFocus()
                return@setOnClickListener
            }
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build()

            user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        icUnverified.setOnClickListener {
            user?.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification Email Sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Action")

        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    try {
                        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
                options[item] == "Choose from Gallery" -> {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, REQUEST_CAMERA)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            val imageBitmap = when {
                data?.extras?.containsKey("data") == true -> data.extras?.get("data") as Bitmap
                data?.data != null -> {
                    val selectedImageUri: Uri = data.data!!
                    // Update imageUri with the selected image URI
                    imageUri = selectedImageUri.toString()
                    MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                }
                else -> throw IllegalArgumentException("Invalid image data")
            }
            uploadImage(imageBitmap)
        }
    }

    private fun uploadImage(imageBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storagePath = "images/${FirebaseAuth.getInstance().currentUser?.uid}"

        val ref = FirebaseStorage.getInstance().reference.child(storagePath)

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val uploadTask = ref.putBytes(image)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            ref.downloadUrl.addOnCompleteListener { uriTask ->
                if (uriTask.isSuccessful) {
                    // The image has been uploaded successfully, and uriTask.result contains the download URL
                    val downloadUrl = uriTask.result.toString()
                    // You can use the downloadUrl as needed (e.g., save it in a database or display it in your app)
                    findViewById<CircleImageView>(R.id.ivProfile)?.setImageBitmap(imageBitmap)
                } else {
                    // Handle the case where getting the download URL fails
                    Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->
            // Handle the case where the upload task fails
            Log.e("ProfilesActivity", "Failed to upload image: ${exception.message}", exception)
            Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}