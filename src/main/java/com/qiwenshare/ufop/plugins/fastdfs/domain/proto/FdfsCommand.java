package com.qiwenshare.ufop.plugins.fastdfs.domain.proto;

import com.qiwenshare.ufop.plugins.fastdfs.domain.conn.Connection;

/**
 * Fdfs交易命令抽象
 *
 * @author tobato
 */
public interface FdfsCommand<T> {

    /**
     * 执行交易命令
     *
     * @param conn 连接对象
     * @return 响应对象
     */
    public T execute(Connection conn);

}
