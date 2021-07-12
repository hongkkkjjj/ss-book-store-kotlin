package com.softspace.ssbookstore

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.softspace.ssbookstore.adapter.FirestoreAdapter
import com.softspace.ssbookstore.data_class.BookDetail
import com.softspace.ssbookstore.enum_file.TakePhotoType
import com.softspace.ssbookstore.utility.Base64Util
import com.softspace.ssbookstore.utility.Util
import com.softspace.ssbookstore.utility.hideSoftKeyboard
import java.time.LocalDate

class BookDetailsActivity : AppCompatActivity(), BottomSheetCustomInterface {

    private lateinit var callback: BottomSheetCustomInterface
    private val TAG = "Book Details Activity"
    private var isNewBook = false
    private var isEditing = false
    private var itemEdit: MenuItem? = null
    private var itemDone: MenuItem? = null
    private var selectedBook: BookDetail = BookDetail()
    private var base64Image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        hideLoadingScreen()
        callback = this

        val ivBookCover = findViewById<ImageView>(R.id.ivBookImage)
        ivBookCover.setBackgroundDrawable(
                resources.getDrawable(
                        R.drawable.light_green_border,
                        null
                )
        )

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.title = ""
        val intent = intent
        if (intent.extras != null) {
            intent.extras?.let {
                selectedBook = it.get("book_detail") as BookDetail
                setupDisplayText(selectedBook)
            }
        } else {
            isNewBook = true
            isEditing = true
            enableTextEdit()
        }
    }

    override fun onResume() {
        super.onResume()

        setupUI(findViewById(R.id.clBookDetailsActivity))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_book_details, menu)

        this.itemEdit = menu?.findItem(R.id.itemEdit)
        this.itemDone = menu?.findItem(R.id.itemDone)

        hideMenuItem()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemDone -> {
                validateBookDetailTextField()
            }
            R.id.itemEdit -> {
                this.isEditing = true
                hideMenuItem()
                enableTextEdit()
            }
            else -> {
                Log.i("Book_detail_activity", "Else")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun callbackMethod(takePhotoType: TakePhotoType) {
        when (takePhotoType) {
            TakePhotoType.camera -> {
                val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if (takePhotoIntent.resolveActivity(this.packageManager) != null) {
                    startActivityForResult(takePhotoIntent, 42)
                } else {
                    Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
                }
            }
            TakePhotoType.gallery -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 43)
            }
            else -> {}
        }
        Log.i(TAG, "Take photo type is $takePhotoType")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            val takenImage = data?.extras?.get("data") as Bitmap
            val ivBookCover = findViewById<ImageView>(R.id.ivBookImage)
            ivBookCover.setImageBitmap(takenImage)
            base64Image = Base64Util.convertImageToString64(takenImage)
        } else if (requestCode == 43 && resultCode == Activity.RESULT_OK) {
            val ivBookCover = findViewById<ImageView>(R.id.ivBookImage)
            ivBookCover.setImageURI(data?.data)
            val bitmap = (ivBookCover.drawable as BitmapDrawable).bitmap
            base64Image = Base64Util.convertImageToString64(bitmap)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun hideMenuItem() {
        itemEdit?.isVisible = !isEditing
        itemDone?.isVisible = isEditing
    }

    private fun enableTextEdit() {
        val ivBookImage = findViewById<ImageView>(R.id.ivBookImage)
        val etBookName = findViewById<EditText>(R.id.etBookName)
        val etAuthorName = findViewById<EditText>(R.id.etAuthorName)
        val etBookDesc = findViewById<EditText>(R.id.etBookDesc)

        etBookName.isEnabled = true
        etAuthorName.isEnabled = true
        etBookDesc.isEnabled = true
        ivBookImage.setBackgroundDrawable(resources.getDrawable(R.drawable.light_green_border, null))
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.

        if (view !is EditText && view !is ImageView) {
            view.setOnClickListener {
                hideSoftKeyboard()
            }
        } else if (view is ImageView && view.id == R.id.ivBookImage) {
            val bottomSheet = BottomSheetBookImageFragment(callback)
            view.setOnClickListener {
                Log.i(TAG, "ivBookImage clicked")
                if (isEditing) {
                    bottomSheet.show(supportFragmentManager, "BottomSheetDialog")
                }
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView: View = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun setupDisplayText(bookData: BookDetail) {
        Log.i(TAG, "Book detail is ${bookData.title}")
        val ivBookImage = findViewById<ImageView>(R.id.ivBookImage)
        val etBookName = findViewById<EditText>(R.id.etBookName)
        val etAuthorName = findViewById<EditText>(R.id.etAuthorName)
        val etBookDesc = findViewById<EditText>(R.id.etBookDesc)

        etBookName.isEnabled = false
        etAuthorName.isEnabled = false
        etBookDesc.isEnabled = false
        ivBookImage.setBackgroundDrawable(null)
        this.base64Image = bookData.image
        ivBookImage.setImageBitmap(Base64Util.convertString64ToImage(bookData.image))
        etBookName.setText(bookData.title)
        etAuthorName.setText(bookData.author)
        etBookDesc.setText(bookData.desc)
    }

    private fun showLoadingScreen() {
        Log.i(TAG, "Show loading screen")
        val bookListLoadingScreen = findViewById<ConstraintLayout>(R.id.bookDetailLoadingScreen)
        val bookListAnimationView = findViewById<LottieAnimationView>(R.id.bookDetailAnimationView)
        bookListLoadingScreen.isVisible = true
        bookListAnimationView.isVisible = true
        bookListAnimationView.playAnimation()
    }

    private fun hideLoadingScreen() {
        Log.i(TAG, "Hide loading screen")
        val bookListLoadingScreen = findViewById<ConstraintLayout>(R.id.bookDetailLoadingScreen)
        val bookListAnimationView = findViewById<LottieAnimationView>(R.id.bookDetailAnimationView)
        bookListLoadingScreen.isVisible = false
        bookListAnimationView.isVisible = false
        bookListAnimationView.pauseAnimation()
    }

    private fun validateBookDetailTextField() {
        val etBookName = findViewById<EditText>(R.id.etBookName)
        val etAuthorName = findViewById<EditText>(R.id.etAuthorName)
        val etBookDesc = findViewById<EditText>(R.id.etBookDesc)

        val tvBookNameError = findViewById<TextView>(R.id.tvBookNameError)
        val tvAuthorNameError = findViewById<TextView>(R.id.tvAuthorNameError)
        val tvBookDescError = findViewById<TextView>(R.id.tvBookDescError)
        val tvBookImageError = findViewById<TextView>(R.id.tvBookImageError)

        val bookName = etBookName.text.toString()
        val authorName = etAuthorName.text.toString()
        val bookDesc = etBookDesc.text.toString()

        tvBookNameError.isVisible = false
        tvAuthorNameError.isVisible = false
        tvBookDescError.isVisible = false
        tvBookImageError.isVisible = false

        when {
            base64Image.isEmpty() -> {
                tvBookImageError.text = getString(R.string.message_book_cover_empty)
                tvBookImageError.isVisible = true
            }
            bookName.isEmpty() -> {
                tvBookNameError.text = getString(R.string.message_book_name_empty)
                tvBookNameError.isVisible = true
            }
            authorName.isEmpty() -> {
                tvAuthorNameError.text = getString(R.string.message_author_name_empty)
                tvAuthorNameError.isVisible = true
            }
            bookDesc.isEmpty() -> {
                tvBookDescError.text = getString(R.string.message_book_desc_empty)
                tvBookDescError.isVisible = true
            }
            else -> {
                val dateNow = LocalDate.now()
                val strDate = Util.convertDateToString(dateNow)

                if (isNewBook) {
                    createBook(BookDetail(
                            "",
                            bookName,
                            authorName,
                            bookDesc,
                            base64Image,
                            strDate,
                            dateNow
                    ))
                } else {
                    updateBook(BookDetail(
                            selectedBook.fireStoreId,
                            bookName,
                            authorName,
                            bookDesc,
                            base64Image,
                            strDate,
                            dateNow
                    )
                    )
                }
            }
        }
    }

    private fun updateBook(bookDetail: BookDetail) {
        showLoadingScreen()
        FirestoreAdapter.updateBook(bookDetail) { boolResult ->
            hideLoadingScreen()
            if (boolResult) {
                this.isEditing = false
                hideMenuItem()
                showAlertDialog {
                    setMessage(R.string.message_success_update_book)
                    positiveButton(getString(R.string.ok)) {
                        finish()
                    }
                }
            } else {
                showAlertDialog {
                    setMessage(R.string.message_failed_update_book)
                    positiveButton(getString(R.string.ok)) {}
                }
            }
        }
    }

    private fun createBook(bookDetail: BookDetail) {
        showLoadingScreen()
        FirestoreAdapter.createBook(bookDetail) { boolResult ->
            hideLoadingScreen()
            if (boolResult) {
                this.isEditing = false
                hideMenuItem()
                showAlertDialog {
                    setMessage(R.string.message_success_create_book)
                    positiveButton(getString(R.string.ok)) {
                        finish()
                    }
                }
            } else {
                showAlertDialog {
                    setMessage(R.string.message_failed_create_book)
                    positiveButton(getString(R.string.ok)) {}
                }
            }
        }
    }

    private fun showAlertDialog(dialogBuilder: AlertDialog.Builder.() -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.dialogBuilder()
        val dialog = builder.create()

        dialog.show()
    }

    private fun AlertDialog.Builder.positiveButton(text: String = "Okay", handleClick: (which: Int) -> Unit = {}) {
        this.setPositiveButton(text) { dialogInterface, which -> handleClick(which) }
    }
}