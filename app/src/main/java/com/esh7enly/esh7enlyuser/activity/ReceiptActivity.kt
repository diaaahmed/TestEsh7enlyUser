package com.esh7enly.esh7enlyuser.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.esh7enly.domain.entity.PaymentEntity
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityReceiptBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Screenshot
import com.esh7enly.esh7enlyuser.util.Utils
import java.io.File
import java.io.FileOutputStream

private const val TAG = "ReceiptActivity"

class ReceiptActivity : AppCompatActivity()
{
    private lateinit var view:View
    private var ii = 1

    companion object{

        private var DATA_ENTITY: PaymentEntity.DataEntity? = null
        private var PAYMENTPOJOMODEL: PaymentPojoModel? = null
        private var SERVICE_TYPE = 0
        private var BULK_NUMBER = 0
        private var IS_BULK = false

        fun getIntent(activity:Activity,isBulk:Boolean,bulkNumber:Int,
                      data:PaymentEntity.DataEntity,paymentPojoModel: PaymentPojoModel,
                      serviceType:Int)
        {
            val intent = Intent(activity,ReceiptActivity::class.java)
            this.DATA_ENTITY = data
            this.SERVICE_TYPE = serviceType
            this.PAYMENTPOJOMODEL = paymentPojoModel
            this.BULK_NUMBER = bulkNumber
            this.IS_BULK = isBulk

            activity.startActivity(intent)
        }

        fun getIntent(activity:Activity,data:PaymentEntity.DataEntity,serviceType:Int)
        {
            val intent = Intent(activity,ReceiptActivity::class.java)
            this.DATA_ENTITY = data
            this.SERVICE_TYPE = serviceType
            this.BULK_NUMBER = 1
            this.IS_BULK = false

            activity.startActivity(intent)
        }
    }

    lateinit var bmp:Bitmap

    private val ui by lazy{
        ActivityReceiptBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        view = window.decorView.rootView

//        ui.backBtn.setOnClickListener {
//            finish()
//        }

        ui.chatWhats.setOnClickListener {
            shareImage() }

        ui.generatePdf.setOnClickListener{
            bmp = loadBitmap(view,view.width,view.height) as Bitmap
            pdf()
        }

        initData()

    }

    private fun loadBitmap(linear: View, width: Int, height: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        linear.draw(canvas)
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun pdf() {
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val convertWidth = width
        val convertHeight = height

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(
            convertWidth,
            convertHeight, 1
        ).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        val bitmap = Bitmap.createScaledBitmap(bmp, convertWidth, convertHeight, true)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        //val targetPdf = "sdcard/page.pdf"
        val targetPdf = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(targetPdf, "page.pdf")
        try{
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
        }
        catch(e:Exception)
        {
            Toast.makeText(this, "Wrong ${e.message}", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "diaa pdf error: ${e.message}")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        // Show prepaid card details
        if(SERVICE_TYPE == Constants.PREPAID_CARD)
        {
            if(DATA_ENTITY?.description != null && DATA_ENTITY?.description != "")
            {
                val lines = DATA_ENTITY?.description!!.split("\n") as ArrayList<String>

                Log.d(TAG, "diaa lines one: $lines")
                for(oneLine in lines)
                {
                    if(oneLine.contains("PIN"))
                    {
                        ui.lytPin.visibility = View.VISIBLE
                        ui.lytSerial.visibility = View.VISIBLE
                    }
                    else
                    {
                        val stringBuilder = StringBuilder()

                        for(i in lines.indices)
                        {
                            val key = lines[i]
                            stringBuilder.append("• ")
                            stringBuilder.append(key)
                            if(i != lines.size -1)
                                stringBuilder.append("\n")
                            ui.lytPin.visibility = View.VISIBLE
                            ui.tvPinTitle.visibility = View.GONE
                            ui.tvPin.text = stringBuilder.toString()
                        }
                    }
                }
            }

            if(IS_BULK)
            {
                ui.cardBulk.visibility = View.VISIBLE
                ui.tvBulkNum.text = BULK_NUMBER.toString() + resources.getString(R.string.cards)
                ui.tvCurrIndex.text = ii.toString() + resources.getString(R.string.of) + BULK_NUMBER.toString()
            }
        }

        if(DATA_ENTITY!!.description != null && DATA_ENTITY!!.description != "")
        {
            val lines = DATA_ENTITY!!.description.split("\n")

            Log.d(TAG, "diaa lines two: $lines")

            for(oneline in lines)
            {
                if(oneline.contains("Serial"))
                {
                    ui.tvSerial.setText(oneline.trim().lowercase().replace("serial:",""))
                }
                else if(oneline.contains("PIN"))
                {
                    ui.tvPin.setText(oneline.trim().lowercase().replace("pin:",""))
                }
            }
        }

        // Set Data
        ui.tvCreatedAt.text = DATA_ENTITY?.createdAt
        ui.tvTransactionNum.text = DATA_ENTITY?.id.toString()
        ui.tvProviderName.text = DATA_ENTITY?.service?.provider?.name
        ui.tvServiceName.text = DATA_ENTITY?.service?.name
        ui.tvAmount.text = Utils.format(DATA_ENTITY?.amount) + resources.getString(R.string.egp)
        ui.tvAmountHead.text = Utils.format(DATA_ENTITY?.totalAmount) + resources.getString(R.string.egp)
        ui.tvTotalAmount.text = Utils.format(DATA_ENTITY?.totalAmount) + resources.getString(R.string.egp)
        ui.tvBalanceBefore.text = Utils.format(DATA_ENTITY?.balanceBefore) + resources.getString(R.string.egp)
        ui.tvBalanceAfter.text = Utils.format(DATA_ENTITY?.balanceAfter) + resources.getString(R.string.egp)

        val type = DATA_ENTITY?.type?.toInt()

        when(type)
        {
            Constants.DONE ->
            {
                ui.tvTransactionStatus.text = resources.getString(R.string.print_succes)
               // ui.btnPrint.visibility = View.VISIBLE
            }
            Constants.FAIL -> {
                ui.tvTransactionStatus.text = resources.getString(R.string.print_failed)
                ui.lytBalanceAfter.visibility = View.GONE
                ui.lytBalanceBefore.visibility = View.GONE
                ui.lytCommision.visibility = View.GONE
            }
            Constants.PENDING -> ui.tvTransactionStatus.text = resources.getString(R.string.print_pending)
        }

        if(DATA_ENTITY?.isSettled == 0)
        {
            ui.lytCommision.visibility = View.GONE
        }

        // Display client number
        if(DATA_ENTITY?.clientNumber != null)
        {
            ui.lytClientNumber.visibility = View.VISIBLE
            ui.tvClientNumber.text = DATA_ENTITY?.clientNumber
        }

    }

    private fun shareImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            Build.VERSION.SDK_INT > Build.VERSION_CODES.Q
        ) {
            val screenShootPath = Screenshot.takeScreenshot(this, view)
            Screenshot.share(this, screenShootPath)
        } else {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                requestPermissions(permission, Constants.REQUEST_CODE__PERMISSION_CODE)
            } else {
                val screenShootPath = Screenshot.takeScreenshot(this, view)
                Screenshot.share(this, screenShootPath)
            }
        }
    }
}