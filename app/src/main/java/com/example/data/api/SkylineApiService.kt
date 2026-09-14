package com.example.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SkylineApiService {

    @POST("auth/login")
    suspend fun login(
        @Body body: EncryptedPayloadRequest
    ): Response<LoginResponse>

    @GET("user")
    suspend fun getUser(
        @Header("Authorization") authHeader: String
    ): Response<ApiResponse<UserAccountData>>

    @GET("service")
    suspend fun getService(
        @Header("Authorization") authHeader: String
    ): Response<ApiResponse<ServiceData>>

    @GET("dashboard")
    suspend fun getDashboard(
        @Header("Authorization") authHeader: String
    ): Response<ApiResponse<DashboardData>>

    @GET("packages")
    suspend fun getPackages(
        @Header("Authorization") authHeader: String
    ): Response<ApiResponse<List<PackageItem>>>

    @POST("redeem")
    suspend fun redeem(
        @Header("Authorization") authHeader: String,
        @Body body: RedeemRequest
    ): Response<GenericActionResponse>

    @POST("user/extend")
    suspend fun extend(
        @Header("Authorization") authHeader: String,
        @Body body: ExtendRequest
    ): Response<GenericActionResponse>

    @POST("user/activate")
    suspend fun activate(
        @Header("Authorization") authHeader: String,
        @Body body: ActivateRequest
    ): Response<GenericActionResponse>

    @POST("service")
    suspend fun changeService(
        @Header("Authorization") authHeader: String,
        @Body body: ChangeServiceRequest
    ): Response<GenericActionResponse>
}
