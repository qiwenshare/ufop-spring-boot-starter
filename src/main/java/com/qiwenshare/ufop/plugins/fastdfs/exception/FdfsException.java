package com.qiwenshare.ufop.plugins.fastdfs.exception;

/**
 * 封装fastdfs的异常，使用运行时异常
 * 
 * @author yuqih
 * @author tobato
 * 
 */
public abstract class FdfsException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    protected FdfsException(String message) {
        super(message);
    }

    /**
     * @param message 异常信息
     * @param cause 异常对象
     */
    protected FdfsException(String message, Throwable cause) {
        super(message, cause);
    }

}
