package com.lyl.core.lock.autoconfig;

import com.lyl.core.lock.interceptor.LockMethodInteceptor;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author lyl
 * @Description
 * @Date 2018/10/7 下午3:55
 */
public class EnableDulplicateConfigurationImport implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{LockMethodInteceptor.class.getName()};
    }

}
