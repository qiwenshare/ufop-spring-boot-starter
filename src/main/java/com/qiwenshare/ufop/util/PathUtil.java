//package com.qiwenshare.ufop.util;
//
//import com.qiwenshare.common.constant.FileConstant;
//import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
//import com.qiwenshare.ufop.autoconfiguration.UFOPAutoConfiguration;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.util.ResourceUtils;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.UUID;
//
//import static com.qiwenshare.ufop.operation.upload.Uploader.FILE_SEPARATOR;
//import static com.qiwenshare.ufop.operation.upload.Uploader.ROOT_PATH;
//
//public class UFOPUtils {
//
//    /**
//     * 获取项目所在的根目录路径 resources路径
//     * @return 结果
//     */
//    public static String getProjectRootPath() {
//        String absolutePath = null;
//        try {
//            String url = ResourceUtils.getURL("classpath:").getPath();
//            absolutePath = urlDecode(new File(url).getAbsolutePath()) + File.separator;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return absolutePath;
//    }
//
//    /**
//     * 路径解码
//     * @param url url
//     * @return 结果
//     */
//    public static String urlDecode(String url){
//        String decodeUrl = null;
//        try {
//            decodeUrl = URLDecoder.decode(url, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return  decodeUrl;
//    }
//
//    /**
//     * 得到static路径
//     *
//     * @return 结果
//     */
//    public static String getStaticPath() {
//        String localStoragePath = UFOPAutoConfiguration.localStoragePath;//PropertiesUtil.getProperty("qiwen-file.local-storage-path")
//        if (StringUtils.isNotEmpty(localStoragePath)) {
//
//            return new File(localStoragePath).getPath() + File.separator;
//        }else {
//            String projectRootAbsolutePath = getProjectRootPath();
//
//            int index = projectRootAbsolutePath.indexOf("file:");
//            if (index != -1) {
//                projectRootAbsolutePath = projectRootAbsolutePath.substring(0, index);
//            }
//
//            return new File(projectRootAbsolutePath + "static").getPath() + File.separator;
//        }
//
//
//    }
//
//    public static String getUploadFileUrl(String identifier, String extendName) {
//
//        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
//        String path = ROOT_PATH + FILE_SEPARATOR + formater.format(new Date()) + FILE_SEPARATOR;
//
//
//
//        File dir = new File(UFOPUtils.getStaticPath() + path);
//
//        if (!dir.exists()) {
//            try {
//                dir.mkdirs();
//            } catch (Exception e) {
//                return "";
//            }
//        }
//
//        path = path + identifier + "." + extendName;
//
//        return path;
//    }
//
//    public static String getAliyunObjectNameByFileUrl(String fileUrl) {
//        if (fileUrl.startsWith("/") || fileUrl.startsWith("\\")) {
//            fileUrl = fileUrl.substring(1);
//        }
//        return fileUrl;
//    }
//
//    public static String getParentPath(String path) {
//        return path.substring(0, path.lastIndexOf(FileConstant.pathSeparator));
//    }
//
//
//
//}
