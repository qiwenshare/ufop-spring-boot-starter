package com.qiwenshare.ufo.service;

import com.qiwenshare.ufo.config.AliyunConfig;
import lombok.Data;

@Data
public class UFOService {
    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyun = new AliyunConfig();
}
