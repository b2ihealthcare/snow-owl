/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.b2international.snowowl.core.api.preferences.io.XStreamWrapper;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 */
@XStreamAlias("tableConfiguration")
public final class TableConfiguration extends AbstractSerializableConfiguration<ColumnConfiguration> {

	/**
	 * Default constructor for serialization.
	 */
	protected TableConfiguration() {}

	public static Builder build(final String key) {
		return new Builder(key);
	}

	public static TableConfiguration deserialize(final String config) {
		return new XStreamWrapper(TableConfiguration.class).fromXML(config);
	}

	public TableConfiguration(final String key) {
		super(key);
	}

	public Iterable<ColumnSetting> getColumnConfigurations() {
		return entries.values().stream().map(config -> config.getColumnSetting()).collect(toList());
	}

	public String serialize() {
		return new XStreamWrapper(TableConfiguration.class).toXML(this);
	}

	public static final class Builder {

		private final String key;
		private final List<ColumnConfiguration> columnConfigs;

		private Builder(final String key) {
			this.key = key;
			this.columnConfigs = newArrayList();
		}

		public Builder withColumn(final String label, final String iconPath, final ColumnSetting columnSetting) {
			columnConfigs.add(new ColumnConfiguration(label, iconPath, columnSetting));
			return this;
		}

		public Builder withColumn(final String label, final ColumnSetting columnSetting) {
			columnConfigs.add(new ColumnConfiguration(label, columnSetting));
			return this;
		}

		public TableConfiguration build() {
			final TableConfiguration configuration = new TableConfiguration(this.key);
			columnConfigs.forEach(config -> configuration.add(config));
			return configuration;
		}

	}

}