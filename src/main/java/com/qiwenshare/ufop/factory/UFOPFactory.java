package com.qiwenshare.ufop.factory;

import com.qiwenshare.ufop.autoconfiguration.UFOPProperties;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.config.QiniuyunConfig;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.product.*;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.product.*;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.product.*;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.product.*;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.product.*;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.product.*;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.product.*;

import javax.annotation.Resource;

public class UFOPFactory {
    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyunConfig;
    private ThumbImage thumbImage;
    private MinioConfig minioConfig;
    private QiniuyunConfig qiniuyunConfig;
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
    @Resource
    private QiniuyunKodoUploader qiniuyunKodoUploader;

    public UFOPFactory() {
    }

    public UFOPFactory(UFOPProperties ufopProperties) {
        this.storageType = ufopProperties.getStorageType();
        this.localStoragePath = ufopProperties.getLocalStoragePath();
        this.aliyunConfig = ufopProperties.getAliyun();
        this.thumbImage = ufopProperties.getThumbImage();
        this.minioConfig = ufopProperties.getMinio();
        this.qiniuyunConfig = ufopProperties.getQiniuyun();
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
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == type) {
            return qiniuyunKodoUploader;
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
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            return new QiniuyunKodoDownloader(qiniuyunConfig);
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
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            return new QiniuyunKodoDeleter(qiniuyunConfig);
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
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            return new QiniuyunKodoReader(qiniuyunConfig);
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
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            return new QiniuyunKodoWriter(qiniuyunConfig);
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
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            return new QiniuyunKodoPreviewer(qiniuyunConfig, thumbImage);
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
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == type) {
            return new QiniuyunKodoCopier(qiniuyunConfig);
        }
        return null;
    }
}
