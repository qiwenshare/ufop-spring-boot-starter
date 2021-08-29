package com.qiwenshare.ufop.exception;

public class OperationException extends RuntimeException {
    public OperationException(Throwable cause) {
        super("操作出现了异常", cause);
    }

    public OperationException(String message) {
        super(message);
    }

    public OperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
