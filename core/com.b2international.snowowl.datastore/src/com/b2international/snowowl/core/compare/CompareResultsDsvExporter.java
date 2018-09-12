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
package com.b2international.snowowl.core.compare;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.ChangeKind;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.datastore.request.compare.CompareResult;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;

/**
 * @since 6.5
 */
public final class CompareResultsDsvExporter {
	
	private static final int PARTITION_SIZE = 10_000;
	
	private final String repositoryUuid;
	private final String baseBranch;
	private final String compareBranch;
	private final Path outputPath;
	private final CompareResult compareResults;
	private final BiFunction<Short, Collection<String>, RevisionIndexRequestBuilder<CollectionResource<IComponent>>> fetcherFunction;
	private final Function<IComponent, String> componentTypeResolver;
	private final Function<IComponent, String> labelResolver;
	private final BiFunction<IComponent, IComponent, Collection<CompareData>> getCompareResultsOfComponent;
	private final char delimiter;
	
	public CompareResultsDsvExporter(
			String repositoryUuid,
			String baseBranch,
			String compareBranch,
			Path outputPath,
			CompareResult compareResults, 
			BiFunction<Short, Collection<String>, RevisionIndexRequestBuilder<CollectionResource<IComponent>>> fetcherFunction,
			Function<IComponent, String> componentTypeResolver,
			Function<IComponent, String> labelResolver,
			BiFunction<IComponent, IComponent, Collection<CompareData>> getCompareResultsOfComponent,
			char delimiter
		) {
			this.repositoryUuid = repositoryUuid;
			this.baseBranch = baseBranch;
			this.compareBranch = compareBranch;
			this.outputPath = outputPath;
			this.compareResults = compareResults;
			this.fetcherFunction = fetcherFunction;
			this.componentTypeResolver = componentTypeResolver;
			this.labelResolver = labelResolver;
			this.getCompareResultsOfComponent = getCompareResultsOfComponent;
			this.delimiter = delimiter;
		}
	
