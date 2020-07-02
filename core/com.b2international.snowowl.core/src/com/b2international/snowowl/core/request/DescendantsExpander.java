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
package com.b2international.snowowl.core.request;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;

/**
 * @since 7.7
 */
public abstract class DescendantsExpander<T extends IComponent> extends ResourceExpander {

	public static final String DEFAULT_DESCENDANTS_EXPAND_KEY = "descendants"; //$NON-NLS-N$
	public static final String OPTION_DIRECT = "direct"; //$NON-NLS-N$
	
	private final String descendantsExpandKey;
	
	public DescendantsExpander(BranchContext context, List<ExtendedLocale> locales, Options expand) {
		this(context, expand, locales, DEFAULT_DESCENDANTS_EXPAND_KEY);
	}
	
	public DescendantsExpander(BranchContext context, Options expand, List<ExtendedLocale> locales, String descendantsExpandKey) {
		super(context, expand, locales);
		this.descendantsExpandKey = descendantsExpandKey;
	}
	
	public final boolean checkDirect(final Options descendantsExpandOptions) {
		return checkDirect(descendantsExpandOptions, descendantsExpandKey);
	}
	
	public static boolean checkDirect(final Options expandOptions, String expandKey) {
		if (!expandOptions.containsKey(OPTION_DIRECT)) {
			throw new BadRequestException("Direct parameter required for '%s' expansion", expandKey);
		}
		return expandOptions.getBoolean(OPTION_DIRECT);
	}
	
	public final void expand(List<T> results) {
		expand(results, results.stream().map(IComponent::getId).collect(Collectors.toSet()));
	}
	
	public final void expand(List<T> results, Set<String> ids) {
		if (!expand().containsKey(descendantsExpandKey)) {
			return;
		}
		
		final Options descendantExpandOptions = expand().get(descendantsExpandKey, Options.class);
		final boolean direct = checkDirect(descendantExpandOptions);		
		expand(results, ids, descendantExpandOptions, direct);
	}
	
	protected abstract void expand(List<T> results, Set<String> ids, Options descendantExpandOptions, boolean direct);
	
	@Override
	protected final BranchContext context() {
		return (BranchContext) super.context();
	}
	
}
