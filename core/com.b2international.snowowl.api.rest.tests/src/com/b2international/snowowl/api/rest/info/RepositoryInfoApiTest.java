package com.b2international.snowowl.api.rest.info;

import static com.b2international.snowowl.api.rest.RepositoryInfoApiAssert.*;

import java.util.UUID;

import org.junit.Test;

/**
 * @since 5.8
 */
public class RepositoryInfoApiTest {
	
	@Test
	public void getAllRepositoryInfo() {
		assertAllRepositoryInfo();
	}

	@Test
	public void getSingleRepositoryInfo() {
		assertRepositoryInfoForExistingRepository("snomedStore");
	}

	@Test
	public void getSingleNonExistentRepositoryInfo() {
		String nonExistentRepositoryId = UUID.randomUUID().toString();
		assertRepositoryInfoForInvalidRepository(nonExistentRepositoryId);
	}
	
	@Test 
	public void refreshRepositoryHealthState() {
		assertAllRepositoryHealthUpdate();
	} 

}
