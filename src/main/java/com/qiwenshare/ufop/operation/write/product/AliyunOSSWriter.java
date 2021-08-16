package com.qiwenshare.ufop.operation.write.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.domain.WriteFile;
import com.qiwenshare.ufop.util.UFOPUtils;

import java.io.InputStream;

public class AliyunOSSWriter extends Writer {
    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        OSS ossClient = new OSSClientBuilder().build(UFOPAutoConfiguration.aliyunConfig.getOss().getEndpoint(), UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeyId(), UFOPAutoConfiguration.aliyunConfig.getOss().getAccessKeySecret());

        ossClient.putObject(UFOPAutoConfiguration.aliyunConfig.getOss().getBucketName(), UFOPUtils.getAliyunObjectNameByFileUrl(writeFile.getFileUrl()), inputStream);
        ossClient.shutdown();
    }



}
