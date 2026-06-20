package com.qiwenshare.ufop.operation.write.product;

import com.aliyun.oss.OSS;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.domain.WriteFile;
import com.qiwenshare.ufop.util.UFOPUtils;

import java.io.InputStream;

public class AliyunOSSWriter extends Writer {

    private AliyunConfig aliyunConfig;
    private OSS ossClient;

    public AliyunOSSWriter() {

    }

    public AliyunOSSWriter(AliyunConfig aliyunConfig, OSS ossClient) {
        this.aliyunConfig = aliyunConfig;
        this.ossClient = ossClient;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        ossClient.putObject(aliyunConfig.getOss().getBucketName(), UFOPUtils.getAliyunObjectNameByFileUrl(writeFile.getFileUrl()), inputStream);
    }

}
