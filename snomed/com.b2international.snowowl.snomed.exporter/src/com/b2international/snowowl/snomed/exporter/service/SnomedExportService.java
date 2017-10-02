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
package com.b2international.snowowl.snomed.exporter.service;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.datastore.SnomedConfiguration;
import com.b2international.snowowl.snomed.exporter.model.SnomedExportResult;
import com.b2international.snowowl.snomed.exporter.model.SnomedRf2ExportModel;

/**
 * @since 2.7
 */
public class SnomedExportService {

	/**
	 * Executes an RF2 publication export process on the server side using the given {@link SnomedRf2ExportModel} as
	 * configuration.
	 * 
	 * @param model
	 * @param monitor
	 * @return 
	 * @throws Exception 
	 */
	public File export(SnomedRf2ExportModel model, IProgressMonitor monitor) throws Exception {
		monitor.beginTask("Exporting SNOMED CT into RF2 format...", IProgressMonitor.UNKNOWN);

		if (StringUtils.isEmpty(model.getCountryAndNamespaceId())) {
			model.setCountryAndNamespaceId(getNamespace());			
		}
		
		final SnomedExportClientRequest snomedExportClientRequest = new SnomedExportClientRequest(SnomedClientProtocol.getInstance(), model);
		final StringBuilder sb = new StringBuilder("Performing SNOMED CT publication into ");
		
		if (model.isExportToRf1()) {
			sb.append("RF1 and ");
		}
		
		sb.append("RF2 release format...");
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor, sb.toString(), 1000).newChild(1000, SubMonitor.SUPPRESS_ALL_LABELS);
		subMonitor.worked(5);
		
		final File resultFile = snomedExportClientRequest.send(new EclipseMonitor(subMonitor));
		final SnomedExportResult result = snomedExportClientRequest.getExportResult();
		model.getExportResult().setResultAndMessage(result.getResult(), result.getMessage());

		return resultFile;
	}

	/**
	 * Returns with the default namespace ID.
	 * 
	 * @return the namespace ID.
	 */
	protected String getNamespace() {
		return ApplicationContext.getInstance().getService(SnomedConfiguration.class).getNamespaces()
				.getDefaultChildKey();
	}

}