package com.qiwenshare.ufop.operation.read.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.exception.operation.ReadException;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.domain.ReadFile;
import com.qiwenshare.ufop.util.AliyunUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import com.qiwenshare.ufop.util.ReadFileUtils;
import org.apache.commons.io.FilenameUtils;

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
        String fileType = FilenameUtils.getExtension(fileUrl);
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        try {
            return ReadFileUtils.getContentByInputStream(fileType, inputStream);
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    public InputStream getInputStream(String fileUrl) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(fileUrl));
        return ossObject.getObjectContent();
    }

}
