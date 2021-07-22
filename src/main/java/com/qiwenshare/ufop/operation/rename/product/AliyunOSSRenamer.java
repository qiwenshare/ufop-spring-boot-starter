package com.qiwenshare.ufop.operation.rename.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.ObjectMetadata;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.operation.rename.Renamer;
import com.qiwenshare.ufop.operation.rename.domain.RenameFile;
import org.springframework.stereotype.Component;

@Component
public class AliyunOSSRenamer extends Renamer {

    @Override
    public void rename(RenameFile renameFile) {
        String endpoint = UFOPAutoConfiguration.aliyunConfig.getOss().getEndpoint();
        String accessKeyId = UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeyId();
        String accessKeySecret = UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeySecret();
        String bucketName = UFOPAutoConfiguration.aliyunConfig.getOss().getBucketName();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        CopyObjectResult result = ossClient.copyObject(bucketName, renameFile.getSrcName(), bucketName, renameFile.getDestName());

        ossClient.deleteObject(bucketName, renameFile.getSrcName());
        ObjectMetadata metadata = new ObjectMetadata();
//        if ("pdf".equals(FileUtil.getFileType(objectName))) {
//            metadata.setContentDisposition("attachment");
//        }

//        ossClient.putObject(bucketName, objectName, inputStream, metadata);


        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
