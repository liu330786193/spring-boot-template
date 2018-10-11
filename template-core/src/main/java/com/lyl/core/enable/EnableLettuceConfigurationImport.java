package com.lyl.core.enable;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author lyl
 * @Description
 * @Date 2018/10/7 下午3:55
 */
public class EnableLettuceConfigurationImport implements ImportSelector {


    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{RedisConfig.class.getName(), RedisTemplate.class.getName()};
    }

}
