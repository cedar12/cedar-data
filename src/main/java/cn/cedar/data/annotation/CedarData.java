package cn.cedar.data.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author 413338772@qq.com
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CedarData{
    String value() default "";
    String dms() default "";
}
