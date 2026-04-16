package com.cxcode.exam.infrastructure.persistence;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("mysql")
@MapperScan("com.cxcode.exam.infrastructure.persistence.mapper")
public class MybatisMapperConfig {
}
