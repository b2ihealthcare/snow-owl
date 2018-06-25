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
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 6.5
 */
public final class CompareResultsDsvExporter {
	
	private static final int BUFFER = 1000;
	
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
			BiFunction<Short, Collection<String>, RevisionIndexRequestBuilder<CollectionResource<IComponent>>> requestBuilderFunction,
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
			this.fetcherFunction = requestBuilderFunction;
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
			
			List<List<ComponentIdentifier>> newComponentIdentifierChunks = Lists.partition(Lists.newArrayList(compareResults.getNewComponents()), BUFFER);

			for (Collection<ComponentIdentifier> newComponentsIdentifiersChunk : newComponentIdentifierChunks) {
				
				Multimap<Short, ComponentIdentifier> componentsInChunkByTerminologyComponentId = Multimaps.index(newComponentsIdentifiersChunk, ComponentIdentifier::getTerminologyComponentId);
				
				for (Entry<Short, Collection<ComponentIdentifier>> entriesByTerminologyComponentId : componentsInChunkByTerminologyComponentId.asMap().entrySet()) {
					
					short terminologyComponentIdForThisChunk = entriesByTerminologyComponentId.getKey();
					Set<String> componentIdsInThisChunk = entriesByTerminologyComponentId.getValue().stream().map(ComponentIdentifier::getComponentId).collect(Collectors.toSet());
					
					CollectionResource<IComponent> newComponents = fetcherFunction.apply(terminologyComponentIdForThisChunk, componentIdsInThisChunk)
						.build(repositoryUuid, compareBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.getSync();
					
					for (IComponent component : newComponents) {
						writer.write(new CompareData(component, ChangeKind.ADDED));
					}
						
				};
				
				
				monitor.worked(newComponentsIdentifiersChunk.size());
			}
		
			// changed
			
			List<List<ComponentIdentifier>> changedComponentIdentifiersChunks = Lists.partition(Lists.newArrayList(compareResults.getChangedComponents()), BUFFER);
			
			for (List<ComponentIdentifier> changedComponentIdentifiersChunk : changedComponentIdentifiersChunks) {
				
				Multimap<String, IComponent> changedComponentsChunk = ArrayListMultimap.create(changedComponentIdentifiersChunk.size(), 2);
				
				Multimap<Short, ComponentIdentifier> componentsInChunkByTerminologyComponentId = Multimaps.index(changedComponentIdentifiersChunk, ComponentIdentifier::getTerminologyComponentId);
				
				for (Entry<Short, Collection<ComponentIdentifier>> entriesByTerminologyComponentId : componentsInChunkByTerminologyComponentId.asMap().entrySet()) {
					
					short terminologyComponentIdForThisChunk = entriesByTerminologyComponentId.getKey();
					Set<String> componentIdsInThisChunk = entriesByTerminologyComponentId.getValue().stream().map(ComponentIdentifier::getComponentId).collect(Collectors.toSet());
					
					CollectionResource<IComponent> changedComponentsOnBaseBranch = fetcherFunction.apply(terminologyComponentIdForThisChunk, componentIdsInThisChunk)
						.build(repositoryUuid, baseBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.getSync();
					
					changedComponentsOnBaseBranch.forEach(component -> changedComponentsChunk.put(component.getId(), component));
					
					CollectionResource<IComponent> changedComponentsOnCompareBranch = fetcherFunction.apply(terminologyComponentIdForThisChunk, componentIdsInThisChunk)
						.build(repositoryUuid, compareBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.getSync();
					
					changedComponentsOnCompareBranch.forEach(component -> changedComponentsChunk.put(component.getId(), component));
				};
				
				for (Entry<String, Collection<IComponent>> entry : changedComponentsChunk.asMap().entrySet()) {
					
					IComponent baseComponent = ((List<IComponent>) (entry.getValue())).get(0);
					IComponent compareComponent = ((List<IComponent>) (entry.getValue())).get(1);
					
					Collection<CompareData> comparisonResultsOfThisChunk = getCompareResultsOfComponent.apply(baseComponent, compareComponent);
					
					for (CompareData d : comparisonResultsOfThisChunk) {
						writer.write(d);
					};
					
					
				};
				
				monitor.worked(changedComponentIdentifiersChunk.size());
			}
			
			// deleted
			
			List<List<ComponentIdentifier>> deletedComponentIdentifiersChunks = Lists.partition(Lists.newArrayList(compareResults.getDeletedComponents()), BUFFER);
			
			for (List<ComponentIdentifier> deletedComponentIdentifiersChunk : deletedComponentIdentifiersChunks) {
				
				Multimap<Short, ComponentIdentifier> componentsInChunkByTerminologyComponentId = Multimaps.index(deletedComponentIdentifiersChunk, ComponentIdentifier::getTerminologyComponentId);
				
				for (Entry<Short, Collection<ComponentIdentifier>> entriesByTerminologyComponentId : componentsInChunkByTerminologyComponentId.asMap().entrySet()) {

					short terminologyComponentIdForThisChunk = entriesByTerminologyComponentId.getKey();
					Set<String> componentIdsInThisChunk = entriesByTerminologyComponentId.getValue().stream().map(ComponentIdentifier::getComponentId).collect(Collectors.toSet());
					
					CollectionResource<IComponent> deletedComponents = fetcherFunction.apply(terminologyComponentIdForThisChunk, componentIdsInThisChunk)
						.build(repositoryUuid, baseBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.getSync();
					
					for (IComponent component : deletedComponents) {
						writer.write(new CompareData(component, ChangeKind.DELETED));
					};
				};
				monitor.worked(deletedComponentIdentifiersChunk.size());
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
