package com.example.tempdirectory

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.tempdirectory.databinding.FragmentFirstBinding
import com.example.tempdirectory.util.snackBar
import com.example.tempdirectory.worker.DeleteFolderWorker
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class FirstFragment : Fragment(), LifecycleObserver {

    private lateinit var file: File
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val PERMISSION_EXTERNAL_STORAGE_REQ_CODE = 7
    private var folderName: String = ""

    companion object {
        private var sessionDepth = 0
    }


    //To handle permission granted, not granted, denied..
    //object that let us launch it every time we ask for the permission and we pass as parameter req the proper contract to ask it
    private val externalStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            with(binding.root) {

                when {
                    granted -> {
                        snackBar("Permission granted!")
                        if (folderName != "") {
                            createFolder(folderName)
                        } else {
                            snackBar("Enter folder name first")
                        }
                    }
                    else -> {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            //this option is available starting in API 23
                            snackBar("Permission denied, show more info!")
                            gotoSettings()
                        } else {
                            snackBar("Permission denied")
                        }
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        * Create Folder
        * */
        binding.btnCreate.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            //get folder name from et
            folderName = binding.tipFolderName.editText?.text.toString().trim()
            //check if WRITE permission is granted
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Permission is granted!! .. Create Folder
                if (folderName != "") {
                    createFolder(folderName)
                } else {
                    Toast.makeText(requireContext(), "Enter folder name first", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                //Todo: if permissions Denied permanently by user..
                askExternalStoragePermission()
            }
        }

        binding.btnOpen.setOnClickListener {
            //get folder name from et
//            folderName = binding.tipFolderName.editText?.text.toString().trim()
//            //Initialize uri
//            val uri = Uri.parse("${context?.getExternalFilesDir(null)}/$folderName/")
//            //open file manager
//            startActivity(
//                Intent(Intent.ACTION_VIEW)
//                    .setDataAndType(uri, "*/*")
//            )
//.................
//            file = File("${requireContext().getExternalFilesDir(null)}/$folderName")
//
////            val path = Uri.fromFile(file)
//            val path = FileProvider.getUriForFile(requireContext(),"com.example.tempdirectory.fileprovider",file);
//
//            val pdfOpenintent = Intent(Intent.ACTION_VIEW)
//            pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
////            pdfOpenintent.setDataAndType(path, "application/pdf")
//            pdfOpenintent.setDataAndType(path, "*/*")
//            try {
//                startActivity(pdfOpenintent)
//            } catch (e: ActivityNotFoundException) {
//            }
//..................
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//                //if you want to open PDF file
//                type = "*/*"
//                addCategory(Intent.CATEGORY_OPENABLE)
//                //Adding Read URI permission
//                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
//            }
            //................
            val path = "${context?.getExternalFilesDir(null)}/$folderName/"
            //initialize uri
//            val uri = Uri.parse("${context?.getExternalFilesDir(null)}/$folderName/")
            val uri: Uri = Uri.parse(path)
            //initialize intent
            var intent = Intent(Intent.ACTION_PICK)
                .setDataAndType(uri, "*/*")
            //start activity
            startActivity(intent)

        }
        /*binding.btnDelete.setOnClickListener {
            deleteFolder()
        }*/
    }

    private fun createFolder(folderName: String): File {

        chooseFileFromDevice()

        //Initialize file
        file = File("${requireContext().getExternalFilesDir(null)}/$folderName")
        if (!file.exists()) {
            //If file hasn't created yet,
            file.mkdir()
            binding.root.snackBar("Created new directory: $folderName")
        } else if (file.exists()) {
            binding.root.snackBar("File with same name already exist")
        }
        return file
    }

    //....... to receive file path which user open during "CreateFolder"
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // There are no request codes
////            val data: Intent? = result.data
//            val uriOfSelectedFile: Uri? = result.data?.data
//                val uriPath = uriOfSelectedFile?.path
//
////                val getPath = uriOfSelectedFile?.let { getPath(it) }
//
//                //....
//            val intent = Intent(Intent.ACTION_PICK)
//                .setDataAndType(uriOfSelectedFile, "*/*")
//            //start activity
//            startActivity(intent)
//                //..
//
//                Log.d("VRAJTEST", "197: Uri of User Selected file:: $uriOfSelectedFile")
//                Log.d("VRAJTEST", "198: Path of User Selected file:: $uriPath")
////                Log.d("VRAJTEST", "198: getPath fun result:: $getPath")
//            }
            //....from scoped storage project
            //Permission needed if you want to retain access even after reboot
            result?.data?.data?.let { documentUri ->
                context?.contentResolver?.takePersistableUriPermission(
                    documentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Toast.makeText(requireContext(), documentUri.path.toString(), Toast.LENGTH_LONG).show()
            }
        }

/*    private fun getPath(uri: Uri): String? {

        var path: String  = "";
        val projection: Array<String> = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor: Cursor? = context?.contentResolver?.query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.path!!
        }
        else{
            cursor.moveToFirst();
            val column_index: Int = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        if(path == null || path.isEmpty()) {
            return uri.path
        } else {
            return path
        }
//        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }*/

//    private fun getRealPathFromURI(contentUri: Uri?): String? {
//        var cursor: Cursor? = null
//        return try {
//            val proj = arrayOf(MediaStore.Images.Media.DATA)
//            cursor = contentUri?.let { context?.contentResolver?.query(it, proj, null, null, null) }
//            val columnIndex: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            cursor?.moveToFirst()
//            columnIndex?.let { cursor?.getString(it) }
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//    }

    fun chooseFileFromDevice() {
//        var chooseFileIntent = Intent(Intent.ACTION_GET_CONTENT)
//        chooseFileIntent.type = "*/*"
//        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file")
////        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)
//
        //....from scoped storage project

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            //if you want to open PDF file
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            //Adding Read URI permission
            flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }


        resultLauncher.launch(intent)
    }

    //...........
    fun deleteFolder() {
        //get folder name from et
        folderName = binding.tipFolderName.editText?.text.toString().trim()

        //Initialize file
        if (folderName != "") {
            file = File("${requireContext().getExternalFilesDir(null)}/$folderName")

//            deleteRecursive(file)

            if (file.exists()) {
                //Work request
                val deleteRequest = OneTimeWorkRequest.Builder(DeleteFolderWorker::class.java)
                //After 10 minutes .. Worker start its work
                deleteRequest.setInitialDelay(10, TimeUnit.MINUTES)          //3 SECONDS ...........

                val data = Data.Builder()
                //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
                data.putString("folder_name", folderName)
                //Set Input Data into the Request
                deleteRequest.setInputData(data.build())
                //enqueue worker - Passed work request to the manager
                WorkManager.getInstance().enqueue(deleteRequest.build())

                Toast.makeText(
                    requireContext(),
                    "Folder \"$folderName\" will get vanished after 10 minutes!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("VRAJTEST", "deleteFolder: deleting .. $folderName")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Folder $folderName doesn't exist",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(requireContext(), "Enter folder name first", Toast.LENGTH_SHORT).show()
        }
    }

/*    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles()) deleteRecursive(child)
        fileOrDirectory.delete()
        Log.d("VRAJTEST", "deleteRecursive: ${fileOrDirectory.name}, Directory is being deleted")
    }*/

    private fun askExternalStoragePermission() {
        //launch the permission
        externalStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun gotoSettings() {
        AlertDialog.Builder(requireContext())
            .setTitle("Go to Settings")
            .setMessage("enable the permission")
            .setNegativeButton("cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                Toast.makeText(requireContext(), "Moving to Settings", Toast.LENGTH_SHORT).show()
                try {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } catch (e: Exception) {
                    binding.root.snackBar("failed to open Settings")
                    Log.d("error", e.toString())
                }
            }).create().show()
    }

    @Throws(IOException::class)
    private fun exportFile(src: File, dst: File): File? {
        //if folder does not exist
        if (!dst.exists()) {
            if (!dst.mkdir()) {
                return null
            }
        }
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val expFile = File(dst.path + File.separator + "IMG_" + timeStamp + ".jpg")
        var inChannel: FileChannel? = null
        var outChannel: FileChannel? = null
        try {
            inChannel = FileInputStream(src).channel
            outChannel = FileOutputStream(expFile).channel
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            inChannel?.transferTo(0, inChannel.size(), outChannel)
        } finally {
            if (inChannel != null) inChannel.close()
            if (outChannel != null) outChannel.close()
        }
        return expFile
    }

    override fun onStart() {
        super.onStart()
        sessionDepth++
        if (sessionDepth == 1) {
            //app came to foreground;
        }
    }

    override fun onStop() {
        super.onStop()
        if (sessionDepth > 0)
            sessionDepth--
        if (sessionDepth == 0) {
            // app went to background
            deleteFolder()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}