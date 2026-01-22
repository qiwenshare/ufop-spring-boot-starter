//package com.qiwenshare.ufop.operation.query.product;
//
//import com.qiwenshare.ufop.exception.operation.QueryException;
//import com.qiwenshare.ufop.operation.query.Queryer;
//import com.qiwenshare.ufop.operation.query.domain.FileInfo;
//import com.qiwenshare.ufop.operation.query.domain.QueryFile;
//import com.qiwenshare.ufop.operation.query.domain.QueryFileList;
//import com.qiwenshare.ufop.operation.query.domain.QueryListResult;
//import com.qiwenshare.ufop.plugins.fastdfs.service.FastFileStorageClient;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FastDFSQueryer extends Queryer {
//
//    @Autowired
//    private FastFileStorageClient fastFileStorageClient;
//
//    @Override
//    public FileInfo query(QueryFile queryFile) {
//        FileInfo fileInfo = new FileInfo();
//        try {
//            String fileUrl = queryFile.getFileUrl();
//            // FastDFS的文件查询相对简单，这里我们假设fileUrl是完整的文件路径
//            // 在实际应用中，可能需要根据FastDFS的API进行更详细的查询
//
//            // 注意：FastDFS的Java客户端API可能没有直接的文件元数据查询方法
//            // 这里我们暂时返回基本信息，实际项目中可能需要根据具体的FastDFS客户端实现进行调整
//            fileInfo.setFileUrl(fileUrl);
//            fileInfo.setFileName(fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
//            fileInfo.setExists(true);
//            // 其他信息如文件大小、修改时间等可能需要通过其他方式获取
//        } catch (Exception e) {
//            throw new QueryException("FastDFS文件查询出现异常", e);
//        }
//        return fileInfo;
//    }
//
//    @Override
//    public QueryListResult queryList(QueryFileList queryFileList) {
//        QueryListResult result = new QueryListResult();
//        List<FileInfo> fileList = new ArrayList<>();
//        try {
//            String directoryPath = queryFileList.getDirectoryPath();
//            // FastDFS的文件列表查询相对复杂，这里我们暂时返回一个空列表
//            // 在实际应用中，可能需要根据FastDFS的API进行更详细的实现
//            // 注意：FastDFS的Java客户端API可能没有直接的目录列表查询方法
//            // 这里我们暂时返回一个空列表，实际项目中可能需要根据具体的FastDFS客户端实现进行调整
//
//            result.setFileList(fileList);
//            result.setContinuationToken(null); // FastDFS不提供continuationToken
//            result.setTruncated(false); // FastDFS暂时返回空列表
//        } catch (Exception e) {
//            throw new QueryException("FastDFS文件列表查询出现异常", e);
//        }
//        return result;
//    }
//}
