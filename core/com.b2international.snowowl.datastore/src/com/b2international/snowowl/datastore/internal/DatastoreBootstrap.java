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
package com.b2international.snowowl.datastore.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.BootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.internal.branch.BranchEventHandler;
import com.b2international.snowowl.datastore.internal.branch.CDOBranchManagerImpl;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 4.1
 */
public class DatastoreBootstrap implements BootstrapFragment {

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
	}

	@Override
	public void run(SnowOwlConfiguration configuration, Environment env, IProgressMonitor monitor) throws Exception {
		if (env.isServer()) {
			ICDOConnection cdoConnection = env.service(ICDOConnectionManager.class).getByUuid("snomedStore");
			CDOBranchManager cdoBranchManager = cdoConnection.getMainBranch().getBranchManager();
			BranchManager branchManager = new CDOBranchManagerImpl((InternalCDOBranchManager) cdoBranchManager, cdoConnection);
		
			env.service(IEventBus.class).registerHandler("/branches", new BranchEventHandler(branchManager));
		}
	}
}
