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
package com.b2international.snowowl.snomed.exporter.server.refset;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;

import java.util.Collections;

import org.eclipse.emf.cdo.view.CDOView;

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
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.exporter.server.sandbox.NoopExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedAssociationRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedAttributeValueRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedComplexMapRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedConcreteDomainRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedDescriptionTypeRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedLanguageRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedModuleDependencyRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedQueryRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedSimpleMapRefSetExporter;
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
	 * @return the reference set exporter instance
	 * 
	 * @throws IllegalArgumentException
	 *             if the type based on the reference set identifier concept id
	 *             cannot be determined, or the resolved reference set is not
	 *             exportable
	 */
	public static SnomedExporter getRefSetExporter(final String refSetId, final SnomedExportConfiguration configuration) {
		
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
					final boolean includeMapTargetDescription = isSddMapping(createPath(cdoView), refSetId);
					return new SnomedSimpleMapRefSetExporter(configuration, refSetId, type, includeMapTargetDescription);
				case COMPLEX_MAP: //$FALL-THROUGH$
				case EXTENDED_MAP:
					final boolean extended = SnomedRefSetType.EXTENDED_MAP.equals(refSet.getType());
					return new SnomedComplexMapRefSetExporter(configuration, refSetId, type, extended);
				case LANGUAGE:
					return new SnomedLanguageRefSetExporter(configuration, refSetId, type);
				case QUERY:
					return new SnomedQueryRefSetExporter(configuration, refSetId, type);
				case ATTRIBUTE_VALUE:
					return new SnomedAttributeValueRefSetExporter(configuration, refSetId, type);
				case SIMPLE:
					return new SnomedRefSetExporter(configuration, refSetId, type);
				case DESCRIPTION_TYPE:
					return new SnomedDescriptionTypeRefSetExporter(configuration, refSetId, type);
				case CONCRETE_DATA_TYPE:
					return new SnomedConcreteDomainRefSetExporter(configuration, refSetId, type);
				case ASSOCIATION:
					return new SnomedAssociationRefSetExporter(configuration, refSetId, type);
				case MODULE_DEPENDENCY:
					return new SnomedModuleDependencyRefSetExporter(configuration, refSetId, type);

				default:
					throw new IllegalArgumentException("Unknown reference set type.");
			}
		} finally {
			if (null != cdoView)
				cdoView.close();
		}
	}
	
	private static final Iterable<SnomedExporter> NULL_EXPORTERS = Collections.<SnomedExporter>singleton(NoopExporter.INSTANCE);
	
	public static Iterable<SnomedExporter> getSubsetExporter(final String refSetId, final SnomedExportConfiguration configuration) {
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
					final SnomedSubsetMemberExporter memberExporter = new SnomedSubsetMemberExporter(configuration, refSetId);
					final SnomedSubsetExporter subsetExporter = new SnomedSubsetExporter(configuration, refSetId, memberExporter);
				return Sets.<SnomedExporter>newHashSet(memberExporter, subsetExporter);
				default: return NULL_EXPORTERS;
			}
		} finally {
			if (null != view)
				view.close();
		}
	}
	
	public static Iterable<SnomedExporter> getCrossMapExporter(final String refSetId, final SnomedExportConfiguration configuration, 
			final SnomedMapSetSetting mapSetSetting) {
		
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
						new SnomedCrossMapExporter(configuration, refSetId, mapSetSetting),
						new SnomedCrossMapSetExporter(configuration, refSetId, mapSetSetting),
						new SnomedCrossMapTargetExporter(configuration, refSetId, mapSetSetting));
				default: return NULL_EXPORTERS;
			}
		} finally {
			if (null != view)
				view.close();
		}
	}

	private static boolean isSddMapping(final IBranchPath branchPath, final String refSetId) {
		return isChildOfSddSimpleMap(branchPath, refSetId) && isConceptReferencedComponent(branchPath, refSetId);
	}

	private static boolean isChildOfSddSimpleMap(final IBranchPath branchPath, final String refSetId) {
		return getServiceForClass(SnomedTerminologyBrowser.class).isSuperTypeOfById(branchPath, Concepts.SDD_DRUG_REFERENCE_SET, refSetId);
	}

	private static boolean isConceptReferencedComponent(final IBranchPath branchPath, final String refSetId) {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER == getServiceForClass(SnomedRefSetBrowser.class).getRefSet(branchPath, refSetId).getReferencedComponentType();
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