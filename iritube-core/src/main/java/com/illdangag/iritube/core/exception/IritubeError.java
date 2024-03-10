package com.illdangag.iritube.core.exception;

/**
 * code는 8자리
 * message는 not null
 *
 * - 요청 오류: 00******
 * - 인증 오류: 01******
 * - 계정 오류: 02******
 * - 영상 오류: 03******
 * - 스트림 오류: 04******
 * - 재생 목록 오류: 05******
 */
public interface IritubeError {
    String getCode();

    int getHttpStatusCode();

    String getMessage();
}
