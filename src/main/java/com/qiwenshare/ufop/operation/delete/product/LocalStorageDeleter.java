package com.qiwenshare.ufop.operation.delete.product;

import com.qiwenshare.ufop.exception.operation.DeleteException;
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
            boolean result = localSaveFile.delete();
            if (!result) {
                throw new DeleteException("删除本地文件失败");
            }
        }

        deleteCacheFile(deleteFile);
    }
}
