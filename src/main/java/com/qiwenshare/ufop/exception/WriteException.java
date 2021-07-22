package com.qiwenshare.ufop.exception;

public class WriteException extends RuntimeException{
    public WriteException(Throwable cause) {
        super("文件写入出现了异常", cause);
    }

    public WriteException(String message) {
        super(message);
    }

    public WriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
