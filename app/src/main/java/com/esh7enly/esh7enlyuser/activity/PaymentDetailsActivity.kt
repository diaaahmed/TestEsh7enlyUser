package com.esh7enly.esh7enlyuser.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityPaymentDetailsBinding
import com.esh7enly.esh7enlyuser.databinding.ActivityReceiptBinding

class PaymentDetailsActivity : BaseActivity() {

    private val ui by lazy {
        ActivityPaymentDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
    }
}