package com.softspace.ssbookstore.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softspace.ssbookstore.data_class.BookDetail
import com.softspace.ssbookstore.BookDetailsActivity
import com.softspace.ssbookstore.R
import com.softspace.ssbookstore.utility.Base64Util
import kotlinx.android.synthetic.main.item_layout.view.*

class BookListAdapter(
    private var bookList: MutableList<BookDetail>
) : RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {

    class BookListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        return BookListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        val currentBook = bookList[position]

        holder.itemView.apply {
            ivBookCover.setImageBitmap(Base64Util.convertString64ToImage(currentBook.image))
            tvBookTitle.text = currentBook.title
            val authorString = resources.getString(R.string.by_author_placeholder)
            tvBookAuthor.text = String.format(authorString, currentBook.author)
            tvBookDate.text = currentBook.date
        }

        holder.itemView.setOnClickListener { v ->
            Log.i("Book list adapter", "Item click $position")
            val intent = Intent(v.context, BookDetailsActivity::class.java)

            intent.putExtra("book_detail", bookList[position])
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    fun updateBooks(data: MutableList<BookDetail>) {
        bookList = data
        notifyDataSetChanged()
    }

    fun deleteBooks(position: Int, callback: (Boolean) -> Unit) {
        FirestoreAdapter.deleteSelectedBook(bookList[position].fireStoreId) { boolResult ->

            if (boolResult) {
                bookList.removeAt(position)
                notifyDataSetChanged()
            }

            callback(boolResult)
        }
    }
}