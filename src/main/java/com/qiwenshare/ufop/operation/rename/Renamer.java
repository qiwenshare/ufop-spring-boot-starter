package com.qiwenshare.ufop.operation.rename;

import com.qiwenshare.ufop.operation.rename.domain.RenameFile;

import java.io.InputStream;

public abstract class Renamer {
    public abstract void rename(RenameFile renameFile);
}
