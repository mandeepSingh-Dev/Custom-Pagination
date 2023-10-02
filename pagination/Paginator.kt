package com.enact.sharepaca.common.util.pagination

import com.enact.sharepaca.common.util.Resource
import kotlinx.coroutines.flow.Flow

interface Paginator<Key, Item> {
    suspend fun loadNextItems(pageNo: Key, isSearch: Boolean = false, isPaginating : Boolean = false, onRequest: suspend () -> Flow<Resource<Item>>, )

}