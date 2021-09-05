package com.qiwenshare.ufop.exception;

public class UFOPException extends RuntimeException {
    public UFOPException(Throwable cause) {
        super("统一文件操作平台（UFOP）出现异常", cause);
    }

    public UFOPException(String message) {
        super(message);
    }

    public UFOPException(String message, Throwable cause) {
        super(message, cause);
    }

}
