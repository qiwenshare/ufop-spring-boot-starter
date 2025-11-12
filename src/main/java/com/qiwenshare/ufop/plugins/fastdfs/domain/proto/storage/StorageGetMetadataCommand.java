package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage;

import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.MetaData;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.AbstractFdfsCommand;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal.StorageGetMetadataRequest;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal.StorageGetMetadataResponse;

import java.util.Set;

/**
 * 设置文件标签
 *
 * @author tobato
 */
public class StorageGetMetadataCommand extends AbstractFdfsCommand<Set<MetaData>> {


    /**
     * 设置文件标签(元数据)
     *
     * @param groupName 组名
     * @param path      路径
     */
    public StorageGetMetadataCommand(String groupName, String path) {
        this.request = new StorageGetMetadataRequest(groupName, path);
        // 输出响应
        this.response = new StorageGetMetadataResponse();
    }

}
