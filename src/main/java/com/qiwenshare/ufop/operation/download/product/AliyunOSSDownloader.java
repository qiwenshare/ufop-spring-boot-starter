package com.qiwenshare.ufop.operation.download.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class AliyunOSSDownloader extends Downloader {

    private AliyunConfig aliyunConfig;
    private OSS ossClient;

    public AliyunOSSDownloader() {

    }

    public AliyunOSSDownloader(AliyunConfig aliyunConfig, OSS ossClient) {
        this.aliyunConfig = aliyunConfig;
        this.ossClient = ossClient;
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        OSSObject ossObject;
        if (downloadFile.getRange() != null) {
            GetObjectRequest getObjectRequest = new GetObjectRequest(aliyunConfig.getOss().getBucketName(),
                    UFOPUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
            getObjectRequest.setRange(downloadFile.getRange().getStart(),
                    downloadFile.getRange().getStart() + downloadFile.getRange().getLength() - 1);
            ossObject = ossClient.getObject(getObjectRequest);
        } else {
            ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                    UFOPUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
        }

        return ossObject.getObjectContent();
    }


}
