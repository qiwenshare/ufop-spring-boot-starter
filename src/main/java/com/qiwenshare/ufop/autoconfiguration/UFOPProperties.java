package com.qiwenshare.ufop.autoconfiguration;

import com.qiwenshare.ufop.config.*;
import com.qiwenshare.ufop.domain.ThumbImage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ufop")
public class UFOPProperties {

    private String bucketName;
    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyun = new AliyunConfig();
    private ThumbImage thumbImage = new ThumbImage();
    private MinioConfig minio = new MinioConfig();
    private QiniuyunConfig qiniuyun = new QiniuyunConfig();
    private CacheConfig cache;
    private TencentConfig tencent = new TencentConfig();
}
