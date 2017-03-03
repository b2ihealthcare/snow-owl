package com.b2international.snowowl.snomed.api.impl.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserComponentWithId;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConceptUpdate;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentUpdateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentCreateRequest;
import com.google.common.collect.Sets;

public class InputFactory {

	private List<ComponentInputCreator> creators;

	public InputFactory() {
		creators = new ArrayList<>();
		creators.add(new ConceptInputCreator());
		creators.add(new DescriptionInputCreator());
		creators.add(new RelationshipInputCreator());
	}

	public <I extends SnomedComponentCreateRequest> I createComponentInput(String branchPath, ISnomedBrowserComponentWithId component, Class<I> inputType) {
		return getInputDelegate(inputType).createInput(branchPath, component, this);
	}

	public <I extends SnomedComponentCreateRequest> List<I> createComponentInputs(String branchPath,
			List<? extends ISnomedBrowserComponentWithId> newVersionComponents, Class<I> inputType) {
		List<I> inputs = new ArrayList<>();
		for (ISnomedBrowserComponentWithId component : newVersionComponents) {
			if (component.getId() == null) {
				inputs.add(createComponentInput(branchPath, component, inputType));
			}
		}
		return inputs;
	}

	public <U extends SnomedComponentUpdateRequest> U createComponentUpdate(ISnomedBrowserConcept existingVersion, ISnomedBrowserConceptUpdate newVersion, Class<U> updateType) {
		return updateType.cast(getUpdateDelegate(updateType).createUpdate(existingVersion, newVersion));
	}

	public <U extends SnomedComponentUpdateRequest> Map<String, U> createComponentUpdates(
			List<? extends ISnomedBrowserComponentWithId> existingVersions,
			List<? extends ISnomedBrowserComponentWithId> newVersions, Class<U> updateType) {

		Map<String, U> updateMap = new HashMap<>();
		for (ISnomedBrowserComponentWithId existingVersion : existingVersions) {
			for (ISnomedBrowserComponentWithId newVersion : newVersions) {
				final String existingVersionId = existingVersion.getId();
				if (existingVersionId.equals(newVersion.getId())) {
					final U update = (U) getUpdateDelegate(updateType).createUpdate(existingVersion, newVersion);
					if (update != null) {
						updateMap.put(existingVersionId, update);
					}
				}
			}
		}
		return updateMap;
	}

	public Set<String> getComponentDeletions(List<? extends ISnomedBrowserComponentWithId> existingVersion, List<? extends ISnomedBrowserComponentWithId> newVersion) {
		return Sets.difference(toIdSet(existingVersion), toIdSet(newVersion));
	}

	private Set<String> toIdSet(List<? extends ISnomedBrowserComponentWithId> components) {
		Set<String> ids = new HashSet<>();
		for (ISnomedBrowserComponentWithId component : components) {
			ids.add(component.getId());
		}
		return ids;
	}

	private <I extends SnomedComponentCreateRequest> ComponentInputCreator<I, SnomedComponentUpdateRequest, ISnomedBrowserComponentWithId> getInputDelegate(Class<I> inputType) {
		for (ComponentInputCreator creator : creators) {
			if (creator.canCreateInput(inputType)) {
				return creator;
			}
		}
		throw new RuntimeException("No ComponentInputCreator found for input type " + inputType);
	}

	private <U extends SnomedComponentUpdateRequest> ComponentInputCreator getUpdateDelegate(Class<U> updateType) {
		for (ComponentInputCreator creator : creators) {
			if (creator.canCreateUpdate(updateType)) {
				return creator;
			}
		}
		throw new RuntimeException("No ComponentInputCreator found for update type " + updateType);
	}
}
