/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Represents an exported column in DSV files created from reference sets of the SNOMED&nbsp;CT terminology. 
 *
 */
public abstract class AbstractSnomedDsvExportItem implements Serializable {

	private final SnomedDsvExportItemType type;

	protected AbstractSnomedDsvExportItem(final SnomedDsvExportItemType type) {
		this.type = type;
	}
	
	public SnomedDsvExportItemType getType() {
		return type;
	}
	
	/**
	 * Writes the active fields of the item to the given output stream. Used at
	 * {@link SnomedRefSetDSVExportClientRequest} requesting method.
	 * 
	 * @param outputStream
	 * @throws IOException
	 */
	public void writeToOutputStream(final DataOutputStream outputStream) throws IOException {
		outputStream.writeInt(type.ordinal());
	}
	
	public abstract String getDisplayName();
	
	/**
	 * Creates a new {@link SnomedDSVExportItem} and reads its type and fields
	 * from the given input stream. Used at SnomedDSVExportServerIndication
	 * indicating method.
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static AbstractSnomedDsvExportItem createFromInputStream(final DataInputStream inputStream) throws IOException {
		
		final int ordinal = inputStream.readInt();
		final SnomedDsvExportItemType type = SnomedDsvExportItemType.values()[ordinal];
		
		switch (type) {
			case DESCRIPTION:
			case RELATIONSHIP:
				final String componentId = inputStream.readUTF();
				final String componentLabel = inputStream.readUTF();
				return new ComponentIdSnomedDsvExportItem(type, componentId, componentLabel);

			case DATAYPE:
				final String datatypeLabel = inputStream.readUTF();
				final boolean booleanDatatype = inputStream.readBoolean();
				return new DatatypeSnomedDsvExportItem(type, datatypeLabel, booleanDatatype);
						
			default:
				return new SimpleSnomedDsvExportItem(type);
		}
	}
}