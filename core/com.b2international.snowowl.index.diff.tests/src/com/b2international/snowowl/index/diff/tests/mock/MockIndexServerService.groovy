/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.index.diff.tests.mock;

import com.b2international.snowowl.datastore.server.index.ICommitTimeProvider
import com.b2international.snowowl.datastore.server.index.IDirectoryManager
import com.b2international.snowowl.datastore.server.index.IIndexAccessUpdater
import com.b2international.snowowl.datastore.server.index.IndexServerService
import com.b2international.snowowl.datastore.server.index.RAMDirectoryManager

/**
 *
 */
public class MockIndexServerService extends IndexServerService<MockIndexEntry> {

	def singletonManager = {
		new RAMDirectoryManager()
	}.memoize()

	@Override
	public String getRepositoryUuid() {
		MockIndexServerService.class.getName()
	}

	@Override
	protected IDirectoryManager getDirectoryManager() {
		singletonManager.call()
	}
	
	@Override
	protected ICommitTimeProvider getCommitTimeProvider() {
		ICommitTimeProvider.DEFAULT
	}
	
	@Override
	protected IIndexAccessUpdater getIndexAccessUpdater() {
		IIndexAccessUpdater.NOOP
	}
	
}
