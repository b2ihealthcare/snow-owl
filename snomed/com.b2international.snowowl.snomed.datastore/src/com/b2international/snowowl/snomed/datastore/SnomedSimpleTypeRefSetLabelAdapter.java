/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore;

import javax.annotation.Nullable;

import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * Adapter for storing the PT of a SNOMED&nbsp;CT simple type reference set as a string.
 *
 */
public class SnomedSimpleTypeRefSetLabelAdapter extends AdapterImpl {

	public static final String LABEL_PROPERTY = "label";
	
	private String label;
	
	/**Returns with the label. Could be {@code null} if not set yet.*/
	@Nullable public String getLabel() {
		return label;
	}
	
	/**Sets the label value based on the argument.*/
	public void setLabel(final String label) {
		this.label = label;
	}
	
}