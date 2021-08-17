package com.qiwenshare.ufop.operation.delete.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.springframework.stereotype.Component;


public class AliyunOSSDeleter extends Deleter {
    private AliyunConfig aliyunConfig;

    public AliyunOSSDeleter(){

    }

    public AliyunOSSDeleter(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }
    @Override
    public void delete(DeleteFile deleteFile) {
        String endpoint = aliyunConfig.getOss().getEndpoint();
        String accessKeyId = aliyunConfig.getOss().getAccessKeyId();
        String accessKeySecret = aliyunConfig.getOss().getAccessKeySecret();
        String bucketName = aliyunConfig.getOss().getBucketName();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ossClient.deleteObject(bucketName, UFOPUtils.getAliyunObjectNameByFileUrl(deleteFile.getFileUrl()));



        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
