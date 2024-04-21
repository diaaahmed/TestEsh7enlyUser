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

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ui.txtBalance.text = resources.getString(R.string.balance)

        if(connectivity?.isConnected == true)
        {
            balanceViewModel.balance.observe(requireActivity())
            {
                ui.userBalance.text = "$it ${resources.getString(R.string.egp)}"
            }

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

        ui.addBalance.setOnClickListener{
            NavigateToActivity.navigateToAddBalanceActivity(requireActivity())
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


    private fun getDeposits() {

        balanceViewModel.getNewDeposits(sharedHelper?.getUserToken().toString(),page)

        balanceViewModel.responseDeposits.observe(viewLifecycleOwner)
        {
                response->
            when(response)
            {
                is NetworkResult.Success -> {
                    pDialog.cancel()
                    response.data?.data?.data?.let { depositAdapter.setTransactionEntity(it) }
                    ui.depositsRv.adapter = depositAdapter
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
    override fun onResume()
    {
        super.onResume()

//        if(connectivity?.isConnected == true)
//        {
//            getBalance()
//            initRecyclerView()
//            getDeposits()
//        }
//        else
//        {
//            dialog.showErrorDialogWithAction(resources.getString(R.string.no_internet_error),
//                resources.getString(R.string.app__ok))
//            {
//                dialog.cancel()
//
//            }.show()
//        }
    }

}