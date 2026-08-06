package com.qiwenshare.ufop.factory;

import com.aliyun.oss.OSS;
import com.qiwenshare.ufop.autoconfiguration.UFOPProperties;
import io.minio.MinioClient;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.config.QiniuyunConfig;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.domain.ThumbImage;
import com.qiwenshare.ufop.exception.operation.*;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.product.*;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.product.*;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.product.*;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.product.*;
import com.qiwenshare.ufop.operation.query.Querier;
import com.qiwenshare.ufop.operation.query.product.AliyunOSSQuerier;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.product.*;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.product.*;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.product.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class UFOPFactory {
    private String storageType;
    private AliyunConfig aliyunConfig;
    private ThumbImage thumbImage;
    private MinioConfig minioConfig;
    private QiniuyunConfig qiniuyunConfig;

    // FastDFS 相关 (可选)
    @Autowired(required = false)
    private FastDFSCopier fastDFSCopier;
    @Autowired(required = false)
    private FastDFSUploader fastDFSUploader;
    @Autowired(required = false)
    private FastDFSDownloader fastDFSDownloader;
    @Autowired(required = false)
    private FastDFSDeleter fastDFSDeleter;
    @Autowired(required = false)
    private FastDFSReader fastDFSReader;
    @Autowired(required = false)
    private FastDFSPreviewer fastDFSPreviewer;
    @Autowired(required = false)
    private FastDFSWriter fastDFSWriter;

    // 本地存储 (必需)
    @Resource
    private LocalStoragePreviewer localStoragePreviewer;
    @Resource
    private LocalStorageUploader localStorageUploader;
    @Resource
    private LocalStorageDownloader localStorageDownloader;
    @Resource
    private LocalStorageReader localStorageReader;

    // 阿里云 OSS 相关 (可选)
    @Autowired(required = false)
    private AliyunOSSUploader aliyunOSSUploader;
    @Autowired(required = false)
    private AliyunOSSQuerier aliyunOSSQuerier;
    @Autowired(required = false)
    private OSS ossClient;

    // MinIO 相关 (可选)
    @Autowired(required = false)
    private MinioUploader minioUploader;
    @Autowired(required = false)
    private MinioClient minioClient;

    // 七牛云相关 (可选)
    @Autowired(required = false)
    private QiniuyunKodoUploader qiniuyunKodoUploader;
    public UFOPFactory() {
    }

    public UFOPFactory(UFOPProperties ufopProperties) {
        this.storageType = ufopProperties.getStorageType();
        this.aliyunConfig = ufopProperties.getAliyun();
        this.thumbImage = ufopProperties.getThumbImage();
        this.minioConfig = ufopProperties.getMinio();
        this.qiniuyunConfig = ufopProperties.getQiniuyun();
    }

    public Uploader getUploader() {

        int type = Integer.parseInt(storageType);
        Uploader uploader = null;
        if (StorageTypeEnum.LOCAL.getCode() == type) {
            uploader = localStorageUploader;
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type) {
            uploader = aliyunOSSUploader;
        } else if (StorageTypeEnum.FAST_DFS.getCode() == type) {
            uploader = fastDFSUploader;
        } else if (StorageTypeEnum.MINIO.getCode() == type) {
            uploader = minioUploader;
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == type) {
            uploader = qiniuyunKodoUploader;
        }
        if (uploader == null) {
            log.error("上传失败，文件存储类型不支持预览，storageType:{}", storageType);
            throw new UploadException("上传失败");
        }
        return uploader;
    }

    public Uploader getUploader(int storageType) {

        Uploader uploader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            uploader = localStorageUploader;
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            uploader = aliyunOSSUploader;
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            uploader = fastDFSUploader;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            uploader = minioUploader;
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            uploader = qiniuyunKodoUploader;
        }
        if (uploader == null) {
            log.error("上传失败，文件存储类型不支持预览，storageType:{}", storageType);
            throw new UploadException("上传失败");
        }
        return uploader;
    }


    public Downloader getDownloader(int storageType) {
        Downloader downloader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            downloader = localStorageDownloader;
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            downloader = new AliyunOSSDownloader(aliyunConfig, ossClient);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            downloader = fastDFSDownloader;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            downloader = new MinioDownloader(minioConfig, minioClient);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            downloader = new QiniuyunKodoDownloader(qiniuyunConfig);
        }
        if (downloader == null) {
            log.error("下载失败，文件存储类型不支持预览，storageType:{}", storageType);
            throw new DownloadException("下载失败");
        }
        return downloader;
    }


    public Deleter getDeleter(int storageType) {
        Deleter deleter = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            deleter = new LocalStorageDeleter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            deleter = new AliyunOSSDeleter(aliyunConfig, ossClient);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            deleter = fastDFSDeleter;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            deleter = new MinioDeleter(minioConfig, minioClient);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            deleter = new QiniuyunKodoDeleter(qiniuyunConfig);
        }
        if (deleter == null) {
            log.error("删除失败，文件存储类型不支持预览，storageType:{}", storageType);
            throw new DeleteException("删除失败");
        }
        return deleter;
    }

    public Reader getReader(int storageType) {
        Reader reader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            reader = localStorageReader;
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            reader = new AliyunOSSReader(aliyunConfig, ossClient);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            reader = fastDFSReader;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            reader = new MinioReader(minioConfig, minioClient);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            reader = new QiniuyunKodoReader(qiniuyunConfig);
        }
        if (reader == null) {
            log.error("读取失败，文件存储类型不支持预览，storageType:{}", storageType);
            throw new ReadException("读取失败");
        }
        return reader;
    }

    public Writer getWriter(int storageType) {
        Writer writer = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            writer = new LocalStorageWriter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            writer = new AliyunOSSWriter(aliyunConfig, ossClient);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            writer = fastDFSWriter;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            writer = new MinioWriter(minioConfig, minioClient);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            writer = new QiniuyunKodoWriter(qiniuyunConfig);
        }
        if (writer == null) {
            log.error("写入失败，文件存储类型不支持预览，storageType:{}", storageType);
            throw new WriteException("写入失败");
        }
        return writer;
    }

    public Previewer getPreviewer(int storageType) {
        Previewer previewer = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            previewer = localStoragePreviewer;
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            previewer = new AliyunOSSPreviewer(aliyunConfig, thumbImage, ossClient);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            previewer = fastDFSPreviewer;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            previewer = new MinioPreviewer(minioConfig, minioClient, thumbImage);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            previewer = new QiniuyunKodoPreviewer(qiniuyunConfig, thumbImage);
        }
        if (previewer == null) {
            log.error("预览失败，文件存储类型不支持预览，storageType:{}", storageType);
            throw new PreviewException("预览失败");
        }
        return previewer;
    }

    public Copier getCopier() {
        int type = Integer.parseInt(storageType);
        Copier copier = null;
        if (StorageTypeEnum.LOCAL.getCode() == type) {
            copier = new LocalStorageCopier();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type) {
            copier = new AliyunOSSCopier(aliyunConfig, ossClient);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == type) {
            copier = fastDFSCopier;
        } else if (StorageTypeEnum.MINIO.getCode() == type) {
            copier = new MinioCopier(minioConfig, minioClient);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == type) {
            copier = new QiniuyunKodoCopier(qiniuyunConfig);
        }
        if (copier == null) {
            log.error("拷贝失败，文件存储类型不支持预览，storageType:{}", storageType);
            throw new CopyException("拷贝失败");
        }
        return copier;
    }

    public Querier getQuerier(int storageType) {
        Querier querier = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
//            previewer = localStoragePreviewer;
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            querier = new AliyunOSSQuerier(aliyunConfig, ossClient);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
//            previewer = fastDFSPreviewer;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
//            previewer = new MinioPreviewer(minioConfig, thumbImage);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
//            previewer = new QiniuyunKodoPreviewer(qiniuyunConfig, thumbImage);
        }
        if (querier == null) {
            log.error("预览失败，文件存储类型不支持预览，storageType:{}", storageType);
            throw new PreviewException("预览失败");
        }
        return querier;
    }
}
