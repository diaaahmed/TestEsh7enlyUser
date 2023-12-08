package com.esh7enly.esh7enlyuser.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityPdfTestBinding
import java.io.File
import java.io.FileOutputStream

private const val TAG = "PdfTestActivity"

class PdfTestActivity : AppCompatActivity()
{
    private val ui by lazy{
        ActivityPdfTestBinding.inflate(layoutInflater)
    }

    // declaring width and height
    // for our PDF file.


    // creating a bitmap variable
    // for storing our images
    lateinit var bmp:Bitmap
    private var scaledbmp:Bitmap?= null

    // constant code for runtime permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        bmp = BitmapFactory.decodeResource(resources,R.drawable.logo)
        scaledbmp = Bitmap.createScaledBitmap(bmp,200,200,false)

        ui.generatePdf.setOnClickListener{

            loadBitmap(ui.testLayout,ui.testLayout.width,ui.testLayout.height)
            pdf()
        }
    }

    private fun loadBitmap(linear: View, width: Int, height: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        linear.draw(canvas)
        return bitmap
    }
    private fun pdf()
    {
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val convertWidth = width.toInt()
//        val convertHeight = height.toInt()

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(
            200,
            200, 1
        ).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        val bitmap = Bitmap.createScaledBitmap(bmp, 200, 200, true)
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

//    private fun generatePdf()
//    {
//        val myPdfDocument = PdfDocument()
//        val myPaint = Paint()
//        val myPageInfo = PdfDocument.PageInfo.Builder(250,400,1).create()
//        val myPage1 = myPdfDocument.startPage(myPageInfo)
//        canvas = myPage1.canvas
//        canvas.drawPaint(myPaint)
//        scaledbmp = Bitmap.createScaledBitmap(bmp,200,200,true)
//        canvas.drawBitmap(scaledbmp,0,0,null)
//        myPdfDocument.finishPage(myPage1)
//    }
}