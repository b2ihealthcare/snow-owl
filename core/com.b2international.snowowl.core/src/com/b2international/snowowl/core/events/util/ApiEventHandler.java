/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.events.util;

import java.lang.reflect.Method;
import java.util.Collections;

import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.snowowl.core.exceptions.ApiException;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.google.common.base.Predicate;

/**
 * @since 4.1
 */
public abstract class ApiEventHandler implements IHandler<IMessage> {

	private PolymorphicDispatcher<Object> handlerDispatcher = new PolymorphicDispatcher<Object>(Collections.singletonList(this), new Predicate<Method>() {
		@Override
		public boolean apply(Method input) { 
			return input.getAnnotation(Handler.class) != null; 
		}
	}, new PolymorphicDispatcher.DefaultErrorHandler<Object>() {
		@Override
		public Object handle(Object[] params, Throwable e) {
			if (e instanceof NoSuchMethodException) {
				throw new NotImplementedException("Event handling not implemented: " + params[0]);
			}
			return super.handle(params, e);
		}
	});
	
	private final ClassLoader classLoader;
	private final Class<?> eventInterface;
	
	protected ApiEventHandler(Class<?> eventInterface, ClassLoader classLoader) {
		this.eventInterface = eventInterface;
		this.classLoader = classLoader;
	}
	
	@Override
	public final void handle(IMessage message) {
		try {
			message.reply(handlerDispatcher.invoke(message.body(eventInterface, classLoader)));
		} catch (WrappedException e) {
			message.fail(e.getCause());
		} catch (ApiException e) {
			message.fail(e);
		} catch (Throwable e) {
			message.fail(e);
		}
	}

}
