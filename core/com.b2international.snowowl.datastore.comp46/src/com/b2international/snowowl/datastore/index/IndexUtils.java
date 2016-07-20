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
package com.b2international.snowowl.datastore.index;

import static com.b2international.commons.collect.LongSets.parallelForEach;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import com.b2international.collections.PrimitiveLists;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.compat.Highlighting;
import com.b2international.index.compat.TextConstants;
import com.b2international.index.lucene.DocIdCollector.DocIds;
import com.b2international.index.lucene.DocIdCollector.DocIdsIterator;
import com.b2international.index.lucene.LongIndexField;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.api.index.IndexException;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Ints;

/**
 * Contains utility methods for easier handling of Lucene's various aspects.
 */
public abstract class IndexUtils {

	/**Transforms an {@link IndexableField} into the stored string value.*/
	public static final Function<IndexableField, String> TO_STRING_VALUE_FUNC = new Function<IndexableField, String>() {
		@Override public String apply(final IndexableField field) {
			return checkNotNull(field, "field").stringValue();
		}
	};
	
	/**
	 * User data entry for storing the child branch path.
	 * 
	 * @see IndexCommit
	 * @see IndexWriter#setCommitData(java.util.Map)
	 */
	public static final String INDEX_BRANCH_PATH_KEY = "branchPath";

	/**
	 * User data entry for storing the base timestamp of the child branch.
	 * 
	 * @see IndexCommit
	 * @see IndexWriter#setCommitData(java.util.Map)
	 */
	public static final String INDEX_BASE_TIMESTAMP_KEY = "baseTimestamp";

	/**
	 * User data entry for storing the CDO branch ID sequence, corresponding to the child branch path.
	 * 
	 * @see IndexCommit
	 * @see IndexWriter#setCommitData(java.util.Map)
	 */
	public static final String INDEX_CDO_BRANCH_PATH_KEY = "cdoBranchPath";
	
	/**
	 * The default sort criteria which sorts by relevancy and then by sort key.
	 */
	public static final Sort DEFAULT_SORT = new Sort(SortField.FIELD_SCORE, new SortField(CommonIndexConstants.COMPONENT_LABEL_SORT_KEY, SortField.Type.STRING));
	
	/**
	 * Default score for a document.
	 * <br>Value: {@value}.
	 */
	public static final float DEFAULT_SCORE = 0.0F;

	/**
	 * A {@link FieldType} for long fields which shouldn't be indexed as multiple terms with decreasing
	 * numeric precision. Fields with this type are <b>not stored</b>.
	 */
	public static final FieldType TYPE_PRECISE_LONG_NOT_STORED = createFieldType(FieldType.NumericType.LONG, false);

	/**
	 * A {@link FieldType} for long fields which shouldn't be indexed as multiple terms with decreasing
	 * numeric precision. Fields with this type are <b>stored</b>.
	 */
	public static final FieldType TYPE_PRECISE_LONG_STORED = createFieldType(FieldType.NumericType.LONG, true);

	/**
	 * A {@link FieldType} for integer fields which shouldn't be indexed as multiple terms with decreasing
	 * numeric precision. Fields with this type are <b>stored</b>.
	 */
	public static final FieldType TYPE_PRECISE_INT_STORED = createFieldType(FieldType.NumericType.INT, true);
	
	private static FieldType createFieldType(final FieldType.NumericType numericType, final boolean stored) {

		final FieldType result = new FieldType();
		
		result.setTokenized(true);
		result.setOmitNorms(true);
		result.setIndexOptions(org.apache.lucene.index.IndexOptions.DOCS);
		result.setNumericType(numericType);
		result.setNumericPrecisionStep(Integer.MAX_VALUE);
		result.setStored(stored);
		result.freeze();
		
		return result;
	}
	
	public static final Function<BytesRef, Long> LONG_CONVERTER = new Function<BytesRef, Long>() { @Override public Long apply(final BytesRef input) {
		return NumericUtils.prefixCodedToLong(input);
	}};
	
	public static final Function<BytesRef, String> STRING_CONVERTER = new Function<BytesRef, String>() { @Override public String apply(final BytesRef input) {
		return input.utf8ToString();
	}};

	/**
	 * Creates a field with the string value of {@code '1'} if {@code value} is
	 * {@code true}, {@code '0'} otherwise. The value will be stored in the
	 * index, but not analyzed.
	 * 
	 * @param name the name of the new field (may not be {@code null})
	 * @param value the boolean value to store
	 * @return the populated {@link Field} instance
	 */
	@Deprecated
	public static @Nonnull Field createBooleanField(final @Nonnull String name, final boolean value) {
		return new StringField(name, value ? "1" : "0", Store.YES);
	}
	
	/**Returns {@code true} if the {@link TopDocs} argument is either {@code null} or the {@link ScoreDoc} is empty or {@code null}.
	 *Otherwise returns with {@code false}.*/
	public static boolean isEmpty(final TopDocs docs) {
		return null == docs || CompareUtils.isEmpty(docs.scoreDocs);
	}
	
