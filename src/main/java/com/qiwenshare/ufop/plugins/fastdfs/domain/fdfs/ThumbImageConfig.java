package com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs;

/**
 * 缩略图生成配置支持
 *
 * @author tobato
 */
public interface ThumbImageConfig {

    /**
     * 获得缩略图宽
     *
     * @return 缩略图宽
     */
    int getWidth();

    /**
     * 获得缩略图高
     *
     * @return 缩略图高
     */
    int getHeight();

    /**
     * 获得缩略图前缀
     *
     * @return 缩略图前缀
     */
    String getPrefixName();

    /**
     * 获得缩略图路径
     *
     * @param masterFilename     主文件名
     * @return 缩略图路径
     */
    String getThumbImagePath(String masterFilename);

}
