package com.b2international.snowowl.api.rest.info;

import static com.b2international.snowowl.api.rest.RepositoryApiAssert.assertAllRepositoryInfo;
import static com.b2international.snowowl.api.rest.RepositoryApiAssert.assertRepositoryInfoForExistingRepository;
import static com.b2international.snowowl.api.rest.RepositoryApiAssert.assertRepositoryInfoForInvalidRepository;

import java.util.UUID;

import org.junit.Test;

import com.b2international.snowowl.core.RepositoryInfo.Health;

/**
 * @since 5.8
 */
public class RepositoryApiTest {
	
	@Test
	public void getAllRepositoryInfo() {
		assertAllRepositoryInfo();
	}

	@Test
	public void getSingleRepositoryInfo() {
		assertRepositoryInfoForExistingRepository("snomedStore", Health.GREEN.name());
	}

	@Test
	public void getSingleNonExistentRepositoryInfo() {
		String nonExistentRepositoryId = UUID.randomUUID().toString();
		assertRepositoryInfoForInvalidRepository(nonExistentRepositoryId);
	}
	
}
