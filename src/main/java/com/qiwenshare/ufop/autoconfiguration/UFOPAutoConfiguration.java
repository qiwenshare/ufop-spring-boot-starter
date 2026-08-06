package com.qiwenshare.ufop.autoconfiguration;

import com.qiwenshare.ufop.cache.CacheService;
import com.qiwenshare.ufop.cache.impl.CacheServiceJDKImpl;
import com.qiwenshare.ufop.cache.impl.CacheServiceRedisImpl;
import com.qiwenshare.ufop.config.CacheConfig;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.lock.LockService;
import com.qiwenshare.ufop.lock.impl.LockServiceJDKImpl;
import com.qiwenshare.ufop.lock.impl.LockServiceRedisImpl;
import com.qiwenshare.ufop.operation.copy.product.FastDFSCopier;
import com.qiwenshare.ufop.operation.delete.product.FastDFSDeleter;
import com.qiwenshare.ufop.operation.download.product.FastDFSDownloader;
import com.qiwenshare.ufop.operation.download.product.LocalStorageDownloader;
import com.qiwenshare.ufop.operation.preview.product.FastDFSPreviewer;
import com.qiwenshare.ufop.operation.preview.product.LocalStoragePreviewer;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.config.MinioConfig;
import com.qiwenshare.ufop.operation.query.product.AliyunOSSQuerier;
import io.minio.MinioClient;
import com.qiwenshare.ufop.operation.read.product.FastDFSReader;
import com.qiwenshare.ufop.operation.read.product.LocalStorageReader;
import com.qiwenshare.ufop.operation.upload.product.*;
import com.qiwenshare.ufop.operation.write.product.FastDFSWriter;
import com.qiwenshare.ufop.plugins.fastdfs.FdfsClientConfig;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;


@Slf4j
//@Configuration
@AutoConfiguration
//@ConditionalOnClass(UFOService.class)
@EnableConfigurationProperties({UFOPProperties.class})
@Import(FdfsClientConfig.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class UFOPAutoConfiguration {

    @Autowired
    private UFOPProperties ufopProperties;

    @Bean
    public UFOPFactory ufopFactory() {
        UFOPUtils.LOCAL_STORAGE_PATH = ufopProperties.getLocalStoragePath();
        String bucketName = ufopProperties.getBucketName();
        if (StringUtils.isNotEmpty(bucketName)) {
            UFOPUtils.BUCKET_NAME = ufopProperties.getBucketName();
        } else {
            UFOPUtils.BUCKET_NAME = "upload";
        }
        return new UFOPFactory(ufopProperties);
    }

    // ==================== FastDFS 相关 (storage-type=2) ====================
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "2")
    public FastDFSCopier fastDFSCreater() {
        return new FastDFSCopier();
    }
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "2")
    public FastDFSUploader fastDFSUploader() {
        return new FastDFSUploader();
    }
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "2")
    public FastDFSDownloader fastDFSDownloader() {
        return new FastDFSDownloader();
    }
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "2")
    public FastDFSDeleter fastDFSDeleter() {
        return new FastDFSDeleter();
    }
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "2")
    public FastDFSReader fastDFSReader() {
        return new FastDFSReader();
    }
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "2")
    public FastDFSWriter fastDFSWriter() {
        return new FastDFSWriter();
    }
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "2")
    public FastDFSPreviewer fastDFSPreviewer() {
        return new FastDFSPreviewer(ufopProperties.getThumbImage());
    }

    // ==================== MinIO 相关 (storage-type=3) ====================
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "3")
    public MinioClient minioClient() {
        MinioConfig minio = ufopProperties.getMinio();
        if (minio != null) {
            return MinioClient.builder()
                    .endpoint(minio.getEndpoint())
                    .credentials(minio.getAccessKey(), minio.getSecretKey())
                    .build();
        }
        return null;
    }
    @Bean
    @ConditionalOnBean(MinioClient.class)
    public MinioUploader minioUploader(MinioClient minioClient) {
        return new MinioUploader(ufopProperties.getMinio(), minioClient);
    }

    // ==================== 阿里云 OSS 相关 (storage-type=1) ====================
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "1")
    public OSS ossClient() {
        AliyunConfig aliyun = ufopProperties.getAliyun();
        if (aliyun != null && aliyun.getOss() != null) {
            return new OSSClientBuilder().build(
                    aliyun.getOss().getEndpoint(),
                    aliyun.getOss().getAccessKeyId(),
                    aliyun.getOss().getAccessKeySecret()
            );
        }
        return null;
    }
    @Bean
    @ConditionalOnBean(OSS.class)
    public AliyunOSSQuerier aliyunOSSQuerier(OSS ossClient) {
        return new AliyunOSSQuerier(ufopProperties.getAliyun(), ossClient);
    }
    @Bean
    @ConditionalOnBean(OSS.class)
    public AliyunOSSUploader aliyunOSSUploader(OSS ossClient) {
        return new AliyunOSSUploader(ufopProperties.getAliyun(), ossClient);
    }

    // ==================== 七牛云 相关 (storage-type=4) ====================
    @Bean
    @ConditionalOnProperty(name = "ufop.storage-type", havingValue = "4")
    public QiniuyunKodoUploader qiniuyunKodoUploader() {
        return new QiniuyunKodoUploader(ufopProperties.getQiniuyun());
    }

    // ==================== 本地存储 (默认) ====================
    @Bean
    public LocalStoragePreviewer localStoragePreviewer() {
        return new LocalStoragePreviewer(ufopProperties.getThumbImage());
    }
    @Bean
    public LocalStorageDownloader localStorageDownloader() {
        return new LocalStorageDownloader();
    }
    @Bean
    public LocalStorageReader localStorageReader() {
        return new LocalStorageReader();
    }
    @Bean
    private LocalStorageUploader localStorageUploader() {
        return new LocalStorageUploader();
    }

    // ==================== 缓存服务 (必需) ====================
    @Bean
    public CacheService cacheService() {
        CacheConfig cache = ufopProperties.getCache();
        if (cache != null && "redis".equals(cache.getType())) {
            return new CacheServiceRedisImpl();
        }
        return new CacheServiceJDKImpl();
    }

    @Bean
    public LockService lockService() {
        CacheConfig cache = ufopProperties.getCache();
        if (cache != null && "redis".equals(cache.getType())) {
            return new LockServiceRedisImpl();
        }
        return new LockServiceJDKImpl();
    }
}
