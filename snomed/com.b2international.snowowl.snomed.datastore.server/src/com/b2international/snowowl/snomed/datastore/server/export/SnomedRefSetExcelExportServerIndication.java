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
package com.b2international.snowowl.snomed.datastore.server.export;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.datastore.server.importer.ITerminologyExporter;
import com.b2international.snowowl.datastore.server.net4j.AbstractExportIndication;

/**
 * The server-side signal handler when exporting simple type reference set to Excel format.
 * Uses {@link Net4jProtocolConstants#SNOMED_EXPORT_REFSET_TO_EXCEL_SIGNAL}.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class SnomedRefSetExcelExportServerIndication extends AbstractExportIndication {

	private String refSetId;

	public SnomedRefSetExcelExportServerIndication(SignalProtocol<?> protocol) {
		super(protocol, Net4jProtocolConstants.SNOMED_EXPORT_REFSET_TO_EXCEL_SIGNAL);
	}
	
	@Override
	protected void postIndicating(ExtendedDataInputStream in) throws Exception {
		refSetId = in.readUTF();
	}

	@Override
	protected ITerminologyExporter getExporter() {
		return new SnomedSimpleTypeRefSetExcelExporter(getUserId(), getBranchPath(), refSetId);
	}

}