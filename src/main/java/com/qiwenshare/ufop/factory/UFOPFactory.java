package com.qiwenshare.ufop.factory;

import com.qiwenshare.ufop.autoconfiguration.UFOPProperties;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.product.AliyunOSSCopier;
import com.qiwenshare.ufop.operation.copy.product.FastDFSCopier;
import com.qiwenshare.ufop.operation.copy.product.LocalStorageCopier;
import com.qiwenshare.ufop.operation.copy.product.MinioCopier;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.product.AliyunOSSDeleter;
import com.qiwenshare.ufop.operation.delete.product.FastDFSDeleter;
import com.qiwenshare.ufop.operation.delete.product.LocalStorageDeleter;
import com.qiwenshare.ufop.operation.delete.product.MinioDeleter;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.product.AliyunOSSDownloader;
import com.qiwenshare.ufop.operation.download.product.FastDFSDownloader;
import com.qiwenshare.ufop.operation.download.product.LocalStorageDownloader;
import com.qiwenshare.ufop.operation.download.product.MinioDownloader;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.product.AliyunOSSPreviewer;
import com.qiwenshare.ufop.operation.preview.product.FastDFSPreviewer;
import com.qiwenshare.ufop.operation.preview.product.LocalStoragePreviewer;
import com.qiwenshare.ufop.operation.preview.product.MinioPreviewer;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.product.AliyunOSSReader;
import com.qiwenshare.ufop.operation.read.product.FastDFSReader;
import com.qiwenshare.ufop.operation.read.product.LocalStorageReader;
import com.qiwenshare.ufop.operation.read.product.MinioReader;
import com.qiwenshare.ufop.operation.rename.Renamer;
import com.qiwenshare.ufop.operation.rename.product.AliyunOSSRenamer;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.product.AliyunOSSUploader;
import com.qiwenshare.ufop.operation.upload.product.FastDFSUploader;
import com.qiwenshare.ufop.operation.upload.product.LocalStorageUploader;
import com.qiwenshare.ufop.operation.upload.product.MinioUploader;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.product.AliyunOSSWriter;
import com.qiwenshare.ufop.operation.write.product.FastDFSWriter;
import com.qiwenshare.ufop.operation.write.product.LocalStorageWriter;
import com.qiwenshare.ufop.operation.write.product.MinioWriter;

import javax.annotation.Resource;

public class UFOPFactory {
    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyunConfig;
    private ThumbImage thumbImage;
    private MinioConfig minioConfig;
    @Resource
    private FastDFSCopier fastDFSCopier;
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
    @Resource
    private AliyunOSSUploader aliyunOSSUploader;
    @Resource
    private MinioUploader minioUploader;

    public UFOPFactory() {
    }

    public UFOPFactory(UFOPProperties ufopProperties) {
        this.storageType = ufopProperties.getStorageType();
        this.localStoragePath = ufopProperties.getLocalStoragePath();
        this.aliyunConfig = ufopProperties.getAliyun();
        this.thumbImage = ufopProperties.getThumbImage();
        this.minioConfig = ufopProperties.getMinio();
    }

    public Uploader getUploader() {

        int type = Integer.parseInt(storageType);

        if (StorageTypeEnum.LOCAL.getCode() == type) {
            return new LocalStorageUploader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type) {
            return aliyunOSSUploader;
        } else if (StorageTypeEnum.FAST_DFS.getCode() == type) {
            return fastDFSUploader;
        } else if (StorageTypeEnum.MINIO.getCode() == type) {
            return minioUploader;
        }
        return null;
    }


    public Downloader getDownloader(int storageType) {
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            return new LocalStorageDownloader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            return new AliyunOSSDownloader(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            return fastDFSDownloader;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            return new MinioDownloader(minioConfig);
        }
        return null;
    }


    public Deleter getDeleter(int storageType) {
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            return new LocalStorageDeleter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            return new AliyunOSSDeleter(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            return fastDFSDeleter;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            return new MinioDeleter(minioConfig);
        }
        return null;
    }

    public Renamer getRenamer(int storageType) {
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            return null;
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            return new AliyunOSSRenamer(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            return null;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            return null;
        }
        return null;
    }

    public Reader getReader(int storageType) {
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            return new LocalStorageReader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            return new AliyunOSSReader(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            return fastDFSReader;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            return new MinioReader(minioConfig);
        }
        return null;
    }

    public Writer getWriter(int storageType) {
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            return new LocalStorageWriter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            return new AliyunOSSWriter(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            return fastDFSWriter;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            return new MinioWriter(minioConfig);
        }
        return null;
    }

    public Previewer getPreviewer(int storageType) {
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            return new LocalStoragePreviewer(thumbImage);
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            return new AliyunOSSPreviewer(aliyunConfig, thumbImage);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            return fastDFSPreviewer;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            return new MinioPreviewer(minioConfig, thumbImage);
        }
        return null;
    }

    public Copier getCopier() {
        int type = Integer.parseInt(storageType);

        if (StorageTypeEnum.LOCAL.getCode() == type) {
            return new LocalStorageCopier();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type) {
            return new AliyunOSSCopier(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == type) {
            return fastDFSCopier;
        } else if (StorageTypeEnum.MINIO.getCode() == type) {
            return new MinioCopier(minioConfig);
        }
        return null;
    }
}
