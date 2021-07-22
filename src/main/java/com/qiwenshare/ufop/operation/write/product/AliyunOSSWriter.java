package com.qiwenshare.ufop.operation.write.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.domain.WriteFile;
import lombok.Data;

import java.io.InputStream;

public class AliyunOSSWriter extends Writer {
    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        OSS ossClient = getClient();

        ossClient.putObject(UFOPAutoConfiguration.aliyunConfig.getOss().getBucketName(), writeFile.getFileUrl().substring(1), inputStream);
        ossClient.shutdown();
    }



    private synchronized OSS getClient() {
        OSS ossClient = new OSSClientBuilder().build(UFOPAutoConfiguration.aliyunConfig.getOss().getEndpoint(), UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeyId(), UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeySecret());;

        return ossClient;
    }

    @Data
    public class UploadFileInfo {
        private String bucketName;
        private String key;
        private String uploadId;
    }
}
