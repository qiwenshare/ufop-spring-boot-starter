package com.qiwenshare.ufop.config;

import lombok.Data;

@Data
public class QiniuyunConfig {
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
