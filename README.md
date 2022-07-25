# ufop-spring-boot-starter

#### 介绍

UFOP (Unified File Operation Platform) 统一文件操作平台，通过引入该依赖，可以实现文件操作的统一管理

此项目为奇文网盘核心功能，之前有不少人咨询，如何将网盘集成到自己的项目?出于这个目的，就把这块功能剥离出来供大家方便引入，目前实现的主要功能如下：

1. 本地文件上传、下载，删除，预览，重命名，读文件流，写文件流
2. 阿里云OSS上传，下载，删除，预览，重命名，读文件流，写文件流
3. FastDFS上传，下载，删除，预览，重命名，读文件流，写文件流
4. FastDFS+Redis实现集群化部署
5. 图片支持缩略图预览


#### 软件架构
#### 安装教程
mvn clean install org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests
#### 使用说明

1.  引入pom依赖

这里的具体版本号建议引入最新版本
```xml
<dependency>
    <groupId>com.qiwenshare</groupId>
    <artifactId>ufop-spring-boot-starter</artifactId>
    <version>{new version}<version>
</dependency>
```
2.  application.properties配置文件说明

配置磁盘存储方式, 0-本地存储， 1-阿里云OSS存储， 2-fastDFS存储, 3-minio存储, 4-七牛云KODO对象存储

```properties
ufop.storage-type=0
```

当选择0-本地磁盘存储之后，你还可以继续配置本地文件存储路径
```properties
ufop.local-storage-path=D://test
```
当选择1-阿里云OSS存储之后，需要配置阿里云OSS相关信息，
```properties
#阿里云oss基本配置
ufop.aliyun.oss.endpoint=
ufop.aliyun.oss.access-key-id=
ufop.aliyun.oss.access-key-secret=
ufop.aliyun.oss.bucket-name=
#阿里云oss绑定域名
ufop.aliyun.oss.domain=oss.qiwenshare.com
```
当选择2-FastDFS存储之后，则需要配置FastDFS服务器信息

```properties
#FastDFS配置
fdfs.so-timeout=1501
fdfs.connect-timeout=601
fdfs.thumb-image.width=150
fdfs.thumb-image.height=150
fdfs.tracker-list=127.0.0.1:22122 
```

```properties

# Redis数据库索引（默认为0）
spring.redis.database=0  
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=ma123456
# 连接池最大连接数（使用负值表示没有限制） 默认 8
spring.redis.lettuce.pool.max-active=8
# 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
spring.redis.lettuce.pool.max-wait=10000
# 连接池中的最大空闲连接 默认 8
spring.redis.lettuce.pool.max-idle=30
# 连接池中的最小空闲连接 默认 0
spring.redis.lettuce.pool.min-idle=10
#连接超时时间（毫秒）
spring.redis.timeout=5000
```

```java
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }
    /**
     * 设置 redisTemplate 的序列化设置
     * @param redisConnectionFactory redis连接工厂
     * @return redisTemplate
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 1.创建 redisTemplate 模版
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // 2.关联 redisConnectionFactory
        template.setConnectionFactory(redisConnectionFactory);
        // 3.创建 序列化类
        GenericToStringSerializer genericToStringSerializer = new GenericToStringSerializer(Object.class);
        // 6.序列化类，对象映射设置
        // 7.设置 value 的转化格式和 key 的转化格式
        template.setValueSerializer(genericToStringSerializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

}
```

当配置完基础信息之后，使用就非常简单了，伪代码如下：

注入UFOPFactory
```java
@Resource
UFOPFactory ufopFactory;
```
上传文件操作，具体这个上传操作是哪种存储实现，由`ufop.storage-type`配置项决定，

```java

//上传操作
Uploader uploader = ufopFactory.getUploader();
uploader.upload(request, uploadFile);
```

下载和删除则需要用户自己传入文件存储类型
```java
//下载操作
Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
downloader.download(httpServletResponse, downloadFile);
//删除操作
Deleter deleter = ufopFactory.getDeleter(fileBean.getStorageType());
deleter.delete(deleteFile);

```

该工程目前已经在奇文网盘运行了一年多时间，但不免还是会有很多缺陷，如果遇到问题，欢迎大家参与贡献，一块去完善它

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
