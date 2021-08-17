package com.qiwenshare.ufop.operation.read.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.domain.AliyunOSS;
import com.qiwenshare.ufop.exception.ReadException;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.domain.ReadFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import com.qiwenshare.ufop.util.ReadFileUtils;

import java.io.IOException;
import java.io.InputStream;

public class AliyunOSSReader extends Reader {

    private AliyunConfig aliyunConfig;

    public AliyunOSSReader(){

    }

    public AliyunOSSReader(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = UFOPUtils.getFileExtendName(fileUrl);
        try {
            return ReadFileUtils.getContentByInputStream(fileType, getInputStream(readFile.getFileUrl()));
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        }
    }

    public InputStream getInputStream(String fileUrl) {
        OSS ossClient = createOSSClient(aliyunConfig.getOss());
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }


    public OSS createOSSClient(AliyunOSS aliyunOSS) {
        String endpoint = aliyunOSS.getEndpoint();
        String accessKeyId = aliyunOSS.getAccessKeyId();
        String accessKeySecret = aliyunOSS.getAccessKeySecret();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }
}
