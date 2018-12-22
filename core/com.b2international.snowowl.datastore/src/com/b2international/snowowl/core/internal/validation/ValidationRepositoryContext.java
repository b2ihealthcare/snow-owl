/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal.validation;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.BulkUpdate;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteList;
import com.b2international.snowowl.core.validation.whitelist.WhiteListNotification;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 6.3
 */
public final class ValidationRepositoryContext extends DelegatingContext {

	// actual raw mapping changes
	private final Map<String, Object> newObjects = newHashMap();
	private final Multimap<Class<?>, String> objectsToDelete = HashMultimap.create();
	
	
	// higher level aggregated changes
	
	ValidationRepositoryContext(ServiceProvider delegate) {
		super(delegate);
	}

	/**
	 * Marks the given validation object to save it into the Validation Repository.
	 * 
	 * @param id
	 * @param doc
	 */
	public void save(String id, Object doc) {
		newObjects.put(id, doc);
	}
	
	/**
	 * Marks the given validation object for deletion.
	 *  
	 * @param type
	 * @param id
	 */
	public void delete(Class<?> type, String id) {
		objectsToDelete.put(type, id);
	}
	
	void commit() {
		if (!newObjects.isEmpty() || !objectsToDelete.isEmpty()) {
			final Set<String> ruleIdsAffectedByDeletion = Sets.newHashSet();
			service(ValidationRepository.class).write(writer -> {
				writer.putAll(newObjects);
				
				final Multimap<String, ComponentIdentifier> addToWhitelist = HashMultimap.create();
				newObjects.values()
					.stream()
					.filter(ValidationWhiteList.class::isInstance)
					.map(ValidationWhiteList.class::cast)
					.forEach(whitelist -> addToWhitelist.put(whitelist.getRuleId(), whitelist.getComponentIdentifier()));
				
				if (!addToWhitelist.isEmpty()) {
					ExpressionBuilder filter = Expressions.builder();
					for (String ruleId : addToWhitelist.keySet()) {
						filter.should(Expressions.builder()
							.filter(Expressions.exactMatch(ValidationIssue.Fields.RULE_ID, ruleId))
							.filter(Expressions.matchAny(ValidationIssue.Fields.AFFECTED_COMPONENT_ID, addToWhitelist.get(ruleId).stream().map(ComponentIdentifier::getComponentId).collect(Collectors.toSet())))
							.build());
					}
					writer.bulkUpdate(new BulkUpdate<>(ValidationIssue.class, filter.build(), ValidationIssue.Fields.ID, ValidationIssue.Scripts.WHITELIST, ImmutableMap.of("whitelisted", true)));
				}
				
				final Multimap<String, ComponentIdentifier> removeFromWhitelist = HashMultimap.create();
				ValidationRequests.whiteList().prepareSearch()
					.all()
					.filterByIds(ImmutableSet.copyOf(objectsToDelete.get(ValidationWhiteList.class)))
					.build()
					.execute(this)
					.forEach(whitelist -> removeFromWhitelist.put(whitelist.getRuleId(), whitelist.getComponentIdentifier()));
				
				if (!removeFromWhitelist.isEmpty()) {
					ExpressionBuilder filter = Expressions.builder();
					for (String ruleId : removeFromWhitelist.keySet()) {
						ruleIdsAffectedByDeletion.add(ruleId);
						filter.should(Expressions.builder()
							.filter(Expressions.exactMatch(ValidationIssue.Fields.RULE_ID, ruleId))
							.filter(Expressions.matchAny(ValidationIssue.Fields.AFFECTED_COMPONENT_ID, removeFromWhitelist.get(ruleId).stream().map(ComponentIdentifier::getComponentId).collect(Collectors.toSet())))
							.build());
					}
					writer.bulkUpdate(new BulkUpdate<>(ValidationIssue.class, filter.build(), ValidationIssue.Fields.ID, ValidationIssue.Scripts.WHITELIST, ImmutableMap.of("whitelisted", false)));
				}
				
				final Map<Class<?>, Set<String>> docsToDelete = newHashMap();
				objectsToDelete.asMap().forEach((type, ids) -> docsToDelete.put(type, ImmutableSet.copyOf(ids)));
				writer.removeAll(docsToDelete);
				
				writer.commit();
				return null;
			});
			
			if (!newObjects.isEmpty()) {
				final Set<String> addedWhiteLists = newHashSet();
				final Set<String> affectedRuleIds = newHashSet();
				newObjects.forEach((id, doc) -> {
					if (doc instanceof ValidationWhiteList) {
						ValidationWhiteList whitelistedDoc = (ValidationWhiteList) doc;
						affectedRuleIds.add(whitelistedDoc.getRuleId());
						addedWhiteLists.add(id);
					}
				});
				if (!addedWhiteLists.isEmpty()) {
					WhiteListNotification.added(addedWhiteLists, affectedRuleIds).publish(service(IEventBus.class));
				}
			}
			
			if (!objectsToDelete.isEmpty()) {
				final Set<String> removedWhiteLists = ImmutableSet.copyOf(objectsToDelete.get(ValidationWhiteList.class));
				if (!removedWhiteLists.isEmpty()) {
					WhiteListNotification.removed(removedWhiteLists, ruleIdsAffectedByDeletion).publish(service(IEventBus.class));
				}
			}
		}
	}

}