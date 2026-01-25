package com.mready.common.error.exception;

import com.mready.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class ConsultingException extends BusinessException {
    public ConsultingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
