package com.qiwenshare.ufop.operation.delete.product;

import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufop.util.PathUtil;
import org.springframework.stereotype.Component;

@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        FileOperation.deleteFile(PathUtil.getStaticPath() + deleteFile.getFileUrl());
        String extendName = FileUtil.getFileExtendName(deleteFile.getFileUrl());
        if (FileUtil.isImageFile(extendName)) {
            FileOperation.deleteFile(PathUtil.getStaticPath() + deleteFile.getFileUrl().replace("." + extendName, "_min." + extendName));
        }
    }
}
