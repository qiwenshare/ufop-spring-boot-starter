package com.qiwenshare.ufop.operation.copy.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
import com.qiwenshare.ufop.util.AliyunUtils;
import com.qiwenshare.ufop.util.UFOPUtils;

import java.io.InputStream;
import java.util.UUID;

public class AliyunOSSCopier extends Copier {

    private AliyunConfig aliyunConfig;

    public AliyunOSSCopier(){

    }

    public AliyunOSSCopier(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = UFOPUtils.getUploadFileUrl(uuid, copyFile.getExtendName());
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        try {
            ossClient.putObject(aliyunConfig.getOss().getBucketName(), fileUrl, inputStream);
        } finally {
            ossClient.shutdown();
        }
        return fileUrl;
    }

}
