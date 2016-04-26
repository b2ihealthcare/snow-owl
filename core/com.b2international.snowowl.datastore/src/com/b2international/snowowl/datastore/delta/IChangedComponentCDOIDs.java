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
package com.b2international.snowowl.datastore.delta;

import java.io.Serializable;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.google.common.base.Preconditions;

/**
 * Represents a change set with the {@link CDOID CDO ID} (as a long) of a changed component
 * and all related or associated changed component CDO IDs (as a {@link LongSet}.
 */
public interface IChangedComponentCDOIDs extends Serializable {

	/**Returns with the focus component {@link CDOID CDO ID} as a long.*/
	long getCdoId();
	
	/**Returns with a {@link LongSet set of long}s as the {@link CDOID CDO ID}s of the associated components.*/
	LongSet getRelatedCdoIds();
	
	public static final class Utils {

		/**
		 * Creates and returns with a new {@link IChangedComponentCDOIDs} instance where the
		 * {@link IChangedComponentCDOIDs#getCdoId()} is the given argument and the {@link 
		 * IChangedComponentCDOIDs#getRelatedCdoIds()}
		 * is a singleton set with the given CDO ID argument.
		 */
		public static IChangedComponentCDOIDs createSinglton(final long cdoId) {
			return new IChangedComponentCDOIDs() {
				private static final long serialVersionUID = -2778009020012073970L;
				@Override public LongSet getRelatedCdoIds() {
					return LongCollections.singletonSet(cdoId); 
				}
				@Override public long getCdoId() { return cdoId; }
			};
		}
		
		/**
		 * Creates and returns with {@link IChangedComponentCDOIDs} initialized with the focus component 
		 * CDO ID and the related component CDO IDs arguments.
		 */
		public static IChangedComponentCDOIDs create(final long cdoId, final LongSet relatedCdoIds) {
			
			if (CompareUtils.isEmpty(Preconditions.checkNotNull(relatedCdoIds))) {
				return createSinglton(cdoId);
			}
			
			return new IChangedComponentCDOIDs() {
				private static final long serialVersionUID = 6133179031856682715L;
				@Override public LongSet getRelatedCdoIds() {
					final LongSet $ = PrimitiveSets.newLongOpenHashSet();
					$.addAll(relatedCdoIds);
					$.add(cdoId);
					return LongCollections.unmodifiableSet($); 
				}
				@Override public long getCdoId() { return cdoId; }
			};
		}
		
	}
	
}