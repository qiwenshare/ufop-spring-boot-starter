package com.qiwenshare.ufop.operation.download.product;

import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.exception.operation.DownloadException;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import io.minio.MinioClient;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            // 调用statObject()来判断对象是否存在。
            // 如果不存在, statObject()抛出异常,
            // 否则则代表对象存在。
            minioClient.statObject(minioConfig.getBucketName(), downloadFile.getFileUrl());

            // 获取"myobject"的输入流。
            inputStream = minioClient.getObject(minioConfig.getBucketName(), downloadFile.getFileUrl());

        } catch (MinioException e) {
            throw new DownloadException("Minio is abnormal. Please check whether the connection configuration is correct!", e);
        } catch (IOException e) {
            throw new DownloadException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new DownloadException(e);
        } catch (InvalidKeyException e) {
            throw new DownloadException(e);
        }


        return inputStream;
    }

}
