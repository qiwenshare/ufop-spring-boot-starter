package com.qiwenshare.ufop.result;

import lombok.Data;

@Data
public class ImageInfo {

//    @Column(columnDefinition = "varchar(500) comment '图片预览url'")
    private String imgPreviewUrl;
//    @Column(columnDefinition="int(10) comment '图像的宽'")
    private int imageWidth;
//    @Column(columnDefinition="int(10) comment '图像的高'")
    private int imageHeight;


//    @Column(columnDefinition="int(10) comment '像素深度'")
    private int bitsPerPixel; // 像素深度是指存储每个像素所用的位数，也用它来度量图像的分辨率。
//    @Column(columnDefinition="varchar(10) comment '图像格式'")
    private String format;
//    @Column(columnDefinition="varchar(500) comment '图像格式名称'")
    private String formatName;
//    @Column(columnDefinition="varchar(500) comment '图像格式详情'")
    private String formatDetails;
//    @Column(columnDefinition="varchar(20) comment 'mimeType'")
    private String mimeType;
//    @Column(columnDefinition="int(5) comment '图片数量'")
    private Integer numberOfImages;
//    @Column(columnDefinition="int(10) comment '物理高度DPI'")
    private Integer physicalHeightDpi;
//    @Column(columnDefinition="varchar(20) comment '物理高度英寸'")
    private String physicalHeightInch;
//    @Column(columnDefinition="int(10) comment '物理宽度DPI'")
    private int physicalWidthDpi;
//    @Column(columnDefinition="varchar(20) comment '物理宽度英寸'")
    private String physicalWidthInch;


    private int channels;
    private int type;

}
