package com.qiwenshare.ufop.operation.download.product;

import com.qiniu.util.Auth;

import com.qiwenshare.ufop.config.QiniuyunConfig;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class QiniuyunKodoDownloader extends Downloader {

    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoDownloader() {

    }

    public QiniuyunKodoDownloader(QiniuyunConfig qiniuyunConfig) {
        this.qiniuyunConfig = qiniuyunConfig;
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());

        String urlString = auth.privateDownloadUrl(qiniuyunConfig.getKodo().getDomain() + "/" + downloadFile.getFileUrl());

        try (InputStream inputStream = this.downloadAsStream(urlString)) {
            if (downloadFile.getRange() != null) {
                inputStream.skip(downloadFile.getRange().getStart());
                byte[] bytes = new byte[downloadFile.getRange().getLength()];
                IOUtils.read(inputStream, bytes);
                return new ByteArrayInputStream(bytes);
            }
            // 非 range 请求：需要保留流，由调用者关闭，所以不能使用 try-with-resources
            // 这里复制一份返回
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
