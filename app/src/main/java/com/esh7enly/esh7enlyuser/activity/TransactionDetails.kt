package com.esh7enly.esh7enlyuser.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.TransactionDetailsEntity
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityTransactionDetailsBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.Screenshot
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

import java.util.Locale


private const val TAG = "TransactionDetails"

@AndroidEntryPoint
class TransactionDetails : BaseActivity()
{

    private val ui by lazy {
        ActivityTransactionDetailsBinding.inflate(
            layoutInflater
        )
    }

    private var type: Int? = null

    private var transactionId = ""

    private lateinit var view: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.serviceText.text = resources.getString(R.string.trans_details_service_title)
        ui.comissionText.text = resources.getString(R.string.trans_details_commission_title)
        ui.messageErrorText.text = resources.getString(R.string.trans_details_message_error_title)
        ui.tvProviderName.text = resources.getString(R.string.trans_details_provider_title)
        ui.createdAtText.text = resources.getString(R.string.trans_details_created_at_title)
        ui.amountText.text = resources.getString(R.string.trans_details_amount_title_new)
        ui.serialText.text = resources.getString(R.string.trans_details_serial_title)
        ui.clientNumberText.text = resources.getString(R.string.trans_details_client_number_title)
        ui.balanceAfterText.text = resources.getString(R.string.trans_details_balance_after_titlea)
        ui.balanceBeforeText.text =
            resources.getString(R.string.trans_details_balance_before_titlea)
        ui.totalAmountText.text = resources.getString(R.string.trans_details_total_amount_title_new)
        ui.transactionNumberText.text =
            resources.getString(R.string.trans_details_transaction_number_title)

        view = window.decorView.rootView

        getIntentData()

        loadData()


        ui.chatWhats.setOnClickListener {
            shareImage()
        }

