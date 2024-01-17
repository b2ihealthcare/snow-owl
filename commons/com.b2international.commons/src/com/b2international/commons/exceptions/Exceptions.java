/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons.exceptions;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.ReflectionUtils;
import com.b2international.commons.StringUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

/**
 * Utility class for exceptions.
 *
 */
public abstract class Exceptions {

	private static final Logger LOGGER = getLogger(Exceptions.class);
	
	/**Represents the '&#58' character as a string.*/
	private static final String COLON = ":";

	/**Represents the message field on a {@link Throwable}. See {@link Throwable#getMessage()};*/
	private static final String THROWABLE_DETAILED_MESSAGE = "detailMessage";

	/**Pattern for a fully qualified JAVA class name.*/
	private static final Pattern JAVA_FQCN_PATTERN = Pattern.compile("([a-z][a-z_0-9]*\\.)*[A-Z_]($[A-Z_]|[\\w_])*");
	/**Pattern for a valid stack trace element prefix.*/
	private static final Pattern STACK_TRACE_ELEMENT_PATTERN = Pattern.compile("\\s*at\\s+([\\w\\.$_]+)\\.([\\w$_]+)(\\(.*java)?:(\\d+)\\)");
	
	/**
	 * Tries to extract the expected cause from the commit exception and returns with it. May return with {@code null}
	 * if the expected exception cannot be extracted from the wrapper commit exception.
	 * @param t the throwable possible wrapping the original cause of a failure commit.
	 * @param classLoader class loader for resolving the excepted cause. May be {@code null}. 
	 * @param expectedCauseType the expected case type.
	 * @return the wrapped cause or {@code null} if cannot be extracted.
	 */
	public static <T extends Throwable> T extractCause(final Throwable t, final ClassLoader classLoader, final Class<? extends T> expectedCauseType) {
		
		Preconditions.checkNotNull(t, "Exception argument cannot be null.");
		Preconditions.checkNotNull(expectedCauseType, "Expected cause type class argument cannot be null.");
		
		@SuppressWarnings("rawtypes") final Pair<Class, String> cause = extractCause0(t, classLoader, expectedCauseType);
		if (null != cause && expectedCauseType.isAssignableFrom(cause.getA())) {
			try {
				final T $ = expectedCauseType.newInstance();
				ReflectionUtils.setField(Throwable.class, $, THROWABLE_DETAILED_MESSAGE, Strings.nullToEmpty(cause.getB()));
				return $;
			} catch (final InstantiationException e1) {
				LOGGER.error("Cannot instantiate " + cause.getA().getSimpleName() + " instance as the cause of " + t + ".");
			} catch (final IllegalAccessException e1) {
				LOGGER.error("Cannot instantiate " + cause.getA().getSimpleName() + " instance as the cause of " + t + ".");
			}
		}
		
		return null;
	}
	
	/*tries to extract the original cause and its detailed message of commit attempt from the given commit exception. can be null, if extraction failed.*/
	@SuppressWarnings({ "rawtypes" })
	private static <T extends Throwable> Pair<Class, String> extractCause0(final Throwable t, final ClassLoader classLoader, final Class<T> expectedCauseType) {
		
		Preconditions.checkNotNull(t, "Throwable argument cannot be null.");
		final String message = Throwables.getStackTraceAsString(t);
		
		if (StringUtils.isEmpty(message)) {

			//return with the original exception			
			return null;
			
		}
		
		final Matcher matcher = JAVA_FQCN_PATTERN.matcher(message);
		
		Class<?> clazz = null;
		String detailedMessage = null;
		
		while (matcher.find()) {
			
			final String fqcn = matcher.group(0);
			
			try {
				
				if (null != classLoader) {
					
					clazz = classLoader.loadClass(fqcn);
					
				}
				
				if (null == clazz) {
					
					clazz = Class.forName(fqcn);
					
				}
				
				if (expectedCauseType.isAssignableFrom(clazz)) {
					
					detailedMessage = extractCauseMessage(clazz, message);
					return Pair.<Class, String>of(clazz, detailedMessage);
					
				}
				
			} catch (final ClassNotFoundException cnfe) {
				
				//ignore
				
			}
			
		}
		
		return null == clazz ? null : Pair.of((Class) clazz, Strings.nullToEmpty(detailedMessage));
		
	}
	
	/*extracts the throwable message from the stack trace message. may return with an empty string is the extraction failed.*/
	private static String extractCauseMessage(final Class<?> clazz, final String message) {
		
		final String classNameRegexp = clazz.getName() + COLON;
		final String[] split = message.split(classNameRegexp, 2);
		
		if (CompareUtils.isEmpty(split)) {

			return Strings.nullToEmpty(null);
			
		} else {
			
			final String causePart = Strings.nullToEmpty(split[1 == split.length ? 0 : 1]);
			final Matcher matcher = STACK_TRACE_ELEMENT_PATTERN.matcher(causePart);
			return matcher.find() 
					? Strings.nullToEmpty(causePart).length() > 0 
						? causePart.substring(1, matcher.start()) : Strings.nullToEmpty(null) 
						: Strings.nullToEmpty(null);
			
		}
		
	}
	
	private Exceptions() {
		//suppressed constructor
	}
	
}