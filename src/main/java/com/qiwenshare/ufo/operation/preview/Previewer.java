package com.qiwenshare.ufo.operation.preview;

import com.qiwenshare.ufo.operation.download.domain.DownloadFile;
import com.qiwenshare.ufo.operation.preview.domain.PreviewFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

public abstract class Previewer {
    public abstract void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile);
    public abstract void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile);
}
