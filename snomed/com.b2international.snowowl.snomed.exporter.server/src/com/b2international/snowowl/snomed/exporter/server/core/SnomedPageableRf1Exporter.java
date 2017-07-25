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
package com.b2international.snowowl.snomed.exporter.server.core;

import java.util.Iterator;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.exporter.server.Id2Rf1PropertyMapper;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExporter;

/**
 * @since 4.6.12
 */
public abstract class SnomedPageableRf1Exporter<T extends PageableCollectionResource<S>, S> implements SnomedExporter {

	protected static final int DEFAULT_LIMIT = 10_000;
	
	private int offset = 0;
	private int currentIndex = 0;
	private T items;
	
	private SnomedExportConfiguration configuration;
	private Id2Rf1PropertyMapper mapper;

	public SnomedPageableRf1Exporter(SnomedExportConfiguration configuration, Id2Rf1PropertyMapper mapper) {
		this.configuration = configuration;
		this.mapper = mapper;
		this.items = initItems();
	}
	
	protected abstract T executeQuery(int offset);
	
	protected abstract String convertToString(S element);
	
	protected abstract T initItems();
	
	@Override
	public boolean hasNext() {
		
		if (currentIndex == items.getItems().size() && offset != items.getTotal()) {
			items = executeQuery(offset);
			currentIndex = 0;
			offset += items.getItems().size();
		}
		
		return items.getItems().size() > 0 && currentIndex < items.getItems().size();
	}
	
	@Override
	public String next() {
		return convertToString(items.getItems().get(currentIndex++));
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws Exception {
		// ignore
	}

	@Override
	public Iterator<String> iterator() {
		return this;
	}
	
	@Override
	public String getRelativeDirectory() {
		return RF1_CORE_RELATIVE_DIRECTORY;
	}
	
	@Override
	public String getFileName() {
		return SnomedRfFileNameBuilder.buildCoreRf1FileName(getType(), getConfiguration());
	}

	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
	}
	
	public Id2Rf1PropertyMapper getMapper() {
		return mapper;
	}
	
	protected IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
	
	protected List<ExtendedLocale> getLocales() {
		return ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference();
	}
	
	protected String getBranch() {
		return getConfiguration().getCurrentBranchPath().getPath();
	}
}
