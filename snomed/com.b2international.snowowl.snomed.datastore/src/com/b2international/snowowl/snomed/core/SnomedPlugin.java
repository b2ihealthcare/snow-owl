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
package com.b2international.snowowl.snomed.core;

import java.util.Collection;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;

import com.b2international.commons.extension.Component;
import com.b2international.index.revision.Hooks.PreCommitHook;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.repository.TerminologyRepositoryInitializer;
import com.b2international.snowowl.core.repository.TerminologyRepositoryPlugin;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.TransactionalRequest;
import com.b2international.snowowl.datastore.version.VersioningRequestBuilder;
import com.b2international.snowowl.rpc.RpcUtil;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclParser;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclSerializer;
import com.b2international.snowowl.snomed.core.ecl.EclParser;
import com.b2international.snowowl.snomed.core.ecl.EclSerializer;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.core.lang.StaticLanguageSetting;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmExporter;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmExporterImpl;
import com.b2international.snowowl.snomed.core.mrcm.io.MrcmImporter;
import com.b2international.snowowl.snomed.core.mrcm.io.XMIMrcmImporter;
import com.b2international.snowowl.snomed.core.version.SnomedVersioningRequest;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.assigner.SnomedNamespaceAndModuleAssignerProvider;
import com.b2international.snowowl.snomed.datastore.index.change.SnomedRepositoryPreCommitHook;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.internal.SnomedRepositoryInitializer;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.snomed.validation.SnomedQueryValidationRuleEvaluator;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

/**
 * @since 7.0
 */
@Component
public final class SnomedPlugin extends TerminologyRepositoryPlugin {

	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("snomed", SnomedCoreConfiguration.class);
	}
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final SnomedCoreConfiguration coreConfig = configuration.getModuleConfig(SnomedCoreConfiguration.class);
		env.services().registerService(SnomedCoreConfiguration.class, coreConfig);
		env.services().registerService(LanguageSetting.class, new StaticLanguageSetting(coreConfig.getLanguage(), SnomedCoreConfiguration.DEFAULT_LANGUAGE));
		
		final Injector injector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
		env.services().registerService(EclParser.class, new DefaultEclParser(injector.getInstance(IParser.class), injector.getInstance(IResourceValidator.class)));
		env.services().registerService(EclSerializer.class, new DefaultEclSerializer(injector.getInstance(ISerializer.class)));
		
		// register SNOMED CT Query based validation rule evaluator
		ValidationRuleEvaluator.Registry.register(new SnomedQueryValidationRuleEvaluator());

		env.services().registerService(SnomedNamespaceAndModuleAssignerProvider.class, SnomedNamespaceAndModuleAssignerProvider.INSTANCE);
		
		// initialize MRCM Import-Export API
		if (!env.isEmbedded()) {
			env.services().registerService(MrcmImporter.class, RpcUtil.createProxy(env.container(), MrcmImporter.class));
			env.services().registerService(MrcmExporter.class, RpcUtil.createProxy(env.container(), MrcmExporter.class));
		}
		if (env.isServer() || env.isEmbedded()) {
			env.services().registerService(MrcmExporter.class, new MrcmExporterImpl());
			RpcUtil.getInitialServerSession(env.container()).registerClassLoader(MrcmExporter.class, MrcmExporterImpl.class.getClassLoader());
			env.services().registerService(MrcmImporter.class, new XMIMrcmImporter());
			RpcUtil.getInitialServerSession(env.container()).registerClassLoader(MrcmImporter.class, XMIMrcmImporter.class.getClassLoader());
		}
	}
	
	@Override
	protected Collection<Class<?>> getMappings() {
		return ImmutableList.<Class<?>>of(
			SnomedConceptDocument.class,
			SnomedDescriptionIndexEntry.class,
			SnomedRelationshipIndexEntry.class,
//			SnomedConstraintDocument.class,
			SnomedRefSetMemberIndexEntry.class
		);
	}
	
	@Override
	protected String getRepositoryId() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
	@Override
	public String getId() {
		return SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
	}
	
	@Override
	public String getName() {
		return SnomedTerminologyComponentConstants.SNOMED_NAME;
	}
	
	@Override
	public boolean isEffectiveTimeSupported() {
		return true;
	}
	
	@Override
	public Collection<Class<? extends IComponent>> getTerminologyComponents() {
		return ImmutableList.<Class<? extends IComponent>>of(
			SnomedConcept.class,
			SnomedDescription.class,
			SnomedRelationship.class,
//			SnomedConstraint.class,
			SnomedReferenceSet.class
		);
	}
	
	@Override
	protected VersioningRequestBuilder getVersioningRequestBuilder() {
		return (config) -> new TransactionalRequest(
			config.getUser(), 
			"Create version " + config.getVersionId(), 
			new SnomedVersioningRequest(config), 
			0L, 
			DatastoreLockContextDescriptions.CREATE_VERSION
		);
	}
	
	@Override
	protected TerminologyRepositoryInitializer getTerminologyRepositoryInitializer() {
		return new SnomedRepositoryInitializer();
	}
	
	@Override
	protected PreCommitHook getTerminologyRepositoryPreCommitHook() {
		return new SnomedRepositoryPreCommitHook(log());
	}

}
