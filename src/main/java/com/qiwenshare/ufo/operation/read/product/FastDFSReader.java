package com.qiwenshare.ufo.operation.read.product;

import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.ufo.exception.ReadException;
import com.qiwenshare.ufo.operation.download.domain.DownloadFile;
import com.qiwenshare.ufo.operation.read.Reader;
import com.qiwenshare.ufo.operation.read.domain.ReadFile;
import com.qiwenshare.ufo.util.PathUtil;
import com.qiwenshare.ufo.util.ReadFileUtils;

import java.io.*;

public class FastDFSReader extends Reader {
    @Override
    public String read(ReadFile readFile) {
//        String extendName = FileUtil.getFileExtendName(readFile.getFileUrl());
//        return null;

        String fileUrl = readFile.getFileUrl();
        String fileType = FileUtil.getFileExtendName(fileUrl);
        try {
            return ReadFileUtils.getContentByInputStream(fileType, getInputStream(readFile.getFileUrl()));
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        }
    }

    public InputStream getInputStream(String fileUrl) {
        //设置文件路径
        File file = FileOperation.newFile(PathUtil.getStaticPath() + fileUrl);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;

    }
}
