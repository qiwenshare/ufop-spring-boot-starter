package com.qiwenshare.ufo.operation.delete.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.ufo.autoconfiguration.UFOAutoConfiguration;
import com.qiwenshare.ufo.autoconfiguration.UFOProperties;
import com.qiwenshare.ufo.operation.delete.Deleter;
import com.qiwenshare.ufo.operation.delete.domain.DeleteFile;
import org.springframework.stereotype.Component;


@Component
public class AliyunOSSDeleter extends Deleter {

    @Override
    public void delete(DeleteFile deleteFile) {
        String endpoint = UFOAutoConfiguration.aliyunConfig.getOss().getEndpoint();
        String accessKeyId = UFOAutoConfiguration.aliyunConfig.getOss().getAccessKeyId();
        String accessKeySecret = UFOAutoConfiguration.aliyunConfig.getOss().getAccessKeySecret();
        String bucketName = UFOAutoConfiguration.aliyunConfig.getOss().getBucketName();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ossClient.deleteObject(bucketName, deleteFile.getFileUrl().substring(1));



        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
