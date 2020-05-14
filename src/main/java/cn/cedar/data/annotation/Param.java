package cn.cedar.data.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author cedar12.zxd@qq.com
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Param {
    String value();
}
