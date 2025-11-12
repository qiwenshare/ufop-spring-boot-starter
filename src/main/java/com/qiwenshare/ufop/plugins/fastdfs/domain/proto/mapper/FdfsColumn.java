package com.qiwenshare.ufop.plugins.fastdfs.domain.proto.mapper;

import java.lang.annotation.*;

/**
 * 传输参数定义标签
 *
 * @author tobato
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FdfsColumn {
    /**
     * 映射顺序(从0开始)
     * @return 映射顺序
     */
    int index() default 0;

    /**
     * String最大值
     * @return String最大值
     */
    int max() default 0;

    /**
     * 动态属性
     * @return 动态属性
     */
    DynamicFieldType dynamicField() default DynamicFieldType.NULL;

}
