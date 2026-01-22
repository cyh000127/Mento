package com.mready.common.error.exception;

import com.mready.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends BusinessException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
