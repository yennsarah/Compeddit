package com.yennsarah.compeddit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yennsarah.data.RedditChildrenResponse

class MainViewModel : ViewModel() {

    // LiveData holds state which is observed by the UI
    // (state flows down from ViewModel)
    private val _postList: MutableLiveData<List<RedditChildrenResponse>> =
        MutableLiveData(emptyList())
    val postList: LiveData<List<RedditChildrenResponse>> = _postList

    // onNameChanged is an event we're defining that the UI can invoke
    // (events flow up from UI)
    fun onPostsChanged(list: List<RedditChildrenResponse>) {
        _postList.value = list
    }
}