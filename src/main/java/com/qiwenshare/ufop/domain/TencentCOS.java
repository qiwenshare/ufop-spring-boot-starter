package com.qiwenshare.ufop.domain;

import lombok.Data;

@Data
public class TencentCOS {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String objectName;
}
