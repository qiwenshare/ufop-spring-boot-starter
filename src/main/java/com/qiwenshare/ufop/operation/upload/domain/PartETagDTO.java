package com.qiwenshare.ufop.operation.upload.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PartETag 数据传输对象
 * 用于解决 Jackson 反序列化阿里云 SDK 中 PartETag 类的问题
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartETagDTO {
    private int partNumber;
    private long partSize;

    @JsonProperty("etag")
    private String eTag;
}