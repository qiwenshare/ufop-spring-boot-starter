package com.qiwenshare.ufop.operation.download.product;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qiwenshare.ufop.config.TencentConfig;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.util.TencentUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class TencentCOSDownloader extends Downloader {

    private TencentConfig tencentConfig;

    public TencentCOSDownloader() {

    }

    public TencentCOSDownloader(TencentConfig tencentConfig) {
        this.tencentConfig = tencentConfig;
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {

        COSClient cosClient = TencentUtils.getCOSClient(tencentConfig);

        COSObject cosObject = new COSObject();
        if (downloadFile.getRange() != null) {
            GetObjectRequest getObjectRequest = new GetObjectRequest(tencentConfig.getCos().getBucketName(),
                    UFOPUtils.getTencentObjectNameByFileUrl(downloadFile.getFileUrl()));
            getObjectRequest.setRange(downloadFile.getRange().getStart(),
                    downloadFile.getRange().getStart() + downloadFile.getRange().getLength() - 1);
            cosObject = cosClient.getObject(getObjectRequest);
        } else {
            cosObject = cosClient.getObject(tencentConfig.getCos().getBucketName(),
                    UFOPUtils.getTencentObjectNameByFileUrl(downloadFile.getFileUrl()));
        }

        InputStream inputStream = cosObject.getObjectContent();

        downloadFile.setCosClient(cosClient);
        return inputStream;
    }
}
