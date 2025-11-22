package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal;

import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.CmdConstants;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.FdfsRequest;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.OtherConstants;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.ProtoHead;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.mapper.DynamicFieldType;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.mapper.FdfsColumn;

/**
 * 查询文件信息命令
 *
 * @author tobato
 */
public class StorageQueryFileInfoRequest extends FdfsRequest {

    /**
     * 组名
     */
    @FdfsColumn(max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
    private String groupName;
    /**
     * 路径名
     */
    @FdfsColumn(index = 1, dynamicField = DynamicFieldType.allRestByte)
    private String path;

    /**
     * 删除文件命令
     *
     * @param groupName  组名
     * @param path       文件路径
     */
    public StorageQueryFileInfoRequest(String groupName, String path) {
        super();
        this.groupName = groupName;
        this.path = path;
        this.head = new ProtoHead(CmdConstants.STORAGE_PROTO_CMD_QUERY_FILE_INFO);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
