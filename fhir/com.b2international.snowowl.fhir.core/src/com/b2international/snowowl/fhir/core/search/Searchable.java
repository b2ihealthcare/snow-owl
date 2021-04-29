package com.b2international.snowowl.fhir.core.search;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Searchable {
	
	String name() default "";
	
	/**
	 * Type of the searchable parameter.
	 * @see https://www.hl7.org/fhir/search.html#ptypes
	 * @return
	 */
	String type() default "String";

	/**
	 * Supported modifiers for the given searchable property.
	 * E.g. exact, missing, below, etc.
	 * @return
	 */
	String[] modifiers() default {};
	
}
