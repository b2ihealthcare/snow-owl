/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.sets;

import java.util.Collection;

import com.b2international.commons.extension.ClassPathScanner;

/**
 * @since 7.7
 *
 */
public enum SetMemberExtensionProvider {

	INSTANCE;
	
	@SuppressWarnings("rawtypes")
	private final Collection<SetMemberExtension> memberExtensions;

	private SetMemberExtensionProvider() {
		this.memberExtensions = ClassPathScanner.INSTANCE.getComponentsByInterface(SetMemberExtension.class);
	}
	
	@SuppressWarnings("rawtypes")
	public SetMemberExtension getExtension(short terminologyComponentId) {
		return memberExtensions.stream()
			.filter(memberExtension -> memberExtension.terminologyComponentId() == terminologyComponentId)
			.findFirst()
			.orElseThrow(() -> {
				final String message = String.format("Unsopported terminology component id %d", terminologyComponentId);
				return new UnsupportedOperationException(message);
			});
	}
	
}
