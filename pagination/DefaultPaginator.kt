package com.enact.sharepaca.common.util.pagination

import android.util.Log
import com.enact.sharepaca.common.util.Constants.PAGINATION_DELAY
import com.enact.sharepaca.common.util.Constants.TRY_AGAIN
import com.enact.sharepaca.common.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class DefaultPaginator<Key, Item>(
    private inline val onLoadUpdated: (isLoading: Boolean, isSearch: Boolean) -> Unit,
    private inline val onError: suspend (message: String) -> Unit,
    private inline val onSuccess: suspend (item: Item, newKey: Key, isSearch: Boolean) -> Unit
) : Paginator<Key, Item> {

    private var isMakingRequest = false
    override suspend fun loadNextItems(
        pageNo: Key,
        isSearch: Boolean,
        isPaginating: Boolean,
        onRequest: suspend () -> Flow<Resource<Item>>
    ) {
        try {
            if (isMakingRequest) {
                return
            }
            isMakingRequest = true
            val pageResult = onRequest()
            pageResult.collect { result ->
                when (result) {
                    is Resource.Error -> {
                        Log.e("TAG", "loadNextItems: $result", )
                        onLoadUpdated(false, isSearch)
                        onError(result.message ?: TRY_AGAIN)
                    }
                    is Resource.Loading -> {
                        onLoadUpdated(true, isSearch)
                    }
                    is Resource.Success -> {
                        if (isPaginating) {
                            delay(PAGINATION_DELAY)
                        }
                        isMakingRequest = false
                        onLoadUpdated(false, isSearch)
                        result.data?.let {
                            onSuccess(it, pageNo, isSearch)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            isMakingRequest = false
        }
    }
}