	@Deprecated
	public static boolean getBooleanValue(final @Nonnull IndexableField fieldable) {
		checkNotNull(fieldable, "Field must not be null.");
		final Number numericValue = fieldable.numericValue();
		if (null == numericValue) {
			return "1".equals(fieldable.stringValue());
		} else {
			final int intValue = getIntValue(fieldable);
			if (intValue == 0) {
				return false;
			} else if (intValue == 1) {
				return true;
			} else {
				throw new IllegalArgumentException("Unexpected numeric field value: " + intValue);
			}
		}
	}

	@Deprecated
	public static long getLongValue(final @Nonnull IndexableField fieldable) {
		return getNumber(fieldable).longValue();
	}

	@Deprecated
	public static short getShortValue(final @Nonnull IndexableField fieldable) {
		return getNumber(fieldable).shortValue();
	}
	
	@Deprecated
	public static int getIntValue(final @Nonnull IndexableField fieldable) {
		return getNumber(fieldable).intValue();
	}

	@Deprecated
	public static float getFloatValue(final @Nonnull IndexableField fieldable) {
		return getNumber(fieldable).floatValue();
	}

	@Deprecated
	private static Number getNumber(final @Nonnull IndexableField fieldable) {
		return fieldable.numericValue();
	}
	
	private IndexUtils() {
		// Suppress instantiation
	}

	/**
	 * Computes a sort key for the specified concept label. The returned sort key will have characters with diacriticals
	 * (eg. &auml;&ouml;&uuml;) replaced with their plain counterparts (eg. aou); all delimiter characters given in
	 * {@value DELIMITERS} will also be removed. Sort keys are used when sorting the results returned from
	 * the index.
	 * 
	 * @param label the label to create a sort key for (may not be {@code null})
	 * @return the transformed sort key
	 */
	@Deprecated
	public static String getSortKey(final @Nonnull String label) {
		final String labelWithoutDiacriticals = StringUtils.removeDiacriticals(label);
		// whitespace characters can be kept
		final String labelWithoutDiacriticalsAndTermSeparators = TextConstants.DELIMITER_MATCHER.replaceFrom(labelWithoutDiacriticals, ' '); 
		return labelWithoutDiacriticalsAndTermSeparators;
	}
	
	@Deprecated
	public static Collection<BytesRef> longToPrefixCoded(final Collection<String> values) {
		return Collections2.transform(values, new Function<String, BytesRef>() {
			@Override public BytesRef apply(final String input) {
				return IndexUtils.longToPrefixCoded(input);
			}
		});
	}

	// TODO: remove method
	@Deprecated
	public static BytesRef longToPrefixCoded(final String value) {
		if (StringUtils.isEmpty(value)) {
			return new BytesRef();
		} else {
			try {
				return LongIndexField._toBytesRef(Long.valueOf(value));
			} catch (final NumberFormatException e) {
				return new BytesRef();
			}
		}
	}
	
	/**
	 * Comparator for ordering {@link AtomicReaderContext}s based on their ordinal.
	 */
	public static final class AtomicReaderContextComparator implements Comparator<LeafReaderContext> {
		
		@Override public int compare(final LeafReaderContext arc1, final LeafReaderContext arc2) {
			return arc1.ord - arc2.ord;
		}
		
	}

	/**
	 * Returns an {@link Optional} wrapping the parsed long value, or {@link Optional#absent()} if the string could not be parsed.
	 * 
	 * @param string the string to parse as a long
	 * @return an {@link Optional} wrapping the parsed long value
	 */
	public static Optional<Long> parseLong(final String string) {
		try {
			return Optional.of(Long.parseLong(string));
		} catch (final NumberFormatException e) {
			return Optional.absent();
		}
	}
	
	/**
	 * Representation of a document ID procedure. Clients may implement this interface when 
	 * would like to resolve a document from the document ID in parallel fashion via 
	 * {@link IndexUtils#parallelForEachDocId(DocIds, DocIdProcedure)}
	 *
	 */
	public static interface DocIdProcedure {
		/**Applies the current procedure on the document ID.*/
		void apply(int docId) throws IOException;
	}
	
	/**
	 * Processes the collected document IDs in parallel fashion with the procedure argument.
	 * @param docIds the document IDs to resolve into a document.
	 * @param procedure the procedure that is used to resolve the document IDs into document.
	 * @throws IOException
	 */
	public static void parallelForEachDocId(final DocIds docIds, final DocIdProcedure procedure) throws IOException {
		
		checkNotNull(docIds, "docIds");
		checkNotNull(procedure, "procedure");
		
		final long[] ids = new long[docIds.size()];
		final DocIdsIterator itr = docIds.iterator();
		int i = 0;
		
		while (itr.next()) {
			ids[i++] = itr.getDocID();
		}

		parallelForEach(PrimitiveLists.newLongArrayList(ids), new LongSets.LongCollectionProcedure() {
			@Override
			public void apply(final long docId) {
				try {
					procedure.apply(Ints.checkedCast(docId));
				} catch (final IOException e) {
					throw new IndexException("Error while applying document ID.", e);
				}
			}
		});
	}

	public static int[][] getMatchRegions(String queryExpression, String label) {
		return Highlighting.getMatchRegions(queryExpression, getSortKey(label.toLowerCase(Locale.ENGLISH)));
	}

	public static String[] getSuffixes(String queryExpression, String label) {
		return Highlighting.getSuffixes(queryExpression, label);
	}
}
