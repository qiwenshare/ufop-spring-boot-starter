package com.qiwenshare.ufop.operation.delete;

import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;

public abstract class Deleter {
    public abstract void delete(DeleteFile deleteFile);
}
