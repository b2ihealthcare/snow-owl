/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.console;

import static com.google.common.base.Preconditions.checkArgument;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 7.0
 */
public abstract class BaseCommand {
	
	private ServiceProvider context;
	
	void setContext(ServiceProvider context) {
		this.context = context;
	}
	
	protected final IEventBus getBus() {
		return context.service(IEventBus.class);
	}
	
	protected final ServiceProvider getContext() {
		return context;
	}
	
	public abstract void run(CommandLineStream out);
	
	final String getCommand() {
		checkArgument(getClass().isAnnotationPresent(picocli.CommandLine.Command.class), "%s class must be annotated and configured with picocli in order to be used as Snow Owl shell command", getClass().getSimpleName());
		return getClass().getAnnotation(picocli.CommandLine.Command.class).name();
	}
	
}
