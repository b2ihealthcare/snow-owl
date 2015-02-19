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
package org.eclipse.net4j.internal.util.bundle;

import java.text.MessageFormat;

import org.eclipse.net4j.util.om.log.OMLogHandler;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.log.OMLogger.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bridge to the Slf4j logging API.
 */
public class Slf4jLoggingBridge implements OMLogHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("Net4j");

	public void logged(OMLogger logger, Level level, String msg, Throwable t) {
		String message = MessageFormat.format("[{0}] {1}", logger.getBundle().getBundleID(), msg);
		switch (level) {
		case DEBUG:
			LOGGER.debug(message, t);
			break;
		case ERROR:
			LOGGER.error(message, t);
			break;
		case INFO:
			LOGGER.info(message, t);
			break;
		case WARN:
			LOGGER.warn(message, t);
			break;
		default:
			LOGGER.info(message, t);
			break;
		}
	}

}
