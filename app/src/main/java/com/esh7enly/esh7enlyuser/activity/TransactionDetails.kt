package com.esh7enly.esh7enlyuser.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.TransactionDetailsEntity
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityTransactionDetailsBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Screenshot
import com.esh7enly.esh7enlyuser.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL


private const val TAG = "TransactionDetails"

@AndroidEntryPoint
class TransactionDetails : BaseActivity() {
    private val ui by lazy {
        ActivityTransactionDetailsBinding.inflate(
            layoutInflater
        )
    }


    lateinit var bmp: Bitmap

    private var scaledbmp: Bitmap? = null

    private var type: Int? = null

    //  private val serviceViewModel: ServiceViewModel by viewModels()
    private var transactionId = ""

    private lateinit var view: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        view = window.decorView.rootView

        getIntentData()

        loadData()

        // ui.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed()}

        ui.chatWhats.setOnClickListener {
            shareImage()
        }

//        ui.generatePdf.setOnClickListener {
//            bmp = loadBitmap(view, view.width, view.height) as Bitmap
//         //   pdf()
//            testPermission()
//        }

    }

    private fun loadBitmap(linear: View, width: Int, height: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        linear.draw(canvas)
        return bitmap
    }

//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun pdf() {
//        takePermission()
//    }

//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun takePermission() {
//        // for sdk 23 to 29
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            val resolver = contentResolver
////            val contentValues = ContentValues()
////            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"image.jpg")
////            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
////            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES
////            + File.separator + "TestEshenly")
////            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
//
//
//            Toast.makeText(this, "Here second", Toast.LENGTH_SHORT).show()
//
//
//            savePdf()
//
////            if(!Environment.isExternalStorageManager())
////            {
////                Toast.makeText(this, "Here forth", Toast.LENGTH_SHORT).show()
////
////                try {
////                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
////                    intent.addCategory("android.intent.category.DEFAULT")
////                    intent.data = Uri.parse(String.format("package:%s",applicationContext.packageName))
////                    startActivityIfNeeded(intent,100)
////                }
////                catch (e: Exception)
////                {
////                    val intent = Intent()
////                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
////                    startActivityIfNeeded(intent,100)
////                }
////            }
////            else
////            {
////                Toast.makeText(this, "Here fifth", Toast.LENGTH_SHORT).show()
////
////                savePdf()
////            }
//        } else {
//            Toast.makeText(this, "Here first", Toast.LENGTH_SHORT).show()
//
//            if (ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                == PackageManager.PERMISSION_DENIED
//            ) {
//                val permission = arrayOf(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                ActivityCompat.requestPermissions(
//                    this,
//                    permission,
//                    Constants.REQUEST_CODE__PERMISSION_CODE
//                )
//            }
//        }
//    }

