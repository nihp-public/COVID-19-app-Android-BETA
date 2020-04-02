/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package com.example.colocate.isolate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colocate.common.ViewState
import com.example.colocate.di.module.AppModule
import com.example.colocate.di.module.PersistenceModule
import com.example.colocate.network.convert
import com.example.colocate.network.convertV2
import com.example.colocate.persistence.ContactEventDao
import com.example.colocate.persistence.ContactEventV2Dao
import com.example.colocate.persistence.ResidentIdProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber.e
import timber.log.Timber.i
import uk.nhs.nhsx.sonar.android.client.colocation.CoLocationApi
import uk.nhs.nhsx.sonar.android.client.colocation.CoLocationData
import javax.inject.Inject
import javax.inject.Named

class IsolateViewModel @Inject constructor(
    private val coLocationApi: CoLocationApi,
    private val contactEventDao: ContactEventDao,
    private val contactEventV2Dao: ContactEventV2Dao,
    @Named(AppModule.DISPATCHER_IO) private val ioDispatcher: CoroutineDispatcher,
    private val residentIdProvider: ResidentIdProvider,
    @Named(PersistenceModule.USE_CONNECTION_V2) private val useConnectionV2: Boolean
) : ViewModel() {

    private val _isolationResult = MutableLiveData<ViewState>()
    val isolationResult: LiveData<ViewState> = _isolationResult

    fun onNotifyClick() {
        viewModelScope.launch(ioDispatcher) {
            val coLocationData = if (useConnectionV2) {
                val events: JSONArray = convertV2(contactEventV2Dao.getAll())
                CoLocationData(residentIdProvider.getResidentId(), events)
            } else {
                val events: JSONArray = convert(contactEventDao.getAll())
                CoLocationData(residentIdProvider.getResidentId(), events)
            }

            coLocationApi.save(coLocationData,
                onSuccess = {
                    i("Success")
                    _isolationResult.value = ViewState.Success
                }, onError = {
                    e("Error: $it")
                    _isolationResult.value = ViewState.Error(it)
                })
        }
    }
}
