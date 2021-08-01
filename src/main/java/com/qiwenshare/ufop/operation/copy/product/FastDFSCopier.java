package com.qiwenshare.ufop.operation.copy.product;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

public class FastDFSCopier extends Copier {
    @Resource
    AppendFileStorageClient defaultAppendFileStorageClient;

    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        StorePath storePath = new StorePath();
        try {
            storePath = defaultAppendFileStorageClient.uploadAppenderFile("group1", inputStream,
                    inputStream.available(), copyFile.getExtendName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storePath.getPath();
    }
}
