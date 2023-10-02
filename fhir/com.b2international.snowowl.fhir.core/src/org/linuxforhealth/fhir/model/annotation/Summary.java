/*
 * (C) Copyright IBM Corp. 2019
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import com.google.common.collect.ImmutableSortedSet;

/*
 * Modifications:
 * 
 * - Extended annotation with constants holding possible values
 *   (see http://hl7.org/fhir/R5/search.html#_summary)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Summary {

	/** Return a limited subset of elements from the resource, ie. all supported elements marked with this annotation */
	public static final String TRUE = "true";
	/** Return all parts of the resource */
	public static final String FALSE = "false";
	/** Return "text", "id", "meta" and top-level mandatory elements (to ensure that the payload is valid FHIR) */
	public static final String TEXT = "text";
	/** Remove the "text" element */
	public static final String DATA = "data";
	/** Search only: return hit count without returning actual matches */
	public static final String COUNT = "count";
	
	public static final Set<String> VALUES = ImmutableSortedSet.of(TRUE, FALSE, TEXT, DATA, COUNT);
}
