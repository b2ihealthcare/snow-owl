/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es.log;

import java.io.Serializable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 6.15
 */
@Plugin(
	name = "slf4j", 
	category = Core.CATEGORY_NAME, 
	elementType = Appender.ELEMENT_TYPE)
public final class Slf4jAppender extends AbstractAppender {

	public Slf4jAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
		super(name, filter, layout);
	}

	@Override
	public void append(LogEvent event) {
		final Logger log = LoggerFactory.getLogger(event.getLoggerName());
		if (Level.TRACE == event.getLevel()) {
			log.trace(event.getMessage().getFormattedMessage());
		} else if (Level.DEBUG == event.getLevel()) {
			log.debug(event.getMessage().getFormattedMessage());
		} else if (Level.ERROR == event.getLevel()) {
			log.error(event.getMessage().getFormattedMessage());
		} else if (Level.WARN == event.getLevel()) {
			log.warn(event.getMessage().getFormattedMessage());
		} else if (Level.INFO == event.getLevel()) {
			log.info(event.getMessage().getFormattedMessage());
		} else {
			log.error("Unhandled level: {}", event.toString());
		}
	}

	@PluginFactory
    public static Slf4jAppender createAppender(
    		@PluginAttribute("name") String name, 
    		@PluginElement("Layout") Layout<? extends Serializable> layout) {
		return new Slf4jAppender(name, null, layout);
    }

}