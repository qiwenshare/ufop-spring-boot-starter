package com.qiwenshare.ufop.operation.download;

import com.aliyun.oss.OSS;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
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
