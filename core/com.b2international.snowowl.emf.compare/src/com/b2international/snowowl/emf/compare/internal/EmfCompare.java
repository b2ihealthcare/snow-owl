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
package com.b2international.snowowl.emf.compare.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine.Factory;
import org.eclipse.emf.compare.match.IMatchEngine.Factory.Registry;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.scope.FilterComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.eclipse.emf.compare.utils.IEqualityHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.commons.emf.NsUriProvider;
import com.google.common.base.Function;
import com.google.common.cache.LoadingCache;

/**
 * Class for executing the EMF based comparison for two given {@link Notifier}s. 
 */
public class EmfCompare {

	private static final Function<EObject, String> GET_CDO_ID_FUNCTION = new Function<EObject, String>() {
		@Override public String apply(final EObject input) {
			return ((CDOObject) input).cdoID().toString();
		}
	};

	/**
	 * Creates a comparison for the two notifiers.
	 * @param left the notifier.
	 * @param right the other notifier.
	 * @return the comparison.
	 */
	public static Comparison createComparison(final Notifier left, final Notifier right, final Collection<EStructuralFeature> excludedFeatures) {
		return createComparison(left, right, excludedFeatures, null);
	}
	
	/**
	 * Creates a comparison for the two notifiers.
	 * @param left the notifier.
	 * @param right the other notifier.
	 * @param excludedFeatures the features to exclude from the comparison.
	 * @param provider namespace URI provider for the EMF compare.
	 * @return the comparison.
	 */
	public static Comparison createComparison(final Notifier left, final Notifier right, final Collection<EStructuralFeature> excludedFeatures, final NsUriProvider provider) {

		checkNotNull(left, "left");
		checkNotNull(right, "right");
		checkState(left.getClass() == right.getClass(), "Left and right should belong to the same class. Left was: " + left + " and right was: " + right + ".");
		
		final Registry engineFactoryRegistry = new MatchEngineFactoryRegistryImpl();
		final IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory() {
			
			@Override
			public IEqualityHelper createEqualityHelper() {
				final LoadingCache<EObject, URI> cache = EqualityHelper.createDefaultCache(getCacheBuilder());
				final IEqualityHelper equalityHelper = new EqualityHelper(cache) {
					
					@Override
					@SuppressWarnings("deprecation")
					protected boolean matchingURIs(final EObject object1, final EObject object2) {
						
						if (object1 instanceof CDOObject && object2 instanceof CDOObject) {
							return ((CDOObject) object1).cdoID().equals(((CDOObject) object2).cdoID());
						}
						
						final URI uri1 = getURI(object1);
						final URI uri2 = getURI(object2);
						return uri1.hasFragment() && uri2.hasFragment() ? uri1.fragment().equals(uri2.fragment()) : uri1.equals(uri2);
					}
					
				};
				
				return equalityHelper;
			}
			
		});
		
		final IEObjectMatcher matcher = new IdentifierEObjectMatcher(GET_CDO_ID_FUNCTION);
		final Factory filter = new MatchEngineFactoryImpl(matcher, comparisonFactory);
		engineFactoryRegistry.add(filter);
		
		final IDiffEngine diffEngine = createDiffEngine(excludedFeatures);
		final EMFCompare compare = EMFCompare.builder().setMatchEngineFactoryRegistry(engineFactoryRegistry).setDiffEngine(diffEngine).build();
		final IComparisonScope scope = creatComparisonScope(left, right, provider);
		
		return compare.compare(scope);
		
	}

	private static DefaultDiffEngine createDiffEngine(final Collection<EStructuralFeature> excludedFeatures) {
		return new DefaultDiffEngine() {
			@Override
			protected FeatureFilter createFeatureFilter() {
				return new FeatureFilter() {

					@Override
					public boolean checkForOrderingChanges(final EStructuralFeature feature) {
						return false;
					}
					
					@Override
					protected boolean isIgnoredReference(final Match match, final EReference reference) {
						return excludedFeatures.contains(reference);
					}
					
				};
			}
		};
	}

	private static IComparisonScope creatComparisonScope(final Notifier left, final Notifier right, final NsUriProvider provider) {
		if (null == provider || NsUriProvider.NULL_IMPL.equals(provider)) {
			return new FilterComparisonScope(left, right, null);
		} else {
			return new ConstantComparisonScope(left, right, null, provider);
		}
	}
	
}