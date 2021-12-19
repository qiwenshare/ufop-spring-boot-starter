package com.qiwenshare.ufop.exception.operation;

public class PreviewException extends RuntimeException {
    public PreviewException(Throwable cause) {
        super("预览出现了异常", cause);
    }

    public PreviewException(String message) {
        super(message);
    }

    public PreviewException(String message, Throwable cause) {
        super(message, cause);
    }

}
