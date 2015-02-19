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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public enum ColumnSetting {

	DISABLED(0, "Disabled", "Disabled"),
	
	ICON(1, "Icon", "Icon"),
	
	TEXT(2, "Text", "Text"),
	
	BOTH(3, "Both", "Both");
	
	public static final int DISABLED_VALUE = 0;
	public static final int ICON_VALUE = 1;
	public static final int TEXT_VALUE = 2;
	public static final int BOTH_VALUE = 3;
	
	private static final ColumnSetting[] VALUES_ARRAY = new ColumnSetting[] {
		DISABLED,
		ICON,
		TEXT,
		BOTH,
	};
	
	public static final List<ColumnSetting> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	public static ColumnSetting getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ColumnSetting result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}
	
	public static ColumnSetting get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ColumnSetting result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}
	
	public static ColumnSetting get(int value) {
		switch (value) {
			case DISABLED_VALUE: return DISABLED;
			case ICON_VALUE: return ICON;
			case TEXT_VALUE: return TEXT;
			case BOTH_VALUE: return BOTH;
		}
		return null;
	}
	
	private final int value;

	private final String name;

	private final String literal;

	private ColumnSetting(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}
	
	public int getValue() {
	  return value;
	}

	public String getName() {
	  return name;
	}

	public String getLiteral() {
	  return literal;
	}
	

	public boolean showIcon() {
		return ColumnSetting.BOTH.equals(this) || ColumnSetting.ICON.equals(this);
	}
	
	public boolean showText() {
		return ColumnSetting.BOTH.equals(this) || ColumnSetting.TEXT.equals(this);
	}
	
	public boolean isColumnDisabled() {
		return ColumnSetting.DISABLED.equals(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return literal;
	}
	
}