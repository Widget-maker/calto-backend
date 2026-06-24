package kr.app.calto.controller.dto.response

class ApiResponse<T>(
    val data: T? = null,
    val code: Int,
    val errorCode: String? = null,
    val message: String,
) {
    companion object {
        fun <T> success(
            data: T? = null,
            message: String = "성공",
        ) = ApiResponse(code = 200, message = message, data = data)

        fun <T> failure(
            code: Int,
            errorCode: String,
            message: String,
        ) = ApiResponse<T>(
            code = code,
            errorCode = errorCode,
            message = message,
            data = null,
        )
    }
}
