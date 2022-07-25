package com.qiwenshare.ufop.util;

import com.qiwenshare.ufop.exception.UFOPException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UFOPUtils {

    public static String LOCAL_STORAGE_PATH;
    public static String ROOT_PATH;
    public static final String[] IMG_FILE = {"bmp", "jpg", "png", "tif", "gif", "jpeg"};
    public static final String[] DOC_FILE = {"doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "hlp", "wps", "rtf", "html", "pdf"};
    public static final String[] VIDEO_FILE = {"avi", "mp4", "mpg", "mov", "swf"};
    public static final String[] MUSIC_FILE = {"wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac"};
    public static final String[] TXT_FILE = {"txt", "html", "java", "xml", "js", "css", "json"};
    public static final int IMAGE_TYPE = 1;
    public static final int DOC_TYPE = 2;
    public static final int VIDEO_TYPE = 3;
    public static final int MUSIC_TYPE = 4;
    public static final int OTHER_TYPE = 5;
    public static final int SHARE_FILE = 6;
    public static final int RECYCLE_FILE = 7;


    /**
     * 判断是否为图片文件
     *
     * @param extendName 文件扩展名
     * @return 是否为图片文件
     */
    public static boolean isImageFile(String extendName) {
        for (String extend : IMG_FILE) {
            if (extendName.equalsIgnoreCase(extend)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为视频文件
     * @param extendName 扩展名
     * @return 是否为视频文件
     */
    public static boolean isVideoFile(String extendName) {
        for (String extend : VIDEO_FILE) {
            if (extendName.equalsIgnoreCase(extend)) {
                return true;
            }
        }
        return false;
    }

    public static String pathSplitFormat(String filePath) {
        return filePath.replace("///", "/")
                .replace("//", "/")
                .replace("\\\\\\", "/")
                .replace("\\\\", "/");
    }

    public static File getLocalSaveFile(String fileUrl) {
        String localSavePath = UFOPUtils.getStaticPath() + fileUrl;
        return new File(localSavePath);
    }

    public static File getCacheFile(String fileUrl) {
        String cachePath = UFOPUtils.getStaticPath() + "cache" + File.separator + fileUrl;

        return new File(cachePath);
    }

    public static File getTempFile(String fileUrl) {
        String tempPath = UFOPUtils.getStaticPath() + "temp" + File.separator + fileUrl;
        File tempFile = new File(tempPath);
        File parentFile = tempFile.getParentFile();
        if (!parentFile.exists()) {
            boolean result = parentFile.mkdirs();
            if (!result) {
                throw new UFOPException("创建temp目录失败：目录路径："+ parentFile.getPath());
            }
        }

        return tempFile;
    }

    public static File getProcessFile(String fileUrl) {
        String processPath = UFOPUtils.getStaticPath() + "temp" + File.separator + "process" + File.separator + fileUrl;
        File processFile = new File(processPath);
        File parentFile = processFile.getParentFile();
        if (!parentFile.exists()) {
            boolean result = parentFile.mkdirs();
            if (!result) {
                throw new UFOPException("创建process目录失败：目录路径："+ parentFile.getPath());
            }
        }
        return processFile;
    }

    /**
     * 获取项目所在的根目录路径 resources路径
     * @return 结果
     */
    public static String getProjectRootPath() {
        String absolutePath;
        try {
            String url = ResourceUtils.getURL("classpath:").getPath();
            absolutePath = urlDecode(new File(url).getAbsolutePath()) + File.separator;
        } catch (FileNotFoundException e) {
            throw new UFOPException(e);
        }

        return absolutePath;
    }

    /**
     * 路径解码
     * @param url url
     * @return 结果
     */
    public static String urlDecode(String url){
        String decodeUrl;
        try {
            decodeUrl = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new UFOPException("不支持的编码格式", e);
        }
        return  decodeUrl;
    }

    /**
     * 得到static路径
     *
     * @return 结果
     */
    public static String getStaticPath() {
        String localStoragePath = LOCAL_STORAGE_PATH;
        if (StringUtils.isNotEmpty(localStoragePath)) {

            return new File(localStoragePath).getPath() + File.separator;
        }else {
            String projectRootAbsolutePath = getProjectRootPath();

            int index = projectRootAbsolutePath.indexOf("file:");
            if (index != -1) {
                projectRootAbsolutePath = projectRootAbsolutePath.substring(0, index);
            }

            return new File(projectRootAbsolutePath + "static").getPath() + File.separator;
        }


    }

    /**
     * 获取上传文件路径
     * 返回路径格式 “upload/yyyyMMdd/”
     * @param identifier 文件名（一般传入md5或uuid,防止文件名重复）
     * @param extendName 文件扩展名
     * @return 返回上传文件路径
     */
    public static String getUploadFileUrl(String identifier, String extendName) {

        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        String path = ROOT_PATH + "/" + formater.format(new Date()) + "/";

        File dir = new File(UFOPUtils.getStaticPath() + path);

        if (!dir.exists()) {

            boolean result = dir.mkdirs();
            if (!result) {
                throw new UFOPException("创建upload目录失败：目录路径："+ dir.getPath());
            }

        }

        path = path + identifier + "." + extendName;

        return path;
    }

    public static String getAliyunObjectNameByFileUrl(String fileUrl) {
        if (fileUrl.startsWith("/") || fileUrl.startsWith("\\")) {
            fileUrl = fileUrl.substring(1);
        }
        return fileUrl;
    }


    public static String formatPath(String path) {
        path = UFOPUtils.pathSplitFormat(path);
        if ("/".equals(path)) {
            return path;
        }
        if (path.endsWith("/")) {
            int length = path.length();
            return path.substring(0, length - 1);
        }
        return path;
    }

}