//    private fun testPermission()
//    {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//        {
//            Toast.makeText(this, "Here 11", Toast.LENGTH_SHORT).show()
//
//            if (ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                == PackageManager.PERMISSION_DENIED
//            ) {
//                val permission = arrayOf(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                ActivityCompat.requestPermissions(
//                    this,
//                    permission,
//                    Constants.REQUEST_CODE__PERMISSION_CODE
//                )
//            }
//            else
//            {
//                Toast.makeText(this, "Already accepted", Toast.LENGTH_SHORT).show()
//
//            }
//        }
//        else
//        {
//            Toast.makeText(this, "Here 10", Toast.LENGTH_SHORT).show()
//
//            if (ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                == PackageManager.PERMISSION_DENIED
//            ) {
//                val permission = arrayOf(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                ActivityCompat.requestPermissions(
//                    this,
//                    permission,
//                    Constants.REQUEST_CODE__PERMISSION_CODE
//                )
//            }
//        }
//    }
//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    private fun savePdf() {
//
//        //   val windowManager = getSystemService(Context.WINDOW_SERVICE)
//        val displayMetrics = DisplayMetrics()
//        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val width = displayMetrics.widthPixels
//        val height = displayMetrics.heightPixels
//        val convertWidth = width
//        val convertHeight = height
//
//        val pdfDocument = PdfDocument()
//        val pageInfo = PdfDocument.PageInfo.Builder(
//            convertWidth,
//            convertHeight, 1
//        ).create()
//        val page = pdfDocument.startPage(pageInfo)
//        val canvas = page.canvas
//        val paint = Paint()
//        canvas.drawPaint(paint)
//        val bitmap = Bitmap.createScaledBitmap(bmp, convertWidth, convertHeight, true)
//        canvas.drawBitmap(bitmap, 0f, 0f, null)
//        pdfDocument.finishPage(page)
//
//        // val mPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/Ticket_" + date + ".jpeg"
//        val mPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()
//
//        val targetPdf =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//
//        val file = File(targetPdf, "esh7enly transaction.pdf")
//
//        Log.d(TAG, "diaa pdf: $file")
//
//        try {
//
//            pdfDocument.writeTo(FileOutputStream(file))
//            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Toast.makeText(this, resources.getString(R.string.need_permission), Toast.LENGTH_SHORT)
//                .show()
//            Log.d(TAG, "diaa pdf error: ${e.message}")
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    private fun saveFileUsingMediaStore(context: Context, url: String, fileName: String) {
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//            //  put(MediaStore.MediaColumns.MIME_TYPE, " application/pdf")
//            put(MediaStore.MediaColumns.MIME_TYPE, " text/plain")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
//        }
//        val resolver = context.contentResolver
//        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
//        if (uri != null) {
//            URL(url).openStream().use { input ->
//                resolver.openOutputStream(uri).use { output ->
//                    input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
//                }
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == Constants.REQUEST_CODE__PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                savePdf()
//            } else {
//
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun shareImage() {
        // Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        val screenShootPath = Screenshot.takeScreenshot(this, view)
        Screenshot.share(this, screenShootPath)
    }


    private fun loadData() {
        transactionsViewModel.getTransactionDetails(
            sharedHelper?.getUserToken().toString(),
            transactionId
        )
        lifecycleScope.launch {
            transactionsViewModel.transactionDetails.collect {
                replaceData(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun replaceData(transaction: TransactionDetailsEntity?) {
        ui.tvCreatedAt.text = transaction?.data?.createdAt

        ui.tvTransactionNum.text = transaction?.data?.id.toString()
        ui.tvServiceName.text = transaction?.data?.service?.name + " : " +
                transaction?.data?.service?.provider?.name


        ui.tvProviderName.text = transaction?.data?.service?.provider?.name
        ui.tvAmount.text =
            Utils.format(transaction?.data?.amount?.toDouble()) + resources.getString(R.string.egp)
        ui.tvAmountHead.text =
            Utils.format(transaction?.data?.totalAmount?.toDouble()) + resources.getString(R.string.egp)
        ui.tvTotalAmount.text =
            Utils.format(transaction?.data?.totalAmount?.toDouble()) + resources.getString(R.string.egp)
        ui.tvBalanceBefore.text =
            Utils.format(transaction?.data?.balanceBefore?.toDouble()) + resources.getString(R.string.egp)
        ui.tvBalanceAfter.text =
            Utils.format(transaction?.data?.balanceAfter?.toDouble()) + resources.getString(R.string.egp)
        ui.tvCommision.text =
            Utils.format(transaction?.data?.merchantCommission?.toDouble()) + resources.getString(R.string.egp)


        if (transaction?.data?.isSettled === 0) {
            ui.lytCommision.visibility = View.GONE
        }

        if (transaction?.data?.clientNumber != null) {
            ui.lytClientNumber.visibility = View.VISIBLE
            ui.tvClientNumber.text = transaction.data.clientNumber
        }

        // Show prepaid card details
        if (transaction?.data?.service?.type == Constants.PREPAID_CARD) {
            if (transaction.data?.description != null && transaction.data?.description != "") {
                val lines =
                    transaction.data?.description?.split("\n")
                            as ArrayList<String>

                Log.d(TAG, "diaa lines one: $lines")
                for (oneLine in lines) {
                    if (oneLine.contains("PIN")) {
                        ui.lytPin.visibility = View.VISIBLE
                        ui.lytSerial.visibility = View.VISIBLE
                    } else {
                        val stringBuilder = StringBuilder()

                        for (i in lines.indices) {
                            val key = lines[i]
                            stringBuilder.append("• ")
                            stringBuilder.append(key)
                            if (i != lines.size - 1)
                                stringBuilder.append("\n")
                            ui.lytPin.visibility = View.VISIBLE
                            ui.tvPinTitle.visibility = View.GONE
                            ui.tvPin.text = stringBuilder.toString()
                        }
                    }
                }
            }
        }

        if (transaction?.data?.description != null && transaction.data?.description != "") {
            val likes: List<String>? = transaction.data?.description?.split("\n")

            likes?.let {
                for (oneLine in it) {
                    if (oneLine.contains("Serial")) {
                        ui.tvSerial.text = oneLine.trim().toLowerCase().replace("serial:", "")
                    } else if (oneLine.contains("PIN")) {
                        ui.tvPin.setText(oneLine.trim().toLowerCase().replace("pin:", ""))
                    }
                }
            }

        }

        type = transaction?.data?.status

        when (type) {
            Constants.DONE -> ui.tvTransactionStatus.text =
                resources.getString(R.string.print_succes)

            Constants.FAIL -> {
                ui.tvTransactionStatus.text = resources.getString(R.string.print_failed)
                ui.lytErrorMsg.visibility = View.VISIBLE
                ui.tvErrorMessage.text = transaction?.data?.message
                ui.lytBalanceAfter.visibility = View.GONE
                ui.lytBalanceBefore.visibility = View.GONE
                ui.lytCommision.visibility = View.GONE
            }

            Constants.PENDING -> ui.tvTransactionStatus.text =
                resources.getString(R.string.print_pending)
        }

    }

    private fun getIntentData() {
        serviceViewModel.transactionId =
            intent.getStringExtra(Constants.TRASACTION_ID).toString()
        serviceViewModel.transactionType = intent.getIntExtra(Constants.SERVICE_TYPE, 0)
        transactionId = intent.getStringExtra(Constants.TRASACTION_ID).toString()
    }
}