package sample.service


import sample.service.stackoverflow.objects.User
import java.io.IOException
import java.util.*

object StackOverflowChallenge {
    fun printUser(user: User, userTags: List<String>) {
        val userName = user.display_name
        val userLocation = user.location
        val linkToAvatar = user.profile_image
        val linkToProfile = user.link

        val stringBuffer = StringBuilder()
        stringBuffer.append("User: ")
        stringBuffer.append(userName)
        stringBuffer.append(" | Location: ")
        stringBuffer.append(userLocation)
        stringBuffer.append(" | Answer count: ")
        stringBuffer.append(user.answer_count)
        stringBuffer.append(" | Question count: ")
        stringBuffer.append(user.question_count)
        stringBuffer.append(" | Tags: ")
        stringBuffer.append(userTags.joinToString(", "))
        stringBuffer.append(" | Link to profile: ")
        stringBuffer.append(linkToProfile)
        stringBuffer.append(" | Link to avatar: ")
        stringBuffer.append(linkToAvatar)
        stringBuffer.append("\n")
        println(stringBuffer.toString())
    }


    fun tagsFitsRequirements(tags: List<String>?): Boolean {
        if (tags == null)
            throw NullPointerException("tags is null")

        var contains = tags.filter {  it.equals("java", true) || it.equals("c#", true) || it.equals(".net", true) || it.equals("docker", true)}.count() > 0
        return contains
    }

    fun isNullOrEmpty(str: String?): Boolean {
        if (str != null && str.isNotEmpty())
            return false
        return true
    }

    @Throws(IOException::class)
    fun getAllUserTags(userId: Int, stackExchange: StackExchangeService): List<String> {
        val userTags = LinkedList<String>()
        var page = 1
        var hasMore = true
        while (hasMore) {
            val response = stackExchange.getAllUserTags(userId, page)
            if (response != null) {
                userTags.addAll(
                    response.items.filter { x -> !isNullOrEmpty(x.name) }.map { x -> x.name }
                )
            }
            if (response != null) {
                hasMore = response.has_more
            }
            page++
            Thread.sleep(500)
        }
        return userTags
    }

    //@Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        //AppKey retrieved for my app to enlarge number of quota requests
        val stackExchange = StackExchangeService("TnWGwQfIf9SAk3Gkz2H5Lw((")
        //create new filter on base of default
        //add new fields in filter
        val include = listOf("user.answer_count", "user.question_count")
        //exclude unused user fields
        val exclude = listOf("accept_rate"
            ,"age"
            ,"badge_counts"
            ,"badge_counts"
            ,"creation_date"
            ,"is_employee"
            ,"last_access_date"
            ,"last_modified_date"
            ,"reputation_change_day"
            ,"reputation_change_month"
            ,"reputation_change_quarter"
            ,"reputation_change_week"
            ,"reputation_change_year"
            ,"timed_penalty_date"
            ,"user_type"
            ,"website_url")

        val filter = stackExchange.createFilter(include, exclude, "default", false)

        //start from page 1
        var page = 1
        var hasMore = true
        //there is no sense to request users in multi threads, because StackExchange blocks ip address if there are to many requests per second from single ip address
        while (hasMore) {

            val commonWrapperObject = stackExchange.getUsers(page, 223, filter.filter)
            commonWrapperObject?.items?.stream()?.filter { x -> x.answer_count > 0 }?.filter { x ->
                x?.location != null && (x.location.contains("Moldova", true) || x.location.contains("Romania", true))
            }?.forEach { x ->
                //final filter by tags
                //get user tags only for users that pass check with location and answer_count
                try {
                    val userTags = getAllUserTags(x.user_id, stackExchange)
                    if (tagsFitsRequirements(userTags))
                        printUser(x, userTags)
                } catch (e: IOException) {
                    println(e.message)
                }
            }
            if (commonWrapperObject != null) {
                hasMore = commonWrapperObject.has_more
            }
            page++

            //throttling sleep
            Thread.sleep(500)
        }
    }

}