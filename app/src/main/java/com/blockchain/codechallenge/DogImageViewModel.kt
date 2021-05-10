package com.blockchain.codechallenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class DogImageViewModel(
    private val dogImageRepo: DogImageRepo,
    private val ioScheduler: Scheduler,
    private val uiScheduler: Scheduler
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _dogImageObservable: MutableLiveData<Result<String>> = MutableLiveData()
    val dogImageObservable: LiveData<Result<String>> = _dogImageObservable

    fun updateDogImage() {

        val disposable = dogImageRepo.getDogImage()
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe({ dogImageUrl ->
                _dogImageObservable.postValue(Result.success(dogImageUrl))
            }, {
                _dogImageObservable.postValue(Result.failure<String>(it))
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}