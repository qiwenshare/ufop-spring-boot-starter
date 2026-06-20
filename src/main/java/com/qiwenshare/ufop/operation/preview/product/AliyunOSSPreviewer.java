package com.qiwenshare.ufop.operation.preview.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class AliyunOSSPreviewer extends Previewer {

    private AliyunConfig aliyunConfig;
    private OSS ossClient;

    public AliyunOSSPreviewer() {

    }

    public AliyunOSSPreviewer(AliyunConfig aliyunConfig, ThumbImage thumbImage, OSS ossClient) {
        this.aliyunConfig = aliyunConfig;
        this.ossClient = ossClient;
        setThumbImage(thumbImage);
    }

    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(previewFile.getFileUrl()));
        return ossObject.getObjectContent();
    }

}