        ui.generatePdf.setOnClickListener {
            lifecycleScope.launch {
                transactionsViewModel.transactionDetails.collect {
                    generateReceiptPDF(it)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun generateReceiptPDF(transaction: TransactionDetailsEntity?)
    {
        generatePdf(transaction)
//
//        if (hasPermissions()) {
//            generatePdf(transaction)
//        } else {
//            checkPermissions()
//        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }


    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun generatePdf(transaction: TransactionDetailsEntity?)
    {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(
            600, 842, 1
        ).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        paint.textSize = 18f

        drawData(canvas, paint, document, transaction, page)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun drawData(
        canvas: Canvas,
        paint: Paint,
        document: PdfDocument,
        transaction: TransactionDetailsEntity?,
        page: PdfDocument.Page
    ) {
        // Load the image from resources
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.new_logo_trans)
        val `scaled-bmp` = Bitmap.createScaledBitmap(bitmap, 250, 200, false)

        // Draw the image
        val left = 80f
        val top = 100f
        val right = left + bitmap.width
        val bottom = top + bitmap.height

        canvas.drawBitmap(`scaled-bmp`, 200f, 0f, paint)

        drawText(
            canvas,
            "الخدمة: ${transaction!!.data?.service!!.name} ${transaction.data.service.provider.name}",
            200f
        )
        drawText(canvas, "رقم العملية: ${transaction.data?.id.toString()}", 220f)
        drawText(
            canvas,
            "المبلغ: " + Utils.format(transaction.data?.amount?.toDouble()) + resources.getString(R.string.egp),
            240f
        )
        drawText(
            canvas,
            "المبلغ الإجمالى: " + Utils.format(transaction.data?.totalAmount?.toDouble()) + resources.getString(
                R.string.egp
            ),
            260f
        )
        drawText(canvas, "الوقت : ${transaction.data?.createdAt.toString()}", 280f)

        transaction.data.clientNumber.let {
            drawText(canvas, "رقم العميل : $it", 300f)

        }

        transaction.data.description.let {

            try {
                var dimen = 320f

                val lines =
                    transaction.data?.description?.split("\n")
                            as ArrayList<String>

                for (oneLine in lines) {
                    if (oneLine.contains(
                            "card_data"
                        )
                        || oneLine.contains("CardMetadata")
                    ) {
                        continue
                    }

                    drawText(canvas, oneLine, dimen)
                    dimen += 20f

                }
            } catch (e: Exception) {
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "Print description in PDF"
                )

                Log.d(TAG, "diaa generatePdf exception: ${e.message}")
            }
        }

        transaction.data?.qr_code?.let { qrCode ->
            var finalQr = qrCode

            if (finalQr == "") {
                finalQr = "Esh7enly test"
            }

            val qrgEncoder = QRGEncoder(
                finalQr,
                null, QRGContents.Type.TEXT, 200
            )

            val bitmapQr: Bitmap = qrgEncoder.getBitmap(0)

            val stream = ByteArrayOutputStream()
            bitmapQr.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            canvas.drawBitmap(bitmapQr, 200f, 410f, paint)
        }

        drawText(canvas, getString(R.string.when_issue_occur), 430f)
        drawText(canvas, getString(R.string._24), 450f)
        drawText(canvas, getString(R.string.call_center), 470f)
        drawText(canvas, getString(R.string.client_receipt), 490f)
        drawText(canvas, getString(R.string.thnx_using_esh7enly), 510f)

        // Finish the page
        document.finishPage(page)

        savePdfToMediaStore(document, "${System.currentTimeMillis()}.pdf")

        // Create the output file
//        val targetPdf = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DOWNLOADS
//        )
//
//        val file = File(targetPdf, "esh7enly transaction.pdf")
//
//        try {
//            if (file.exists()) {
//                file.delete()
//                document.writeTo(FileOutputStream(file))
//            } else {
//                document.writeTo(FileOutputStream(file))
//
//            }
//            Log.d(TAG, "diaa generatePdf: done ")
//        } catch (e: IOException) {
//            sendIssueToCrashlytics(
//                e.message.toString(),
//                "Error in save pdf file"
//            )
//
//            Log.d(TAG, "diaa generatePdf: error ${e.message}")
//        }


        // Close the document
        document.close()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun savePdfToMediaStore(document: PdfDocument, fileName: String) {
      try{
          val values = ContentValues().apply {
              put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
              put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
              put(MediaStore.MediaColumns.RELATIVE_PATH,
                  Environment.DIRECTORY_DOWNLOADS + File.separator + "Esh7enly all transactions"
              )
          }

          val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
          uri?.let {
              contentResolver.openOutputStream(it)?.use { outputStream ->
                  document.writeTo(outputStream)
              }
          }

          Toast.makeText(this,"Saved to mobile",Toast.LENGTH_SHORT).show()

      }
      catch (e: Exception)
      {
          Log.d(TAG, "diaa testPdf exception: ${e.message}")
          Toast.makeText(this,"Error while saving receipt",
              Toast.LENGTH_SHORT).show()

          sendIssueToCrashlytics(
              key = "Save receipt",
              provider = "Save receipt",
              msg = e.message.toString(),
              functionName = "Save receipt pdf TransactionDetails"
          )
      }

    }


    private fun drawText(canvas: Canvas, data: String, dimenH: Float) {
        val paint = Paint()
        paint.textSize = 18f

        canvas.drawText(data, 200f, dimenH, paint)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun shareImage() {
        val screenShootPath = Screenshot.takeScreenshot(this, view)
        try {
            Screenshot.share(this, screenShootPath)

        } catch (e: Exception) {
            sendIssueToCrashlytics(
                e.message.toString(),
                "Share receipt image from TransactionDetails"
            )

            Toast.makeText(this, "Error with share ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadData() {
        transactionsViewModel.getTransactionDetails(transactionId)
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


        if (transaction?.data?.isSettled == 0) {
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
                        ui.tvSerial.text = oneLine.trim().lowercase(Locale.ROOT)
                            .replace("serial:", "")
                    } else if (oneLine.contains("PIN")) {
                        ui.tvPin.setText(oneLine.trim().lowercase(Locale.ROOT).replace("pin:", ""))
                    }
                }
            }

        }

        type = transaction?.data?.status

        when (type) {
            Constants.DONE -> ui.tvTransactionStatus.text =
                resources.getString(R.string.print_succes)

            Constants.FAIL -> {
                ui.generatePdf.visibility = View.GONE
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