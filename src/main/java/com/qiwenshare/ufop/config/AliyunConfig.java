package com.qiwenshare.ufop.config;

import com.qiwenshare.ufop.domain.AliyunOSS;
import lombok.Data;

@Data
public class  AliyunConfig {
    private AliyunOSS oss = new AliyunOSS();


}
