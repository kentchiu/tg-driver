package com.kent.base.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DomainUtil {

    private DomainUtil() {
    }


    public static void copyNotNullProperties(Object source, Object target, String... ignoreProperties) {

        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(source.getClass());

        List<String> nullValueProperties = Arrays.stream(pds).filter(pd -> {
            try {
                Object invoke = pd.getReadMethod().invoke(source);
                return invoke == null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return true;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return true;
            }
        }).map(pd -> pd.getName()).collect(Collectors.toList());
        String[] nulls = Iterables.toArray(nullValueProperties, String.class);
        String[] ignores = ArrayUtils.addAll(ignoreProperties, nulls);
        BeanUtils.copyProperties(source, target, ignores);
        copyDateProperties(source, target, ignores);
    }

    private static void copyDateProperties(Object source, Object target, String... ignoreProperties) {
        // 處理 target 的 date properties
        List<String> dateProperties = Arrays.stream(BeanUtils.getPropertyDescriptors(target.getClass()))
                .filter(pd -> pd.getPropertyType().isAssignableFrom(Date.class))
                .filter(pd -> !ArrayUtils.contains(ignoreProperties, pd.getName()))
                .map(pd -> pd.getName()).collect(Collectors.toList());

        for (String p : dateProperties) {
            try {
                // 取得source中對應名稱的屬性
                PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(source.getClass(), p);
                if (pd != null) {
                    Date srcDateValue = getDateValueOfSource(source, target, p);
                    PropertyDescriptor pd2 = BeanUtils.getPropertyDescriptor(target.getClass(), p);
                    pd2.getWriteMethod().invoke(target, srcDateValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Date getDateValueOfSource(Object source, Object target, String p) throws IllegalAccessException, InvocationTargetException {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(source.getClass(), p);
        if (pd == null) {
            return null;
        }
        Object srcValue = pd.getReadMethod().invoke(source);

        if (pd.getReadMethod().getReturnType().equals(String.class)) {
            String strValue = (String) srcValue;
            if (strValue == null) {
                return (Date) pd.getReadMethod().invoke(target);
            } else if (StringUtils.isBlank(strValue)) {
                return null;
            } else {
                try {
                    return DateUtils.parseDate(srcValue.toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy-MM-dd HH:mm");
                } catch (ParseException e) {
                    return null;
                }
            }
        } else if (pd.getReadMethod().getReturnType().isAssignableFrom(Date.class)) {
            Date dateValue = (Date) srcValue;
            return dateValue != null ? dateValue : (Date) pd.getReadMethod().invoke(target);
        } else {
            return null;
        }
    }


    /**
     * 建立可以处理 Java 8 java.time.LocalDateTime 的 Object Mapper。
     * 注意： 尽可能的使用 spring 提供的 ObjectMapper， 在无法处理（通常是做 deserialize）时，才用这个，这个版本的 convert 不一定和跟 spring 提供的完全相同
     *
     * @return
     */
    public static ObjectMapper createObjectMapper() {
        /** 默认日期时间格式 */
        final String DEFAULT_DATE_TIME_FORMAT = "[yyyyMMdd][yyyy-MM-dd][yyyy-DDD]['T'[HHmmss][HHmm][HH:mm:ss][HH:mm][.SSSSSSSSS][.SSSSSS][.SSS][.SS][.S]][OOOO][O][z][XXXXX][XXXX]['['VV']']";
        /** 默认日期格式 */
        final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
        /** 默认时间格式 */
        final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        objectMapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());
        return objectMapper;
    }


    public static Map<String, Object> convertPojoToMap(Object pojo) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(pojo, new TypeReference<Map<String, Object>>() {
        });
    }

}
