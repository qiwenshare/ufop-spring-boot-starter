package com.qiwenshare.ufop.factory;

import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.product.AliyunOSSDeleter;
import com.qiwenshare.ufop.operation.delete.product.FastDFSDeleter;
import com.qiwenshare.ufop.operation.delete.product.LocalStorageDeleter;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.product.AliyunOSSDownloader;
import com.qiwenshare.ufop.operation.download.product.FastDFSDownloader;
import com.qiwenshare.ufop.operation.download.product.LocalStorageDownloader;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.product.AliyunOSSPreviewer;
import com.qiwenshare.ufop.operation.preview.product.FastDFSPreviewer;
import com.qiwenshare.ufop.operation.preview.product.LocalStoragePreviewer;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.product.AliyunOSSReader;
import com.qiwenshare.ufop.operation.read.product.FastDFSReader;
import com.qiwenshare.ufop.operation.read.product.LocalStorageReader;
import com.qiwenshare.ufop.operation.rename.Renamer;
import com.qiwenshare.ufop.operation.rename.product.AliyunOSSRenamer;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.product.AliyunOSSUploader;
import com.qiwenshare.ufop.operation.upload.product.FastDFSUploader;
import com.qiwenshare.ufop.operation.upload.product.LocalStorageUploader;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.product.AliyunOSSWriter;
import com.qiwenshare.ufop.operation.write.product.FastDFSWriter;
import com.qiwenshare.ufop.operation.write.product.LocalStorageWriter;
import lombok.Data;

import javax.annotation.Resource;

@Data
public class UFOPFactory {
    private String storageType;

    @Resource
    private FastDFSUploader fastDFSUploader;
    @Resource
    private FastDFSDownloader fastDFSDownloader;
    @Resource
    private  FastDFSDeleter fastDFSDeleter;
    @Resource
    private FastDFSReader fastDFSReader;
    @Resource
    private FastDFSPreviewer fastDFSPreviewer;
    @Resource
    private FastDFSWriter fastDFSWriter;

    public UFOPFactory() {
    }

    public UFOPFactory(String storageType) {
        this.storageType = storageType;
    }

    public Uploader getUploader() {

        int type = Integer.parseInt(storageType);

        if (StorageTypeEnum.LOCAL.getStorageType() == type) {
            return new LocalStorageUploader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getStorageType() == type) {
            return new AliyunOSSUploader();
        } else if (StorageTypeEnum.FAST_DFS.getStorageType() == type) {
            return fastDFSUploader;
        }
        return null;
    }


    public Downloader getDownloader(int storageType) {
        if (StorageTypeEnum.LOCAL.getStorageType() == storageType) {
            return new LocalStorageDownloader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getStorageType() == storageType) {
            return new AliyunOSSDownloader();
        } else if (StorageTypeEnum.FAST_DFS.getStorageType() == storageType) {
            return fastDFSDownloader;
        }
        return null;
    }


    public Deleter getDeleter(int storageType) {
        if (StorageTypeEnum.LOCAL.getStorageType() == storageType) {
            return new LocalStorageDeleter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getStorageType() == storageType) {
            return new AliyunOSSDeleter();
        } else if (StorageTypeEnum.FAST_DFS.getStorageType() == storageType) {
            return fastDFSDeleter;
        }
        return null;
    }

    public Renamer getRenamer(int storageType) {
        if (StorageTypeEnum.LOCAL.getStorageType() == storageType) {
            return null;
        } else if (StorageTypeEnum.ALIYUN_OSS.getStorageType() == storageType) {
            return new AliyunOSSRenamer();
        } else if (StorageTypeEnum.FAST_DFS.getStorageType() == storageType) {
            return null;
        }
        return null;
    }

    public Reader getReader(int storageType) {
        if (StorageTypeEnum.LOCAL.getStorageType() == storageType) {
            return new LocalStorageReader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getStorageType() == storageType) {
            return new AliyunOSSReader();
        } else if (StorageTypeEnum.FAST_DFS.getStorageType() == storageType) {
            return fastDFSReader;
        }
        return null;
    }

    public Writer getWriter(int storageType) {
        if (StorageTypeEnum.LOCAL.getStorageType() == storageType) {
            return new LocalStorageWriter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getStorageType() == storageType) {
            return new AliyunOSSWriter();
        } else if (StorageTypeEnum.FAST_DFS.getStorageType() == storageType) {
            return fastDFSWriter;
        }
        return null;
    }

    public Previewer getPreviewer(int storageType) {
        if (StorageTypeEnum.LOCAL.getStorageType() == storageType) {
            return new LocalStoragePreviewer();
        } else if (StorageTypeEnum.ALIYUN_OSS.getStorageType() == storageType) {
            return new AliyunOSSPreviewer();
        } else if (StorageTypeEnum.FAST_DFS.getStorageType() == storageType) {
            return fastDFSPreviewer;
        }
        return null;
    }
}
