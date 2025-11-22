package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.storage.internal;

import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.CmdConstants;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.FdfsRequest;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.ProtoHead;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.mapper.DynamicFieldType;
import com.qiwenshare.ufop.plugins.fastdfs.domain.proto.mapper.FdfsColumn;

import java.nio.charset.Charset;

/**
 * 文件Truncate命令
 * 使用限制：创建文件时候需要采用(源追加)模式,之后才能Truncate
 * size使用也有限制
 *
 * @author tobato
 */
public class StorageTruncateRequest extends FdfsRequest {

    /**
     * 文件路径长度
     */
    @FdfsColumn()
    private long pathSize;
    /**
     * 截取文件长度
     */
    @FdfsColumn(index = 1)
    private long fileSize;
    /**
     * 文件路径
     */
    @FdfsColumn(index = 2, dynamicField = DynamicFieldType.allRestByte)
    private String path;

    /**
     * 文件Truncate命令
     *
     * @param path       文件路径
     * @param fileSize 截取文件长度
     */
    public StorageTruncateRequest(String path, long fileSize) {
        super();
        this.fileSize = fileSize;
        this.path = path;
        head = new ProtoHead(CmdConstants.STORAGE_PROTO_CMD_TRUNCATE_FILE);
    }

    /**
     * 打包参数
     *
     * @param charset  字符集
     * @return  参数域字节数组
     */
    @Override
    public byte[] encodeParam(Charset charset) {
        // 运行时参数在此计算值
        this.pathSize = path.getBytes(charset).length;
        return super.encodeParam(charset);
    }

    public long getPathSize() {
        return pathSize;
    }

    public void setPathSize(long pathSize) {
        this.pathSize = pathSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        return "StorageAppendFileRequest [pathSize=" + pathSize + ", fileSize=" + fileSize + ", path=" + path + "]";
    }

}
