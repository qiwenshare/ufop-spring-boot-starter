//package com.qiwenshare.ufop.operation.query.product;
//
//import com.qcloud.cos.COSClient;
//import com.qcloud.cos.model.ListObjectsRequest;
//import com.qcloud.cos.model.ObjectListing;
//import com.qcloud.cos.model.ObjectMetadata;
//import com.qcloud.cos.model.ObjectSummary;
//import com.qiwenshare.ufop.config.TencentConfig;
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
//public class TencentCOSQueryer extends Queryer {
//
//    @Autowired
//    private TencentConfig tencentConfig;
//
//    @Autowired
//    private COSClient cosClient;
//
//    @Override
//    public FileInfo query(QueryFile queryFile) {
//        FileInfo fileInfo = new FileInfo();
//        try {
//            String objectName = queryFile.getFileUrl();
//
//            ObjectMetadata metadata = cosClient.getObjectMetadata(tencentConfig.getBucketName(), objectName);
//
//            if (metadata != null) {
//                fileInfo.setFileUrl(queryFile.getFileUrl());
//                fileInfo.setFileName(objectName.substring(objectName.lastIndexOf("/") + 1));
//                fileInfo.setFileSize(metadata.getContentLength());
//                fileInfo.setLastModified(metadata.getLastModified().getTime());
//                fileInfo.setContentType(metadata.getContentType());
//                fileInfo.setExists(true);
//            } else {
//                fileInfo.setFileUrl(queryFile.getFileUrl());
//                fileInfo.setExists(false);
//            }
//        } catch (Exception e) {
//            throw new QueryException("腾讯云COS文件查询出现异常", e);
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
//            ListObjectsRequest request = new ListObjectsRequest()
//                    .withBucketName(tencentConfig.getBucketName())
//                    .withPrefix(prefix)
//                    .withMaxKeys(maxKeys);
//
//            // 执行列举操作
//            ObjectListing listing = cosClient.listObjects(request);
//            List<ObjectSummary> summaries = listing.getObjectSummaries();
//
//            for (ObjectSummary summary : summaries) {
//                if (!summary.getKey().equals(prefix)) { // 排除目录本身
//                    FileInfo fileInfo = new FileInfo();
//                    fileInfo.setFileUrl(summary.getKey());
//                    fileInfo.setFileName(summary.getKey().substring(summary.getKey().lastIndexOf("/") + 1));
//                    fileInfo.setFileSize(summary.getSize());
//                    fileInfo.setLastModified(summary.getLastModified().getTime());
//                    fileInfo.setFile(!summary.getKey().endsWith("/"));
//                    fileInfo.setDirectory(summary.getKey().endsWith("/"));
//                    fileList.add(fileInfo);
//                }
//            }
//
//            result.setFileList(fileList);
//            result.setContinuationToken(listing.getNextMarker()); // 使用nextMarker作为continuationToken
//            result.setTruncated(listing.isTruncated());
//        } catch (Exception e) {
//            throw new QueryException("腾讯云COS文件列表查询出现异常", e);
//        }
//        return result;
//    }
//}
