package kr.app.calto.controller.dto.response

import kr.app.calto.exception.CalToException
import kr.app.calto.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity

object Responses {
    private val log = LoggerFactory.getLogger(javaClass)

    fun <T : Any> success(data: T?): ResponseEntity<ApiResponse<T>> = ResponseEntity.ok(ApiResponse.success(data))

    fun success(): ResponseEntity<ApiResponse<Nothing>> = ResponseEntity.ok(ApiResponse.success())

    fun <T> failure(throwable: Throwable): ResponseEntity<ApiResponse<T>> {
        val errorCode =
            (throwable as? CalToException)?.errorCode
                ?: ErrorCode.INTERNAL_ERROR

        // 예상치 못한 NullPointerException, SQLException 등 메시지는 ErrorCode 의 기본 메세지로 대체
        val message =
            if (throwable is CalToException) {
                throwable.message ?: errorCode.message
            } else {
                errorCode.message
            }

        if (throwable is CalToException) {
            log.warn("CalToException: code={}, message={}", errorCode, throwable.message)
        } else {
            log.error("Unexpected exception in controller", throwable)
        }

        return ResponseEntity
            .status(errorCode.status)
            .body(
                ApiResponse.failure(
                    code = errorCode.status,
                    errorCode = errorCode.name,
                    message = message,
                ),
            )
    }
}
