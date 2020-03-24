package cn.cedar.data;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author 413338772@qq.com
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Param {
    String value();
}
