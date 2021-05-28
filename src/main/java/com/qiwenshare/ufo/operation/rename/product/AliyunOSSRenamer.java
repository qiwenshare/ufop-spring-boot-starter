package com.qiwenshare.ufo.operation.rename.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.ObjectMetadata;
import com.qiwenshare.ufo.autoconfiguration.UFOProperties;
import com.qiwenshare.ufo.operation.rename.Renamer;
import com.qiwenshare.ufo.operation.rename.domain.RenameFile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AliyunOSSRenamer extends Renamer {
    @Resource
    UFOProperties ufoProperties;

    @Override
    public void rename(RenameFile renameFile) {
        String endpoint = ufoProperties.getAliyun().getOss().getEndpoint();
        String accessKeyId = ufoProperties.getAliyun().getOss().getAccessKeyId();
        String accessKeySecret = ufoProperties.getAliyun().getOss().getAccessKeySecret();
        String bucketName = ufoProperties.getAliyun().getOss().getBucketName();
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
