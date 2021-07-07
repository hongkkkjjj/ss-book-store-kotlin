package com.softspace.ssbookstore

data class BookDetail(
    var fireStoreId: String,
    var title: String,
    var author: String,
    var desc: String,
    var image: String,
    var date: String
)