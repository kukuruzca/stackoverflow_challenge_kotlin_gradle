package sample.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import sample.service.stackoverflow.objects.CommonWrapperObject
import sample.service.stackoverflow.objects.Filter
import sample.service.stackoverflow.objects.Tag
import sample.service.stackoverflow.objects.User

interface StackOverflowRestApi {

    @GET("2.2/users")
    fun getUsers(
        @Query("sort") sort: String?,
        @Query("min") min: Int?,
        @Query("max") max: Int?,
        @Query("order") order: String?,
        @Query("site") site: String?,
        @Query("pagesize") pagesize: Int?,
        @Query("page") page: Int?,
        @Query("filter") filter: String?,
        @Query("key") key: String?
    ): Call<CommonWrapperObject<User>>

    @GET("2.2/users/{ids}")
    fun getUsersById(
        @Path("ids")
        @Query("sort") sort: String?,
        @Query("order") order: String?,
        @Query("site") site: String?,
        @Query("key") key: String?
    ): Call<CommonWrapperObject<User>>

    @GET("2.2/filters/create")
    fun createFilter(
        @Query("include") include: List<String>,
        @Query("exclude") exclude: List<String>,
        @Query("base") base: String?,
        @Query("unsafe") unsafe: Boolean,
        @Query("key") key: String?

    ): Call<CommonWrapperObject<Filter>>

    @GET("2.2/filters/{filters}")
    fun getFilters(
        @Path("filters") filters: String?,
        @Query("key") key: String?
    ): Call<CommonWrapperObject<Filter>>

    @GET("2.2/users/{ids}/tags")
    fun getUserTags(
        @Path("ids") ids: Int?,
        @Query("site") site: String?,
        @Query("page") page: Int?,
        @Query("pagesize") pagesize: Int?,
        @Query("key") key: String?
    ): Call<CommonWrapperObject<Tag>>
}

