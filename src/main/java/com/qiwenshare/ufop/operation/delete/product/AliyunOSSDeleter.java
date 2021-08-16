package com.qiwenshare.ufop.operation.delete.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.springframework.stereotype.Component;


@Component
public class AliyunOSSDeleter extends Deleter {

    @Override
    public void delete(DeleteFile deleteFile) {
        String endpoint = UFOPAutoConfiguration.aliyunConfig.getOss().getEndpoint();
        String accessKeyId = UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeyId();
        String accessKeySecret = UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeySecret();
        String bucketName = UFOPAutoConfiguration.aliyunConfig.getOss().getBucketName();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ossClient.deleteObject(bucketName, UFOPUtils.getAliyunObjectNameByFileUrl(deleteFile.getFileUrl()));



        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
