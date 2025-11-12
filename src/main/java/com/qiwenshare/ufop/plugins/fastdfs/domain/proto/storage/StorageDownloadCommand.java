package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage;

import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.AbstractFdfsCommand;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal.StorageDownloadRequest;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal.StorageDownloadResponse;

/**
 * 文件下载命令
 *
 * @param <T>  回调结果类型
 * @author tobato
 */
public class StorageDownloadCommand<T> extends AbstractFdfsCommand<T> {

    /**
     * 下载部分文件
     *
     * @param groupName 组名
     * @param path      路径
     * @param fileOffset  开始位置
     * @param downloadBytes 下载长度
     * @param callback  回调函数
     */
    public StorageDownloadCommand(String groupName, String path, long fileOffset, long downloadBytes,
                                  DownloadCallback<T> callback) {
        super();
        this.request = new StorageDownloadRequest(groupName, path, fileOffset, downloadBytes);
        // 输出响应
        this.response = new StorageDownloadResponse<T>(callback);
    }

    /**
     * 下载文件
     *
     * @param groupName 组名
     * @param path      路径
     * @param callback  回调函数
     */
    public StorageDownloadCommand(String groupName, String path, DownloadCallback<T> callback) {
        super();
        this.request = new StorageDownloadRequest(groupName, path, 0, 0);
        // 输出响应
        this.response = new StorageDownloadResponse<T>(callback);
    }
}
