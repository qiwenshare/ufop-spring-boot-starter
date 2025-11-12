package com.qiwenshare.ufop.plugins.fastdfs.service;

import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorePath;

import java.io.InputStream;


/**
 * 支持断点续传的文件服务接口
 * 适合处理大文件，分段传输
 *
 * @author tobato
 */
public interface AppendFileStorageClient extends GenerateStorageClient {

    /**
     * 上传支持断点续传的文件
     *
     * @param groupName 组名
     * @param inputStream 输入流
     * @param fileSize 文件大小
     * @param fileExtName 文件扩展名
     * @return 存储路径
     */
    StorePath uploadAppenderFile(String groupName, InputStream inputStream, long fileSize, String fileExtName);

    /**
     * 断点续传文件
     *
     * @param groupName 组名
     * @param path 存储路径
     * @param inputStream 输入流
     * @param fileSize 文件大小
     */
    void appendFile(String groupName, String path, InputStream inputStream, long fileSize);

    /**
     * 修改续传文件的内容
     *
     * @param groupName 组名
     * @param path 存储路径
     * @param inputStream 输入流
     * @param fileSize 文件大小
     * @param fileOffset 文件偏移量
     */
    void modifyFile(String groupName, String path, InputStream inputStream, long fileSize, long fileOffset);

    /**
     * 清除续传类型文件的内容
     *
     * @param groupName 组名
     * @param path 存储路径
     * @param truncatedFileSize 截断文件大小
     */
    void truncateFile(String groupName, String path, long truncatedFileSize);

    /**
     * 清除续传类型文件的内容
     *
     * @param groupName 组名
     * @param path 存储路径
     */
    void truncateFile(String groupName, String path);

}
