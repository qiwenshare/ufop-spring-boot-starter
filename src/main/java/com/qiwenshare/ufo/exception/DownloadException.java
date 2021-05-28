package com.qiwenshare.ufo.exception;

public class DownloadException extends RuntimeException{
    public DownloadException(Throwable cause) {
        super("上传出现了异常", cause);
    }

    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }

}
