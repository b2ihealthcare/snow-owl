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
package com.b2international.snowowl.snomed.datastore.internal.rf2;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 */
public class DatatypeSnomedDsvExportItem extends AbstractSnomedDsvExportItem {

	private final String datatypeLabel;
	private final boolean booleanDatatype;

	/**
	 * 
	 * @param type
	 * @param datatypeLabel
	 * @param booleanDatatype
	 */
	public DatatypeSnomedDsvExportItem(final SnomedDsvExportItemType type, final String datatypeLabel, final boolean booleanDatatype) {
		super(type);
		this.datatypeLabel = datatypeLabel;
		this.booleanDatatype = booleanDatatype;
	}
	
	public String getDatatypeLabel() {
		return datatypeLabel;
	}
	
	public boolean isBooleanDatatype() {
		return booleanDatatype;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.exporter.model.AbstractSnomedDsvExportItem#writeToOutputStream(java.io.DataOutputStream)
	 */
	@Override
	public void writeToOutputStream(final DataOutputStream outputStream) throws IOException {
		super.writeToOutputStream(outputStream);
		outputStream.writeUTF(datatypeLabel);
		outputStream.writeBoolean(booleanDatatype);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.exporter.model.AbstractSnomedDsvExportItem#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getDatatypeLabel();
	}
}