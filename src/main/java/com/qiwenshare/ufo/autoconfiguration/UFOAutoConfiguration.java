package com.qiwenshare.ufo.autoconfiguration;

import com.qiwenshare.ufo.config.AliyunConfig;
import com.qiwenshare.ufo.factory.UFOFactory;
import com.qiwenshare.ufo.operation.delete.product.FastDFSDeleter;
import com.qiwenshare.ufo.operation.download.product.FastDFSDownloader;
import com.qiwenshare.ufo.operation.preview.product.FastDFSPreviewer;
import com.qiwenshare.ufo.operation.read.product.FastDFSReader;
import com.qiwenshare.ufo.operation.upload.product.FastDFSUploader;
        import com.qiwenshare.ufo.util.RedisUtil;
import com.qiwenshare.ufo.util.concurrent.locks.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
//@ConditionalOnClass(UFOService.class)
@EnableConfigurationProperties({UFOProperties.class})
public class UFOAutoConfiguration {
    public static String localStoragePath;
    public static AliyunConfig aliyunConfig;
    public static int thumbImageWidth;
    public static int thumbImageHeight;

    @Bean
    public UFOFactory ufoFactory(UFOProperties ufoProperties) {
        localStoragePath = ufoProperties.getLocalStoragePath();
        aliyunConfig = ufoProperties.getAliyun();
        thumbImageWidth = ufoProperties.getThumbImage().getWidth();
        thumbImageHeight = ufoProperties.getThumbImage().getHeight();
        return new UFOFactory(ufoProperties.getStorageType());
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
