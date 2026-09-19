package com.example.trabajo03_apis_placeholder.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface JsonPlaceholderApi {

    @GET("todos/{id}")
    suspend fun getTodoById(
        @Path("id") id: Int
    ): Response<Todo>

}