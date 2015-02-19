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
package com.b2international.snowowl.snomed.datastore.index;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.BytesRefFieldSource;
import org.apache.lucene.queries.function.valuesource.ConstValueSource;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.queries.function.valuesource.IfFunction;
import org.apache.lucene.queries.function.valuesource.LongFieldSource;
import org.apache.lucene.queries.function.valuesource.ProductFloatFunction;
import org.apache.lucene.queries.function.valuesource.SimpleBoolFunction;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfile;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfileDomain;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfileInterest;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfileRule;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter;
import com.google.common.base.CharMatcher;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;

public class SearchProfileValueSourceBuilder {

	private static final ConstValueSource ABOVE_AVERAGE_SCALE = new ConstValueSource(1.4f);
	private static final ConstValueSource AVERAGE_SCALE = new ConstValueSource(1.0f);
	private static final ConstValueSource BELOW_AVERAGE_SCALE = new ConstValueSource(0.6f);
	private static final ConstValueSource EXCLUDE_SCALE = new ConstValueSource(0.0f);
	
	/**
	 * A subclass of {@link ProductFloatFunction} which stops evaluating sources whenever the product drops to
	 * {@code 1e-5f}.
	 * 
	 */
	private static class LazyProductFloatFunction extends ProductFloatFunction {

		private static final float EPSILON = 1e-5f;
		
		private static final float MINUS_ONE = -1.0f;

		public LazyProductFloatFunction(final ValueSource[] sources) {
			super(sources);
		}

		@Override
		protected float func(final int doc, final FunctionValues[] valsArr) {
			float val = 1.0f;
			for (final FunctionValues vals : valsArr) {
				val *= vals.floatVal(doc);
				if (val < EPSILON) {
					return MINUS_ONE;
				}
			}
			return val;
		}
	}
	
	private static abstract class DomainBoolFunction extends SimpleBoolFunction {

		private final SearchProfileDomain domain;
		
		protected final String contextId;

		protected DomainBoolFunction(final ValueSource source, final SearchProfileDomain domain, final String contextId) {
			super(source);
			this.contextId = contextId;
			this.domain = domain;
		}

		@Override
		protected final String name() {
			return MessageFormat.format("{0} [{1}]", domain, contextId);
		}

		@Override
		protected final boolean func(final int doc, final FunctionValues vals) {
			return func(vals.strVal(doc));
		}

		protected abstract boolean func(final String conceptId);
	}
	
	private static class ApplicationContextSupplier<T> implements Supplier<T> {

		private final Class<T> clazz;
		
		public static <T> ApplicationContextSupplier<T> fromClass(final Class<T> clazz) {
			return new ApplicationContextSupplier<T>(clazz);
		}
		
		private ApplicationContextSupplier(final Class<T> clazz) {
			this.clazz = clazz;
		}
		
		@Override
		public T get() {
			return ApplicationContext.getInstance().getService(clazz);
		}
	}

	private final Supplier<SnomedTerminologyBrowser> terminologyBrowserSupplier = Suppliers.memoize(ApplicationContextSupplier.fromClass(SnomedTerminologyBrowser.class));
	
	private final Supplier<SnomedIndexService> indexServiceSupplier = Suppliers.memoize(ApplicationContextSupplier.fromClass(SnomedIndexService.class));
	
	private final IBranchPath branchPath;
	
	public SearchProfileValueSourceBuilder(final IBranchPath branchPath) {
		this.branchPath = branchPath;
	}

	public ValueSource buildProfileValueSource(final SearchProfile profile) {
		checkNotNull(profile, "profile");
		
		final Set<SearchProfileRule> rules = profile.getRules();
		final LongFieldSource idSource = new LongFieldSource(SnomedIndexBrowserConstants.COMPONENT_ID);
		final FloatFieldSource doiSource = new FloatFieldSource(SnomedIndexBrowserConstants.CONCEPT_DEGREE_OF_INTEREST);
		
		final List<ValueSource> sources = newArrayList();
	
		sources.add(doiSource); // start with the stored (absolute) interest value
		
		for (final SearchProfileRule rule : rules) {
			if (!SearchProfileInterest.AVERAGE.equals(rule.getInterest())) { // ignore average interest as it does not affect the results
				sources.add(buildRuleValueSource(rule, idSource));
			}
		}
		
		return new LazyProductFloatFunction((ValueSource[]) sources.toArray(new ValueSource[sources.size()]));
	}

