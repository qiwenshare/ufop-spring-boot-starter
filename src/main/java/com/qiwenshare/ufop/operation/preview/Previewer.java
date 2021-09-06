package com.qiwenshare.ufop.operation.preview;

import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.common.operation.VideoOperation;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.CharsetUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Data
public abstract class Previewer {

    private ThumbImage thumbImage;

    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        String fileUrl = previewFile.getFileUrl();
        boolean isVideo = UFOPUtils.isVideoFile(UFOPUtils.getFileExtendName(fileUrl));
        String thumbnailImgUrl = previewFile.getFileUrl();
        if (isVideo) {
            thumbnailImgUrl = fileUrl.replace("." + UFOPUtils.getFileExtendName(fileUrl), ".jpg");
        }


        File saveFile = UFOPUtils.getCacheFile(thumbnailImgUrl);

        if (saveFile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(saveFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            com.qiwenshare.ufop.util.IOUtils.writeInputStreamToResponse(fis, httpServletResponse);

        } else {
            InputStream inputstream = getInputStream(previewFile.getFileUrl());
            InputStream in = null;
            try {
                int thumbImageWidth = thumbImage.getWidth();
                int thumbImageHeight = thumbImage.getHeight();
                int width = thumbImageWidth == 0 ? 150 : thumbImageWidth;
                int height = thumbImageHeight == 0 ? 150 : thumbImageHeight;

                if (isVideo) {
                    in = VideoOperation.thumbnailsImage(inputstream, saveFile, width, height);
                } else {
                    in = ImageOperation.thumbnailsImage(inputstream, saveFile, width, height);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            com.qiwenshare.ufop.util.IOUtils.writeInputStreamToResponse(in, httpServletResponse);

        }
    }

    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        InputStream inputStream = getInputStream(previewFile.getFileUrl());

        OutputStream outputStream = null;

        try {
            outputStream = httpServletResponse.getOutputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            bytes = CharsetUtils.convertCharset(bytes, UFOPUtils.getFileExtendName(previewFile.getFileUrl()));
            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    protected abstract InputStream getInputStream(String fileUrl);
}
