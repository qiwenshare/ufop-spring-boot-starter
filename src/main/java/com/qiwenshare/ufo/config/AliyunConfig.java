package com.qiwenshare.ufo.config;

import com.qiwenshare.ufo.domain.AliyunOSS;
import lombok.Data;

@Data
public class  AliyunConfig {
    private AliyunOSS oss = new AliyunOSS();


}
