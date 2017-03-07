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

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.datastore.net4j.AbstractExportRequest;
import com.b2international.snowowl.snomed.SnomedPackage;

/**
 * This class sends simple type reference set into Excel export request to the server-side.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class SnomedRefSetExcelExportClientRequest extends AbstractExportRequest {
	
	private final SnomedRefSetExcelExportModel model;

	public SnomedRefSetExcelExportClientRequest(final SignalProtocol<?> protocol, final IBranchPath branchPath, final SnomedRefSetExcelExportModel model) {
		super(protocol, Net4jProtocolConstants.SNOMED_EXPORT_REFSET_TO_EXCEL_SIGNAL, branchPath, model.getExportPath());
		this.model = model;
	}
	
	@Override
	protected void postRequesting(ExtendedDataOutputStream out) throws Exception {
		out.writeUTF(model.getRefSetId());
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}

}