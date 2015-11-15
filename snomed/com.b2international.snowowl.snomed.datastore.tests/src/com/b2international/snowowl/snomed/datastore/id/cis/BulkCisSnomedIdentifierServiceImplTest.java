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
package com.b2international.snowowl.snomed.datastore.id.cis;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;

/**
 * @since 4.5
 */
public class BulkCisSnomedIdentifierServiceImplTest {

	private static final String B2I_NAMESPACE = "1000129";

	private CisSnomedIdentfierServiceImpl service;

	@Before
	public void init() {
		SnomedCoreConfiguration conf = new SnomedCoreConfiguration();
		conf.setCisUrl("http://107.170.101.181");
		conf.setCisPort("3000");
		conf.setCisContextRoot("api");
		conf.setCisClientSoftwareKey("Snow Owl dev. tests");
		conf.setCisUserName("snowowl-dev-b2i");
		conf.setCisPassword("hAAYLYMX5gc98SDEz9cr");

		service = new CisSnomedIdentfierServiceImpl(conf, null);
	}

	@Test
	public void whenGeneratingIds_ThenItShouldReturnTheGeneratedIds() {
		Collection<String> componentIds = null;

		try {
			componentIds = service.bulkGenerate(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2);
			final Collection<SctId> sctIds = service.getSctIds(componentIds);
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be assigned",
						IdentifierStatus.ASSIGNED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		} finally {
			if (!componentIds.isEmpty())
				service.bulkRelease(componentIds);
		}
	}

	@Test
	public void whenReservingIds_ThenItShouldReturnTheReservedIds() {
		Collection<String> componentIds = null;

		try {
			componentIds = service.bulkReserve(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2);
			final Collection<SctId> sctIds = service.getSctIds(componentIds);
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be reserved",
						IdentifierStatus.RESERVED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		} finally {
			if (!componentIds.isEmpty())
				service.bulkRelease(componentIds);
		}
	}

	@Test
	public void whenRegisteringReservedIds_ThenTheyShouldBeRegistered() {
		Collection<String> componentIds = null;

		try {
			componentIds = service.bulkReserve(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2);
			service.bulkRegister(componentIds);
			final Collection<SctId> sctIds = service.getSctIds(componentIds);
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be assigned",
						IdentifierStatus.ASSIGNED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		} finally {
			if (!componentIds.isEmpty())
				service.bulkRelease(componentIds);
		}
	}

	@Test
	public void whenReleasingReservedIds_ThenTheyShouldBeAvailable() {
		try {
			final Collection<String> componentIds = service.bulkReserve(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2);
			service.bulkRegister(componentIds);
			service.bulkRelease(componentIds);
			final Collection<SctId> sctIds = service.getSctIds(componentIds);
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be available",
						IdentifierStatus.AVAILABLE.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		}
	}

	@Test
	public void whenPublishingAssignedIds_ThenTheyShouldBePublished() {
		try {
			final Collection<String> componentIds = service.bulkGenerate(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2);
			service.bulkPublish(componentIds);
			final Collection<SctId> sctIds = service.getSctIds(componentIds);
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be published",
						IdentifierStatus.PUBLISHED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		}
	}

	@Test
	public void whenDeprecatingAssignedIds_ThenTheyShouldBeDeprecated() {
		try {
			final Collection<String> componentIds = service.bulkGenerate(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2);
			service.bulkDeprecate(componentIds);
			final Collection<SctId> sctIds = service.getSctIds(componentIds);
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be deprecated",
						IdentifierStatus.DEPRECATED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		}
	}

}
