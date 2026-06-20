package com.qiwenshare.ufop.operation.copy.product;

import com.aliyun.oss.OSS;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.UUID;

public class AliyunOSSCopier extends Copier {

    private AliyunConfig aliyunConfig;
    private OSS ossClient;

    public AliyunOSSCopier() {

    }

    public AliyunOSSCopier(AliyunConfig aliyunConfig, OSS ossClient) {
        this.aliyunConfig = aliyunConfig;
        this.ossClient = ossClient;
    }

    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = UFOPUtils.getUploadFileUrl(uuid, copyFile.getExtendName());
        try {
            ossClient.putObject(aliyunConfig.getOss().getBucketName(), fileUrl, inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return fileUrl;
    }

}
