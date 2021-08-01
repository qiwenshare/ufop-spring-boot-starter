package com.qiwenshare.ufop.exception;

public class CopyException extends RuntimeException {
    public CopyException(Throwable cause) {
        super("创建出现了异常", cause);
    }

    public CopyException(String message) {
        super(message);
    }

    public CopyException(String message, Throwable cause) {
        super(message, cause);
    }

}
