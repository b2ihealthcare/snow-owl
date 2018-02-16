package com.b2international.snowowl.snomed.api.japi.branches;

import static com.b2international.snowowl.snomed.api.rest.SnomedImportRestRequests.createImport;
import static com.b2international.snowowl.snomed.api.rest.SnomedImportRestRequests.getImport;
import static com.b2international.snowowl.snomed.api.rest.SnomedImportRestRequests.uploadImportFile;
import static com.b2international.snowowl.snomed.api.rest.SnomedImportRestRequests.waitForImportJob;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.request.Branching;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.collect.ImmutableMap;

public class BranchTimestampUpdateTest {
	
	final static String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;
	
	private IEventBus bus;
	
	@Before
	public void setup() {
		bus = ApplicationContext.getInstance().getService(IEventBus.class);
	}
	
	private void importArchive(final String fileName, String branchPath, boolean createVersion, String releaseType) {
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", releaseType)
				.put("branchPath", branchPath)
				.put("createVersions", createVersion)
				.build();
		importArchive(fileName, importConfiguration);
	}

	private void importArchive(final String fileName, Map<?, ?> importConfiguration) {
		final String importId = lastPathSegment(createImport(importConfiguration).statusCode(201)
				.extract().header("Location"));

		getImport(importId).statusCode(200).body("status", equalTo(ImportStatus.WAITING_FOR_FILE.name()));
		uploadImportFile(importId, getClass(), fileName).statusCode(204);
		waitForImportJob(importId).statusCode(200).body("status", equalTo(ImportStatus.COMPLETED.name()));
	}
	
	@Test
	public void testHeadTimestampUpdateAfterImporting() {
		final Branching branches = RepositoryRequests.branching();
		
		final String branchA = "a";
		
		final Branch branchBeforeImport = branches.prepareCreate()
				.setParent(Branch.MAIN_PATH)
				.setName(branchA)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync(1, TimeUnit.MINUTES);
		
		assertEquals("After branch creation timestamps should still be equal", branchBeforeImport.baseTimestamp(), branchBeforeImport.headTimestamp());
		
		importArchive("SnomedCT_Release_INT_20150131_new_concept.zip", branchBeforeImport.path(), false, Rf2ReleaseType.DELTA.name());
		
		final Branch branchAfterImport = branches.prepareGet(branchBeforeImport.path())
			.build(REPOSITORY_ID)
			.execute(bus)
			.getSync(1, TimeUnit.MINUTES);
		
		assertThat(branchBeforeImport.baseTimestamp(), not(equalTo(branchAfterImport.headTimestamp())));
	}
	
}
