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
package com.b2international.snowowl.core;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;

/**
 * Utility for logging Snow Owl's messages. This implementation is Logback specific.
 * It is the logger's configuration's responsibility to capture and collect the user events
 * sent by this method to a proper user event log.
 * A typical configuration that filters these events would look like this:
 * <pre>
 * {@code
 *  <!-- Application specific auditing log.-->
 *	<appender name="SNOWOWL_LOG_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
 *		<file>serviceability/logs/snowowl_user_audit.log</file>
 *		<rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
 *			<FileNamePattern>serviceability/logs/snowowl_user_audit%i.log</FileNamePattern>
 *			<MinIndex>1</MinIndex>
 *			<MaxIndex>10</MaxIndex>
 *		</rollingPolicy>
 *		<triggeringPolicy
 *			class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
 *			<MaxFileSize>50MB</MaxFileSize>
 *		</triggeringPolicy>
 *		<filter class="ch.qos.logback.core.filter.EvaluatorFilter">
 *			<evaluator class="ch.qos.logback.classic.boolex.OnMarkerEvaluator">
 *				<marker>SNOW_OWL</marker>
 *			</evaluator>
 *			<onMismatch>DENY</onMismatch>
 *			<onMatch>ACCEPT</onMatch>
 *		</filter>
 *		<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
 *			<Pattern>..Your pattern comes here..</Pattern>
 *	 </encoder>
 *	</appender>
 *  }
 *  </pre>
 *
 *
 */
public class LogUtils {
	
	public static final String MDC_USER_KEY = "USER"; //$NON-NLS-N$
	public static final String MDC_BRANCH_KEY = "BRANCH"; //$NON-NLS-N$
	
	public static final String SNOWOWL_MARKER = "SNOW_OWL"; //$NON-NLS-N$
	public static final String SNOWOWL_USER_ACCESS_MARKER = "SNOW_OWL_USER_ACCESS"; //$NON-NLS-N$
	public static final String SNOWOWL_IMPORT_MARKER = "SNOW_OWL_IMPORT"; //$NON-NLS-N$
	public static final String SNOWOWL_EXPORT_MARKER = "SNOW_OWL_EXPORT"; //$NON-NLS-N$

	/**
	 * User event logging.  Logs the message with a marker and MDC information such as user and fully qualified
	 * branch path.
	 * 
	 * @param logger
	 * @param user
	 * @param brancPath
	 * @param message
	 */
	public static void logUserEvent(Logger logger, String user, String branchPath, String message) {
		MDC.put(MDC_USER_KEY, user);
		MDC.put(MDC_BRANCH_KEY, branchPath);
		logger.info(MarkerFactory.getMarker(SNOWOWL_MARKER), message);
		MDC.remove(MDC_USER_KEY);
		MDC.remove(MDC_BRANCH_KEY);
	}
	
	/**
	 * User event logging.  Logs the message with a marker and MDC information such as user.
	 * 
	 * @param logger
	 * @param user
	 * @param brancPath
	 * @param message
	 */
	public static void logUserEvent(Logger logger, String user, String message) {
		MDC.put(MDC_USER_KEY, user);
		logger.info(MarkerFactory.getMarker(SNOWOWL_MARKER), message);
		MDC.remove(MDC_USER_KEY);
	}
	
	/**
	 * User access logging. Logs the user access message with the marker and the MDC information such as user.
	 * @param logger
	 * @param user
	 * @param message
	 */
	public static void logUserAccess(Logger logger, String user, String message) {
		MDC.put(MDC_USER_KEY, user);
		logger.info(MarkerFactory.getMarker(SNOWOWL_USER_ACCESS_MARKER), message);
		MDC.remove(MDC_USER_KEY);
	}
	
	/**
	 * Import activity logging. Logs the import activity message with the marker and the MDC information such as user.
	 * @param logger
	 * @param user
	 * @param branchPath
	 * @param message
	 */
	public static void logImportActivity(Logger logger, String user, String branchPath, String message, Object...arguments) {
		MDC.put(MDC_USER_KEY, user);
		MDC.put(MDC_BRANCH_KEY, branchPath);
		logger.info(MarkerFactory.getMarker(SNOWOWL_IMPORT_MARKER), message, arguments);
		MDC.remove(MDC_USER_KEY);
		MDC.remove(MDC_BRANCH_KEY);
	}
	
	/**
	 * Import activity logging. Logs the import activity message as a warning 
	 * with the marker and the MDC information such as user.
	 * @param logger
	 * @param user
	 * @param branchPath
	 * @param message
	 */
	public static void logImportWarning(Logger logger, String user,  String branchPath, String message) {
		MDC.put(MDC_USER_KEY, user);
		MDC.put(MDC_BRANCH_KEY, branchPath);
		logger.warn(MarkerFactory.getMarker(SNOWOWL_IMPORT_MARKER), message);
		MDC.remove(MDC_USER_KEY);
		MDC.remove(MDC_BRANCH_KEY);
	}
	
	/**
	 * Export activity logging. Logs the export activity message with the marker and the MDC information such as user.
	 * @param logger
	 * @param user
	 * @param branchPath
	 * @param message
	 */
	public static void logExportActivity(Logger logger, String user, String branchPath, String message) {
		MDC.put(MDC_USER_KEY, user);
		MDC.put(MDC_BRANCH_KEY, branchPath);
		logger.info(MarkerFactory.getMarker(SNOWOWL_EXPORT_MARKER), message);
		MDC.remove(MDC_USER_KEY);
		MDC.remove(MDC_BRANCH_KEY);
	}
}