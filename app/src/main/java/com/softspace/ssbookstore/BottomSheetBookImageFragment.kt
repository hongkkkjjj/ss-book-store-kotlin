package com.softspace.ssbookstore

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.softspace.ssbookstore.enum_file.TakePhotoType

class BottomSheetBookImageFragment(callback: BottomSheetCustomInterface) : BottomSheetDialogFragment() {

    private var interfaceCallback: BottomSheetCustomInterface = callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_book_image_fragment, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCamera = view.findViewById<Button>(R.id.btnCamera)
        val btnGallery = view.findViewById<Button>(R.id.btnGallery)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        btnCamera.setOnClickListener {
            interfaceCallback.callbackMethod(TakePhotoType.camera)
            dismiss()
        }

        btnGallery.setOnClickListener {
            interfaceCallback.callbackMethod(TakePhotoType.gallery)
            dismiss()
        }

        btnCancel.setOnClickListener {
            interfaceCallback.callbackMethod(TakePhotoType.none)
            dismiss()
        }
    }
}