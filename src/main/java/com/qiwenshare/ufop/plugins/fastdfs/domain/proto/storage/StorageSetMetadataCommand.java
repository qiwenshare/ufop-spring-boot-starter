package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage;

import com.qiwenshare.ufop.plugins.fastdfs.domain.fdfs.MetaData;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.AbstractFdfsCommand;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.FdfsResponse;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.enums.StorageMetadataSetType;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal.StorageSetMetadataRequest;

import java.util.Set;

/**
 * 设置文件标签
 *
 * @author tobato
 */
public class StorageSetMetadataCommand extends AbstractFdfsCommand<Void> {

    /**
     * 设置文件标签(元数据)
     *
     * @param groupName 组名
     * @param path      路径
     * @param metaDataSet 元数据集合
     * @param type       设置类型
     */
    public StorageSetMetadataCommand(String groupName, String path, Set<MetaData> metaDataSet,
                                     StorageMetadataSetType type) {
        this.request = new StorageSetMetadataRequest(groupName, path, metaDataSet, type);
        // 输出响应
        this.response = new FdfsResponse<Void>() {
            // default response
        };
    }

}