	public File export(IProgressMonitor monitor) throws IOException {
		
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = mapper.schemaFor(CompareData.class)
			.withColumnSeparator(delimiter)
			.withHeader()
			.sortedBy("componentType", "componentId", "componentType", "label", "changeKind", "attribute", "from", "to");
		
		try (SequenceWriter writer = mapper.writer(schema).writeValues(outputPath.toFile())) {
			monitor.beginTask("Exporting compare results to DSV", compareResults.getTotalNew() + compareResults.getTotalChanged() + compareResults.getTotalDeleted());
			
			ListMultimap<Short, ComponentIdentifier> newComponentIdentifiers = Multimaps.index(compareResults.getNewComponents(), ComponentIdentifier::getTerminologyComponentId);
			ListMultimap<Short, String> newComponentIds = ImmutableListMultimap.copyOf(Multimaps.transformValues(newComponentIdentifiers, ComponentIdentifier::getComponentId));
			
			for (short terminologyComponentId : newComponentIds.keySet()) {
				for (List<String> componentIds : Lists.partition(newComponentIds.get(terminologyComponentId), PARTITION_SIZE)) {
					RevisionIndexRequestBuilder<CollectionResource<IComponent>> componentFetchRequest = fetcherFunction.apply(terminologyComponentId, componentIds);
					
					if (componentFetchRequest == null) {
						break;
					}
					
					CollectionResource<IComponent> components = componentFetchRequest
						.build(repositoryUuid, compareBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.getSync();
					
					for (IComponent component : components) {
						writer.write(new CompareData(component, ChangeKind.ADDED));
					}
					
					monitor.worked(components.getItems().size());
				}
			}

			ListMultimap<Short, ComponentIdentifier> changedComponentIdentifiers = Multimaps.index(compareResults.getChangedComponents(), ComponentIdentifier::getTerminologyComponentId);
			ListMultimap<Short, String> changedComponentIds = ImmutableListMultimap.copyOf(Multimaps.transformValues(changedComponentIdentifiers, ComponentIdentifier::getComponentId));
			ListMultimap<String, IComponent> componentPairs = ArrayListMultimap.create(PARTITION_SIZE, 2);
			
			for (short terminologyComponentId : changedComponentIds.keySet()) {
				for (List<String> componentIds : Lists.partition(changedComponentIds.get(terminologyComponentId), PARTITION_SIZE)) {
					componentPairs.clear();
					RevisionIndexRequestBuilder<CollectionResource<IComponent>> componentFetchRequest = fetcherFunction.apply(terminologyComponentId, componentIds);
					
					if (componentFetchRequest == null) {
						break;
					}
					
					componentFetchRequest
						.build(repositoryUuid, baseBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.getSync()
						.forEach(c -> componentPairs.put(c.getId(), c));
					
					componentFetchRequest
						.build(repositoryUuid, compareBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.getSync()
						.forEach(c -> componentPairs.put(c.getId(), c));
					
					for (Entry<String, List<IComponent>> entry : Multimaps.asMap(componentPairs).entrySet()) {
						IComponent baseComponent = entry.getValue().get(0);
						IComponent compareComponent = entry.getValue().get(1);
						Collection<CompareData> compareResults = getCompareResultsOfComponent.apply(baseComponent, compareComponent);
						
						for (CompareData d : compareResults) {
							writer.write(d);
						}
					}
					
					monitor.worked(componentPairs.keySet().size());
				}
			}
			
			ListMultimap<Short, ComponentIdentifier> deletedComponentIdentifiers = Multimaps.index(compareResults.getDeletedComponents(), ComponentIdentifier::getTerminologyComponentId);
			ListMultimap<Short, String> deletedComponentIds = ImmutableListMultimap.copyOf(Multimaps.transformValues(deletedComponentIdentifiers, ComponentIdentifier::getComponentId));
			
			for (short terminologyComponentId : deletedComponentIds.keySet()) {
				for (List<String> componentIds : Lists.partition(deletedComponentIds.get(terminologyComponentId), PARTITION_SIZE)) {
					RevisionIndexRequestBuilder<CollectionResource<IComponent>> componentFetchRequest = fetcherFunction.apply(terminologyComponentId, componentIds);
					
					if (componentFetchRequest == null) {
						break;
					}
					
					CollectionResource<IComponent> components = componentFetchRequest
						.build(repositoryUuid, baseBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.getSync();
					
					for (IComponent component : components) {
						writer.write(new CompareData(component, ChangeKind.DELETED));
					}
					
					monitor.worked(components.getItems().size());
				}
			}
			
		} finally {
			monitor.done();
		}
		
		return outputPath.toFile();
	}
	
	/**
	 * XXX: Convenience factory method for (non static) {@link CompareData} for groovy scripting. 
	 * 
	 * @see http://groovy-lang.org/differences.html#_creating_instances_of_non_static_inner_classes 
	 */
	public CompareData createCompareData(IComponent component, String attribute, String from, String to) {
		return new CompareData(component, attribute, from, to);
	}
	
	public class CompareData {
		
		private final ChangeKind changeKind;
		private final IComponent component;
		private final String label;
		private String attribute;
		private String from;
		private String to;
		private final String componentType;
		
		public CompareData(IComponent component, String attribute, String from, String to) {
			this.component = component;
			this.componentType = componentTypeResolver.apply(component);
			this.label = labelResolver.apply(component);
			this.changeKind = ChangeKind.UPDATED;
			this.attribute = attribute;
			this.from = from;
			this.to = to;
		}
		
		private CompareData(IComponent component, ChangeKind changeKind) {
			Preconditions.checkArgument(changeKind == ChangeKind.ADDED || changeKind == ChangeKind.DELETED);
			
			this.changeKind = changeKind;
			this.component = component;
			this.componentType = componentTypeResolver.apply(component);
			this.label = labelResolver.apply(component);
			
		}
		
		public ChangeKind getChangeKind() {
			return changeKind;
		}
		
		public String getComponentId() {
			return component.getId();
		}
		
		public String getLabel() {
			return label;
		}
		
		public String getComponentType() {
			return componentType;
		}
		
		public String getAttribute() {
			return attribute;
		}
		
		public String getFrom() {
			return from;
		}
		
		public String getTo() {
			return to;
		}
	}
}
