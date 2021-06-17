package com.qiwenshare.ufo.operation.preview.domain;

import lombok.Data;

@Data
public class PreviewFile {
    private String fileUrl;
    private int width;
    private int height;
    private int rotate;
    private long fileSize;
}
