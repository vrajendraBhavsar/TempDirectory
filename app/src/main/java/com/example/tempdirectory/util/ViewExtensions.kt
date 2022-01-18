package com.example.tempdirectory.util

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

//object ViewExtensions {
//    private fun snackBar(text: String){
//        val snackBar = view?.let {
//            Snackbar.make(
//                it, "Replace with your own action",
//                Snackbar.LENGTH_LONG
//            ).setAction("Action", null)
//        }
//        snackBar?.setActionTextColor(requireContext().getColor(R.color.black))
//        val snackBarView = snackBar?.view
//        snackBarView?.setBackgroundColor(requireContext().getColor(R.color.rich_red))
//        snackBar?.show()
//    }

    fun View.snackBar(message: String, duration: Int = BaseTransientBottomBar.LENGTH_SHORT) {
        Snackbar.make(this, message, duration).show()
    }
//}