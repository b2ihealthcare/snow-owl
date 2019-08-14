/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.cis.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.google.common.collect.Sets;

/**
 * @since 4.5
 */
public abstract class AbstractIdentifierServiceTest {

	private static final String B2I_NAMESPACE = Concepts.B2I_NAMESPACE;

	protected abstract ISnomedIdentifierService getIdentifierService();

	@Test
	public void whenGeneratingIds_ThenItShouldReturnTheGeneratedIds() {
		final Set<String> componentIds = Sets.newHashSet();

		try {
			componentIds.addAll(getIdentifierService().generate(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2));
			assertTrue(String.format("Component IDs size is %d instead of 2.", componentIds.size()), componentIds.size() == 2);

			final Collection<SctId> sctIds = getIdentifierService().getSctIds(componentIds).values();
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be assigned", IdentifierStatus.ASSIGNED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown: %s.", e.getMessage()));
		} finally {
			if (!componentIds.isEmpty())
				getIdentifierService().release(componentIds);
		}
	}

	@Test
	public void whenReservingIds_ThenItShouldReturnTheReservedIds() {
		final Set<String> componentIds = Sets.newHashSet();

		try {
			componentIds.addAll(getIdentifierService().reserve(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2));
			assertTrue(String.format("Component IDs size is %d instead of 2.", componentIds.size()), componentIds.size() == 2);

			final Collection<SctId> sctIds = getIdentifierService().getSctIds(componentIds).values();
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be reserved", IdentifierStatus.RESERVED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown: %s.", e.getMessage()));
		} finally {
			if (!componentIds.isEmpty())
				getIdentifierService().release(componentIds);
		}
	}

	@Test
	public void whenRegisteringReservedIds_ThenTheyShouldBeRegistered() {
		final Set<String> componentIds = Sets.newHashSet();

		try {
			componentIds.addAll(getIdentifierService().reserve(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2));
			assertTrue(String.format("Component IDs size is %d instead of 2.", componentIds.size()), componentIds.size() == 2);
			getIdentifierService().register(componentIds);
			final Collection<SctId> sctIds = getIdentifierService().getSctIds(componentIds).values();
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be assigned", IdentifierStatus.ASSIGNED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown: %s.", e.getMessage()));
		} finally {
			if (!componentIds.isEmpty())
				getIdentifierService().release(componentIds);
		}
	}

	@Test
	public void whenReleasingReservedIds_ThenTheyShouldBeAvailable() {
		try {
			final Set<String> componentIds = getIdentifierService().reserve(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2);
			getIdentifierService().register(componentIds);
			getIdentifierService().release(componentIds);
			final Collection<SctId> sctIds = getIdentifierService().getSctIds(componentIds).values();
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be available", IdentifierStatus.AVAILABLE.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown: %s.", e.getMessage()));
		}
	}

	@Test
	public void whenPublishingAssignedIds_ThenTheyShouldBePublished() {
		try {
			final Set<String> componentIds = getIdentifierService().generate(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2);
			getIdentifierService().publish(componentIds);
			final Collection<SctId> sctIds = getIdentifierService().getSctIds(componentIds).values();
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be published", IdentifierStatus.PUBLISHED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown: %s.", e.getMessage()));
		}
	}

	@Test
	public void whenDeprecatingAssignedIds_ThenTheyShouldBeDeprecated() {
		try {
			final Set<String> componentIds = getIdentifierService().generate(B2I_NAMESPACE, ComponentCategory.CONCEPT, 2);
			getIdentifierService().deprecate(componentIds);
			final Collection<SctId> sctIds = getIdentifierService().getSctIds(componentIds).values();
			for (final SctId sctId : sctIds) {
				assertTrue("Status must be deprecated", IdentifierStatus.DEPRECATED.getSerializedName().equals(sctId.getStatus()));
			}
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown: %s.", e.getMessage()));
		}
	}

	@Test
	public void whenReleasingPublishedId_ThenExceptionShouldBeThrown() {
		try {
			final Set<String> componentIds = getIdentifierService().generate(B2I_NAMESPACE, ComponentCategory.CONCEPT, 1);
			getIdentifierService().publish(componentIds);
			getIdentifierService().release(componentIds);
			fail("No exception was thrown when releasing already published ID.");
		} catch (BadRequestException e) {
			// correct behavior
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		}
	}
}
