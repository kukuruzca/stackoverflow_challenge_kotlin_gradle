package sample.service

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sample.service.stackoverflow.objects.CommonWrapperObject
import sample.service.stackoverflow.objects.Filter
import sample.service.stackoverflow.objects.Tag
import sample.service.stackoverflow.objects.User
import java.io.IOException

class StackExchangeService(private val appKey: String) {

    private val stackOverflowApi: StackOverflowRestApi

    init {
        // Create REST adapter
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of stackOverflow REST api
        stackOverflowApi = retrofit.create(StackOverflowRestApi::class.java)
    }

    @Throws(IOException::class)
    fun getUsers(page: Int, reputationMinimum: Int, filterName: String): CommonWrapperObject<User>? {
        // Create a call instance for getting users.
        //no order
        //no max reputation
        val call = stackOverflowApi.getUsers(
            "reputation",
            reputationMinimum,
            null,
            null,
            SITE,
            MAX_PAGE_SIZE,
            page,
            filterName,
            appKey
        )

        return executeCallAndGetResponse(call)
    }

    @Throws(IOException::class)
    fun getAllUserTags(userId: Int, page: Int): CommonWrapperObject<Tag>? {
        return executeCallAndGetResponse(stackOverflowApi.getUserTags(userId, SITE, page, MAX_PAGE_SIZE, appKey))
    }

    @Throws(IOException::class)
    fun createFilter(include: List<String>, exclude: List<String>, base: String, unsafe: Boolean): Filter {
        val response = executeCallAndGetResponse(
            stackOverflowApi.createFilter(
                include,
                exclude,
                base,
                unsafe,
                appKey
            )
        )

        if (response != null) {
                val filterFound = response.items.stream().findAny()
                if (filterFound.isPresent)
                    return filterFound.get()
        }
        throw RuntimeException("Unable to create filter")
    }

    @Throws(IOException::class)
    private fun <T> handleResponse(response: Response<T>) {
        if (!response.isSuccessful) {
            throw RuntimeException(response.errorBody()?.string())
        }
    }

    @Throws(IOException::class)
    private fun <T> executeCallAndGetResponse(call: Call<T>): T? {
        val execution = call.execute()
        handleResponse<T>(execution)
        return execution.body()
    }

    companion object {
        private const val API_URL = "https://api.stackexchange.com"
        private const val SITE = "stackoverflow"
        private const val MAX_PAGE_SIZE = 100
    }


}
