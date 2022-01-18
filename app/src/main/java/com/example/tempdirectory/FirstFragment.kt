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


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val PERMISSION_EXTERNAL_STORAGE_REQ_CODE = 7
    private var folderName: String = ""

    //To handle permission granted, not granted, denied..
    //object that let us launch it every time we ask for the permission and we pass as parameter req the proper contract to ask it
    private val externalStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            with(binding.root) {
//                when {
//                    granted -> snackBar("Permission granted!")
//                    //handle when the user denies it the first time
//                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
//                        //this option is available starting in API 23
//                        snackBar("Permission denied, show more info!")
////                        gotoSettings()
//                    }

                /*
                * Activity.shouldShowRequestPermissionRationale(String) method.
                * This method returns true if the app has requested this permission previously and the user denied the request.
                * */
//                    if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//                        snackBar("Permission granted!")
//                    } else if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                        //Permission hasn't granted :( -> Ask for it..
//                        snackBar("Permission denied, show more info!")
//                        askExternalStoragePermission()
//                    }
//                    else {
//                        snackBar("Permission denied")
////                        gotoSettings()
//                    }
//                }

                when {
                    granted -> {
                        snackBar("Permission granted!")

                    }


//                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
//                        //this option is available starting in API 23
//                        snackBar("Permission denied, show more info!")
//                        gotoSettings()
//                    }
                    else -> {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
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
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Permission is granted!! .. Create Folder
                createFolder()
            } else {
                //Todo: if permissions Denied permanently by user..
                askExternalStoragePermission()
            }
        }

        /*
        * Open Folder
        * */
        binding.btnOpen.setOnClickListener {

        }
    }

    private fun createFolder() {
        //Initialize file
//        var file: File = File(Environment.getExternal(), folderName)
    }

    private fun askExternalStoragePermission() {
//        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_EXTERNAL_STORAGE_REQ_CODE)

        //launch the permission
        externalStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

//    private fun snackBar(text: String) {
//        val snackBar = view?.let {
//            Snackbar.make(
//                it, text,
//                Snackbar.LENGTH_LONG
//            ).setAction("Action", null)
//        }
//        snackBar?.setActionTextColor(requireContext().getColor(R.color.black))
//        val snackBarView = snackBar?.view
//        snackBarView?.setBackgroundColor(requireContext().getColor(R.color.rich_red))
//        snackBar?.show()
//    }

//    private fun openSettings() {
//        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//            addCategory(Intent.CATEGORY_DEFAULT)
//            data = Uri.parse("package:$`package`")
//        }.run(::startActivity)
//    }

    private fun gotoSettings() {
        AlertDialog.Builder(requireContext())
            .setTitle("Go to Settings")
            .setMessage("enable the permission")
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
                    Toast.makeText(
                        requireContext(),
                        "failed to open Settings\n$e",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("error", e.toString())
                }
            }).create().show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}