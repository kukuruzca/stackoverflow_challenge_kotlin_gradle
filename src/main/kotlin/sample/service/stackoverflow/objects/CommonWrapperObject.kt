package sample.service.stackoverflow.objects

class CommonWrapperObject<T>(
    val quota_max: Int,
    val quota_remaining: Int,
    val error_message: String,
    val error_name: String,
    val has_more: Boolean,
    val items: List<T>
)
