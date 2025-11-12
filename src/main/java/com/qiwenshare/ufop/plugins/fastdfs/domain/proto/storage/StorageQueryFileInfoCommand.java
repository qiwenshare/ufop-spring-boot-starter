package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage;

import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.FileInfo;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.AbstractFdfsCommand;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.FdfsResponse;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal.StorageQueryFileInfoRequest;

/**
 * 文件查询命令
 *
 * @author tobato
 */
public class StorageQueryFileInfoCommand extends AbstractFdfsCommand<FileInfo> {

    /**
     * 文件查询命令
     *
     * @param groupName 组名
     * @param path      路径
     */
    public StorageQueryFileInfoCommand(String groupName, String path) {
        super();
        this.request = new StorageQueryFileInfoRequest(groupName, path);
        // 输出响应
        this.response = new FdfsResponse<FileInfo>() {
            // default response
        };
    }

}
