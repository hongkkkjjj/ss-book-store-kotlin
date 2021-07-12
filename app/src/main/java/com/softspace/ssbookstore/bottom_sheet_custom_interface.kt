package com.softspace.ssbookstore

import com.softspace.ssbookstore.enum_file.TakePhotoType

interface BottomSheetCustomInterface {
    fun callbackMethod(takePhotoType: TakePhotoType)
}