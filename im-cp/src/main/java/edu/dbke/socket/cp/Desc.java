package edu.dbke.socket.cp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Desc {
	/**
	 * 协议值
	 */
	short key();

	/**
	 * 协议描述
	 */
	String desc() default "";
}
