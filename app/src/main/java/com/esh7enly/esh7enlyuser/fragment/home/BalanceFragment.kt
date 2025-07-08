package com.esh7enly.esh7enlyuser.fragment.home

import android.os.Build
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import com.esh7enly.domain.NetworkResult
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.adapter.DepositsAdapter
import com.esh7enly.esh7enlyuser.databinding.FragmentBalanceBinding

import com.esh7enly.esh7enlyuser.viewModel.BalanceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BalanceFragment : BaseFragment<FragmentBalanceBinding, BalanceViewModel>() {

    private val depositAdapter by lazy {
        DepositsAdapter()
    }

    private var page = 1
    override val viewModel: BalanceViewModel by viewModels()

    override fun getLayoutResID() = R.layout.fragment_balance

    override fun init() {

        binding.txtBalance.text = resources.getString(R.string.balance)

        viewModel.balance.observe(requireActivity())
        {
            if (it.contains("No internet")) {
                binding.userBalance.text = ""
            } else {
                binding.userBalance.text = "$it ${resources.getString(R.string.egp)}"

            }
        }

        getBalance()
        initRecyclerView()
        getDeposits()


        binding.reload.setOnClickListener {
            getBalance()
        }
    }

    private fun getBalance() {
        viewModel.getWalletsUser()
    }


    private fun getDeposits() {

        viewModel.getNewDeposits(page)

        viewModel.responseDeposits.observe(viewLifecycleOwner)
        { response ->
            when (response) {
                is NetworkResult.Success -> {
                    pDialog.cancel()
                    response.data?.data?.data?.let { depositAdapter.setTransactionEntity(it) }
                    binding.depositsRv.adapter = depositAdapter
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
    private fun initRecyclerView() {
        binding.depositsRv.setHasFixedSize(true)

        binding.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener
            { v, _, scrollY, _, _ ->
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    page++
                    viewModel.getNewDeposits(page)
                }
            })

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
    }

}