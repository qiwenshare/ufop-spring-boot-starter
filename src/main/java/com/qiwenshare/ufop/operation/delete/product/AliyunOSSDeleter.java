package com.qiwenshare.ufop.operation.delete.product;

import com.aliyun.oss.OSS;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufop.util.UFOPUtils;


public class AliyunOSSDeleter extends Deleter {
    private AliyunConfig aliyunConfig;
    private OSS ossClient;

    public AliyunOSSDeleter() {

    }

    public AliyunOSSDeleter(AliyunConfig aliyunConfig, OSS ossClient) {
        this.aliyunConfig = aliyunConfig;
        this.ossClient = ossClient;
    }

    @Override
    public void delete(DeleteFile deleteFile) {
        ossClient.deleteObject(aliyunConfig.getOss().getBucketName(), UFOPUtils.getAliyunObjectNameByFileUrl(deleteFile.getFileUrl()));
        deleteCacheFile(deleteFile);
    }
}
