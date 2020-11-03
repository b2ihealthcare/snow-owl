/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch.compare;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.ChangeKind;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
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
	
	private final Map<String, String> repositoryUuids;
	private final Map<String, String> baseBranches;
	private final Map<String, String> compareBranches;
	private final Path outputPath;
	private final Map<String, BranchCompareResult> compareResultsProvider;
	private final Map<String, BiFunction<Short, Collection<String>, RevisionIndexRequestBuilder<CollectionResource<IComponent>>>> fetcherProvider;
	private final Map<String, Function<IComponent, String>> labelResolvers;
	private final Map<String, BiFunction<IComponent, IComponent, Collection<CompareData>>> componentCompareResultProviders;
	private final char delimiter;
	
	public CompareResultsDsvExporter(
			Map<String, String> repositoryUuids,
			Map<String, String> baseBranches,
			Map<String, String> compareBranch,
			Path outputPath,
			Map<String, BranchCompareResult> compareResults,
			Map<String, BiFunction<Short, Collection<String>, RevisionIndexRequestBuilder<CollectionResource<IComponent>>>> fetcherFunction,
			Map<String, Function<IComponent, String>> labelResolver,
			Map<String, BiFunction<IComponent, IComponent, Collection<CompareData>>> componentCompareResultProviders,
			char delimiter
		) {
			this.repositoryUuids = repositoryUuids;
			this.baseBranches = baseBranches;
			this.compareBranches = compareBranch;
			this.outputPath = outputPath;
			this.compareResultsProvider = compareResults;
			this.fetcherProvider = fetcherFunction;
			this.labelResolvers = labelResolver;
			this.componentCompareResultProviders = componentCompareResultProviders;
			this.delimiter = delimiter;
		}
	
	private int totalWork() {
		int totalNew = 0;
		int totalChanged = 0;
		int totalDeleted = 0;
		
		for (BranchCompareResult compareResult : compareResultsProvider.values()) {
			totalNew += compareResult.getTotalNew();
			totalChanged += compareResult.getTotalChanged();
			totalDeleted += compareResult.getTotalDeleted();
		}
		
		return totalNew + totalChanged + totalDeleted;
	}
	
	public File export(IProgressMonitor monitor) throws IOException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = mapper.schemaFor(CompareData.class)
			.withColumnSeparator(delimiter)
			.withHeader()
			.sortedBy("componentType", "componentId", "componentType", "label", "changeKind", "attribute", "from", "to");
		
		try (SequenceWriter writer = mapper.writer(schema).writeValues(outputPath.toFile())) {
			monitor.beginTask("Exporting compare results to DSV", totalWork());
			for (String codeSystem : repositoryUuids.keySet()) {
				exportCodeSystem(codeSystem, writer, monitor);
			}
			
		} finally {
			monitor.done();
		}
		
		return outputPath.toFile();
	}
			
	private void exportCodeSystem(final String codeSystem, SequenceWriter writer, IProgressMonitor monitor) {
		BranchCompareResult compareResults = compareResultsProvider.get(codeSystem);
		BiFunction<Short, Collection<String>, RevisionIndexRequestBuilder<CollectionResource<IComponent>>> fetcherFunction = fetcherProvider.get(codeSystem);
		String repositoryUuid = repositoryUuids.get(codeSystem);
		String compareBranch = compareBranches.get(codeSystem);
		String baseBranch = baseBranches.get(codeSystem);
		BiFunction<IComponent, IComponent, Collection<CompareData>> getCompareResultsOfComponent = componentCompareResultProviders.get(codeSystem);
		
		try {
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
						writer.write(added(codeSystem, component));
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
						Collection<CompareData> compareData = getCompareResultsOfComponent.apply(baseComponent, compareComponent);
						
						for (CompareData d : compareData) {
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
						writer.write(removed(codeSystem, component));
					}
					
					monitor.worked(components.getItems().size());
				}
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}
			
	
	/**
	 * XXX: Convenience factory method for (non static) {@link CompareData} for groovy scripting. 
	 * 
	 * @see http://groovy-lang.org/differences.html#_creating_instances_of_non_static_inner_classes 
	 */
	public CompareData changed(String codeSystem, IComponent component, String attribute, String from, String to) {
		return new CompareData(ChangeKind.UPDATED, component, labelResolvers.get(codeSystem).apply(component), attribute, from, to);
	}
	
	public CompareData removed(String codeSystem, IComponent component) {
		return new CompareData(ChangeKind.DELETED, component, labelResolvers.get(codeSystem).apply(component), null, null, null);
	}
	
	public CompareData added(String codeSystem, IComponent component) {
		return new CompareData(ChangeKind.ADDED, component, labelResolvers.get(codeSystem).apply(component), null, null, null);
	}
	
	public class CompareData {
		
		private final ChangeKind changeKind;
		
		private final String componentType;
		private final IComponent component;
		private final String label;
		
		private final String attribute;
		private final String from;
		private final String to;
		
		private CompareData(ChangeKind changeKind, 
				IComponent component,
				String label,
				String attribute, 
				String from, 
				String to) {
			this.changeKind = changeKind;
			this.component = component;
			this.label = label;
			this.componentType = TerminologyRegistry.INSTANCE.getTerminologyComponentByShortId(component.getTerminologyComponentId()).name();
			this.attribute = attribute;
			this.from = from;
			this.to = to;
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
