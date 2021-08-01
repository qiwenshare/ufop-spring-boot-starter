package com.qiwenshare.ufop.operation.copy;

import com.qiwenshare.ufop.operation.copy.domain.CopyFile;

import java.io.InputStream;

public abstract class Copier {
    /**
     * 将服务器文件流拷贝到云端，并返回文件url
     * @param inputStream 文件流
     * @param copyFile 拷贝文件相关参数
     * @return 文件url
     */
    public abstract String copy(InputStream inputStream, CopyFile copyFile);
}
