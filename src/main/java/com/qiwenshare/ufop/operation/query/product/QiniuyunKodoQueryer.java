//package com.qiwenshare.ufop.operation.query.product;
//
//import com.qiniu.storage.BucketManager;
//import com.qiniu.storage.model.FileInfo;
//import com.qiwenshare.ufop.config.QiniuyunConfig;
//import com.qiwenshare.ufop.exception.operation.QueryException;
//import com.qiwenshare.ufop.operation.query.Queryer;
//import com.qiwenshare.ufop.operation.query.domain.FileInfo;
//import com.qiwenshare.ufop.operation.query.domain.QueryFile;
//import com.qiwenshare.ufop.operation.query.domain.QueryFileList;
//import com.qiwenshare.ufop.operation.query.domain.QueryListResult;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class QiniuyunKodoQueryer extends Queryer {
//
//    @Autowired
//    private QiniuyunConfig qiniuyunConfig;
//
//    @Autowired
//    private BucketManager bucketManager;
//
//    @Override
//    public FileInfo query(QueryFile queryFile) {
//        FileInfo fileInfo = new FileInfo();
//        try {
//            String objectName = queryFile.getFileUrl();
//
//            com.qiniu.storage.model.FileInfo info = bucketManager.stat(qiniuyunConfig.getBucketName(), objectName);
//
//            if (info != null) {
//                fileInfo.setFileUrl(queryFile.getFileUrl());
//                fileInfo.setFileName(objectName.substring(objectName.lastIndexOf("/") + 1));
//                fileInfo.setFileSize(info.fsize);
//                fileInfo.setLastModified(info.putTime / 1000000); // 七牛云返回的是纳秒，转换为毫秒
//                fileInfo.setMimeType(info.mimeType);
//                fileInfo.setExists(true);
//            } else {
//                fileInfo.setFileUrl(queryFile.getFileUrl());
//                fileInfo.setExists(false);
//            }
//        } catch (Exception e) {
//            throw new QueryException("七牛云Kodo文件查询出现异常", e);
//        }
//        return fileInfo;
//    }
//
//    @Override
//    public QueryListResult queryList(QueryFileList queryFileList) {
//        QueryListResult result = new QueryListResult();
//        List<FileInfo> fileList = new ArrayList<>();
//        try {
//            String prefix = queryFileList.getDirectoryPath();
//            if (!prefix.endsWith("/")) {
//                prefix += "/";
//            }
//
//            // 设置最大个数
//            int maxKeys = queryFileList.getPageSize() != null ? queryFileList.getPageSize() : 200;
//
//            BucketManager.FileListIterator iterator = bucketManager.createFileListIterator(
//                    qiniuyunConfig.getBucketName(), prefix, maxKeys, null, null);
//
//            // 只获取一页数据
//            if (iterator.hasNext()) {
//                com.qiniu.storage.model.FileInfo[] items = iterator.next();
//                for (com.qiniu.storage.model.FileInfo item : items) {
//                    FileInfo fileInfo = new FileInfo();
//                    fileInfo.setFileUrl(item.key);
//                    fileInfo.setFileName(item.key.substring(item.key.lastIndexOf("/") + 1));
//                    fileInfo.setFileSize(item.fsize);
//                    fileInfo.setLastModified(item.putTime / 1000000); // 七牛云返回的是纳秒，转换为毫秒
//                    fileInfo.setMimeType(item.mimeType);
//                    fileInfo.setFile(!item.key.endsWith("/"));
//                    fileInfo.setDirectory(item.key.endsWith("/"));
//                    fileList.add(fileInfo);
//                }
//            }
//
//            result.setFileList(fileList);
//            result.setContinuationToken(null); // 七牛云Kodo的FileListIterator不提供continuationToken
//            result.setTruncated(iterator.hasNext()); // 判断是否还有更多数据
//        } catch (Exception e) {
//            throw new QueryException("七牛云Kodo文件列表查询出现异常", e);
//        }
//        return result;
//    }
//}
