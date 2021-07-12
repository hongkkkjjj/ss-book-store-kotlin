package com.softspace.ssbookstore.data_class

import java.io.Serializable
import java.time.LocalDate

data class BookDetail (
    var fireStoreId: String = "",
    var title: String = "",
    var author: String = "",
    var desc: String = "",
    var image: String = "",
    var date: String = "",
    var sortingDate: LocalDate = LocalDate.now()
) : Serializable