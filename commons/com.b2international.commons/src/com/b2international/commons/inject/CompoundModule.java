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
package com.b2international.commons.inject;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * @since 2.9
 */
public class CompoundModule implements Module {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompoundModule.class);
	
	private List<Module> modules = newArrayList();

	@Override
	public void configure(final Binder binder) {
		for (final Module module : modules) {
			try {
				module.configure(binder);
			} catch (final Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public void add(final Module module) {
		modules.add(module);
	}
	
	public void remove(final Module module) {
		modules.remove(module);
	}
	
}