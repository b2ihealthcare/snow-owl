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

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 */
@XStreamAlias("columnConfiguration")
public final class ColumnConfiguration extends AbstractEntrySetting {

	@XStreamAlias("iconPath")
	private String iconPath;
	
	@XStreamAlias("configuration")
	private ColumnSetting columnSetting;
	
	@XStreamAlias("enabled")
	private boolean enabled;
	
	/**
	 * Default constructor for serialization.
	 */
	protected ColumnConfiguration() {
		super();
	}
	
	public ColumnConfiguration(String entryKey, ColumnSetting columnSetting) {
		this(entryKey, null, columnSetting, false);
	}
	
	public ColumnConfiguration(String entryKey, String iconPath, ColumnSetting columnSetting) {
		this(entryKey, iconPath, columnSetting, true);
	}
	
	private ColumnConfiguration(String entryKey, String iconPath, ColumnSetting columnSetting, boolean enabled) {
		super(entryKey);
		this.iconPath = iconPath;
		this.columnSetting = columnSetting;
		this.enabled = enabled;
	}
	
	public final String getIconPath() {
		return iconPath;
	}
	
	public final boolean isEnabled() {
		return enabled;
	}
	
	public final ColumnSetting getColumnSetting() {
		return columnSetting;
	}
	
	public final void setConfiguration(String configurationName) {
		columnSetting = ColumnSetting.getByName(configurationName);
	}
	
	public final void setConfiguration(int configurationValue) {
		columnSetting = ColumnSetting.get(configurationValue);
	}
	
	public final void setColumnSetting(ColumnSetting configuration) {
		this.columnSetting = configuration;
	}
	
}