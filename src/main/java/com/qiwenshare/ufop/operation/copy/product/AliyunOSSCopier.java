package com.qiwenshare.ufop.operation.copy.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.Data;

import java.io.InputStream;
import java.util.UUID;

public class AliyunOSSCopier extends Copier {
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = UFOPUtils.getUploadFileUrl(uuid, copyFile.getExtendName());
        OSS ossClient = getClient();
        ossClient.putObject(UFOPAutoConfiguration.aliyunConfig.getOss().getBucketName()
                , fileUrl, inputStream);
        return fileUrl;
    }

    private synchronized OSS getClient() {
        OSS ossClient = new OSSClientBuilder().build(UFOPAutoConfiguration.aliyunConfig.getOss().getEndpoint(), UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeyId(), UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeySecret());;

        return ossClient;
    }

}
