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
package com.b2international.snowowl.snomed.exporter.server;

import java.util.Collections;

import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedCrossMapExporter;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedCrossMapSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedCrossMapTargetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedSubsetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedSubsetMemberExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.NoopExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedAssociationRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedAttributeValueRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedComplexMapRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedConcreteDomainRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedDescriptionTypeRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedLanguageRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedModuleDependencyRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedQueryRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedSimpleMapRefSetExporter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Sets;

/**
 * Factory class for instantiating type specific exporters without exposing the
 * concrete implementation of the exporter.
 * 
 */
public class SnomedRefSetExporterFactory {
	
	/**
	 * Creates a reference set exporter based on the passed reference set
	 * identifier concept id.
	 * 
	 * @param refSetId
	 *            the reference set's identifier concept id, used for
	 *            determining the reference set type
	 * 
	 * @param clientBranchId
	 *            the current client branch id
	 * 
	 * @param clientBranchBaseTimeStamp
	 *            the current client branch's base timestamp (used only when the
	 *            client is on a task branch)
	 *            
	 * @param fromEffectiveTime
	 *            from effective time, can be {@code null}
	 * 
	 * @param toEffectiveTime
	 *            to effective time, can be {@code null}
	 *            
	 * @param newModuleDependencies           
	 *            set that contains the the new module dependency members
	 *            
	 * @param revisionSearcher the searcher to supply the actual artefacts being exported
	 *            
	 * @return the reference set exporter instance
	 * 
	 * @throws IllegalArgumentException
	 *             if the type based on the reference set identifier concept id
	 *             cannot be determined, or the resolved reference set cannot be exported
	 */
	public static SnomedExporter getRefSetExporter(final String refSetId, final SnomedExportContext configuration,
			final RevisionSearcher revisionSearcher, final boolean unpublished) {
		
		CDOView cdoView = null;
		try {
			
			cdoView = createView(configuration.getCurrentBranchPath());
			final SnomedRefSet refSet =  getRefSet(refSetId, cdoView);
			if (null == refSet) {
				return NoopExporter.INSTANCE;
			}
			
			final SnomedRefSetType type = refSet.getType();
			switch (type) {
				case SIMPLE_MAP:
					return new SnomedSimpleMapRefSetExporter(configuration, refSetId, type, configuration.includeMapTargetDescription(), revisionSearcher, unpublished);
				case COMPLEX_MAP: //$FALL-THROUGH$
				case EXTENDED_MAP:
					final boolean extended = SnomedRefSetType.EXTENDED_MAP.equals(refSet.getType());
					return new SnomedComplexMapRefSetExporter(configuration, refSetId, type, extended, revisionSearcher, unpublished);
				case LANGUAGE:
					return new SnomedLanguageRefSetExporter(configuration, refSetId, type, revisionSearcher, unpublished);
				case QUERY:
					return new SnomedQueryRefSetExporter(configuration, refSetId, type, revisionSearcher, unpublished);
				case ATTRIBUTE_VALUE:
					return new SnomedAttributeValueRefSetExporter(configuration, refSetId, type, revisionSearcher, unpublished);
				case SIMPLE:
					return new SnomedRefSetExporter(configuration, refSetId, type, revisionSearcher, unpublished);
				case DESCRIPTION_TYPE:
					return new SnomedDescriptionTypeRefSetExporter(configuration, refSetId, type, revisionSearcher, unpublished);
				case CONCRETE_DATA_TYPE:
					return new SnomedConcreteDomainRefSetExporter(configuration, refSetId, type, revisionSearcher, unpublished);
				case ASSOCIATION:
					return new SnomedAssociationRefSetExporter(configuration, refSetId, type, revisionSearcher, unpublished);
				case MODULE_DEPENDENCY:
					return new SnomedModuleDependencyRefSetExporter(configuration, refSetId, type, revisionSearcher, unpublished);

				default:
					throw new IllegalArgumentException("Unknown reference set type.");
			}
		} finally {
			if (null != cdoView)
				cdoView.close();
		}
	}
	
	private static final Iterable<SnomedExporter> NULL_EXPORTERS = Collections.<SnomedExporter>singleton(NoopExporter.INSTANCE);
	
	public static Iterable<SnomedExporter> getSubsetExporter(final String refSetId, final SnomedExportContext configuration, 
			final RevisionSearcher revisionSearcher, final boolean unpublished) {
		CDOView view = null;
		try {
			view = createView(configuration.getCurrentBranchPath());
			final SnomedRefSet refSet = getRefSet(refSetId, view);
			if (null == refSet) {
				return NULL_EXPORTERS;
			}
			
			switch (refSet.getType()) {
				case LANGUAGE: //$FALL-THROUGH$
				case SIMPLE: 
					final SnomedSubsetMemberExporter memberExporter = new SnomedSubsetMemberExporter(configuration, refSetId, revisionSearcher);
					final SnomedSubsetExporter subsetExporter = new SnomedSubsetExporter(configuration, refSetId, memberExporter, revisionSearcher);
				return Sets.<SnomedExporter>newHashSet(memberExporter, subsetExporter);
				default: return NULL_EXPORTERS;
			}
		} finally {
			if (null != view)
				view.close();
		}
	}
	
	public static Iterable<SnomedExporter> getCrossMapExporter(final String refSetId, final SnomedExportContext configuration, 
			final SnomedMapSetSetting mapSetSetting, final RevisionSearcher revisionSearcher, final boolean unpublished) {
		
		if (Concepts.CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refSetId) || Concepts.SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refSetId))
			return NULL_EXPORTERS;
		
		CDOView view = null;
		try {
			view = createView(configuration.getCurrentBranchPath());
			final SnomedRefSet refSet = getRefSet(refSetId, view);
			if (null == refSet) {
				return NULL_EXPORTERS;
			}
			
			switch (refSet.getType()) {
				case EXTENDED_MAP: //$FALL-THROUGH$
				case COMPLEX_MAP: //$FALL-THROUGH$
				case SIMPLE_MAP: return Sets.<SnomedExporter>newHashSet(
						new SnomedCrossMapExporter(configuration, refSetId, mapSetSetting, revisionSearcher),
						new SnomedCrossMapSetExporter(configuration, refSetId, mapSetSetting, revisionSearcher),
						new SnomedCrossMapTargetExporter(configuration, refSetId, mapSetSetting, revisionSearcher));
				default: return NULL_EXPORTERS;
			}
		} finally {
			if (null != view)
				view.close();
		}
	}

	/*returns with a SNOMED CT reference set identified by the identifier concept ID, opened in the specified CDO view*/
	private static SnomedRefSet getRefSet(final String id, final CDOView cdoView) {
		final ILookupService<String, SnomedRefSet, CDOView> lookupService = CoreTerminologyBroker
				.getInstance()
				.getLookupService(SnomedTerminologyComponentConstants.REFSET);
		
		return lookupService.getComponent(id, cdoView);
	}
	
	private static CDOView createView(final IBranchPath branchPath) {
		
		final ICDOConnectionManager manager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection connection = manager.get(SnomedPackage.eINSTANCE);
		
		return connection.createView(branchPath);
	}

}