package com.example.tempdirectory.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.io.File

//Code to delete file will be written here
object WorkerUtils {
    fun deleteFolder(context: Context, folderName: String) {
        //Initialize uri
        val uri = Uri.parse("${context.getExternalFilesDir(null)}/$folderName/")
        //Initialize file
        val file: File = File(uri.path)

        deleteRecursive(file)

//        file.delete()
//        if(file.exists()){
//            file.canonicalFile.delete()
//            Log.d("VRAJTEST", "$folderName .. File got deleted!")
//            if(file.exists()){
//                context.deleteFile(file.name)
//                Log.d("VRAJTEST", "$folderName .. File got deleted!")
//            }
//            Log.d("VRAJTEST", "$folderName .. File got deleted!")
//        } else {
//            Log.d("VRAJTEST", "$folderName .. File got deleted!")
//        }
    }

    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles()) deleteRecursive(child)
        fileOrDirectory.delete()
        Log.d("VRAJTEST", "deleteRecursive: WorkUtils:: ${fileOrDirectory.name}, Directory is being deleted")

    }
}