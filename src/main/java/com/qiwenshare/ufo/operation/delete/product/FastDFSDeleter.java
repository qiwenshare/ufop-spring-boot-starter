package com.qiwenshare.ufo.operation.delete.product;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.ufo.operation.delete.Deleter;
import com.qiwenshare.ufo.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufo.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FastDFSDeleter extends Deleter {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Override
    public void delete(DeleteFile deleteFile) {
        fastFileStorageClient.deleteFile(deleteFile.getFileUrl().replace("M00", "group1"));
        if (FileUtil.isImageFile(FileUtil.getFileExtendName(deleteFile.getFileUrl()))) {
            FileOperation.deleteFile(PathUtil.getStaticPath() + "cache" + File.separator + deleteFile.getFileUrl());
        }
    }
}
