package com.qiwenshare.ufop.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Jackson 工具类 (仿 FastJSON 风格 API)
 */
public class JSON {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 初始化配置
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        MAPPER.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        // 支持 Java8 时间类型
//        MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * 对象转 JSON 字符串
     */
    public static String toJSONString(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to JSON string error", e);
        }
    }

    /**
     * JSON 字符串转对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null)  {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("JSON string to Object error", e);
        }
    }

    /**
     * JSON 字符串转复杂类型对象 (如 List, Map 等)
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        if (json == null)  {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("JSON string to Object error", e);
        }
    }

    /**
     * JSON 字符串转 List
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
            return MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException("JSON string to List error", e);
        }
    }

    /**
     * JSON 字符串转 Map
     */
    public static <K, V> Map<K, V> parseMap(String json, Class<K> keyClass, Class<V> valueClass) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
            return MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException("JSON string to Map error", e);
        }
    }

    /**
     * 对象转 JSONP 格式
     */
    public static String toJSONPString(String functionName, Object obj) {
        try {
            return MAPPER.writeValueAsString(new JSONPObject(functionName, obj));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to JSONP string error", e);
        }
    }

    /**
     * 对象转美化格式的 JSON 字符串
     */
    public static String toPrettyJSONString(Object obj) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to pretty JSON string error", e);
        }
    }

    /**
     * 获取原始 ObjectMapper (用于特殊需求)
     */
    public static ObjectMapper getObjectMapper() {
        return MAPPER;
    }

    /**
     * 对象转换为另一个对象 (通过 JSON 中转)
     */
    public static <T> T convert(Object fromValue, Class<T> toValueType) {
        return MAPPER.convertValue(fromValue, toValueType);
    }

    /**
     * 对象转换为另一个对象 (复杂类型)
     */
    public static <T> T convert(Object fromValue, TypeReference<T> toValueTypeRef) {
        return MAPPER.convertValue(fromValue, toValueTypeRef);
    }
}