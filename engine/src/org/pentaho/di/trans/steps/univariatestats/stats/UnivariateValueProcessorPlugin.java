package org.pentaho.di.trans.steps.univariatestats.stats;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.Target;

@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface UnivariateValueProcessorPlugin {

  String id();

  String name();

  String description() default "";

  boolean isSeparateClassLoaderNeeded() default false;

  String documentationUrl() default "";

  String casesUrl() default "";

  String forumUrl() default "";

  String classLoaderGroup() default "";

  String[] parameterNames() default { };

  int[] parameterTypes() default { };

  boolean hidden() default false;
}
