/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core;

import java.util.List;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.slf4j.Logger;

import com.b2international.index.revision.Hooks.PreCommitHook;
import com.b2international.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.ContextConfigurer;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.ComponentDeletionPolicy;
import com.b2international.snowowl.core.repository.CompositeComponentDeletionPolicy;
import com.b2international.snowowl.core.repository.ContentAvailabilityInfoProvider;
import com.b2international.snowowl.core.repository.TerminologyRepositoryPlugin;
import com.b2international.snowowl.core.request.*;
import com.b2international.snowowl.core.request.version.VersioningRequestBuilder;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.rpc.RpcUtil;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclParser;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclSerializer;
import com.b2international.snowowl.snomed.core.ecl.EclParser;
import com.b2international.snowowl.snomed.core.ecl.EclSerializer;
import com.b2international.snowowl.snomed.core.merge.SnomedComponentRevisionConflictProcessor;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmExporter;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmExporterImpl;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmImporter;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmJsonImporter;
import com.b2international.snowowl.snomed.core.request.SnomedConceptSearchRequestEvaluator;
import com.b2international.snowowl.snomed.core.request.SnomedQueryOptimizer;
import com.b2international.snowowl.snomed.core.uri.SnomedURLSchemaSupport;
import com.b2international.snowowl.snomed.core.version.SnomedVersioningRequest;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.index.change.SnomedRepositoryPreCommitHook;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptMapSearchRequestEvaluator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.SnomedValueSetMemberSearchRequestEvaluator;
import com.b2international.snowowl.snomed.datastore.request.Synonyms;
import com.b2international.snowowl.snomed.validation.SnomedQueryValidationRuleEvaluator;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

/**
 * @since 7.0
 */
@Component
public final class SnomedPlugin extends TerminologyRepositoryPlugin {

	/**
	 * Unique identifier of the bundle. ID: {@value}
	 */
	public static final String PLUGIN_ID = "com.b2international.snowowl.snomed.datastore"; //$NON-NLS-1$
	
	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("snomed", SnomedCoreConfiguration.class);
	}
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final SnomedCoreConfiguration coreConfig = configuration.getModuleConfig(SnomedCoreConfiguration.class);
		env.services().registerService(SnomedCoreConfiguration.class, coreConfig);
		
		final Injector injector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
		env.services().registerService(EclParser.class, new DefaultEclParser(injector.getInstance(IParser.class), injector.getInstance(IResourceValidator.class)));
		env.services().registerService(EclSerializer.class, new DefaultEclSerializer(injector.getInstance(ISerializer.class)));
		
		// register SNOMED CT Query based validation rule evaluator
		ValidationRuleEvaluator.Registry.register(new SnomedQueryValidationRuleEvaluator());
	}
	
	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
		// initialize MRCM Import-Export API
		if (env.isServer()) {
			env.services().registerService(MrcmExporter.class, new MrcmExporterImpl(env.provider(IEventBus.class)));
			RpcUtil.getInitialServerSession(env.container()).registerClassLoader(MrcmExporter.class, MrcmExporterImpl.class.getClassLoader());
			env.services().registerService(MrcmImporter.class, new MrcmJsonImporter(env.provider(IEventBus.class)));
			RpcUtil.getInitialServerSession(env.container()).registerClassLoader(MrcmImporter.class, MrcmJsonImporter.class.getClassLoader());
		} else {
			env.services().registerService(MrcmImporter.class, RpcUtil.createProxy(env.container(), MrcmImporter.class));
			env.services().registerService(MrcmExporter.class, RpcUtil.createProxy(env.container(), MrcmExporter.class));
		}
	}
	
	@Override
	protected ResourceURLSchemaSupport getTerminologyURISupport() {
		return new SnomedURLSchemaSupport();
	}
	
	@Override
	protected ContentAvailabilityInfoProvider getContentAvailabilityInfoProvider() {
		return context -> {
			return SnomedRequests.prepareSearchConcept()
				.setLimit(0)
				.filterById(Concepts.ROOT_CONCEPT)
				.build()
				.execute(context)
				.getTotal() > 0;
		};
	}
	
	@Override
	protected ContextConfigurer getRequestConfigurer() {
		return new ContextConfigurer() {
			@Override
			public <C extends ServiceProvider> C configure(C context) {
				// enhance all branch context by attaching the Synonyms cache to it
				if (context instanceof BranchContext) {
					BranchContext branchContext = (BranchContext) context;
					return (C) branchContext.inject()
							.bind(Synonyms.class, new Synonyms(branchContext))
							.bind(ModuleIdProvider.class, c -> c.getModuleId())
							.build();
				} else {
					return context;
				}
			}
		};
	}
	
	@Override
	protected ConceptSearchRequestEvaluator getConceptSearchRequestEvaluator() {
		return new SnomedConceptSearchRequestEvaluator();
	}
	
	@Override
	protected QueryOptimizer getQueryOptimizer() {
		return new SnomedQueryOptimizer();
	}
	
	@Override
	protected ComponentDeletionPolicy getComponentDeletionPolicy() {
		return CompositeComponentDeletionPolicy.of(SnomedDocument.class, doc -> !((SnomedDocument) doc).isReleased());
	}
	
	@Override
	public String getToolingId() {
		return SnomedTerminologyComponentConstants.TOOLING_ID;
	}
	
	@Override
	public String getName() {
		return "SNOMED CT";
	}
	
	@Override
	public boolean isEffectiveTimeSupported() {
		return true;
	}
	
	@Override
	public List<Class<? extends IComponent>> getTerminologyComponents() {
		return ImmutableList.<Class<? extends IComponent>>of(
			SnomedConcept.class,
			SnomedDescription.class,
			SnomedRelationship.class,
			SnomedReferenceSet.class,
			SnomedReferenceSetMember.class
		);
	}
	
	@Override
	protected VersioningRequestBuilder getVersioningRequestBuilder() {
		return (config) -> new TransactionalRequest(
			config.getUser(), 
			VersioningRequestBuilder.defaultCommitComment(config), 
			new SnomedVersioningRequest(config), 
			0L, 
			DatastoreLockContextDescriptions.CREATE_VERSION
		);
	}
	
	@Override
	protected PreCommitHook getTerminologyRepositoryPreCommitHook(Logger log) {
		return new SnomedRepositoryPreCommitHook(log);
	}
	
	@Override
	protected ComponentRevisionConflictProcessor getComponentRevisionConflictProcessor() {
		return new SnomedComponentRevisionConflictProcessor();
	}
	
	@Override
	protected ValueSetMemberSearchRequestEvaluator getMemberSearchRequestEvaluator() {
		return new SnomedValueSetMemberSearchRequestEvaluator();
	}
	
	@Override
	protected ConceptMapMappingSearchRequestEvaluator getConceptMapMappingSearchRequestEvaluator() {
		return new SnomedConceptMapSearchRequestEvaluator();
	}
	
}
