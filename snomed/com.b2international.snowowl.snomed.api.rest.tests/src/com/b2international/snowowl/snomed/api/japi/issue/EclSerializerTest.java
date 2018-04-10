/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.api.japi.issue;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Throwables;

/**
 * 
 * @since 6.4
 */
public class EclSerializerTest extends AbstractSnomedApiTest {
	
	@Test
	public void verify() throws Exception {
		for (int rounds = 0; rounds < 100; rounds++) {
		
			AsyncRequest<SnomedConcepts> eclRequest = SnomedRequests.prepareSearchConcept()
					.all()
					.filterByEcl("<<" + Concepts.ROOT_CONCEPT + ":" + Concepts.MORPHOLOGY + " = " + Concepts.ROOT_CONCEPT)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, Branch.MAIN_PATH);
			
			List<Promise<SnomedConcepts>> promises = newArrayList();
			
			for (int i = 0; i < 4; i++) {
				promises.add(eclRequest.execute(getBus()));
			}
			
			final String error = Promise.all(promises)
					.then(concepts -> {
						return (String) null;
					})
					.fail(throwable -> {
						return (throwable.getMessage() != null) 
								? throwable.getMessage() 
								: Throwables.getRootCause(throwable).getClass().getSimpleName();
					})
					.getSync();
			
			assertNull(error, error);
		}
	}
}
