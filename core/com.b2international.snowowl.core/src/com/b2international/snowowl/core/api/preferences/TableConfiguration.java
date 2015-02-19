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
package com.b2international.snowowl.core.api.preferences;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 */
@XStreamAlias("tableConfiguration")
public final class TableConfiguration extends AbstractSerializableConfiguration<ColumnConfiguration> {

	/**
	 * Default constructor for serialization.
	 */
	protected TableConfiguration() {
	}
		
	public TableConfiguration(String key) {
		super(key);
	}

	public Iterable<ColumnSetting> getEntrySettings() {
		return Iterables.transform(entires.values(), new Function<ColumnConfiguration, ColumnSetting>() {
			@Override
			public ColumnSetting apply(ColumnConfiguration columnConfiguration) {
				return columnConfiguration.getColumnSetting();
			}
		});
	}

	
}