package com.qiwenshare.ufo.exception;

public class DeleteException extends RuntimeException{
    public DeleteException(Throwable cause) {
        super("上传出现了异常", cause);
    }

    public DeleteException(String message) {
        super(message);
    }

    public DeleteException(String message, Throwable cause) {
        super(message, cause);
    }

}
