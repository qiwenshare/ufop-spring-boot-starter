package com.qiwenshare.ufop.operation.download.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.domain.AliyunOSS;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.util.AliyunUtils;
import com.qiwenshare.ufop.util.IOUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AliyunOSSDownloader extends Downloader {

    private AliyunConfig aliyunConfig;

    public AliyunOSSDownloader(){

    }

    public AliyunOSSDownloader(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }
    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {



        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();

        IOUtils.writeInputStreamToResponse(inputStream, httpServletResponse);
        ossClient.shutdown();
    }



    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }

}
