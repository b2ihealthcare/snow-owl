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
package com.b2international.snowowl.snomed.datastore.id;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.cis.IdentifierStatus;
import com.b2international.snowowl.snomed.datastore.id.cis.SctId;

/**
 * @since 4.5
 */
public abstract class AbstractIdentifierServiceTest {

	private static final String B2I_NAMESPACE = "1000129";

	protected abstract ISnomedIdentifierService getIdentifierService();

	@Test
	public void whenGeneratingId_ThenItShouldReturnTheGeneratedId() {
		String componentId = null;

		try {
			componentId = getIdentifierService().generate(B2I_NAMESPACE, ComponentCategory.CONCEPT);
			final SctId sctId = getIdentifierService().getSctId(componentId);
			assertTrue("Status must be assigned", IdentifierStatus.ASSIGNED.getSerializedName().equals(sctId.getStatus()));
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		} finally {
			if (null != componentId)
				getIdentifierService().release(componentId);
		}
	}

	@Test
	public void whenReservingId_ThenItShouldReturnTheReservedId() {
		String componentId = null;

		try {
			componentId = getIdentifierService().reserve(B2I_NAMESPACE, ComponentCategory.CONCEPT);
			final SctId sctId = getIdentifierService().getSctId(componentId);
			assertTrue("Status must be reserved", IdentifierStatus.RESERVED.getSerializedName().equals(sctId.getStatus()));
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		} finally {
			if (null != componentId)
				getIdentifierService().release(componentId);
		}
	}

	@Test
	public void whenRegisteringReservedId_ThenItShouldBeRegistered() {
		String componentId = null;

		try {
			componentId = getIdentifierService().reserve(B2I_NAMESPACE, ComponentCategory.CONCEPT);
			getIdentifierService().register(componentId);
			final SctId sctId = getIdentifierService().getSctId(componentId);
			assertTrue("Status must be assigned", IdentifierStatus.ASSIGNED.getSerializedName().equals(sctId.getStatus()));
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		} finally {
			if (null != componentId)
				getIdentifierService().release(componentId);
		}
	}

	@Test
	public void whenReleasingReservedId_ThenItShouldBeAvailable() {
		try {
			final String componentId = getIdentifierService().reserve(B2I_NAMESPACE, ComponentCategory.CONCEPT);
			getIdentifierService().register(componentId);
			getIdentifierService().release(componentId);
			final SctId sctId = getIdentifierService().getSctId(componentId);
			assertTrue("Status must be available", IdentifierStatus.AVAILABLE.getSerializedName().equals(sctId.getStatus()));
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		}
	}

	@Test
	public void whenPublishingAssignedId_ThenItShouldBePublished() {
		try {
			final String componentId = getIdentifierService().generate(B2I_NAMESPACE, ComponentCategory.CONCEPT);
			getIdentifierService().publish(componentId);
			final SctId sctId = getIdentifierService().getSctId(componentId);
			assertTrue("Status must be published", IdentifierStatus.PUBLISHED.getSerializedName().equals(sctId.getStatus()));
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		}
	}

	@Test
	public void whenDeprecatingAssignedId_ThenItShouldBeDeprecated() {
		try {
			final String componentId = getIdentifierService().generate(B2I_NAMESPACE, ComponentCategory.CONCEPT);
			getIdentifierService().deprecate(componentId);
			final SctId sctId = getIdentifierService().getSctId(componentId);
			assertTrue("Status must be deprecated", IdentifierStatus.DEPRECATED.getSerializedName().equals(sctId.getStatus()));
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		}
	}

	@Test
	public void whenReleasingPublishedId_ThenExceptionShouldBeThrown() {
		try {
			final String componentId = getIdentifierService().generate(B2I_NAMESPACE, ComponentCategory.CONCEPT);
			getIdentifierService().publish(componentId);
			getIdentifierService().release(componentId);
			fail("No exception was thrown when released published ID.");
		} catch (BadRequestException e) {
			// correct behavior
		} catch (Exception e) {
			fail(String.format("Unexpected exception was thrown. Exception class: %s.", e.getClass()));
		}
	}

}
