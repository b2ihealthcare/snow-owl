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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.b2international.snowowl.snomed.exporter.server.rf1.SnomedRf1ReleaseFileHeaders.RF1_SUBSET_MEMBERS_HEADER;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * Implementation of the SNOMED&nbsp;CT subset exporter. Supports RF1 output
 * format. Used for simple type and language type reference sets.
 * <p>
 * <b>RR1</b> CSV header:
 * <ul>
 * <li>SUBSETID&#9;MEMBERID&#9;MEMBERSTATUS&#9;LINKEDID</li>
 * </ul>
 * </p>
 * 
 * @see SnomedRf1Exporter
 * @see AbstractSnomedSubsetExporter
 * @see SnomedExportSetting
 */
public class SnomedSubsetMemberExporter extends AbstractSnomedSubsetExporter {

	private final boolean languageType;
	private final Id2Rf1PropertyMapper mapper;
	private final LongSet distinctEffectiveTimeSet;

	private Iterator<String> itr;

	public SnomedSubsetMemberExporter(final SnomedExportContext configuration, final String refSetId, final RevisionSearcher revisionSearcher) {
		super(configuration, refSetId, revisionSearcher);
		mapper = new Id2Rf1PropertyMapper();
		languageType = isLanguageType(refSetId);
		distinctEffectiveTimeSet = PrimitiveSets.newLongOpenHashSet();
		itr = Iterators.transform(createResultSet().iterator(), new Function<ReferencedComponentIdStatus, String>() {
			@Override
			public String apply(ReferencedComponentIdStatus input) {
				return new StringBuilder(getRefSetId()).append(HT).append(input.referencedComponentId).append(HT).append(input.status).append(HT).toString();
			}
		});
	}

	private Collection<ReferencedComponentIdStatus> createResultSet() {

		LongKeyLongMap descriptionIdTypeMap = PrimitiveMaps.newLongKeyLongOpenHashMap();

		try {
			// get referenced component's (description) ID to description type ID mapping
			if (languageType) {
				Query<SnomedDescriptionIndexEntry> allDescriptionsQuery = Query.select(SnomedDescriptionIndexEntry.class)
						.where(Expressions.matchAll()).limit(Integer.MAX_VALUE).build();
				Hits<SnomedDescriptionIndexEntry> allDescriptionsHits;
				allDescriptionsHits = revisionSearcher.search(allDescriptionsQuery);

				for (SnomedDescriptionIndexEntry snomedDescriptionIndexEntry : allDescriptionsHits) {
					descriptionIdTypeMap.put(Long.parseLong(snomedDescriptionIndexEntry.getId()), Long.parseLong(snomedDescriptionIndexEntry.getTypeId()));
				}
			}
			// we need every target, limit needs to be set as the default is 50
			// hits
			Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class)
					.where(Expressions.matchAll()).limit(Integer.MAX_VALUE).build();
			Hits<SnomedRefSetMemberIndexEntry> hits = revisionSearcher.search(query);

			Set<ReferencedComponentIdStatus> referencedComponentIdStatuses = Sets.newHashSet();

			for (SnomedRefSetMemberIndexEntry snomedRefSetMemberIndexEntry : hits) {

				final ReferencedComponentIdStatus referencedComponentIdStatus = new ReferencedComponentIdStatus();
				referencedComponentIdStatus.referencedComponentId = snomedRefSetMemberIndexEntry.getReferencedComponentId();

				if (!languageType) {
					referencedComponentIdStatus.status = snomedRefSetMemberIndexEntry.isActive() ? "1" : "0";
				} else {
					final String acceptabilityId = snomedRefSetMemberIndexEntry.getAcceptabilityId();

					if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(acceptabilityId)) {

						// if the referenced component was an FSN, it cannot be
						// preferred
						if (Concepts.FULLY_SPECIFIED_NAME.equals(descriptionIdTypeMap.get(Long.parseLong(referencedComponentIdStatus.referencedComponentId)))) {
							referencedComponentIdStatus.status = "3"; // non preferred FSN code
						} else {
							referencedComponentIdStatus.status = "1"; // preferred member is always 1
						}
					} else {
						final String descriptionType = mapper
								.getDescriptionType(Long.toString(descriptionIdTypeMap.get(Long.parseLong(referencedComponentIdStatus.referencedComponentId))));
						referencedComponentIdStatus.status = null == descriptionType ? "0" : descriptionType;
					}
				}
				referencedComponentIdStatuses.add(referencedComponentIdStatus);
			}
			return referencedComponentIdStatuses;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static final class ReferencedComponentIdStatus {
		private String status;
		private String referencedComponentId;
	}

	@Override
	public String[] getColumnHeaders() {
		return RF1_SUBSET_MEMBERS_HEADER;
	}

	@Override
	public String getFileName() {
		return new StringBuilder("der1_SubsetMembers_").append(isLanguageType(getRefSetId()) ? getLanguageCode(getRefSetId()) : getFolderName()).append("_INT_")
				.append(Dates.formatByHostTimeZone(new Date(), DateFormats.SHORT)).append(".txt").toString();
	}

	/**
	 * Returns the number of (distinct) effective time values as a version
	 * number found during the export.
	 */
	public int getVersion() {
		return distinctEffectiveTimeSet.size();
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.REF_SET;
	}

	@Override
	public boolean hasNext() {
		return itr.hasNext();
	}

	@Override
	public String next() {
		return itr.next();
	}

	@Override
	public Iterator<String> iterator() {
		return itr;
	}
	
}