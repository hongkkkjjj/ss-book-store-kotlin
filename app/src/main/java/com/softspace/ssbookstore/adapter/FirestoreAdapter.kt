package com.softspace.ssbookstore.adapter

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.softspace.ssbookstore.data_class.BookDetail
import com.softspace.ssbookstore.data_class.RegisterUserObj
import com.softspace.ssbookstore.enum_file.SortType
import com.softspace.ssbookstore.utility.Util

object FirestoreAdapter {
    private val TAG = "Firestore Adapter"
    var bookDetails: MutableLiveData<MutableList<BookDetail>> = MutableLiveData()

    fun getBookListData(sortType: SortType) {
        val db = Firebase.firestore
        val bookList = mutableListOf<BookDetail>()
        db.collection("book")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "id is ${document.id}")

                    val title = document.data["title"] as? String ?: ""
                    val author = document.data["author"] as? String ?: ""
                    val desc = document.data["desc"] as? String ?: ""
                    val image = document.data["image"] as? String ?: ""
                    val date = document.data["date"] as? String ?: ""
                    val sortingDate = Util.convertStringToDate(date)
                    bookList.add(BookDetail(document.id, title, author, desc, image, date, sortingDate))
                }
                sortBookDetails(sortType, bookList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                bookDetails.postValue(mutableListOf())
            }
    }

    fun sortBookDetails(sortType: SortType, list: MutableList<BookDetail>?) {
        val bookList = list ?: bookDetails.value
        when (sortType) {
            SortType.nameAsc -> {
                bookList?.let { tempList ->
                    tempList.sortBy { it.title }
                    bookDetails.value = tempList
                }
            }
            SortType.nameDesc -> {
                bookList?.let { tempList ->
                    tempList.sortByDescending { it.title }
                    bookDetails.value = tempList
                }
            }
            SortType.authorAsc -> {
                bookList?.let { tempList ->
                    tempList.sortBy { it.author }
                    bookDetails.value = tempList
                }
            }
            SortType.authorDesc -> {
                bookList?.let { tempList ->
                    tempList.sortByDescending { it.author }
                    bookDetails.value = tempList
                }
            }
            SortType.dateAsc -> {
                bookList?.let { tempList ->
                    tempList.sortBy { it.sortingDate }
                    bookDetails.value = tempList
                }
            }
            SortType.dateDesc -> {
                bookList?.let { tempList ->
                    tempList.sortByDescending { it.sortingDate }
                    bookDetails.value = tempList
                }
            }
        }
    }

    fun loginWithFirestore(username: String, password: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val userRef = db.collection("user")
        val query = userRef.whereEqualTo("username", username)
        query.get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.documents.size == 1) {
                        val document = snapshot.documents[0]
                        if (document.data?.get("password") as? String == password) {
                            callback(true)
                        } else {
                            callback(false)
                        }
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    callback(false)
                }
    }

    fun checkUsernameExistence(username: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val userRef = db.collection("user")
        val query = userRef.whereEqualTo("username", username)
        query.get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.documents.size > 0) {
                        callback(false)
                    } else {
                        callback(true)
                    }
                }
                .addOnFailureListener {
                    callback(false)
                }
    }

    fun registerUserWithFirestore(username: String, password: String, callback: (RegisterUserObj) -> Unit) {
        val db = Firebase.firestore
        val data = hashMapOf(
                "username" to username,
                "password" to password
        )

        checkUsernameExistence(username) { usernameNotExist ->
            if (usernameNotExist) {
                db.collection("user").document(username)
                        .set(data)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully written!")
                            callback(RegisterUserObj(true, "Successfully registered"))
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                            callback(RegisterUserObj(false, "Registration failed"))
                        }
            } else {
                callback(RegisterUserObj(false, "Username is in used"))
            }
        }
    }

    fun deleteSelectedBook(bookId: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore
        db.collection("book").document(bookId)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                    callback(false)
                }
    }

    fun updateBook(bookDetail: BookDetail, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val book = hashMapOf(
                "author" to bookDetail.author,
                "date" to bookDetail.date,
                "desc" to bookDetail.desc,
                "image" to bookDetail.image,
                "title" to bookDetail.title
        )

        db.collection("book").document(bookDetail.fireStoreId)
                .set(book)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    callback(false)
                }
    }

    fun createBook(bookDetail: BookDetail, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val book = hashMapOf(
                "author" to bookDetail.author,
                "date" to bookDetail.date,
                "desc" to bookDetail.desc,
                "image" to bookDetail.image,
                "title" to bookDetail.title
        )

        db.collection("book")
                .add(book)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    callback(false)
                }
    }
}