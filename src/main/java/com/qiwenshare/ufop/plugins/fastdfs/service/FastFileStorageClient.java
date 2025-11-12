package com.qiwenshare.ufop.plugins.fastdfs.service;

import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.MetaData;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorePath;
import com.qiwenshare.ufop.plugins.fastdfs.domain.upload.FastFile;
import com.qiwenshare.ufop.plugins.fastdfs.domain.upload.FastImageFile;

import java.io.InputStream;
import java.util.Set;

/**
 * 面向普通应用的文件操作接口封装
 *
 * @author tobato
 */
public interface FastFileStorageClient extends GenerateStorageClient {

    /**
     * 上传一般文件
     *
     * @param inputStream 输入流
     * @param fileSize 文件大小
     * @param fileExtName 文件扩展名
     * @param metaDataSet 元数据集合
     * @return 存储路径
     */
    StorePath uploadFile(InputStream inputStream, long fileSize, String fileExtName, Set<MetaData> metaDataSet);

    /**
     * 上传图片并且生成缩略图
     * 支持的图片格式包括"JPG", "JPEG", "PNG", "GIF", "BMP", "WBMP"
     *
     * 缩略图为上传文件名+缩略图后缀 _150x150,如 xxx.jpg,缩略图为 xxx_150x150.jpg
     *
     * 实际样例如下
     *
     *  原图   http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374.jpg
     *  缩略图 http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374_150x150.jpg
     *
     *
     * @param inputStream 输入流
     * @param fileSize 文件大小
     * @param fileExtName 文件扩展名
     * @param metaDataSet 元数据集合
     * @return 存储路径
     */
    StorePath uploadImageAndCrtThumbImage(InputStream inputStream, long fileSize, String fileExtName,
                                          Set<MetaData> metaDataSet);

    /**
     * 上传图片
     * 可通过fastImageFile对象配置
     * 1. 上传图像分组
     * 2. 上传元数据metaDataSet
     * 3. 是否生成缩略图
     *   3.1 根据默认配置生成缩略图
     *   3.2 根据指定尺寸生成缩略图
     *   3.3 根据指定比例生成缩略图
     *
     * @param fastImageFile 上传文件配置
     * @return 存储路径
     */
    StorePath uploadImage(FastImageFile fastImageFile);


    /**
     * 上传文件
     * 可通过fastFile对象配置
     * 1. 上传图像分组
     * 2. 上传元数据metaDataSet
     * @param fastFile 上传文件配置
     * @return 存储路径
     */
    StorePath uploadFile(FastFile fastFile);

    /**
     * 删除文件
     *
     * @param filePath 文件路径(groupName/path)
     */
    void deleteFile(String filePath);

}
