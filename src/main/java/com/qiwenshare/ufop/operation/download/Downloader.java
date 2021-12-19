package com.qiwenshare.ufop.operation.download;

import com.aliyun.oss.OSS;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Downloader {

    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {

        InputStream inputStream = getInputStream(downloadFile);
        OutputStream outputStream = null;
        try {
            outputStream = httpServletResponse.getOutputStream();
            IOUtils.copyLarge(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            OSS ossClient = downloadFile.getOssClient();
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

    }
    public abstract InputStream getInputStream(DownloadFile downloadFile);
}
