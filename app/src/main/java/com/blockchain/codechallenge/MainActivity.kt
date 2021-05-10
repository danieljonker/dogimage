package com.blockchain.codechallenge

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blockchain.codechallenge.imageLoader.ImageLoader
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dog.ceo/api/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create()).build()

    private val dogImageService = retrofit.create(DogImageService::class.java)

    private val dogImageRepo: DogImageRepo = DogImageRepoImpl(dogImageService)

    private val ioScheduler = Schedulers.io()
    private val uiScheduler = AndroidSchedulers.mainThread()

    private val viewModelProviderFactory =
        DogImageViewModelProviderFactory(
            dogImageRepo,
            ioScheduler,
            uiScheduler
        )
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(
            DogImageViewModel::class.java
        )
    }

    private val imageLoader = ImageLoader(ioScheduler, uiScheduler)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.dogImageObservable.observe(this, Observer {
            it.onFailure { throwable ->
                showError(throwable)
            }
            it.onSuccess { url ->
                showDogImage(url)
            }
        })

        viewModel.updateDogImage()
    }

    private fun showDogImage(url: String) {
        Timber.i(url)

        val imageView = findViewById<ImageView>(R.id.imageView)
        imageLoader.loadImage(imageView, url)
    }

    private fun showError(t: Throwable) {
        Timber.e(t)
    }
}


class DogImageViewModelProviderFactory(
    private val dogImageRepo: DogImageRepo,
    private val ioScheduler: Scheduler,
    private val uiScheduler: Scheduler
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DogImageViewModel(dogImageRepo, ioScheduler, uiScheduler) as T
    }
}
