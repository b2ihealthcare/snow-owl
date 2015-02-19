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
package com.b2international.snowowl.snomed.mrcm.core.widget;

import com.b2international.snowowl.datastore.serviceconfig.ClientServiceConfigJob;
import com.b2international.snowowl.snomed.mrcm.core.MrcmCoreActivator;

/**
 * Configuration job to initialize and register the widget bean provider on the client side.
 * <p>
 * <b>Note:</b> this class belongs to the {@link MrcmCoreActivator#PLUGIN_ID SNOMED&nbsp;CT data store plug-in identifier} job
 * family.
 * 
 */
public class ClientWidgetBeanProviderConfigJob extends ClientServiceConfigJob<IWidgetBeanProvider, IClientWidgetBeanProvider> {

	private static final String JOB_NAME = "Client widget bean provider configuration...";
	
	public ClientWidgetBeanProviderConfigJob() {
		super(JOB_NAME, MrcmCoreActivator.PLUGIN_ID);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.ClientServiceConfigJob#getBranchAwareClass()
	 */
	@Override
	protected Class<IWidgetBeanProvider> getServiceClass() {
		return IWidgetBeanProvider.class;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.ClientServiceConfigJob#getTrackingClass()
	 */
	@Override
	protected Class<IClientWidgetBeanProvider> getTrackingClass() {
		return IClientWidgetBeanProvider.class;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.ClientServiceConfigJob#createTrackingService(java.lang.Object)
	 */
	@Override
	protected IClientWidgetBeanProvider createTrackingService(final IWidgetBeanProvider branchAwareService) {
		return new ClientWidgetBeanProvider(branchAwareService);
	}
}