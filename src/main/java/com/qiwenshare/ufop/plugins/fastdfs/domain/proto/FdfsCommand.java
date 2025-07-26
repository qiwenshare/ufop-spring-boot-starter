package com.qiwenshare.ufop.plugins.fastdfs.domain.proto;

import com.qiwenshare.ufop.plugins.fastdfs.domain.conn.Connection;

/**
 * Fdfs交易命令抽象
 *
 * @author tobato
 */
public interface FdfsCommand<T> {

    /**
     * 执行交易
     */
    public T execute(Connection conn);

}
