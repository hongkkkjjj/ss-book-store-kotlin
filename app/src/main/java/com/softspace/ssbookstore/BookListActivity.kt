package com.softspace.ssbookstore

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.softspace.ssbookstore.adapter.BookListAdapter
import com.softspace.ssbookstore.adapter.FirestoreAdapter
import com.softspace.ssbookstore.data_class.BookDetail
import com.softspace.ssbookstore.enum_file.SortType
import com.softspace.ssbookstore.utility.SharePrefKeys
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_book_list.*


class BookListActivity : AppCompatActivity() {

    private val TAG = "Book List Activity"
    private lateinit var bookListAdapter: BookListAdapter
    private var sortType = SortType.nameAsc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_exit_to_app)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bookListAdapter = BookListAdapter(mutableListOf<BookDetail>())
        rvBookList.adapter = bookListAdapter
        rvBookList.layoutManager = LinearLayoutManager(this)


        setupObservers()
        setupRecyclerView()
        setupSortingButtonAction()
        setupSortingButtonText()
    }

    override fun onResume() {
        super.onResume()

        refreshBookData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_book_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                val sharedPref = getSharedPreferences(SharePrefKeys.isUserLogin, 0)
                val editor = sharedPref.edit()
                editor.putBoolean(SharePrefKeys.isUserLogin, false)
                editor.apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.addBook -> {
                val intent = Intent(this, BookDetailsActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Log.i(TAG, "Else")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // VIEW SETUPS

    private fun setupRecyclerView() {
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    when(direction) {
                        ItemTouchHelper.LEFT -> {
                            Log.i(TAG, "ItemTouchHelper - Swipe left")

                            deleteConfirmationAlertDialog(viewHolder.adapterPosition)
                        }
                    }
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(applicationContext, R.color.system_red))
                        .addSwipeLeftLabel(resources.getString(R.string.delete_text))
                        .addBackgroundColor(ContextCompat.getColor(applicationContext, R.color.system_red))
                        .create()
                        .decorate()

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvBookList)
    }

    private fun setupObservers(){
        FirestoreAdapter.bookDetails.observe(this, {
            bookListAdapter.updateBooks(it)
            hideLoadingScreen()
        })
    }

    private fun setupSortingButtonAction() {
        val sortByNameButton = findViewById<Button>(R.id.btnTitle)
        val sortByAuthorButton = findViewById<Button>(R.id.btnAuthor)
        val sortByDateButton = findViewById<Button>(R.id.btnDate)

        sortByNameButton.setOnClickListener {
            sortType = if (sortType == SortType.nameAsc) SortType.nameDesc else SortType.nameAsc
            setupSortingButtonText()
            FirestoreAdapter.sortBookDetails(sortType, null)
        }

        sortByAuthorButton.setOnClickListener {
            sortType = if (sortType == SortType.authorAsc) SortType.authorDesc else SortType.authorAsc
            setupSortingButtonText()
            FirestoreAdapter.sortBookDetails(sortType, null)
        }

        sortByDateButton.setOnClickListener {
            sortType = if (sortType == SortType.dateAsc) SortType.dateDesc else SortType.dateAsc
            setupSortingButtonText()
            FirestoreAdapter.sortBookDetails(sortType, null)
        }
    }

    private fun setupSortingButtonText() {
        val sortByNameButton = findViewById<Button>(R.id.btnTitle)
        val sortByAuthorButton = findViewById<Button>(R.id.btnAuthor)
        val sortByDateButton = findViewById<Button>(R.id.btnDate)

        sortByNameButton.text = getString(R.string.title_idle)
        sortByAuthorButton.text = getString(R.string.author_idle)
        sortByDateButton.text = getString(R.string.date_idle)
        sortByNameButton.setTextColor(ContextCompat.getColor(this, R.color.system_blue))
        sortByAuthorButton.setTextColor(ContextCompat.getColor(this, R.color.system_blue))
        sortByDateButton.setTextColor(ContextCompat.getColor(this, R.color.system_blue))


        when (sortType) {
            SortType.nameAsc -> {
                sortByNameButton.text = getString(R.string.title_asc)
                sortByNameButton.setTextColor(ContextCompat.getColor(this, R.color.system_red))
            }
            SortType.nameDesc -> {
                sortByNameButton.text = getString(R.string.title_desc)
                sortByNameButton.setTextColor(ContextCompat.getColor(this, R.color.system_red))
            }
            SortType.authorAsc -> {
                sortByAuthorButton.text = getString(R.string.author_asc)
                sortByAuthorButton.setTextColor(ContextCompat.getColor(this, R.color.system_red))
            }
            SortType.authorDesc -> {
                sortByAuthorButton.text = getString(R.string.author_desc)
                sortByAuthorButton.setTextColor(ContextCompat.getColor(this, R.color.system_red))
            }
            SortType.dateAsc -> {
                sortByDateButton.text = getString(R.string.date_asc)
                sortByDateButton.setTextColor(ContextCompat.getColor(this, R.color.system_red))
            }
            SortType.dateDesc -> {
                sortByDateButton.text = getString(R.string.date_desc)
                sortByDateButton.setTextColor(ContextCompat.getColor(this, R.color.system_red))
            }
        }
    }

    private fun refreshBookData() {
        showLoadingScreen()
        FirestoreAdapter.getBookListData(sortType)
    }

    // LOADING SCREEN

    private fun showLoadingScreen() {
        Log.i(TAG, "Show loading screen")
        val bookListLoadingScreen = findViewById<ConstraintLayout>(R.id.bookListLoadingScreen)
        val bookListAnimationView = findViewById<LottieAnimationView>(R.id.bookListAnimationView)
        bookListLoadingScreen.isVisible = true
        bookListAnimationView.isVisible = true
        bookListAnimationView.playAnimation()
    }

    private fun hideLoadingScreen() {
        Log.i(TAG, "Hide loading screen")
        val bookListLoadingScreen = findViewById<ConstraintLayout>(R.id.bookListLoadingScreen)
        val bookListAnimationView = findViewById<LottieAnimationView>(R.id.bookListAnimationView)
        bookListLoadingScreen.isVisible = false
        bookListAnimationView.isVisible = false
        bookListAnimationView.pauseAnimation()
    }

    private fun deleteConfirmationAlertDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Androidly Alert")
        builder.setMessage("Do you want to delete this book?")


        builder.setPositiveButton(R.string.ok) { dialog, which ->
            showLoadingScreen()
            bookListAdapter.deleteBooks(position) {
                hideLoadingScreen()
                refreshBookData()
            }
        }

        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            bookListAdapter.notifyDataSetChanged()
        }
        builder.show()
    }
}