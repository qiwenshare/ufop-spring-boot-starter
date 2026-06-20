package com.qiwenshare.ufop.operation.read.product;

import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.exception.operation.ReadException;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.domain.ReadFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class MinioReader extends Reader {

    private MinioConfig minioConfig;
    private MinioClient minioClient;

    public MinioReader(){

    }

    public MinioReader(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    public MinioReader(MinioConfig minioConfig, MinioClient minioClient) {
        this.minioConfig = minioConfig;
        this.minioClient = minioClient;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = FilenameUtils.getExtension(fileUrl);
        InputStream inputStream = null;
        try {
            inputStream = getInputStream(readFile.getFileUrl());
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    protected InputStream getInputStream(String fileUrl) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileUrl).build());
        } catch (MinioException e) {
            log.error("Minio error: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage());
        }
        return null;
    }


}
