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
package com.b2international.snowowl.datastore.server.personalization;

import java.io.File;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.personalization.IBookmarksManager;
import com.b2international.snowowl.datastore.server.DatastoreServerActivator;
import com.b2international.snowowl.datastore.server.index.SingleDirectoryIndexManager;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;

/**
 */
public class BookmarksConfigJob extends AbstractServerServiceConfigJob<IBookmarksManager> {

	private static final String INDEX_DIRECTORY = "bookmarks";
	
	private static final String JOB_NAME = "Bookmarks server service configuration...";

	public BookmarksConfigJob() {
		super(JOB_NAME, DatastoreServerActivator.PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob#getServiceClass()
	 */
	@Override
	protected Class<IBookmarksManager> getServiceClass() {
		return IBookmarksManager.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob#createServiceImplementation()
	 */
	@Override
	protected BookmarksManager createServiceImplementation() throws SnowowlServiceException {
		final File dir = new File(new File(getEnvironment().getDataDirectory(), "indexes"), INDEX_DIRECTORY);
		final BookmarksManager manager = new BookmarksManager(dir);
		ApplicationContext.getInstance().getServiceChecked(SingleDirectoryIndexManager.class).registerIndex(manager);
		return manager;
	}
	
}