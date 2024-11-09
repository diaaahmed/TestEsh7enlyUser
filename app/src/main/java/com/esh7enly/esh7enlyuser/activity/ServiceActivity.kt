package com.esh7enly.esh7enlyuser.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope

import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.servicesNew.ServiceData
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.adapter.ServiceAdapter
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.click.ServiceClick
import com.esh7enly.esh7enlyuser.databinding.ActivityServiceBinding
import com.esh7enly.esh7enlyuser.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val TAG = "ServiceActivity"


val Context.dataStoreProto by dataStore("setting.json",StringSer)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("language")


@AndroidEntryPoint
class ServiceActivity : BaseActivity(), ServiceClick, IToolbarTitle {

    val TOKEN_KEY = stringPreferencesKey("TOKEN_KEY")

    private val ui by lazy {
        ActivityServiceBinding.inflate(layoutInflater)
    }

    private val adapter by lazy {
        ServiceAdapter(this)
    }

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }


    private var providerID = 0
    lateinit var providerName: String

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)


        Language.setLanguageNew(this, Constants.LANG)

        initToolBar()

        ui.serviceRv.setHasFixedSize(true)

        providerID = intent.getIntExtra(Constants.PROVIDER_ID, 0)
        providerName = intent.getStringExtra(Constants.PROVIDER_NAME).toString()

        getServices()
    }

    override fun initToolBar() {
        ui.serviceToolbar.title = intent.getStringExtra(Constants.PROVIDER_NAME) ?: ""

        ui.serviceToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getServices() {

        pDialog.show()

        serviceViewModel.getServicesNew(sharedHelper?.getUserToken().toString(),
            providerID.toString(), object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    pDialog.cancel()

                    val services = obj as List<ServiceData>

                    adapter.submitList(services)

                    ui.serviceRv.adapter = adapter
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        if (code == Constants.CODE_UNAUTH_NEW ||
                            code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
                        ) {
                            NavigateToActivity.navigateToAuthActivity(this@ServiceActivity)
                        }
                    }.show()
                }
            })


    }

    private fun addDataStore() {

        lifecycleScope.launch {

            dataStoreProto.updateData {
                it.copy(id = 5, name = "", password = "")
            }
            // Save data
            dataStore.edit {
                it[TOKEN_KEY] = "dasdsadasdgsdlrerweb"
            }

            dataStoreProto.data.onEach {
                it.password
            }
            // Read data
            dataStore.data.map {
                it[TOKEN_KEY]
            }.catch {

            }.launchIn(lifecycleScope)
        }


    }


    override fun click(service: ServiceData) {

        serviceViewModel.serviceType = service.type

        Log.d(TAG, "diaa test type: ${service.type}")

        if (service.type == Constants.PREPAID_CARD) {
            if (connectivity?.isConnected == true) {
                pDialog.show()

                val totalAmountPojoModel =
                    TotalAmountPojoModel(Constants.IMEI, service.id, service.priceValue)

                lifecycleScope.launch(Dispatchers.IO) {

                    getTotalAmount(totalAmountPojoModel)
                }
            } else {
                dialog.showWarningDialog(
                    resources.getString(R.string.no_internet_error),
                    resources.getString(R.string.app__ok)
                )
                dialog.show()
            }

        } else {
            navigateToParametersActivity(service)
        }
    }

}