package com.qiwenshare.ufop.autoconfiguration;

import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.delete.product.FastDFSDeleter;
import com.qiwenshare.ufop.operation.download.product.FastDFSDownloader;
import com.qiwenshare.ufop.operation.preview.product.FastDFSPreviewer;
import com.qiwenshare.ufop.operation.read.product.FastDFSReader;
import com.qiwenshare.ufop.operation.upload.product.FastDFSUploader;
import com.qiwenshare.ufop.operation.write.product.FastDFSWriter;
import com.qiwenshare.ufop.util.RedisUtil;
import com.qiwenshare.ufop.util.concurrent.locks.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
//@ConditionalOnClass(UFOService.class)
@EnableConfigurationProperties({UFOPProperties.class})
public class UFOPAutoConfiguration {
    public static String localStoragePath;
    public static AliyunConfig aliyunConfig;
    public static int thumbImageWidth;
    public static int thumbImageHeight;

    @Bean
    public UFOPFactory ufopFactory(UFOPProperties UFOPProperties) {
        localStoragePath = UFOPProperties.getLocalStoragePath();
        aliyunConfig = UFOPProperties.getAliyun();
        thumbImageWidth = UFOPProperties.getThumbImage().getWidth();
        thumbImageHeight = UFOPProperties.getThumbImage().getHeight();
        return new UFOPFactory(UFOPProperties.getStorageType());
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
    public FastDFSPreviewer fastDFSPreviewer() {
        return new FastDFSPreviewer();
    }


    @Bean
    public RedisLock redisLock() {
        return new RedisLock();
    }
    @Bean
    public RedisUtil redisUtil() {
        return new RedisUtil();
    }

}
