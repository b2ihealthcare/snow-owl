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
package com.b2international.snowowl.datastore.server.snomed;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.index.compat.Highlighting;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.quicksearch.FullQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.AbstractQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.datastore.DataTypeUtils;
import com.b2international.snowowl.snomed.datastore.quicksearch.DataTypeLabelQuickSearchProvider;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * Quick search content provider for data type labels.
 * 
 * For the time being, this implementation filters the labels using String.contains().
 * 
 */
public class DataTypeLabelQuickSearchContentProvider extends AbstractQuickSearchContentProvider implements IQuickSearchContentProvider {

	private static final class DataTypeLabelToQuickSearchElementFunction implements Function<String, QuickSearchElement> {
		private String queryExpression;
		private String terminologyComponentId;

		public DataTypeLabelToQuickSearchElementFunction(String queryExpression, String terminologyComponentId) {
			this.queryExpression = queryExpression;
			this.terminologyComponentId = terminologyComponentId;
		}

		@Override
		public QuickSearchElement apply(String input) {
			// TODO: Add an icon ID for each reference set, reflecting the data type (#, abc, calendar, #.##, checkbox?)
			final String label = DataTypeUtils.getDataTypePredicateLabel(input, Collections.<SnomedConstraintDocument>emptyList());

			return new FullQuickSearchElement(
					input, 
					Concepts.ROOT_CONCEPT, 
					label, 
					false, 
					terminologyComponentId,
					Highlighting.getMatchRegions(queryExpression, label),
					Highlighting.getSuffixes(queryExpression, label));
		}
	};

	@Override
	public QuickSearchContentResult getComponents(final String queryExpression, IBranchPathMap branchPathMap, int limit, Map<String, Object> configuration) {
		final IBranchPath branchPath = getBranchPath(branchPathMap);
		final Object object = configuration.get(DataTypeLabelQuickSearchProvider.CONFIGURATION_DATA_TYPE);
		final List<QuickSearchElement> elements;

		if (object instanceof DataType) {
			final DataType dataType = (DataType) object;
			elements = getElementsForDataType(queryExpression, branchPath, dataType);
		} else {
			elements = FluentIterable
					.from(ImmutableList.copyOf(DataType.values()))
					.transformAndConcat(new Function<DataType, Iterable<QuickSearchElement>>() {
						@Override
						public Iterable<QuickSearchElement> apply(DataType input) {
							return getElementsForDataType(queryExpression, branchPath, input);
						}
					})
					.toList();
		}

		return new QuickSearchContentResult(elements.size(), elements);
	}

	private ImmutableList<QuickSearchElement> getElementsForDataType(final String queryExpression, final IBranchPath branchPath, final DataType dataType) {
		return getFilteredDataTypeLabelSet(queryExpression, branchPath, dataType)
				.transform(new DataTypeLabelToQuickSearchElementFunction(queryExpression, getTerminologyComponentId(dataType)))
				.toList();
	}

	private String getTerminologyComponentId(DataType dataType) {
		switch (dataType) {
		case BOOLEAN:
			return SnomedTerminologyComponentConstants.DATA_TYPE_BOOLEAN;
		case DATE:
			return SnomedTerminologyComponentConstants.DATA_TYPE_DATE;
		case DECIMAL:
			return SnomedTerminologyComponentConstants.DATA_TYPE_DECIMAL;
		case INTEGER:
			return SnomedTerminologyComponentConstants.DATA_TYPE_INTEGER;
		case STRING:
			return SnomedTerminologyComponentConstants.DATA_TYPE_STRING;
		default:
			throw new IllegalArgumentException("Unexpected data type: " + dataType);
		}
	}

	private FluentIterable<String> getFilteredDataTypeLabelSet(final String queryExpression, IBranchPath branchPath, DataType dataType) {
		final SnomedConstraints dataTypeConstraints = SnomedRequests.prepareSearchConstraint()
				.all()
				.filterByType(SnomedConstraintDocument.PredicateType.DATATYPE)
				.build(branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
		
		// TODO support filtering of constraints by datatype
		final Multimap<DataType, String> labelsByType = HashMultimap.create();
		for (SnomedConstraintDocument constraint : dataTypeConstraints) {
			labelsByType.put(constraint.getDataType(), constraint.getDataTypeLabel());
		}

		return FluentIterable
				.from(labelsByType.get(dataType))
				.filter(new Predicate<String>() {
					@Override
					public boolean apply(String input) {
						return input.toLowerCase().contains(queryExpression.toLowerCase());
					}
				});
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}
