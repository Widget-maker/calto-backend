package kr.app.calto.exception

import kr.app.calto.controller.dto.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

// Spring MVC 프레임워크 예외(요청 파싱, 매핑 단계) : 400/405/415 로 매핑
// 그 외 Exception: 500 INTERNAL_ERROR 로 일률 변환 (내부 예외 메시지는 노출하지 않음)
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CalToException::class)
    fun handleCalToException(e: CalToException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("CalToException: code={}, message={}", e.errorCode, e.message)
        return buildResponse(e.errorCode, e.message)
    }

    // JSON 파싱 실패 (잘못된 body 형식) → 400
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(e: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("HttpMessageNotReadableException: {}", e.message)
        return buildResponse(ErrorCode.INVALID_REQUEST)
    }

    // 필수 query parameter 누락 → 400
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(e: MissingServletRequestParameterException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("MissingServletRequestParameterException: {}", e.message)
        return buildResponse(ErrorCode.INVALID_REQUEST)
    }

    // Path variable, query param 타입 변환 실패 (ex: /blogs/abc) → 400
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("MethodArgumentTypeMismatchException: {}", e.message)
        return buildResponse(ErrorCode.INVALID_REQUEST)
    }

    // 허용되지 않은 HTTP 메서드 → 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("HttpRequestMethodNotSupportedException: {}", e.message)
        return buildResponse(ErrorCode.METHOD_NOT_ALLOWED)
    }

    // 지원하지 않는 Content-Type → 415
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleUnsupportedMediaType(e: HttpMediaTypeNotSupportedException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("HttpMediaTypeNotSupportedException: {}", e.message)
        return buildResponse(ErrorCode.UNSUPPORTED_MEDIA_TYPE)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknown(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unhandled exception", e)
        return buildResponse(ErrorCode.INTERNAL_ERROR)
    }

    private fun buildResponse(
        errorCode: ErrorCode,
        message: String = errorCode.message,
    ): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(errorCode.status)
            .body(
                ApiResponse.failure(
                    code = errorCode.status,
                    errorCode = errorCode.name,
                    message = message,
                ),
            )
}
