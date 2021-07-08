package com.qiwenshare.ufo.operation.write.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.ufo.autoconfiguration.UFOAutoConfiguration;
import com.qiwenshare.ufo.operation.upload.domain.UploadFile;
import com.qiwenshare.ufo.operation.write.Writer;
import com.qiwenshare.ufo.operation.write.domain.WriteFile;
import lombok.Data;

import java.io.InputStream;

public class AliyunOSSWriter extends Writer {
    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        OSS ossClient = getClient();

        ossClient.putObject(UFOAutoConfiguration.aliyunConfig.getOss().getBucketName(), writeFile.getFileUrl().substring(1), inputStream);
        ossClient.shutdown();
    }



    private synchronized OSS getClient() {
        OSS ossClient = new OSSClientBuilder().build(UFOAutoConfiguration.aliyunConfig.getOss().getEndpoint(), UFOAutoConfiguration.aliyunConfig.getOss().getAccessKeyId(), UFOAutoConfiguration.aliyunConfig.getOss().getAccessKeySecret());;

        return ossClient;
    }

    @Data
    public class UploadFileInfo {
        private String bucketName;
        private String key;
        private String uploadId;
    }
}
