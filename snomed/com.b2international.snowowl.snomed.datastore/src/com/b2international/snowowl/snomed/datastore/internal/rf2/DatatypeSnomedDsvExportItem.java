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
package com.b2international.snowowl.snomed.datastore.internal.rf2;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @since 1.0
 */
public final class DatatypeSnomedDsvExportItem extends ComponentIdSnomedDsvExportItem {

	private final boolean booleanDatatype;

	public DatatypeSnomedDsvExportItem(final SnomedDsvExportItemType type, final String componentId, final String componentLabel, final boolean booleanDatatype) {
		super(type, componentId, componentLabel);
		this.booleanDatatype = booleanDatatype;
	}
	
	public boolean isBooleanDatatype() {
		return booleanDatatype;
	}
	
	@Override
	public void writeToOutputStream(final DataOutputStream outputStream) throws IOException {
		super.writeToOutputStream(outputStream);
		outputStream.writeBoolean(booleanDatatype);
	}
}
