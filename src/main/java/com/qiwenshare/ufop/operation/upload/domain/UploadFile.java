package com.qiwenshare.ufop.operation.upload.domain;

import lombok.Data;

@Data
public class UploadFile {
    private String fileName;
    private String fileType;
    private long fileSize;
    private int success;
    private String message;
    private String url;
    private Integer storageType;
    //切片上传相关参数
    private int chunkNumber;
    private long chunkSize;
    private int totalChunks;
    private String identifier;
    private long totalSize;
    private long currentChunkSize;


}