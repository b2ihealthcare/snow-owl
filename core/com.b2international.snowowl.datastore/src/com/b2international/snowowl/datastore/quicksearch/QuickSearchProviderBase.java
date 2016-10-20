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
package com.b2international.snowowl.datastore.quicksearch;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.quicksearch.DefaultQuickSearchCallback;
import com.b2international.snowowl.core.quicksearch.IQuickSearchCallback;
import com.b2international.snowowl.core.quicksearch.IQuickSearchProvider;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.google.common.collect.ImmutableMap;

/**
 * Abstract superclass handling common functionality of the {@link IQuickSearchProvider} interface.
 * 
 */
public abstract class QuickSearchProviderBase implements IQuickSearchProvider {
	
	private static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();

	protected QuickSearchContentResult state = new QuickSearchContentResult();
	
	protected IQuickSearchCallback callback = DefaultQuickSearchCallback.INSTANCE;
	
	protected Map<String, Object> configuration;
	
	private static String getCurrentUserId() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getUserId();
	}

	public QuickSearchProviderBase() {
		this(EMPTY_MAP);
	}
	
	public QuickSearchProviderBase(final Map<String, Object> configuration) {
		setConfiguration(configuration);
	}
	
	@Override
	public List<QuickSearchElement> getElements() {
		return state.getElements();
	}

	@Override
	public void setCallback(final IQuickSearchCallback callback) {
		this.callback = checkNotNull(callback, "callback");
	}
	
	/**
	 * <b>NOTE:&nbsp;</b>this method clears all the previously stored {@link QuickSearchElement quick search element}s. 
	 * <p>
	 * {@inheritDoc}}
	 */
	@Override
	public void setState(final QuickSearchContentResult state) {
		this.state = state;
		for (final QuickSearchElement element : state.getElements()) {
			element.setParentProvider(this);
		}
	}
	
	@Override
	public void handleSelection(final QuickSearchElement element) {
		callback.handleSelection(element);
	}

	@Override
	public int getTotalHitCount() {
		return state.getTotalHitCount();
	}
	
	@Override
	public int getSuffixMultiplier() {
		return 1;
	}
	
	@Override
	public void setConfiguration(final Map<String, Object> configuration) {
		this.configuration = ImmutableMap.<String,Object>builder()
				.putAll(configuration)
				.put(IQuickSearchProvider.CONFIGURATION_USER_ID, getCurrentUserId())
				.build();
	}
	
	@Override
	public Map<String, Object> getConfiguration() {
		return configuration;
	}
	
	@Override
	public boolean isGloballyAvailable() {
		return true;
	}
}