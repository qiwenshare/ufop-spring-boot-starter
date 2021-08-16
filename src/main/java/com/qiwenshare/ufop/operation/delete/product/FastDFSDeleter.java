package com.qiwenshare.ufop.operation.delete.product;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufop.util.UFOPUtils;
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
        if (UFOPUtils.isImageFile(UFOPUtils.getFileExtendName(deleteFile.getFileUrl()))) {
            File cacheFile = UFOPUtils.getCacheFile(deleteFile.getFileUrl());
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
        }
    }
}
