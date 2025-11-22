package com.qiwenshare.ufop.operation.preview;

import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.exception.operation.PreviewException;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.CharsetUtils;
import com.qiwenshare.ufop.util.ImageOperation;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
@Data
public abstract class Previewer {

    public ThumbImage thumbImage;

    protected abstract InputStream getInputStream(PreviewFile previewFile);

    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {


        String thumbnailImgUrl = previewFile.getFileUrl();


        File cacheFile = UFOPUtils.getCacheFile(thumbnailImgUrl);
        File tempFile = UFOPUtils.getTempFile(thumbnailImgUrl);

        if (cacheFile.exists()) {
            FileInputStream fis = null;
            OutputStream outputStream = null;
            try {
                fis = new FileInputStream(cacheFile);
                outputStream = httpServletResponse.getOutputStream();
                IOUtils.copy(fis, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fis);
                IOUtils.closeQuietly(outputStream);
            }

        } else {
            OutputStream outputStream = null;
            InputStream inputstream = null;
            try {
                inputstream = getInputStream(previewFile);
                if (!tempFile.exists()) {
                    tempFile.createNewFile();
                }
                FileUtils.copyInputStreamToFile(inputstream, tempFile);
            } catch (PreviewException previewException) {
                log.error(previewException.getMessage());
                return;
            } catch (IOException e) {
                log.error("IO 异常", e);
            } finally {
                IOUtils.closeQuietly(inputstream);
            }

            try {
                outputStream = httpServletResponse.getOutputStream();
                if (!cacheFile.getParentFile().exists()) {
                    cacheFile.getParentFile().mkdirs();
                }
                ImageOperation.thumbnailsImageFile(tempFile, cacheFile, 576, 324);
                FileInputStream tempStream = new FileInputStream(cacheFile);
                try {
                    IOUtils.copy(tempStream, outputStream);
                } finally {
                    IOUtils.closeQuietly(tempStream);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(outputStream);
                if (previewFile.getOssClient() != null) {
                    previewFile.getOssClient().shutdown();
                }
            }


        }
    }

    @Deprecated
    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        preview(httpServletResponse, previewFile);
    }

    public void preview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        InputStream inputStream = null;

        OutputStream outputStream = null;

        try {
            inputStream = getInputStream(previewFile);
            outputStream = httpServletResponse.getOutputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            bytes = CharsetUtils.convertTxtCharsetToUTF8(bytes, FilenameUtils.getExtension(previewFile.getFileUrl()));
            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            if (previewFile.getOssClient() != null) {
                previewFile.getOssClient().shutdown();
            }
        }
    }

    public InputStream downloadAsStream(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                return new ByteArrayInputStream(bytes);
            }
        }
    }
}
