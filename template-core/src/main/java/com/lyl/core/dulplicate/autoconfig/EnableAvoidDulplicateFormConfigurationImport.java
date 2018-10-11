package com.lyl.core.dulplicate.autoconfig;

import com.lyl.core.dulplicate.inteceptor.AvoidDuplicateSubmissionInterceptor;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author lyl
 * @Description
 * @Date 2018/10/7 下午3:55
 */
public class EnableAvoidDulplicateFormConfigurationImport implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{AvoidDuplicateSubmissionInterceptor.class.getName()};
    }

}
