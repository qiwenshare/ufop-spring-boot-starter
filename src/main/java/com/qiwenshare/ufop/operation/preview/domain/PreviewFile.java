package com.qiwenshare.ufop.operation.preview.domain;

import com.aliyun.oss.OSS;
import lombok.Data;

@Data
public class PreviewFile {
    private String fileUrl;
    private OSS ossClient;
}
