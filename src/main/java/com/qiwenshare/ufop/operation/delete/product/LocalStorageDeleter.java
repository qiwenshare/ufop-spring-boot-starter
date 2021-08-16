package com.qiwenshare.ufop.operation.delete.product;

import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        File localSaveFile = UFOPUtils.getLocalSaveFile(deleteFile.getFileUrl());
        if (localSaveFile.exists()) {
            localSaveFile.delete();
        }

        String extendName = UFOPUtils.getFileExtendName(deleteFile.getFileUrl());
        if (UFOPUtils.isImageFile(extendName)) {
            File cacheFile = UFOPUtils.getCacheFile(deleteFile.getFileUrl());
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
        }
    }
}
