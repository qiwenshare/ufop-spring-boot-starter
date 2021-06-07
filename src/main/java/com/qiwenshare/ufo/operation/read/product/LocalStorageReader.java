package com.qiwenshare.ufo.operation.read.product;

import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.ufo.exception.ReadException;
import com.qiwenshare.ufo.operation.read.Reader;
import com.qiwenshare.ufo.operation.read.domain.ReadFile;
import com.qiwenshare.ufo.util.PathUtil;
import com.qiwenshare.ufo.util.ReadFileUtils;

import java.io.IOException;

public class LocalStorageReader extends Reader {
    @Override
    public String read(ReadFile readFile) {

        String fileContent;
        try {
            fileContent = ReadFileUtils.getContentByPath(PathUtil.getStaticPath() + readFile.getFileUrl());
        } catch (IOException e) {
            throw new ReadException("文件读取出现异常", e);
        }
        return fileContent;
    }
}
