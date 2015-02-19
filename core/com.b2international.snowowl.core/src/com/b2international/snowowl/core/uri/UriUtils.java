/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.core.uri;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Collection of utility methods and constants related to URIs.
 * 
 */
public final class UriUtils {
	
	public static final int PROTOCOL_INDEX = 0;
	
	/*
	 * Terminology protocol
	 * 
	 * Examples:
	 * 		terminology:2.16.840.1.113883.6.96?code=284296006
	 * 		terminology:2.16.840.1.113883.6.96?refset=284296006
	 * 		terminology:2.16.840.1.113883.6.3?code=A00
	 */
	public static final String TERMINOLOGY_PROTOCOL = "terminology";
	public static final int TERMINOLOGY_OID_INDEX = 1;
	public static final int COMPONENT_TYPE_INDEX = 2;
	public static final int COMPONENT_ID_INDEX = 3;
	
	/*
	 * Expression protocol
	 * 
	 * Examples: 
	 * 		expression:ocl?self.isNotNull
	 * 		expression:escg?<<284296006
	 */
	public static final String EXPRESSION_PROTOCOL = "expression";
	public static final String OCL_LANGUAGE = "ocl";
	public static final String ESCG_LANGUAGE = "escg";
	public static final int EXPRESSION_LANGUAGE_INDEX = 1;
	public static final int EXPRESSION_STRING_INDEX = 2;
	
	private static final Pattern URI_SEPARATOR_PATTERN = Pattern.compile(":|\\?|=");
	private static final Pattern EXPRESSION_URI_PATTERN = Pattern.compile(EXPRESSION_PROTOCOL + ":\\w+\\?.+");
	private static final Pattern TERMINOLOGY_COMPONENT_URI_PATTERN = Pattern.compile(TERMINOLOGY_PROTOCOL + ":\\w+(\\.(\\w+))+\\?\\w+=.+");

	private UriUtils() {}
	
	/**
	 * Returns the segments of the URI string passed in.
	 * 
	 * @param uri the URI string
	 * @return the list of segments
	 */
	public static List<String> getUriSegments(String uri) {
		checkNotNull(uri, "URI must not be null.");
		Iterable<String> uriSegments = Splitter.on(URI_SEPARATOR_PATTERN).split(uri);
		return ImmutableList.copyOf(uriSegments);
	}
	
	public static String getProtocol(String uri) {
		checkNotNull(uri, "URI must not be null.");
		List<String> uriSegments = getUriSegments(uri);
		return uriSegments.get(PROTOCOL_INDEX);
	}
	
	public static String getExpressionLanguage(String uri) {
		checkNotNull(uri, "URI must not be null.");
		List<String> uriSegments = getUriSegments(uri);
		return uriSegments.get(EXPRESSION_LANGUAGE_INDEX);
	}
	
	public static String getExpression(String uri) {
		checkNotNull(uri, "URI must not be null.");
		Iterable<String> segments = Splitter.on('?').split(uri);
		return Iterables.get(segments, 1);
	}
	
	/**
	 * @param uri the URI
	 * @return true if the URI is a syntactically valid terminology URI, false otherwise
	 */
	public static boolean isTerminologyUri(String uri) {
		return TERMINOLOGY_COMPONENT_URI_PATTERN.matcher(uri).matches();
	}
	
	/**
	 * @param uri the URI
	 * @return true if the URI is a syntactically valid expression URI, false otherwise
	 */
	public static boolean isExpressionUri(String uri) {
		return EXPRESSION_URI_PATTERN.matcher(uri).matches();
	}
}