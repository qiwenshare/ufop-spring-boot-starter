package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage;

import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.AbstractFdfsCommand;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.FdfsResponse;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal.StorageDeleteFileRequest;

/**
 * 文件删除命令
 *
 * @author tobato
 */
public class StorageDeleteFileCommand extends AbstractFdfsCommand<Void> {


    /**
     * 文件删除命令
     * @param groupName 组名
     * @param path      路径
     */
    public StorageDeleteFileCommand(String groupName, String path) {
        super();
        this.request = new StorageDeleteFileRequest(groupName, path);
        // 输出响应
        this.response = new FdfsResponse<Void>() {
            // default response
        };
    }

}
