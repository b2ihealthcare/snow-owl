/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.CommitDetail;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 7.12
 */
public class RepositoryCommitNotificationSender {

	public void publish(RepositoryContext context, Commit commit) {
		// send a commit notification
		TerminologyComponents components = context.service(TerminologyComponents.class);
		new RepositoryCommitNotification(context.info().id(),
				commit.getId(),						
				commit.getGroupId(),
				commit.getBranch(),
				commit.getTimestamp(),
				commit.getAuthor(),
				commit.getComment(),
				getNewObjects(commit, components),
				getChangedObjects(commit, components),
				getRemovedObjects(commit, components),
				commit.getMergeSource())
		.publish(context.service(IEventBus.class));
	}
	
	private Collection<ComponentIdentifier> getNewObjects(Commit commit, TerminologyComponents components) {
		return commit.getDetails().stream()
			.filter(CommitDetail::isAdd)
			.flatMap(detail -> {
				final short terminologyComponentId = getTerminologyComponentId(components, detail.getComponentType());
				return detail.getComponents().stream().flatMap(Set::stream).map(id -> ComponentIdentifier.of(terminologyComponentId, id));
			})
			.collect(Collectors.toSet());
	}

	/* From all commit detail object, extract both component level changes and container related add/change/remove and mark them as CHANGED components */
	private Collection<ComponentIdentifier> getChangedObjects(Commit commit, TerminologyComponents components) {
		return commit.getDetails().stream()
			.flatMap(detail -> {
				final short terminologyComponentId = getTerminologyComponentId(components, detail.getObjectType());
				return detail.getObjects().stream().map(id -> ComponentIdentifier.of(terminologyComponentId, id)); 
			})
			.collect(Collectors.toSet());
	}
	
	private Collection<ComponentIdentifier> getRemovedObjects(Commit commit, TerminologyComponents components) {
		return commit.getDetails().stream()
			.filter(CommitDetail::isRemove)
			.flatMap(detail -> {
				final short terminologyComponentId = getTerminologyComponentId(components, detail.getComponentType());
				return detail.getComponents().stream().flatMap(Set::stream).map(id -> ComponentIdentifier.of(terminologyComponentId, id));
			})
			.collect(Collectors.toSet());
	}
	
	private short getTerminologyComponentId(TerminologyComponents components, String componentType) {
		try {
			return components.getTerminologyComponentId(DocumentMapping.getClass(componentType));
		} catch (IllegalArgumentException e) {
			// return unspecified terminology component for each unknown components committed to the repo
			return TerminologyRegistry.UNSPECIFIED_NUMBER_SHORT;
		}
	}

	
}
