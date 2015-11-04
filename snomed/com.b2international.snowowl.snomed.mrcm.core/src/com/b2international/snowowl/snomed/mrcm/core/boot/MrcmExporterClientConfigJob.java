/*******************************************************************************
 * Copyright (c) 2015 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.mrcm.core.boot;

import com.b2international.snowowl.datastore.serviceconfig.AbstractClientServiceConfigJob;
import com.b2international.snowowl.snomed.mrcm.core.io.MrcmExporter;

/**
 * @since 4.4
 */
public class MrcmExporterClientConfigJob extends AbstractClientServiceConfigJob<MrcmExporter> {

	public MrcmExporterClientConfigJob() {
		super("Configuring MRCM client export service...", "mrcm");
	}

	@Override
	protected Class<MrcmExporter> getServiceClass() {
		return MrcmExporter.class;
	}

}
