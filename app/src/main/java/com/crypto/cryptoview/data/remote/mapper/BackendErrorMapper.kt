package com.crypto.cryptoview.data.remote.mapper

import retrofit2.HttpException

fun Throwable.toGateIoCredentialSaveMessage(): String {
    val code = (this as? HttpException)?.code() ?: return message ?: this::class.simpleName.orEmpty()
    return when (code) {
        400 -> "Gate.io API Key와 Secret Key를 모두 입력하세요"
        401 -> "로그인이 만료되었습니다. 다시 로그인하세요"
        403 -> "Gate.io 권한, IP whitelist, 인증 설정을 확인하세요"
        404 -> "Gate.io 저장 API를 찾을 수 없습니다. 백엔드 배포 상태를 확인하세요"
        500 -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도하세요"
        502 -> "Gate.io API 연결에 실패했습니다. 잠시 후 다시 시도하세요"
        else -> "Gate.io 키 저장 실패: HTTP $code"
    }
}

fun Throwable.toGateIoAccountFetchMessage(): String {
    val code = (this as? HttpException)?.code() ?: return message ?: this::class.simpleName.orEmpty()
    return when (code) {
        401 -> "로그인이 만료되었습니다. 다시 로그인하세요"
        403 -> "Gate.io 권한, IP whitelist, 인증 설정을 확인하세요"
        404 -> "저장된 Gate.io 키가 없습니다"
        500 -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도하세요"
        502 -> "Gate.io API 연결에 실패했습니다. 잠시 후 다시 시도하세요"
        else -> "Gate.io 자산 조회 실패: HTTP $code"
    }
}

fun Throwable.toGateIoCredentialDeleteMessage(): String {
    val code = (this as? HttpException)?.code() ?: return message ?: this::class.simpleName.orEmpty()
    return when (code) {
        401 -> "로그인이 만료되었습니다. 다시 로그인하세요"
        404 -> "삭제할 Gate.io 키가 없습니다"
        500 -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도하세요"
        else -> "Gate.io 키 삭제 실패: HTTP $code"
    }
}
