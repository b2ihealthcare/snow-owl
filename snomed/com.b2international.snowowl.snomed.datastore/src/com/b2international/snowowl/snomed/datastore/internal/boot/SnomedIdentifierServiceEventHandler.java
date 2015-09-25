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
package com.b2international.snowowl.snomed.datastore.internal.boot;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.snomed.core.events.SnomedIdentifierRequestEvent;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.google.inject.Provider;

/**
 * Event delegator implementation on top of {@link ISnomedIdentifierService} interface.
 * Delegates {@link SnomedIdentifierRequestEvent}s to {@link ISnomedIdentifierService#generateId(ComponentCategory)}
 * 
 * @since 4.0
 */
public class SnomedIdentifierServiceEventHandler implements IHandler<IMessage> {

	private Provider<ISnomedIdentifierService> identifierService;

	public SnomedIdentifierServiceEventHandler(Provider<ISnomedIdentifierService> identifierService) {
		this.identifierService = checkNotNull(identifierService, "identifierService");
	}

	@Override
	public void handle(IMessage message) {
		final SnomedIdentifierRequestEvent ev = message.body(SnomedIdentifierRequestEvent.class);
		final ComponentCategory category = ev.getCategory();
		final String namespace = ev.getNamespace();
		message.reply(identifierService.get().generateId(category, namespace));
	}

}
