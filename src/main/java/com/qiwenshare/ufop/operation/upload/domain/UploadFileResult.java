package com.qiwenshare.ufop.operation.upload.domain;

import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import lombok.Data;

@Data
public class UploadFileResult {
    private String fileName;
    private String extendName;
    private long fileSize;
    private String fileUrl;
    private StorageTypeEnum storageType;
    private UploadFileStatusEnum status;

}
