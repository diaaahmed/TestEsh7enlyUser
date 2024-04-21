package com.esh7enly.esh7enlyuser.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import com.esh7enly.data.sharedhelper.SharedHelper

import com.esh7enly.domain.entity.TransactionEntity
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.adapter.NewTransactionAdapter
import com.esh7enly.esh7enlyuser.click.TransactionClick
import com.esh7enly.esh7enlyuser.databinding.ActivityTransactionsBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.NetworkResult
import com.esh7enly.esh7enlyuser.util.ProgressDialog
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.viewModel.TransactionsViewModel
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject

private const val TAG = "TransactionsActivity"

@AndroidEntryPoint
class TransactionsActivity : AppCompatActivity(), TransactionClick
{
    private val ui by lazy{ ActivityTransactionsBinding.inflate(layoutInflater) }

    var sharedHelper: SharedHelper? = null
        @Inject set

    private var page = 1

    private val pDialog by lazy{
        ProgressDialog.createProgressDialog(this)
    }

    private val newTransactionAdapter by lazy{
        NewTransactionAdapter(this)
    }

    private val transactionsViewModel: TransactionsViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.transactionsToolbar.title = resources.getString(R.string.transactions)

        initRecyclerView()

        ui.transactionsToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        getTransactions()
    }

    private fun getTransactions()
    {
        transactionsViewModel.getTransactions(sharedHelper?.getUserToken().toString(),page)

        transactionsViewModel.responseTransactions.observe(this)
        {
                response->
            when(response)
            {
                is NetworkResult.Success -> {
                    pDialog.cancel()
                    response.data?.data?.data?.let { newTransactionAdapter.setTransactionEntity(it) }
                    ui.transactionsRv.adapter = newTransactionAdapter
                }

                is NetworkResult.Loading -> {
                    pDialog.show()
                }
                is NetworkResult.Error -> {
                    pDialog.cancel()
                    showDialogWithAction(response.message.toString())
                }
            }
        }
    }
    private val alertDialog by lazy {
        AppDialogMsg(this, false)
    }

    private fun showDialogWithAction(message: String) {
        alertDialog.showErrorDialogWithAction(
            message, resources.getString(R.string.app__ok)
        ) {
            alertDialog.cancel()
            NavigateToActivity.navigateToMainActivity(this)
        }.show()
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun initRecyclerView() {
        ui.transactionsRv.setHasFixedSize(true)

        ui.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener
        { v, _, scrollY, _, _ ->
            if(scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight)
            {
                Language.setLanguageNew(this, Constants.LANG)

                page++
                transactionsViewModel.getTransactions(sharedHelper?.getUserToken().toString(),page)
            }
        })
    }



    override fun click(transactionEntity: TransactionEntity) {
        val transactionDetails = Intent(this,TransactionDetails::class.java)
        transactionDetails.putExtra(Constants.TRASACTION_ID,transactionEntity.id.toString())
        transactionDetails.putExtra(Constants.SERVICE_TYPE,transactionEntity.type)
        Log.d(TAG, "diaa transaction type: ${transactionEntity.type}")
        startActivity(transactionDetails)
    }

}