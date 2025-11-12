package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage;

import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.StorePath;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.AbstractFdfsCommand;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.FdfsResponse;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal.StorageUploadFileRequest;

import java.io.InputStream;

/**
 * 文件上传命令
 *
 * @author tobato
 */
public class StorageUploadFileCommand extends AbstractFdfsCommand<StorePath> {


    /**
     * 文件上传命令
     *
     * @param storeIndex  存储索引
     * @param inputStream 输入流
     * @param fileExtName 文件扩展名
     * @param fileSize    文件大小
     * @param isAppenderFile 是否追加文件
     */
    public StorageUploadFileCommand(byte storeIndex, InputStream inputStream, String fileExtName, long fileSize,
                                    boolean isAppenderFile) {
        super();
        this.request = new StorageUploadFileRequest(storeIndex, inputStream, fileExtName, fileSize, isAppenderFile);
        // 输出响应
        this.response = new FdfsResponse<StorePath>() {
            // default response
        };
    }

}
