package com.example.tempdirectory.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tempdirectory.util.WorkerUtils

class DeleteFolderWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val TAG = DeleteFolderWorker::class.java.simpleName

    override fun doWork(): Result {
        return try {
            Log.d("VRAJTEST", "doWork Called")

            //We will pass/set folder name when making OneTimeWorkRequest
            //get Input Data back using "inputData" variable
            val folderName: String? = inputData.getString("folder_name")

            folderName?.let { folderName ->
//                WorkerUtils.deleteFolder(context = context, folderName = folderName)
                WorkerUtils.deleteFolder(context = context, folderName = folderName)
                Log.d("VRAJTEST", "doWork: DeleteFolderWorker :: called")
            }
            Result.success()
        } catch (t: Throwable) {
            Log.d("VRAJTEST", "doWork: catch block Throwable:: ${t.localizedMessage}")
            Result.failure()
        }
    }
}