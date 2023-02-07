package com.kent;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }


    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor(DbType.MARIADB);
//        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor(DbType.ORACLE);

        interceptor.addInnerInterceptor(innerInterceptor);
        return interceptor;
    }

    /**
     * Map下划线自动转驼峰
     *
     * @return
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return i -> i.setObjectWrapperFactory(new MybatisMapWrapperFactory());
    }
}

