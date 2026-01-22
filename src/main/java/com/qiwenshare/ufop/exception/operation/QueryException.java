package com.qiwenshare.ufop.exception.operation;

import com.qiwenshare.ufop.exception.UFOPException;

public class QueryException extends UFOPException {
    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
