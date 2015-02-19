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
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.quicksearch.FullQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.AbstractQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.DataTypeUtils;
import com.b2international.snowowl.snomed.datastore.quicksearch.DataTypeLabelQuickSearchProvider;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Quick search content provider for data type labels.
 * 
 * For the time being, this implementation filters the labels using String.contains().
 * 
 */
public class DataTypeLabelQuickSearchContentProvider extends AbstractQuickSearchContentProvider implements IQuickSearchContentProvider {

	private static final class DataTypeLabelToQuickSearchElementFunction implements Function<String, QuickSearchElement> {
		private String terminologyComponentId;

		public DataTypeLabelToQuickSearchElementFunction(String terminologyComponentId) {
			this.terminologyComponentId = terminologyComponentId;
		}

		@Override
		public QuickSearchElement apply(String input) {
			// TODO: icon ID?
			return new FullQuickSearchElement(input, Concepts.ROOT_CONCEPT, DataTypeUtils.getDataTypePredicateLabel(input, Collections.<PredicateIndexEntry>emptyList()), false, terminologyComponentId);
		}
	};
	
	@Override
	public QuickSearchContentResult getComponents(final String queryExpression, IBranchPathMap branchPathMap, int limit, Map<String, Object> configuration) {
		final IBranchPath branchPath = getBranchPath(branchPathMap);
		Object object = configuration.get(DataTypeLabelQuickSearchProvider.CONFIGURATION_DATA_TYPE);
		if (object instanceof DataType) {
			DataType dataType = (DataType) object;
			Set<String> filteredDataTypeLabels = getFilteredDataTypeLabelSet(queryExpression, branchPath, dataType);
			List<QuickSearchElement> list = Lists.newArrayList(Collections2.transform(filteredDataTypeLabels, new DataTypeLabelToQuickSearchElementFunction(getTerminologyComponentId(dataType))));
			return new QuickSearchContentResult(list.size(), list);
		} else {
			// if data type not specified, return labels for all data types
			ImmutableList.Builder<QuickSearchElement> resultBuilder = ImmutableList.<QuickSearchElement>builder();
			for (DataType dataType : DataType.class.getEnumConstants()) {
				Set<String> filteredDataTypeLabelSet = getFilteredDataTypeLabelSet(queryExpression, branchPath, dataType);
				resultBuilder.addAll(Collections2.transform(filteredDataTypeLabelSet, new DataTypeLabelToQuickSearchElementFunction(getTerminologyComponentId(dataType))));
			}
			ImmutableList<QuickSearchElement> list = resultBuilder.build();
			return new QuickSearchContentResult(list.size(), list);
		}
	}

	private String getTerminologyComponentId(DataType dataType) {
		switch (dataType) {
		case BOOLEAN:
			return SnomedTerminologyComponentConstants.DATA_TYPE_BOOLEAN;
		case DATE:
			return SnomedTerminologyComponentConstants.DATA_TYPE_DATE;
		case FLOAT:
			return SnomedTerminologyComponentConstants.DATA_TYPE_FLOAT;
		case INTEGER:
			return SnomedTerminologyComponentConstants.DATA_TYPE_INTEGER;
		case STRING:
			return SnomedTerminologyComponentConstants.DATA_TYPE_STRING;
		default:
			throw new IllegalArgumentException("Unexpected data type: " + dataType);
		}
	}
	
	private Set<String> getFilteredDataTypeLabelSet(final String queryExpression, IBranchPath branchPath, DataType dataType) {
		Set<String> availableDataTypeLabels = new SnomedComponentService().getAvailableDataTypeLabels(branchPath, dataType);
		Set<String> filteredDataTypeLabels = Sets.filter(availableDataTypeLabels, new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.toLowerCase().contains(queryExpression.toLowerCase());
			}
		});
		return filteredDataTypeLabels;
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}

}