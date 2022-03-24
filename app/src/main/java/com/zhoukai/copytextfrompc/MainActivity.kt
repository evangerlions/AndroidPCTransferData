package com.zhoukai.copytextfrompc

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TEMP_FILE_PATH = "temp_trans_file"
        private const val PERMISSION_REQUEST_CODE = 2296
        private const val TAG = "copyTextFromPC"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkReadPermissions()
    }

    private fun checkReadPermissions() {
        if (SDK_INT < Build.VERSION_CODES.R) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                copyTextFromFile()
            } else {
                val requestPermissionLauncher =
                    registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->
                        if (isGranted) {
                            copyTextFromFile()
                        }
                    }
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            copyTextFromFile()
            return
        }

        if (Environment.isExternalStorageManager()) {
            copyTextFromFile()
            return
        }

        val intent = Intent(
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
            Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        )
        startActivityForResult(intent, PERMISSION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode $requestCode ")
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    copyTextFromFile()
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun copyTextFromFile() {
        val path = "/storage/emulated/0/"
        val tempFilePath = path + File.separator + TEMP_FILE_PATH
        val file = File(tempFilePath)

        if (!file.exists()) {
            Toast.makeText(this, "$file not exist", Toast.LENGTH_LONG).show()
        } else {
            val copyStr = file.readText()
            if (copyStr.isEmpty()) {
                Toast.makeText(this, "copy text empty", Toast.LENGTH_LONG).show()
                return
            }
            copyToClipboard(copyStr)
        }
    }

    private fun copyToClipboard(copyStr: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("copyTextFromPC", copyStr))

        val toastLen = 20
        val toastStr = if (copyStr.length > toastLen) copyStr.take(toastLen) + "..." else copyStr
        Toast.makeText(this, "$toastStr copy success", Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).post {
            finish()
        }
    }
}
