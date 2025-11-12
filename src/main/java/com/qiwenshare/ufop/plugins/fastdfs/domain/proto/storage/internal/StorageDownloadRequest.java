package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal;

import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.CmdConstants;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.FdfsRequest;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.OtherConstants;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.ProtoHead;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.mapper.DynamicFieldType;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.mapper.FdfsColumn;

/**
 * 文件下载请求
 *
 * @author tobato
 */
public class StorageDownloadRequest extends FdfsRequest {

    /**
     * 开始位置
     */
    @FdfsColumn(index = 0)
    private long fileOffset;
    /**
     * 读取文件长度
     */
    @FdfsColumn(index = 1)
    private long downloadBytes;
    /**
     * 组名
     */
    @FdfsColumn(index = 2, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
    private String groupName;
    /**
     * 文件路径
     */
    @FdfsColumn(index = 3, dynamicField = DynamicFieldType.allRestByte)
    private String path;

    /**
     * 文件下载请求
     *
     * @param groupName  组名
     * @param path       文件路径
     * @param fileOffset 开始位置
     * @param downloadBytes 读取文件长度
     */
    public StorageDownloadRequest(String groupName, String path, long fileOffset, long downloadBytes) {
        super();
        this.groupName = groupName;
        this.downloadBytes = downloadBytes;
        this.path = path;
        this.fileOffset = fileOffset;
        head = new ProtoHead(CmdConstants.STORAGE_PROTO_CMD_DOWNLOAD_FILE);

    }

    public long getFileOffset() {
        return fileOffset;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getPath() {
        return path;
    }

    public long getDownloadBytes() {
        return downloadBytes;
    }
}
