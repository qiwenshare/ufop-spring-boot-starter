package com.qiwenshare.ufop.operation.query;

import com.qiwenshare.ufop.operation.query.domain.FileInfo;
import com.qiwenshare.ufop.operation.query.domain.QueryFile;
import com.qiwenshare.ufop.operation.query.domain.QueryFileList;
import com.qiwenshare.ufop.operation.query.domain.QueryListResult;

public abstract class Querier {
    public abstract FileInfo query(QueryFile queryFile);
    public abstract QueryListResult queryList(QueryFileList queryFileList);
}
