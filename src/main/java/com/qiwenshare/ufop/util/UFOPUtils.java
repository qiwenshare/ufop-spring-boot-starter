package com.qiwenshare.ufop.util;

import com.qiwenshare.ufop.exception.UFOPException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UFOPUtils {

    public static String LOCAL_STORAGE_PATH;

    public static String BUCKET_NAME;
    public static final String[] TXT_FILE = {"txt", "html", "java", "xml", "js", "css", "json", "sql"};

    // ========== 全局路径缓存（只初始化一次，避免重复 new File / 路径解析）==========
    private static String PROJECT_ROOT_PATH;
    private static String DATA_ROOT_PATH;
    private static String STATIC_ROOT_PATH;
    private static String LOGS_ROOT_PATH;
    private static String BUILD_ROOT_PATH;

    // 线程安全日期格式化（替代非线程安全的 SimpleDateFormat）
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    static {

        initAllFixedPath();
    }

    /**
     * 初始化固定路径，全局只执行一次
     */
    private static void initAllFixedPath() {
        // 静态块仅执行一次，全局初始化所有根路径
        // 改用 DefaultResourceLoader，减少 jar URL 频繁解析
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("classpath:");
        String absolutePath;
        try {
            absolutePath = urlDecode(resource.getFile().getAbsolutePath()) + File.separator;
        } catch (Exception e) {
            throw new UFOPException("获取项目根路径失败", e);
        }
        // 清理 file: 前缀
        int index = absolutePath.indexOf("file:");
        if (index != -1) {
            absolutePath = absolutePath.substring(0, index);
        }
        PROJECT_ROOT_PATH =  absolutePath;
        if (StringUtils.isNotEmpty(LOCAL_STORAGE_PATH)) {
            DATA_ROOT_PATH = new File(LOCAL_STORAGE_PATH).getPath() + File.separator;
        } else {
            DATA_ROOT_PATH = new File(PROJECT_ROOT_PATH, "data").getPath() + File.separator;
        }
        STATIC_ROOT_PATH = new File(PROJECT_ROOT_PATH, "static").getPath() + File.separator;
        LOGS_ROOT_PATH = new File(PROJECT_ROOT_PATH, "logs").getPath() + File.separator;
        BUILD_ROOT_PATH = new File(PROJECT_ROOT_PATH, "build").getPath() + File.separator;
    }

    public static String pathSplitFormat(String filePath) {
        return filePath.replace("///", "/")
                .replace("//", "/")
                .replace("\\\\\\", "/")
                .replace("\\\\", "/");
    }

    public static File getLocalSaveFile(String fileUrl) {
        return new File(getDataPath(), fileUrl);
    }

    public static File getCacheFile(String fileUrl) {
        return new File(getDataPath(), "cache" + File.separator + fileUrl);
    }

    public static File getTempFile(String fileUrl) {
        File tempFile = new File(getDataPath(), "temp" + File.separator + fileUrl);
        File parentFile = tempFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return tempFile;
    }

    public static File getProcessFile(String fileUrl) {
        File processFile = new File(getDataPath(), "temp" + File.separator + "process" + File.separator + fileUrl);
        File parentFile = processFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return processFile;
    }

    /**
     * 获取项目classpath根路径
     * 【优化】弃用 ResourceUtils.getURL 减少 jar:// 协议触发，规避JAR缓存泄漏
     */
    public static String getProjectRootPath() {
        return PROJECT_ROOT_PATH;
    }

    /**
     * 路径解码
     */
    public static String urlDecode(String url) {
        try {
            return URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new UFOPException("不支持的编码格式", e);
        }
    }

    public static String getDataPath() {
        return DATA_ROOT_PATH;
    }

    public static String getStaticPath() {
        return STATIC_ROOT_PATH;
    }

    public static String getLogsPath() {
        return LOGS_ROOT_PATH;
    }

    public static String getBuildPath() {
        return BUILD_ROOT_PATH;
    }

    /**
     * 获取上传文件路径，使用线程安全日期类
     */
    public static String getUploadFileUrl(String bucketName, String identifier, String extendName) {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String path = (StringUtils.isEmpty(bucketName) ? BUCKET_NAME : bucketName) + "/" + dateStr + "/";
        File dir = new File(getDataPath(), path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path + identifier + "." + extendName;
    }

    public static String getAliyunObjectNameByFileUrl(String fileUrl) {
        return getObjectName(fileUrl);
    }

    public static String formatPath(String path) {
        path = pathSplitFormat(path);
        if ("/".equals(path)) {
            return path;
        }
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String getTencentObjectNameByFileUrl(String fileUrl) {
        return getObjectName(fileUrl);
    }

    @NotNull
    private static String getObjectName(String fileUrl) {
        if (fileUrl.startsWith("/") || fileUrl.startsWith("\\")) {
            fileUrl = fileUrl.substring(1);
        }
        return fileUrl;
    }
}