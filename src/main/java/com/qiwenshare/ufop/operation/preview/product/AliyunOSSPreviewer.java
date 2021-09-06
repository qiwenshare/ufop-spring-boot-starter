package com.qiwenshare.ufop.operation.preview.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.common.util.HttpsUtils;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.domain.AliyunOSS;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.util.AliyunUtils;
import com.qiwenshare.ufop.util.CharsetUtils;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class AliyunOSSPreviewer extends Previewer {


    private AliyunConfig aliyunConfig;

    public AliyunOSSPreviewer(){

    }

    public AliyunOSSPreviewer(AliyunConfig aliyunConfig, ThumbImage thumbImage) {
        this.aliyunConfig = aliyunConfig;
        setThumbImage(thumbImage);
    }


    @Override
    public InputStream getInputStream(String fileUrl) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }


}
