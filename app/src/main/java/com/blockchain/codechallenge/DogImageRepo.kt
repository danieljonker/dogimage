package com.blockchain.codechallenge

import io.reactivex.Single
import retrofit2.http.GET

interface DogImageRepo {
    fun getDogImage(): Single<String>
}

class DogImageRepoImpl(private val dogImageService: DogImageService) : DogImageRepo {
    override fun getDogImage(): Single<String> {
        return dogImageService.getDogImage().map { it.message }
    }
}

interface DogImageService {
    @GET("breeds/image/random")
    fun getDogImage(): Single<DogImageResponse>
}


data class DogImageResponse(val message: String, val status: String)
