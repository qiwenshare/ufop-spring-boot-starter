package com.qiwenshare.ufop.operation.query.domain;

import java.util.List;

public class QueryListResult {
    private List<FileInfo> fileList;
    private String continuationToken;
    private boolean isTruncated;

    public List<FileInfo> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileInfo> fileList) {
        this.fileList = fileList;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public boolean isTruncated() {
        return isTruncated;
    }

    public void setTruncated(boolean truncated) {
        isTruncated = truncated;
    }
}
