package com.qiwenshare.ufop.operation.query.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectMetadata;
import com.qiwenshare.ufop.config.AliyunConfig;
import com.qiwenshare.ufop.exception.operation.QueryException;
import com.qiwenshare.ufop.operation.query.Querier;
import com.qiwenshare.ufop.operation.query.domain.FileInfo;
import com.qiwenshare.ufop.operation.query.domain.QueryFile;
import com.qiwenshare.ufop.operation.query.domain.QueryFileList;
import com.qiwenshare.ufop.operation.query.domain.QueryListResult;

import java.util.ArrayList;
import java.util.List;

public class AliyunOSSQuerier extends Querier {

    private AliyunConfig aliyunConfig;
    private OSS ossClient;

    public AliyunOSSQuerier() {
    }

    public AliyunOSSQuerier(AliyunConfig aliyunConfig, OSS ossClient) {
        this.aliyunConfig = aliyunConfig;
        this.ossClient = ossClient;
    }

    @Override
    public FileInfo query(QueryFile queryFile) {
        FileInfo fileInfo = new FileInfo();
        try {
            String objectName = queryFile.getFileUrl();
            if (ossClient.doesObjectExist(aliyunConfig.getOss().getBucketName(), objectName)) {
                ObjectMetadata metadata = ossClient.getObjectMetadata(aliyunConfig.getOss().getBucketName(), objectName);

                fileInfo.setFileUrl(queryFile.getFileUrl());
                fileInfo.setFileName(objectName.substring(objectName.lastIndexOf("/") + 1));
                fileInfo.setFileSize(metadata.getContentLength());
                fileInfo.setLastModified(metadata.getLastModified().getTime());
                fileInfo.setContentType(metadata.getContentType());
                fileInfo.setExists(true);
            } else {
                fileInfo.setFileUrl(queryFile.getFileUrl());
                fileInfo.setExists(false);
            }
        } catch (Exception e) {
            throw new QueryException("阿里云OSS文件查询出现异常", e);
        }
        return fileInfo;
    }

    @Override
    public QueryListResult queryList(QueryFileList queryFileList) {
        QueryListResult result = new QueryListResult();
        List<FileInfo> fileList = new ArrayList<>();
        try {
            int maxKeys = 200;

            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(aliyunConfig.getOss().getBucketName())
                    .withMaxKeys(maxKeys);

            if (queryFileList.getContinuationToken() != null) {
                listObjectsV2Request.setContinuationToken(queryFileList.getContinuationToken());
            }

            ListObjectsV2Result ossResult = ossClient.listObjectsV2(listObjectsV2Request);

            List<OSSObjectSummary> sums = ossResult.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileUrl(s.getKey());
                fileInfo.setFileName(s.getKey().substring(s.getKey().lastIndexOf("/") + 1));
                fileInfo.setFileSize(s.getSize());
                fileInfo.setLastModified(s.getLastModified().getTime());
                fileInfo.setFile(!s.getKey().endsWith("/"));
                fileInfo.setDirectory(s.getKey().endsWith("/"));
                fileList.add(fileInfo);
            }

            result.setFileList(fileList);
            result.setContinuationToken(ossResult.getNextContinuationToken());
            result.setTruncated(ossResult.isTruncated());
        } catch (Exception e) {
            throw new QueryException("阿里云OSS文件列表查询出现异常", e);
        }
        return result;
    }
}
