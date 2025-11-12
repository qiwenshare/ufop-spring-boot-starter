package com.qiwenshare.ufop.plugins.fastdfs.service;

import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.FileInfo;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.MetaData;
import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorePath;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.DownloadCallback;

import java.io.InputStream;
import java.util.Set;

/**
 * 基本文件存储客户端操作
 *
 * @author tobato
 */
public interface GenerateStorageClient {

    /**
     * 上传文件(文件不可修改)
     * <p>
     * 文件上传后不可以修改，如果要修改则删除以后重新上传
     *
     * @param groupName 分组名称
     * @param inputStream 输入流
     * @param fileSize 文件大小
     * @param fileExtName 文件扩展名
     * @return 存储路径
     */
    StorePath uploadFile(String groupName, InputStream inputStream, long fileSize, String fileExtName);

    /**
     * 上传从文件
     *
     * @param groupName 分组名称
     * @param masterFilename 主文件名
     * @param inputStream 输入流
     * @param fileSize 文件大小
     * @param prefixName 前缀名
     * @param fileExtName 文件扩展名
     * @return 存储路径
     */
    StorePath uploadSlaveFile(String groupName, String masterFilename, InputStream inputStream, long fileSize,
                              String prefixName, String fileExtName);

    /**
     * 获取文件元信息
     *
     * @param groupName 分组名称
     * @param path 文件路径
     * @return 元数据集合
     */
    Set<MetaData> getMetadata(String groupName, String path);

    /**
     * 修改文件元信息（覆盖）
     *
     * @param groupName 分组名称
     * @param path 文件路径
     * @param metaDataSet 元数据集合
     */
    void overwriteMetadata(String groupName, String path, Set<MetaData> metaDataSet);

    /**
     * 修改文件元信息（合并）
     *
     * @param groupName 分组名称
     * @param path 文件路径
     * @param metaDataSet 元数据集合
     */
    void mergeMetadata(String groupName, String path, Set<MetaData> metaDataSet);

    /**
     * 查看文件的信息
     *
     * @param groupName 分组名称
     * @param path 文件路径
     * @return 文件信息
     */
    FileInfo queryFileInfo(String groupName, String path);

    /**
     * 删除文件
     *
     * @param groupName 分组名称
     * @param path 文件路径
     */
    void deleteFile(String groupName, String path);

    /**
     * 下载整个文件
     *
     * @param groupName 分组名称
     * @param path 文件路径
     * @param callback 下载回调
     * @return 回调结果
     */
    <T> T downloadFile(String groupName, String path, DownloadCallback<T> callback);

    /**
     * 下载文件片段
     * @param <T> 回调结果类型
     * @param groupName 分组名称
     * @param path 文件路径
     * @param fileOffset 文件偏移量
     * @param fileSize 文件大小
     * @param callback 下载回调
     * @return 回调结果
     */
    <T> T downloadFile(String groupName, String path, long fileOffset, long fileSize, DownloadCallback<T> callback);

}
