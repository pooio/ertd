package com.metaShare.modules.bpm.exception;

import org.springframework.core.NestedRuntimeException;

public class BpmException extends NestedRuntimeException {

    public BpmException(String msg) {
        super(msg);
    }

    public BpmException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
