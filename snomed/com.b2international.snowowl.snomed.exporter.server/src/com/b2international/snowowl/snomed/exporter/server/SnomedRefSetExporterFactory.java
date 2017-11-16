/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static java.util.Collections.emptySet;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedCrossMapExporter;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedCrossMapSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedCrossMapTargetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedSubsetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedSubsetMemberExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedAssociationRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedAttributeValueRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedComplexMapRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedConcreteDomainRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedDescriptionTypeRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedExtendedMapRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedModuleDependencyRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedOWLAxiomRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedQueryRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedRefSetExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedSimpleMapRefSetExporter;
import com.google.common.collect.Sets;

/**
 * Factory class for instantiating type specific exporters without exposing the
 * concrete implementation of the exporter.
 * 
 */
public class SnomedRefSetExporterFactory {
	
	public static SnomedExporter getRefSetExporter(final SnomedReferenceSet refset, final SnomedExportContext exportContext,
			final RevisionSearcher revisionSearcher) {
		
		switch (refset.getType()) {
			case SIMPLE_MAP:
				return new SnomedSimpleMapRefSetExporter(exportContext, refset, exportContext.includeMapTargetDescription(), revisionSearcher);
			case COMPLEX_MAP:
				return new SnomedComplexMapRefSetExporter(exportContext, refset, revisionSearcher);
			case EXTENDED_MAP:
				return new SnomedExtendedMapRefSetExporter(exportContext, refset, revisionSearcher);
			case QUERY:
				return new SnomedQueryRefSetExporter(exportContext, refset, revisionSearcher);
			case ATTRIBUTE_VALUE:
				return new SnomedAttributeValueRefSetExporter(exportContext, refset, revisionSearcher);
			case SIMPLE:
				return new SnomedRefSetExporter(exportContext, refset, revisionSearcher);
			case DESCRIPTION_TYPE:
				return new SnomedDescriptionTypeRefSetExporter(exportContext, refset, revisionSearcher);
			case CONCRETE_DATA_TYPE:
				return new SnomedConcreteDomainRefSetExporter(exportContext, refset, revisionSearcher);
			case ASSOCIATION:
				return new SnomedAssociationRefSetExporter(exportContext, refset, revisionSearcher);
			case MODULE_DEPENDENCY:
				return new SnomedModuleDependencyRefSetExporter(exportContext, refset, revisionSearcher);
			case OWL_AXIOM:
				return new SnomedOWLAxiomRefSetExporter(exportContext, refset, revisionSearcher);

			default:
				throw new IllegalArgumentException("Unknown reference set type.");
		}
	}
	
	public static Iterable<SnomedExporter> getSubsetExporter(final SnomedReferenceSet refset, final SnomedExportContext configuration, 
			final RevisionSearcher revisionSearcher) {
		
		switch (refset.getType()) {
			case LANGUAGE: //$FALL-THROUGH$
			case SIMPLE: 
				final SnomedSubsetMemberExporter memberExporter = new SnomedSubsetMemberExporter(configuration, refset.getId(), revisionSearcher);
				final SnomedSubsetExporter subsetExporter = new SnomedSubsetExporter(configuration, refset.getId(), revisionSearcher, memberExporter.getVersion());
				return Sets.<SnomedExporter>newHashSet(memberExporter, subsetExporter);
			default: return emptySet();
		}
		
	}
	
	public static Iterable<SnomedExporter> getCrossMapExporter(final SnomedReferenceSet refset, final SnomedExportContext configuration, 
			final SnomedMapSetSetting mapSetSetting, final RevisionSearcher revisionSearcher) {
		
		if (Concepts.CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refset.getId()) || Concepts.SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refset.getId())) {
			return emptySet();
		}
		
		switch (refset.getType()) {
			case EXTENDED_MAP: //$FALL-THROUGH$
			case COMPLEX_MAP: //$FALL-THROUGH$
			case SIMPLE_MAP: 
				return Sets.<SnomedExporter>newHashSet(
					new SnomedCrossMapExporter(configuration, refset.getId(), mapSetSetting, revisionSearcher),
					new SnomedCrossMapSetExporter(configuration, refset.getId(), mapSetSetting, revisionSearcher),
					new SnomedCrossMapTargetExporter(configuration, refset.getId(), mapSetSetting, revisionSearcher));
			default: return emptySet();
		}
	}

}