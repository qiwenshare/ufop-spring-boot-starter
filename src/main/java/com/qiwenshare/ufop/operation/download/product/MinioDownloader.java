package com.qiwenshare.ufop.operation.download.product;

import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class MinioDownloader extends Downloader {

    private MinioConfig minioConfig;

    public MinioDownloader(){

    }

    public MinioDownloader(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        InputStream inputStream = null;
        try {

            MinioClient minioClient =
                    MinioClient.builder().endpoint(minioConfig.getEndpoint())
                            .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey()).build();

            if (downloadFile.getRange() != null) {
                inputStream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(downloadFile.getFileUrl())
                        .offset(downloadFile.getRange().getStart())
                        .length((long) downloadFile.getRange().getLength())
                        .build());
            } else {
                inputStream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(downloadFile.getFileUrl())
                        .build());
            }


        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage());
        }


        return inputStream;
    }

}
