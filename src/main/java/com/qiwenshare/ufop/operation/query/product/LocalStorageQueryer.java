//package com.qiwenshare.ufop.operation.query.product;
//
//import com.qiwenshare.ufop.exception.operation.QueryException;
//import com.qiwenshare.ufop.operation.query.Queryer;
//import com.qiwenshare.ufop.operation.query.domain.FileInfo;
//import com.qiwenshare.ufop.operation.query.domain.QueryFile;
//import com.qiwenshare.ufop.operation.query.domain.QueryFileList;
//import com.qiwenshare.ufop.operation.query.domain.QueryListResult;
//import com.qiwenshare.ufop.util.UFOPUtils;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class LocalStorageQueryer extends Queryer {
//    @Override
//    public FileInfo query(QueryFile queryFile) {
//        FileInfo fileInfo = new FileInfo();
//        try {
//            String filePath = UFOPUtils.getDataPath() + queryFile.getFileUrl();
//            File file = new File(filePath);
//
//            if (file.exists()) {
//                fileInfo.setFileUrl(queryFile.getFileUrl());
//                fileInfo.setFileName(file.getName());
//                fileInfo.setFileSize(file.length());
//                fileInfo.setLastModified(file.lastModified());
//                fileInfo.setExists(true);
//                fileInfo.setFile(file.isFile());
//                fileInfo.setDirectory(file.isDirectory());
//            } else {
//                fileInfo.setFileUrl(queryFile.getFileUrl());
//                fileInfo.setExists(false);
//            }
//        } catch (Exception e) {
//            throw new QueryException("文件查询出现异常", e);
//        }
//        return fileInfo;
//    }
//
//    @Override
//    public QueryListResult queryList(QueryFileList queryFileList) {
//        QueryListResult result = new QueryListResult();
//        List<FileInfo> fileList = new ArrayList<>();
//        try {
//            String directoryPath = UFOPUtils.getDataPath() + queryFileList.getDirectoryPath();
//            File directory = new File(directoryPath);
//
//            if (directory.exists() && directory.isDirectory()) {
//                File[] files = directory.listFiles();
//                if (files != null) {
//                    for (File file : files) {
//                        FileInfo fileInfo = new FileInfo();
//                        fileInfo.setFileUrl(queryFileList.getDirectoryPath() + "/" + file.getName());
//                        fileInfo.setFileName(file.getName());
//                        fileInfo.setFileSize(file.length());
//                        fileInfo.setLastModified(file.lastModified());
//                        fileInfo.setFile(file.isFile());
//                        fileInfo.setDirectory(file.isDirectory());
//                        fileList.add(fileInfo);
//                    }
//                }
//            }
//
//            result.setFileList(fileList);
//            result.setContinuationToken(null); // 本地文件系统不需要continuationToken
//            result.setTruncated(false); // 本地文件系统一次性返回所有文件
//        } catch (Exception e) {
//            throw new QueryException("文件列表查询出现异常", e);
//        }
//        return result;
//    }
//}
