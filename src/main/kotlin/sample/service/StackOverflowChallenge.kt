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

        //have at least one of the desired tags
//        val p1 = { e -> e.equals("java", ignoreCase = true) }
//        val p2 = { e -> e.equals("c#", ignoreCase = true) }
//        val p3 = { e -> e.equals(".net", ignoreCase = true) }
//        val p4 = { e -> e.equals("docker", ignoreCase = true) }
//        return tags.stream().anyMatch(p1.or(p2).or(p3).or(p4))
        //TODO:
        return true;
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
        }
        return userTags
    }

    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        //AppKey retrieved for my app to enlarge number of quota requests
        val stackExchange = StackExchangeService("TnWGwQfIf9SAk3Gkz2H5Lw((")
        //create new filter on base of default
        //add new fields in filter
        val include = listOf("user.answer_count", "user.question_count")
        val exclude = listOf<String>()
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