package com.qiwenshare.ufo.autoconfiguration;

import com.qiwenshare.ufo.config.AliyunConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "ufo")
public class UFOProperties {

    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyun = new AliyunConfig();
}
