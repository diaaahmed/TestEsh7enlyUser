package com.esh7enly.esh7enlyuser.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.adapter.DepositsAdapter
import com.esh7enly.esh7enlyuser.databinding.FragmentBalanceBinding

import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BalanceFragment : BaseFragment()
{
    private val ui by lazy{
        FragmentBalanceBinding.inflate(layoutInflater)
    }

    private val depositAdapter by lazy{
        DepositsAdapter()
    }

    private var page = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return ui.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ui.addBalance.setOnClickListener{
            NavigateToActivity.navigateToAddBalanceActivity(requireActivity())
        }

        balanceViewModel.balance.observe(requireActivity())
        {
            ui.userBalance.text = "$it ${resources.getString(R.string.egp)}"
        }

        ui.reload.setOnClickListener {
           getBalance()
        }

    }

    private fun getBalance()
    {
        sharedHelper!!.getUserToken()?.let {
            balanceViewModel.getWalletsUser(it)
        }
    }


    private fun getDeposits()
    {
        balanceViewModel.getNewDeposits(sharedHelper?.getUserToken().toString(),page)

        balanceViewModel.responseDeposits.observe(this)
        {
                response->
            when(response)
            {
                is NetworkResult.Success -> {
                    response.data?.data?.data?.let { depositAdapter.setTransactionEntity(it) }
                    // it?.data?.let { it1 -> newTransactionAdapter.setTransactionEntity(it1) }
                    // transactionAdapter.submitList(it?.data)
                    ui.depositsRv.adapter = depositAdapter
                    pDialog.cancel()
                }

                is NetworkResult.Loading -> {
                    pDialog.show()
                }
                is NetworkResult.Error -> {
                    pDialog.cancel()
                    Log.d("", "diaa getTransactions: ${response.message}")
                }
            }
        }

//        lifecycleScope.launch {
//            serviceViewModel.getDeposits(sharedHelper?.getUserToken().toString(),page,
//                object : OnResponseListener {
//                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
//                    {
//                        val depositResponseData = obj as List<DataX>
//
//                        depositAdapter.setTransactionEntity(depositResponseData)
//                        ui.depositsRv.setHasFixedSize(true)
//                        ui.depositsRv.adapter = depositAdapter
//
//                    }
//
//                    override fun onFailed(code: Int, msg: String?)
//                    {
//                        dialog.showErrorDialogWithAction(msg,resources.getString(R.string.app__ok)
//                        ) {
//                            dialog.cancel()
//
//                            if (code.toString() == Constants.CODE_UNAUTH ||
//                                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED)
//                            {
//                                NavigateToActivity.navigateToMainActivity(requireActivity())
//                            }
//                        }.show()
//                    }
//                })
//        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initRecyclerView()
    {
        ui.depositsRv.setHasFixedSize(true)

        ui.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener
        { v, _, scrollY, _, _ ->
            if(scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                page++
                balanceViewModel.getNewDeposits(sharedHelper?.getUserToken().toString(),
                page)
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()

        if(connectivity?.isConnected == true)
        {
            getBalance()
            initRecyclerView()
            getDeposits()

        }
        else
        {
            dialog.showErrorDialogWithAction(resources.getString(R.string.no_internet_error),
                resources.getString(R.string.app__ok))
            {
                dialog.cancel()

            }.show()
        }
    }

}