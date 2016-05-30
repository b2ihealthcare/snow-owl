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
package com.b2international.snowowl.datastore.cdo;

import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.commons.collect.LongSets.InverseLongFunction;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Utility class for {@link CDOID} instances. 
 */
public abstract class CDOIDUtils {

	private CDOIDUtils() {
		//suppress initialization
	}
	
	/**Function for transforming a primitive long value into a CDO ID instance. Negative long values are not allowed.*/
	public static InverseLongFunction<CDOID> STORAGE_KEY_TO_CDO_ID_FUNCTION = new InverseLongFunction<CDOID>() {
		@Override public CDOID apply(final long input) {
			checkArgument(input > NO_STORAGE_KEY);
			return CDOIDUtil.createLong(input);
		}
	};
	
	private static final Function<CDOObject, CDOID> CDO_OBJECT_TO_ID_FUNCTION = new Function<CDOObject, CDOID>() {
		@Override public CDOID apply(final CDOObject object) {
			return object.cdoID();
		}
	};
	
	private static final Function<CDOIDAndVersion, CDOID> CDO_ID_AND_VERSION_TO_ID_FUNCTION = new Function<CDOIDAndVersion, CDOID>() {
		@Override public CDOID apply(final CDOIDAndVersion object) {
			return object.getID();
		}
	};
	
	private static final Function<Long, CDOID> LONG_TO_CDO_ID_FUNCTION = new Function<Long, CDOID>() {
		public CDOID apply(final Long id) {
			return CDOIDUtil.createLong(id.longValue());
		};
	};
	
	public static final Function<CDOID, Long> CDO_ID_TO_LONG_FUNCTION = new Function<CDOID, Long>() {
		public Long apply(final CDOID id) {
			return CDOIDUtils.asLong(id);
		};
	};

	/**
	 * Checks the state of the specified CDO ID argument and returns with it.
	 * @param cdoId the CDO ID to check.
	 * @return the CDO ID specified as the argument.
	 */
	public static CDOID checkCDOID(final CDOID cdoId) {
		Preconditions.checkNotNull(cdoId, "CDO ID argument cannot be null.");
		Preconditions.checkState(!cdoId.isDangling(), "CDO ID was dangling. CDO ID: " + cdoId);
		Preconditions.checkState(!cdoId.isTemporary(), "CDO ID was temporary. CDO ID: " + cdoId);
		return cdoId;
	}
	
	/**
	 * Returns with the value of the CDO ID as a string.
	 * <p>Specified CDO ID instance should not be either dangling or temporary.
	 * @param cdoId the CDO ID to convert into string.
	 * @return the value of the CDO ID as a string.
	 */
	public static String asString(final CDOID cdoId) {
		Preconditions.checkNotNull(cdoId, "CDO ID argument cannot be null.");
		return String.valueOf(CDOIDUtil.getLong(checkCDOID(cdoId)));
	}
	
	/**
	 * Returns with the value of the CDO ID as a long.
	 * <p>Specified CDO ID instance should not be either dangling or temporary.
	 * @param cdoId the CDO ID to convert into a long value.
	 * @return the long value of the CDO ID.
	 */
	public static long asLong(final CDOID cdoId) {
		Preconditions.checkNotNull(cdoId, "CDO ID argument cannot be null.");
		return CDOIDUtil.getLong(checkCDOID(cdoId));
	}
	
	/**
	 * Returns with the unique CDO ID as a long after extracting it from the specified {@link CDOID} instance.
	 * <br>Returns with {@code -1L} if the specified ID is a temporary.
	 * @param cdoId the CDO ID.
	 * @return the unique CDO ID value as long.
	 */
	public static long asLongSafe(final CDOID cdoId) {
		Preconditions.checkNotNull(cdoId, "CDO ID argument cannot be null.");
		return cdoId.isTemporary() ? -1L : CDOIDUtil.getLong(checkCDOID(cdoId));
	}
	
	/**
	 * Transforms a bunch of CDO ID represented as {@code long} into {@link CDOID} instances.
	 * @param ids the IDs to transform.
	 * @return and iterable of {@link CDOID CDO ID}s.
	 */
	public static Iterable<CDOID> createIds(final Iterable<Long> ids) {
		return Iterables.transform(ids, LONG_TO_CDO_ID_FUNCTION);
	}
	
	/**
	 * Transforms {@link CDOID} instances into a {@code long} represented form.
	 * 
	 * @param ids the IDs to transform
	 * @return iterable of {@code CDO IDs} as {@code long}
	 */
	public static Iterable<Long> createCdoIdToLong(final Iterable<CDOID> ids) {
		return Iterables.transform(ids, CDO_ID_TO_LONG_FUNCTION);
	}
	
	/**
	 * Returns with a {@link Function function} where the input type is a subclass of {@link CDOObject} 
	 * and the output is {@link CDOID}.
	 * @return a function that manages {@link CDOObject} to {@link CDOID} conversion.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends CDOObject> Function<T, CDOID> getObjectToIdFunction() {
		return (Function<T, CDOID>) CDO_OBJECT_TO_ID_FUNCTION;
	}
	
	/**
	 * Returns with a {@link Function function} where the input type is a subclass of {@link CDOIDAndVersion} 
	 * and the output is {@link CDOID}.
	 * @return a function that manages {@link CDOIDAndVersion} to {@link CDOID} conversion.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends CDOIDAndVersion> Function<T, CDOID> getIdAndVersionToIdFunction() {
		return (Function<T, CDOID>) CDO_ID_AND_VERSION_TO_ID_FUNCTION;
	}
	
	/**
	 * Returns with a collection of {@link CDOID} extracted from the {@link CDOObject}s.  
	 * @param objects {@link CDOObject} iterable.
	 * @return the extracted unique {@link CDOID}s.
	 */
	public static Collection<CDOID> getIds(final Iterable<? extends CDOObject> objects) {
		Preconditions.checkNotNull(objects, "Objects argument cannot be null.");
		return Lists.newArrayList(Iterables.transform(objects, getObjectToIdFunction()));
	}
	
