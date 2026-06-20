package com.qiwenshare.ufop.operation.write.product;

import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.domain.WriteFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioWriter extends Writer {

    private MinioConfig minioConfig;
    private MinioClient minioClient;

    public MinioWriter(MinioConfig minioConfig, MinioClient minioClient) {
        this.minioConfig = minioConfig;
        this.minioClient = minioClient;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        try {
            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build());
            if(!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object(UFOPUtils.getAliyunObjectNameByFileUrl(writeFile.getFileUrl())).stream(
                                    inputStream, inputStream.available(), -1)
                            .build());

        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
