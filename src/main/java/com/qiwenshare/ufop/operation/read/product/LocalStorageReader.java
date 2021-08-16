package com.qiwenshare.ufop.operation.read.product;

import com.qiwenshare.ufop.exception.ReadException;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.domain.ReadFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import com.qiwenshare.ufop.util.ReadFileUtils;

import java.io.IOException;

public class LocalStorageReader extends Reader {
    @Override
    public String read(ReadFile readFile) {

        String fileContent;
        try {
            fileContent = ReadFileUtils.getContentByPath(UFOPUtils.getStaticPath() + readFile.getFileUrl());
        } catch (IOException e) {
            throw new ReadException("文件读取出现异常", e);
        }
        return fileContent;
    }
}
