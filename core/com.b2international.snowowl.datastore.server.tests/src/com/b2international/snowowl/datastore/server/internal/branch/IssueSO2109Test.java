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
package com.b2international.snowowl.datastore.server.internal.branch;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.Doc;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchProvider;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionIndexWrite;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.RevisionWriter;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.internal.branch.BranchDocument;
import com.b2international.snowowl.datastore.internal.branch.InternalBranch;
import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.datastore.server.internal.InternalRepository;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 5.4
 */
public class IssueSO2109Test {

	private static final long STORAGE_KEY = 1L;
	private static final String DATA_VALUE = "1";
	private static final String INTERRUPT_MESSAGE = "Interrupting rebase";

	private CDOTimeProvider clock;
	private MockInternalCDOBranchManager cdoBranchManager;

	private CDOBranchManagerImpl manager;

	private InternalRepository repository;
	private Index store;
	private DefaultRevisionIndex revisionIndex;

	@Before
	public void givenCDOBranchManager() {
		clock = new AtomicLongTimestampAuthority();
		cdoBranchManager = new MockInternalCDOBranchManager(clock);
		cdoBranchManager.initMainBranch(false, clock.getTimeStamp());

		repository = mock(InternalRepository.class, RETURNS_MOCKS);
		final ICDOConflictProcessor conflictProcessor = mock(ICDOConflictProcessor.class, RETURNS_DEFAULTS);
		final InternalCDOBranch mainBranch = cdoBranchManager.getMainBranch();

		when(repository.getCdoBranchManager()).thenReturn(cdoBranchManager);
		when(repository.getCdoMainBranch()).thenReturn(mainBranch);
		when(repository.getConflictProcessor()).thenReturn(conflictProcessor);
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		store = Indexes.createIndex(UUID.randomUUID().toString(), mapper, new Mappings(BranchDocument.class, Data.class));
		store.admin().create();

		revisionIndex = new DefaultRevisionIndex(store, new RevisionBranchProvider() {

			@Override
			public RevisionBranch getParentBranch(final String branchPath) {
				return null;
			}

			@Override
			public RevisionBranch getBranch(final String branchPath) {
				final InternalCDOBasedBranch branch = (InternalCDOBasedBranch) manager.getBranch(branchPath);
				final Set<Integer> segments = newHashSet();
				segments.addAll(branch.segments());
				segments.addAll(branch.parentSegments());
				return new RevisionBranch(branchPath, branch.segmentId(), segments);
			}

		});

		when(repository.getIndex()).thenReturn(store);

		manager = new CDOBranchManagerImpl(repository, mapper);
	}

	@After
	public void after() {
		store.admin().delete();
	}

	@Test
	public void branchContentMustNotBeNullAfterFailedRebase() throws Exception {

		final InternalCDOBasedBranch child = (InternalCDOBasedBranch) manager.getMainBranch().createChild("a");

		final long timestamp = clock.getTimeStamp();

		revisionIndex.write(child.path(), timestamp, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(final RevisionWriter write) throws IOException {
				write.put(STORAGE_KEY, new Data(DATA_VALUE));
				write.commit();
				return null;
			}
		});

		final InternalCDOBasedBranch childWithChanges = (InternalCDOBasedBranch) manager.handleCommit(child, timestamp);

		final Data actual = getData(childWithChanges.path(), STORAGE_KEY);

		assertEquals(new Data(DATA_VALUE, STORAGE_KEY, childWithChanges.path(), timestamp, childWithChanges.segmentId(),
				Collections.<Integer> emptyList()), actual);

		try {
			manager.rebase(childWithChanges, (InternalBranch) childWithChanges.parent(), "commit message", new Runnable() {
				@Override
				public void run() {
					throw new RuntimeException(INTERRUPT_MESSAGE);
				}
			});
		} catch (final RuntimeException e) {
			if (!e.getMessage().equals(INTERRUPT_MESSAGE)) {
				throw e;
			}
		}

		final Data dataAfterRebase = getData(child.path(), STORAGE_KEY);

		assertNotNull("Data most not be null after failed rebase attempt", dataAfterRebase);

		final InternalCDOBasedBranch currentChild = (InternalCDOBasedBranch) manager.getBranch(child.path());

		final CDOBranch currentCDOChild = manager.getCDOBranch(currentChild);

		assertEquals(currentCDOChild.getPathName(), currentChild.path());
		
		for (Branch parentChild : child.parent().children()) {
			assertFalse(parentChild.name().startsWith(Branch.TEMP_PREFIX));
		}

	}

	private Data getData(final String path, final long storageKey) {
		return revisionIndex.read(path, new RevisionIndexRead<Data>() {
			@Override
			public Data execute(final RevisionSearcher index) throws IOException {
				return index.get(Data.class, storageKey);
			}
		});
	}

	@Doc
	private static class Data extends Revision {

		@JsonProperty
		private final String field;

		public Data(final String field) {
			this.field = field;
		}

		@JsonCreator
		public Data(@JsonProperty("field") final String field, @JsonProperty("storageKey") final long storageKey,
				@JsonProperty("branchPath") final String branchPath, @JsonProperty("commitTimestamp") final long commitTimestamp,
				@JsonProperty("segmentId") final int segmentId, @JsonProperty("replacedIns") final Collection<Integer> replacedIns) {
			this.field = field;
			setStorageKey(storageKey);
			setBranchPath(branchPath);
			setCommitTimestamp(commitTimestamp);
			setSegmentId(segmentId);
			setReplacedIns(replacedIns);
		}

		@Override
		public int hashCode() {
			return Objects.hash(field);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Data other = (Data) obj;
			return Objects.equals(other.field, field);
		}

	}
}
