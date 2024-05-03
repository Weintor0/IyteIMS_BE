package edu.iyte.ceng.internship.ims.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AssociatedWithEntity {
    String entityName() default "";
}
