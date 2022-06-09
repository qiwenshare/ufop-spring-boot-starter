package com.qiwenshare.ufop.operation.download.product;

import com.qiniu.util.Auth;
import com.qiwenshare.common.util.HttpsUtils;
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

        InputStream inputStream = HttpsUtils.doGet(urlString, null);
        try {
            if (downloadFile.getRange() != null) {
                inputStream.skip(downloadFile.getRange().getStart());
                byte[] bytes = new byte[downloadFile.getRange().getLength()];
                IOUtils.read(inputStream, bytes);
                inputStream = new ByteArrayInputStream(bytes);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return inputStream;
    }

}
