//package com.qiwenshare.ufop.operation.query.product;
//
//import io.minio.MinioClient;
//import io.minio.ListObjectsArgs;
//import io.minio.Result;
//import io.minio.StatObjectArgs;
//import io.minio.StatObjectResponse;
//import io.minio.messages.Item;
//import com.qiwenshare.ufop.config.MinioConfig;
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
//public class MinioQueryer extends Queryer {
//
//    @Autowired
//    private MinioConfig minioConfig;
//
//    @Autowired
//    private MinioClient minioClient;
//
//    @Override
//    public FileInfo query(QueryFile queryFile) {
//        FileInfo fileInfo = new FileInfo();
//        try {
//            String objectName = queryFile.getFileUrl();
//
//            StatObjectResponse response = minioClient.statObject(
//                    StatObjectArgs.builder()
//                            .bucket(minioConfig.getBucketName())
//                            .object(objectName)
//                            .build()
//            );
//
//            fileInfo.setFileUrl(queryFile.getFileUrl());
//            fileInfo.setFileName(objectName.substring(objectName.lastIndexOf("/") + 1));
//            fileInfo.setFileSize(response.size());
//            fileInfo.setLastModified(response.lastModified().getTime());
//            fileInfo.setContentType(response.contentType());
//            fileInfo.setExists(true);
//        } catch (Exception e) {
//            fileInfo.setFileUrl(queryFile.getFileUrl());
//            fileInfo.setExists(false);
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
//            Iterable<Result<Item>> results = minioClient.listObjects(
//                    ListObjectsArgs.builder()
//                            .bucket(minioConfig.getBucketName())
//                            .prefix(prefix)
//                            .recursive(false) // 只列出当前目录
//                            .maxKeys(maxKeys)
//                            .build()
//            );
//
//            for (Result<Item> itemResult : results) {
//                Item item = itemResult.get();
//                if (!item.objectName().equals(prefix)) { // 排除目录本身
//                    FileInfo fileInfo = new FileInfo();
//                    fileInfo.setFileUrl(item.objectName());
//                    fileInfo.setFileName(item.objectName().substring(item.objectName().lastIndexOf("/") + 1));
//                    fileInfo.setFileSize(item.size());
//                    fileInfo.setLastModified(item.lastModified().getTime());
//                    fileInfo.setFile(!item.isDir());
//                    fileInfo.setDirectory(item.isDir());
//                    fileList.add(fileInfo);
//                }
//            }
//
//            result.setFileList(fileList);
//            result.setContinuationToken(null); // Minio的listObjects不提供continuationToken
//            result.setTruncated(false); // Minio的listObjects一次性返回所有匹配的对象
//        } catch (Exception e) {
//            throw new QueryException("Minio文件列表查询出现异常", e);
//        }
//        return result;
//    }
//}
