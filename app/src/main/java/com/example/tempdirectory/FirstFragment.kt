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
import com.example.tempdirectory.databinding.FragmentFirstBinding
import com.example.tempdirectory.util.snackBar
import java.io.File


class FirstFragment : Fragment() {

    private lateinit var file: File
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val PERMISSION_EXTERNAL_STORAGE_REQ_CODE = 7
    private var folderName: String = ""

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

        /*
        * Open Folder
        * */
        binding.btnOpen.setOnClickListener {
            //get folder name from et
            folderName = binding.tipFolderName.editText?.text.toString().trim()
            //Initialize uri
            val uri = Uri.parse("${context?.getExternalFilesDir(null)}/$folderName/")
            //open file manager
            startActivity(
                Intent(Intent.ACTION_GET_CONTENT)
                    .setDataAndType(uri, "*/*")
            )
        }
    }

    private fun createFolder(folderName: String): File {
        /*
        * old approach - which isn't working
        * */
/*        //Initialize file
        val file = File(Environment.getExternalStorageDirectory(), folderName)

        if (!file.exists()) {
            //create new directory
            file.mkdir()
            //check condition
            if (file.isDirectory) {
                //when directory is created
                Toast.makeText(requireContext(), "Successfully created: $folderName", Toast.LENGTH_SHORT).show()
            } else {
                //when directory is not created..display Alert dialog
                AlertDialog.Builder(requireContext())
                    .setTitle("Go to Settings")
                    .setMessage("Message: failed to create directory " +
                            "\nPath : ${Environment.getExternalStorageDirectory()}" +
                            "\nmkdirs : ${file.mkdirs()}")
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "Folder Already Exists", Toast.LENGTH_SHORT).show()
        }*/

        /*
        * New approach - Scoped Storage
        * */

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}