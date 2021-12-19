package com.qiwenshare.ufop.operation.delete.product;

import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.exception.operation.DeleteException;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Slf4j
public class MinioDeleter extends Deleter {
    private MinioConfig minioConfig;

    public MinioDeleter(){

    }

    public MinioDeleter(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Override
    public void delete(DeleteFile deleteFile) {

        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            // 从mybucket中删除myobject。
            minioClient.removeObject(minioConfig.getBucketName(), deleteFile.getFileUrl());
            log.info("successfully removed mybucket/myobject");
        } catch (MinioException e) {
            log.error("Error: " + e);
            throw new DeleteException("Minio删除文件失败", e);
        } catch (IOException e) {
            throw new DeleteException("Minio删除文件失败", e);
        } catch (NoSuchAlgorithmException e) {
            throw new DeleteException("Minio删除文件失败", e);
        } catch (InvalidKeyException e) {
            throw new DeleteException("Minio删除文件失败", e);
        }
        deleteCacheFile(deleteFile);

    }
}
