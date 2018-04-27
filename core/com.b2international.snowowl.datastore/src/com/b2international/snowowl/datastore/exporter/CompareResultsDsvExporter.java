/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.datastore.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
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
	
	public CompareResultsDsvExporter(
			String repositoryUuid,
			String baseBranch,
			String compareBranch,
			Path outputPath,
			CompareResult compareResults, 
			BiFunction<Short, Collection<String>, RevisionIndexRequestBuilder<CollectionResource<IComponent>>> requestBuilderFunction,
			Function<IComponent, String> componentTypeResolver,
			Function<IComponent, String> labelResolver,
			BiFunction<IComponent, IComponent, Collection<CompareData>> getCompareResultsOfComponent
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
		}
	
	public File export(IProgressMonitor monitor) throws IOException {
		
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = mapper.schemaFor(CompareData.class)
			.withColumnSeparator(',')
			.withHeader()
			.sortedBy("componentType", "componentId", "componentType", "label", "changeKind", "attribute", "from", "to");
		
		try (SequenceWriter writer = mapper.writer(schema).writeValues(outputPath.toFile())) {
			monitor.beginTask("Exporting compare results to DSV", compareResults.getTotalChanged());
			
			List<List<ComponentIdentifier>> newComponentIdentifierChunks = Lists.partition(Lists.newArrayList(compareResults.getNewComponents()), BUFFER);

			for (Collection<ComponentIdentifier> newComponentsIdentifiersChunk : newComponentIdentifierChunks) {
				
				Multimap<Short, ComponentIdentifier> componentsInChunkByTerminologyComponentId = Multimaps.index(newComponentsIdentifiersChunk, ComponentIdentifier::getTerminologyComponentId);
				
				componentsInChunkByTerminologyComponentId.asMap().entrySet().forEach(entriesByTerminologyComponentId -> {
					
					short terminologyComponentIdForThisChunk = entriesByTerminologyComponentId.getKey();
					Set<String> componentIdsInThisChunk = entriesByTerminologyComponentId.getValue().stream().map(ComponentIdentifier::getComponentId).collect(Collectors.toSet());
					
					fetcherFunction.apply(terminologyComponentIdForThisChunk, componentIdsInThisChunk)
					.build(repositoryUuid, compareBranch)
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.then((CollectionResource<IComponent> newComponents) -> {
						newComponents.forEach(component -> {
							try {
								writer.write(new CompareData(component, ChangeKind.ADDED));
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
						
						return null;
					})
					.getSync();
				});
				monitor.worked(newComponentsIdentifiersChunk.size());
			}
		
			// changed
			
			List<List<ComponentIdentifier>> changedComponentIdentifiersChunks = Lists.partition(Lists.newArrayList(compareResults.getChangedComponents()), BUFFER);
			
			for (List<ComponentIdentifier> changedComponentIdentifiersChunk : changedComponentIdentifiersChunks) {
				
				Multimap<String, IComponent> changedComponentsChunk = ArrayListMultimap.create(changedComponentIdentifiersChunk.size(), 2);
				
				Multimap<Short, ComponentIdentifier> componentsInChunkByTerminologyComponentId = Multimaps.index(changedComponentIdentifiersChunk, ComponentIdentifier::getTerminologyComponentId);
				
				componentsInChunkByTerminologyComponentId.asMap().entrySet().forEach(entriesByTerminologyComponentId -> {
					
					short terminologyComponentIdForThisChunk = entriesByTerminologyComponentId.getKey();
					Set<String> componentIdsInThisChunk = entriesByTerminologyComponentId.getValue().stream().map(ComponentIdentifier::getComponentId).collect(Collectors.toSet());
					
					fetcherFunction.apply(terminologyComponentIdForThisChunk, componentIdsInThisChunk)
						.build(repositoryUuid, baseBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
						.then((CollectionResource<IComponent> changedComponentsOnBaseBranch) -> {
							changedComponentsOnBaseBranch.forEach(component -> changedComponentsChunk.put(component.getId(), component));
							return null;
						})
						.thenWith(nothing -> {
							return fetcherFunction.apply(terminologyComponentIdForThisChunk, componentIdsInThisChunk)
									.build(repositoryUuid, compareBranch)
									.execute(ApplicationContext.getServiceForClass(IEventBus.class));
						}).then((CollectionResource<IComponent> changedComponentsOnCompareBranch) -> {
							changedComponentsOnCompareBranch.forEach(component -> changedComponentsChunk.put(component.getId(), component));
							return null;
						})
						.getSync();
				});
				
				changedComponentsChunk.asMap().entrySet().forEach(entry -> {
					
					IComponent baseComponent = ((List<IComponent>) (entry.getValue())).get(0);
					IComponent compareComponent = ((List<IComponent>) (entry.getValue())).get(1);
					
					//Collection<CompareEditorMember> // comparisonResultsOfThisChunk = codeSystemState.get().createCompareEditorMembers(baseComponent, compareComponent);
					Collection<CompareData> comparisonResultsOfThisChunk = getCompareResultsOfComponent.apply(baseComponent, compareComponent);
					
					comparisonResultsOfThisChunk.forEach(t -> {
						try {
							writer.write(t);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					
					
				});
				
				monitor.worked(changedComponentIdentifiersChunk.size());
			}
			
			// deleted
			
			List<List<ComponentIdentifier>> deletedComponentIdentifiersChunks = Lists.partition(Lists.newArrayList(compareResults.getDeletedComponents()), BUFFER);
			
			for (List<ComponentIdentifier> deletedComponentIdentifiersChunk : deletedComponentIdentifiersChunks) {
				
				Multimap<Short, ComponentIdentifier> componentsInChunkByTerminologyComponentId = Multimaps.index(deletedComponentIdentifiersChunk, ComponentIdentifier::getTerminologyComponentId);
				
				componentsInChunkByTerminologyComponentId.asMap().entrySet().forEach(entriesByTerminologyComponentId -> {

					short terminologyComponentIdForThisChunk = entriesByTerminologyComponentId.getKey();
					Set<String> componentIdsInThisChunk = entriesByTerminologyComponentId.getValue().stream().map(ComponentIdentifier::getComponentId).collect(Collectors.toSet());
					
					fetcherFunction.apply(terminologyComponentIdForThisChunk, componentIdsInThisChunk)
						.build(repositoryUuid, baseBranch)
						.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.then((CollectionResource<IComponent> deletedComponents) -> {
						deletedComponents.forEach(component -> {
							try {
								writer.write(new CompareData(component, ChangeKind.DELETED));
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
						return null;
					})
					.getSync();
				});
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
