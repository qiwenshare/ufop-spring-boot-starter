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
import com.qiwenshare.ufop.operation.query.product.AliyunOSSQuerier;
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
            UFOPUtils.ROOT_PATH = ufopProperties.getBucketName();
        } else {
            UFOPUtils.ROOT_PATH = "upload";
        }
        return new UFOPFactory(ufopProperties);
    }
    @Bean
    public FastDFSCopier fastDFSCreater() {
        return new FastDFSCopier();
    }
    @Bean
    public FastDFSUploader fastDFSUploader() {
        return new FastDFSUploader();
    }
    @Bean
    public FastDFSDownloader fastDFSDownloader() {
        return new FastDFSDownloader();
    }
    @Bean
    public FastDFSDeleter fastDFSDeleter() {
        return new FastDFSDeleter();
    }
    @Bean
    public FastDFSReader fastDFSReader() {
        return new FastDFSReader();
    }
    @Bean
    public FastDFSWriter fastDFSWriter() {
        return new FastDFSWriter();
    }
    @Bean
    public LocalStoragePreviewer localStoragePreviewer() {
        return new LocalStoragePreviewer(ufopProperties.getThumbImage());
    }
    @Bean
    public FastDFSPreviewer fastDFSPreviewer() {
        return new FastDFSPreviewer(ufopProperties.getThumbImage());
    }
    @Bean
    public AliyunOSSUploader aliyunOSSUploader() {
        return new AliyunOSSUploader(ufopProperties.getAliyun(), ossClient());
    }
    @Bean
    public MinioUploader minioUploader() {
        return new MinioUploader(ufopProperties.getMinio());
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
    public QiniuyunKodoUploader qiniuyunKodoUploader() {
        return new QiniuyunKodoUploader(ufopProperties.getQiniuyun());
    }
    @Bean
    private LocalStorageUploader localStorageUploader() {
        return new LocalStorageUploader();
    }


    @Bean
    public CacheService cacheService() {
        CacheConfig cache = ufopProperties.getCache();
        if (cache != null) {
            String type = cache.getType();
            if ("redis".equals(type)) {
                return new CacheServiceRedisImpl();
            } else {
                return new CacheServiceJDKImpl();
            }
        } else {
            return new CacheServiceJDKImpl();
        }

    }

    @Bean
    public LockService lockService() {
        CacheConfig cache = ufopProperties.getCache();
        if (cache != null) {
            String type = cache.getType();
            if ("redis".equals(type)) {
                return new LockServiceRedisImpl();
            } else {
                return new LockServiceJDKImpl();
            }
        } else {
            return new LockServiceJDKImpl();
        }

    }

    @Bean
    public AliyunOSSQuerier aliyunOSSQuerier() {
        return new AliyunOSSQuerier(ufopProperties.getAliyun(), ossClient());
    }

    @Bean
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
}
