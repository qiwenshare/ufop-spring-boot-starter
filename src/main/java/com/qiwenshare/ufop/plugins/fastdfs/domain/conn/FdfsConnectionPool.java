package com.qiwenshare.ufop.plugins.fastdfs.domain.conn;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 定义Fdfs连接池对象
 * 定义了对象池要实现的功能,对一个地址进行池化Map Pool
 *
 * @author tobato
 */
@Component
public class FdfsConnectionPool extends GenericKeyedObjectPool<InetSocketAddress, Connection> {

    /**
     * 默认构造函数
     *
     * @param factory  连接池工厂
     * @param config   连接池配置
     */
    @Autowired
    public FdfsConnectionPool(KeyedPooledObjectFactory<InetSocketAddress, Connection> factory,
                              GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }

    /**
     * 默认构造函数
     *
     * @param factory   连接池工厂
     */
    public FdfsConnectionPool(KeyedPooledObjectFactory<InetSocketAddress, Connection> factory) {
        super(factory);
    }

}
