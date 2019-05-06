package com.github.karsaig.approvalcrest;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD})
public @interface ApprovedFile {
    /**
     * If non blank value is supplied than it'll be used - instead of the first 6 characters of the SHA-1 hash of the test method name - as the prefix for the approved files.
     */
    String name() default "";

    /**
     * If non blank value is supplied than approved file path will be composed from the test class path + {@link java.io.File#separator} + this supplied value.
     */
    String path() default "";
}