	/**
	 * Returns with a collection of {@link CDOID} extracted from the {@link CDOIDAndVersion}s.  
	 * @param idAndVersions {@link CDOIDAndVersion} iterable.
	 * @return the extracted unique {@link CDOID}s.
	 */
	public static Collection<CDOID> extractIds(final Iterable<? extends CDOIDAndVersion> idAndVersions) {
		Preconditions.checkNotNull(idAndVersions, "CDOIDAndVersions argument cannot be null.");
		return Lists.newArrayList(Iterables.transform(idAndVersions, getIdAndVersionToIdFunction()));
	}
	
	/**
	 * Returns with a list of CDO IDs based on the given collection of primitive longs.
	 * @param ids a collection of longs.
	 * @return a list of CDO IDs.
	 */
	public static List<CDOID> getIds(final LongCollection ids) {
		Preconditions.checkNotNull(ids, "IDs argument cannot be null.");
		final List<CDOID> $ = Lists.newArrayList();
		for (final LongIterator itr = ids.iterator(); itr.hasNext(); /* */) {
			$.add(CDOIDUtil.createLong(itr.next()));
		}
		return $;
	}
	
	/**
	 * Returns {@code true} if the investigated <b>object</b>'S ID can be found among the <b>ids</b>.
	 * @param ids the iterable of {@link CDOID ID}s. 
	 * @param object the {@link CDOObject} we looking for by its ID.
	 * @return {@code true} is it can be found, otherwise it returns with {@code false}.
	 */ 
	public static <T extends CDOObject> boolean containsId(final Iterable<? extends CDOID> ids, final T object) {
		Preconditions.checkNotNull(ids, "IDs argument cannot be null.");
		Preconditions.checkNotNull(object, "Object argument cannot be null.");
		return Lists.newArrayList(ids).contains(object.cdoID());
	}
	
	/**
	 * Returns {@code true} if the investigated <b>object</b> can be found among the <b>objects</b>. Only and if only 
	 * {@code true} if the {@link CDOID}s are equal.
	 * @param objects the iterable of {@link CDOObject objects}. 
	 * @param object the {@link CDOObject} we looking for by its ID.
	 * @return {@code true} is it can be found, otherwise it returns with {@code false}.
	 */ 
	public static <T extends CDOObject> boolean containsById(final Iterable<? extends CDOObject> objects, final T object) {
		Preconditions.checkNotNull(objects, "Objects argument cannot be null.");
		Preconditions.checkNotNull(object, "Object argument cannot be null.");
		return getIds(objects).contains(object.cdoID());
	}
	
	/**
	 * Returns {@code true} if the CDO ID of the investigated {@link CDOObject objects} are equal. Otherwise returns with {@code false}.
	 * @param object1 the first CDO object.
	 * @param object2 the second CDO object.
	 * @return {@code true} if the CDO IDs are equal.
	 */
	public static <T1 extends CDOObject, T2 extends CDOObject> boolean eqaulsById(final T1 object1, final T2 object2) {
		Preconditions.checkNotNull(object1, "Object1 argument cannot be null.");
		Preconditions.checkNotNull(object2, "Object2 argument cannot be null.");
		return null == object1.cdoID() ? null == object2.cdoID() : object1.cdoID().equals(object2.cdoID());
	}
	
	/**
	 * Returns {@code true} if the CDO ID of the investigated objects are equal. Otherwise returns with {@code false}.
	 * @param object the CDO object.
	 * @param idAndVersion the ID and version instance.
	 * @return {@code true} if the CDO IDs are equal.
	 */
	public static <T1 extends CDOObject, T2 extends CDOIDAndVersion> boolean eqaulsById(final T1 object, final T2 idAndVersion) {
		Preconditions.checkNotNull(object, "Object argument cannot be null.");
		Preconditions.checkNotNull(idAndVersion, "IDAndVersion argument cannot be null.");
		return null == object.cdoID() ? null == idAndVersion.getID() : object.cdoID().equals(idAndVersion.getID());
	}
	
	/**
	 * Returns {@code true} if the CDO ID argument is a valid ID, otherwise {@code false}.
	 * <br>More formally, the value of the CDO ID is greater than {@code 0}.
	 * @param cdoId the ID to check.
	 * @return {@code true} if the ID is valid, otherwise {@code false}.
	 */
	public static boolean checkId(final long cdoId) {
		return 0 < cdoId;
	}
	
	/**
	 * Returns {@code true} if the CDO ID argument is a valid ID, otherwise {@code false}.
	 * <br>More formally, the long value of the CDO ID is greater than {@code 0} and the CDO ID is 
	 * not the {@link CDOID#NULL NULL ID} instance.
	 * @param cdoId the ID to check.
	 * @return {@code true} if the ID is valid, otherwise {@code false}.
	 */
	public static boolean checkId(final CDOID cdoId) {
		checkNotNull(cdoId, "cdoId");
		return !CDOID.NULL.equals(cdoId) && checkId(asLong(cdoId));
	}
	
}