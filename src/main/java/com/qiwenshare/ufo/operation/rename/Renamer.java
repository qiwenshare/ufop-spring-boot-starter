package com.qiwenshare.ufo.operation.rename;

import com.qiwenshare.ufo.operation.download.domain.DownloadFile;
import com.qiwenshare.ufo.operation.rename.domain.RenameFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

public abstract class Renamer {
    public abstract void rename(RenameFile renameFile);
}