	private ValueSource buildRuleValueSource(final SearchProfileRule rule, final LongFieldSource idSource) {
		return new IfFunction(buildRuleDomainValueSource(rule, idSource), buildRuleScaleFactorValueSource(rule), AVERAGE_SCALE);
	}

	private ValueSource buildRuleDomainValueSource(final SearchProfileRule rule, final LongFieldSource idSource) {
		
		final SearchProfileDomain domain = rule.getDomain();
		final String contextId = rule.getContextId();
		
		switch (domain) {
		case DESCENDANTS_OF_CONCEPT:
			return new DomainBoolFunction(idSource, domain, contextId) {
				@Override protected boolean func(final String conceptId) {
					final SnomedConceptIndexEntry context = terminologyBrowserSupplier.get().getConcept(branchPath, contextId);
					final Collection<SnomedConceptIndexEntry> superTypes = terminologyBrowserSupplier.get().getSuperTypesById(branchPath, conceptId);
					return superTypes.contains(context);
				}
			};
		case MAPPING_SOURCE_CONCEPTS:
		case REFERENCE_SET_MEMBERS:
			return new DomainBoolFunction(idSource, domain, contextId) {
				@Override protected boolean func(final String conceptId) {
					final SnomedRefSetMembershipIndexQueryAdapter query = SnomedRefSetMembershipIndexQueryAdapter.createFindByRefSetIdQuery(
							SnomedTerminologyComponentConstants.CONCEPT, 
							ImmutableSet.of(contextId), 
							ImmutableSet.of(conceptId));
					final int matchingMembers = indexServiceSupplier.get().getHitCount(branchPath, query);
					return matchingMembers > 0;
				}
			};
		case WITHIN_A_MODULE:
			return new DomainBoolFunction(idSource, domain, contextId) {
				@Override protected boolean func(final String conceptId) {
					final SnomedConceptIndexEntry concept = terminologyBrowserSupplier.get().getConcept(branchPath, conceptId);
					return contextId.equals(concept.getModuleId());
				}
			};
		case WITHIN_A_NAMESPACE:
			return new DomainBoolFunction(idSource, domain, contextId) {
				@Override protected boolean func(final String conceptId) {

					final SnomedConceptIndexEntry context = terminologyBrowserSupplier.get().getConcept(branchPath, contextId);
					if (null == context) return false;
					final SnomedConceptIndexEntry target = terminologyBrowserSupplier.get().getConcept(branchPath, conceptId);
					if (null == target) return false;
	
					final String expectedNamespace = CharMatcher.DIGIT.retainFrom(context.getLabel());
					return hasNamespace(expectedNamespace, target);
				}
				
				private boolean hasNamespace(final String expectedNamespace, final SnomedConceptIndexEntry target) {
					
					final String targetId = target.getId();
					final char idFormat = targetId.charAt(targetId.length() - 3);

					if ('0' == idFormat) { // Short format, no namespace part is present
						return expectedNamespace.isEmpty();
					} else {
						final String actualNamespace = targetId.substring(targetId.length() - 9, targetId.length() - 3);
						return actualNamespace.equals(expectedNamespace);
					}
				}
			};
		default:
			throw new IllegalStateException(MessageFormat.format("Unexpected search profile domain value ''{0}''.", domain));
		}
	}
	
	private ValueSource buildRuleScaleFactorValueSource(final SearchProfileRule rule) {
		
		switch (rule.getInterest()) {
			case ABOVE_AVERAGE:
				return ABOVE_AVERAGE_SCALE;
			case AVERAGE:
				return AVERAGE_SCALE;
			case BELOW_AVERAGE:
				return BELOW_AVERAGE_SCALE;
			case EXCLUDE:
				return EXCLUDE_SCALE;
			default:
				throw new IllegalStateException(MessageFormat.format("Unexpected search profile interest value ''{0}''.", rule.getInterest()));
		}
	}
}