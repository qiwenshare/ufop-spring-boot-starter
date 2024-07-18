package com.qiwenshare.ufop.operation.delete.product;

import com.qcloud.cos.COSClient;
import com.qiwenshare.ufop.config.TencentConfig;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufop.util.TencentUtils;
import com.qiwenshare.ufop.util.UFOPUtils;


public class TencentCOSDeleter extends Deleter {
    private TencentConfig tencentConfig;

    public TencentCOSDeleter(){

    }

    public TencentCOSDeleter(TencentConfig tencentConfig) {
        this.tencentConfig = tencentConfig;
    }

    @Override
    public void delete(DeleteFile deleteFile) {
        COSClient cosClient = TencentUtils.getCOSClient(tencentConfig);
        try {
            cosClient.deleteObject(tencentConfig.getCos().getBucketName(), UFOPUtils.getTencentObjectNameByFileUrl(deleteFile.getFileUrl()));
        } finally {
            cosClient.shutdown();
        }
        deleteCacheFile(deleteFile);
    }
}
