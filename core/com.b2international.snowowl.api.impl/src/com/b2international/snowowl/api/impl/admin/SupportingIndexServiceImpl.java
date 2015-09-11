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
package com.b2international.snowowl.api.impl.admin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import com.b2international.snowowl.api.admin.ISupportingIndexService;
import com.b2international.snowowl.api.admin.exception.SnapshotCreationException;
import com.b2international.snowowl.api.admin.exception.SnapshotListingException;
import com.b2international.snowowl.api.admin.exception.SnapshotReleaseException;
import com.b2international.snowowl.api.admin.exception.SupportingIndexNotFoundException;
import com.b2international.snowowl.api.admin.exception.SupportingIndexSnapshotNotFoundException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.SingleDirectoryIndex;
import com.b2international.snowowl.datastore.server.index.SingleDirectoryIndexManager;
import com.google.common.collect.ImmutableList;

/**
 */
public class SupportingIndexServiceImpl implements ISupportingIndexService {

	private static SingleDirectoryIndexManager getSingleDirectoryIndexManager() {
		return ApplicationContext.getServiceForClass(SingleDirectoryIndexManager.class);
	}

	@Override
	public List<String> getSupportingIndexIds() {
		final List<String> serviceIds = getSingleDirectoryIndexManager().getServiceIds();
		return ImmutableList.copyOf(serviceIds);
	}

	@Override
	public List<String> getSupportingIndexSnapshotIds(final String indexId) {
		checkValidIndexId(indexId);

		final SingleDirectoryIndex service = getSingleDirectoryIndexManager().getService(indexId);
		final List<String> snapshotIds = service.getSnapshotIds();
		return ImmutableList.copyOf(snapshotIds);
	}

	@Override
	public String createSupportingIndexSnapshot(final String indexId) {
		checkValidIndexId(indexId);

		try {
			final SingleDirectoryIndex service = getSingleDirectoryIndexManager().getService(indexId);
			return service.snapshot();
		} catch (final IOException e) {
			throw new SnapshotCreationException(e.getMessage());
		}
	}

	@Override
	public List<String> getSupportingIndexFiles(final String indexId, final String snapshotId) {
		checkValidIndexAndSnapshotId(indexId, snapshotId);

		try {
			final SingleDirectoryIndex service = getSingleDirectoryIndexManager().getService(indexId);
			final List<String> listFiles = service.listFiles(snapshotId);
			return ImmutableList.copyOf(listFiles);
		} catch (final IOException e) {
			throw new SnapshotListingException(e.getMessage());
		}
	}

	@Override
	public void releaseSupportingIndexSnapshot(final String indexId, final String snapshotId) {
		checkValidIndexAndSnapshotId(indexId, snapshotId);

		try {
			final SingleDirectoryIndex service = getSingleDirectoryIndexManager().getService(indexId);
			service.releaseSnapshot(snapshotId);
		} catch (final IOException e) {
			throw new SnapshotReleaseException(e.getMessage());
		}
	}

	private void checkValidIndexId(final String indexId) {
		checkNotNull(indexId, "Index identifier may not be null.");

		if (!getSupportingIndexIds().contains(indexId)) {
			throw new SupportingIndexNotFoundException(indexId);
		}
	}

	private void checkValidIndexAndSnapshotId(final String indexId, final String snapshotId) {
		checkNotNull(snapshotId, "Snapshot identifier may not be null.");

		// XXX: will invoke checkValidIndexId as well
		if (!getSupportingIndexSnapshotIds(indexId).contains(snapshotId)) {
			throw new SupportingIndexSnapshotNotFoundException(snapshotId);
		}
	}
}