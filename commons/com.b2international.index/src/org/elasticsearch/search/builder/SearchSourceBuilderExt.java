/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.search.builder;

import java.io.IOException;

import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent.Params;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.SearchExtBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder.IndexBoost;
import org.elasticsearch.search.builder.SearchSourceBuilder.ScriptField;
import org.elasticsearch.search.fetch.subphase.DocValueFieldsContext.FieldAndFormat;
import org.elasticsearch.search.internal.SearchContext;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.elasticsearch.search.sort.SortBuilder;

public class SearchSourceBuilderExt {
	
	private SearchSourceBuilderExt() {}
	
	public static XContentBuilder innerToXContent(SearchSourceBuilder source, XContentBuilder builder, Params params) throws IOException {
        if (source.from() != -1) {
            builder.field(SearchSourceBuilder.FROM_FIELD.getPreferredName(), source.from());
        }
        if (source.size() != -1) {
            builder.field(SearchSourceBuilder.SIZE_FIELD.getPreferredName(), source.size());
        }

        if (source.timeout() != null && !source.timeout().equals(TimeValue.MINUS_ONE)) {
            builder.field(SearchSourceBuilder.TIMEOUT_FIELD.getPreferredName(), source.timeout().getStringRep());
        }

        if (source.terminateAfter() != SearchContext.DEFAULT_TERMINATE_AFTER) {
            builder.field(SearchSourceBuilder.TERMINATE_AFTER_FIELD.getPreferredName(), source.terminateAfter());
        }

        if (source.query() != null) {
            builder.field(SearchSourceBuilder.QUERY_FIELD.getPreferredName(), source.query());
        }

        if (source.postFilter() != null) {
            builder.field(SearchSourceBuilder.POST_FILTER_FIELD.getPreferredName(), source.postFilter());
        }

        if (source.minScore() != null) {
            builder.field(SearchSourceBuilder.MIN_SCORE_FIELD.getPreferredName(), source.minScore());
        }

        if (source.version() != null) {
            builder.field(SearchSourceBuilder.VERSION_FIELD.getPreferredName(), source.version());
        }

        if (source.explain() != null) {
            builder.field(SearchSourceBuilder.EXPLAIN_FIELD.getPreferredName(), source.explain());
        }

        if (source.profile()) {
            builder.field("profile", true);
        }

        if (source.fetchSource() != null) {
            builder.field(SearchSourceBuilder._SOURCE_FIELD.getPreferredName(), source.fetchSource());
        }

        if (source.storedFields() != null) {
        	source.storedFields().toXContent(SearchSourceBuilder.STORED_FIELDS_FIELD.getPreferredName(), builder);
        }

        if (source.docValueFields() != null) {
            builder.startArray(SearchSourceBuilder.DOCVALUE_FIELDS_FIELD.getPreferredName());
            for (FieldAndFormat docValueField : source.docValueFields()) {
                builder.startObject()
                    .field("field", docValueField.field);
                if (docValueField.format != null) {
                    builder.field("format", docValueField.format);
                }
                builder.endObject();
            }
            builder.endArray();
        }

        if (source.scriptFields() != null) {
            builder.startObject(SearchSourceBuilder.SCRIPT_FIELDS_FIELD.getPreferredName());
            for (ScriptField scriptField : source.scriptFields()) {
                scriptField.toXContent(builder, params);
            }
            builder.endObject();
        }

        if (source.sorts() != null) {
            builder.startArray(SearchSourceBuilder.SORT_FIELD.getPreferredName());
            for (SortBuilder<?> sort : source.sorts()) {
                sort.toXContent(builder, params);
            }
            builder.endArray();
        }

        if (source.trackScores()) {
            builder.field(SearchSourceBuilder.TRACK_SCORES_FIELD.getPreferredName(), true);
        }

        if (source.trackTotalHits() == false) {
            builder.field(SearchSourceBuilder.TRACK_TOTAL_HITS_FIELD.getPreferredName(), false);
        }

        if (source.searchAfter() != null) {
            builder.array(SearchSourceBuilder.SEARCH_AFTER.getPreferredName(), source.searchAfter());
        }

        if (source.slice() != null) {
            builder.field(SearchSourceBuilder.SLICE.getPreferredName(), source.slice());
        }

        if (!source.indexBoosts().isEmpty()) {
            builder.startArray(SearchSourceBuilder.INDICES_BOOST_FIELD.getPreferredName());
            for (IndexBoost ib : source.indexBoosts()) {
                builder.startObject();
                builder.field(ib.getIndex(), ib.getBoost());
                builder.endObject();
            }
            builder.endArray();
        }

        if (source.aggregations() != null) {
            builder.field(SearchSourceBuilder.AGGREGATIONS_FIELD.getPreferredName(), source.aggregations());
        }

        if (source.highlighter() != null) {
            builder.field(SearchSourceBuilder.HIGHLIGHT_FIELD.getPreferredName(), source.highlighter());
        }

        if (source.suggest() != null) {
            builder.field(SearchSourceBuilder.SUGGEST_FIELD.getPreferredName(), source.suggest());
        }

        if (source.rescores() != null) {
            builder.startArray(SearchSourceBuilder.RESCORE_FIELD.getPreferredName());
            for (RescorerBuilder<?> rescoreBuilder : source.rescores()) {
                rescoreBuilder.toXContent(builder, params);
            }
            builder.endArray();
        }

        if (source.stats() != null) {
            builder.field(SearchSourceBuilder.STATS_FIELD.getPreferredName(), source.stats());
        }

        if (source.ext() != null && source.ext().isEmpty() == false) {
            builder.startObject(SearchSourceBuilder.EXT_FIELD.getPreferredName());
            for (SearchExtBuilder extBuilder : source.ext()) {
                extBuilder.toXContent(builder, params);
            }
            builder.endObject();
        }

        if (source.collapse() != null) {
            builder.field(SearchSourceBuilder.COLLAPSE.getPreferredName(), source.collapse());
        }
        return builder;
    }

	
}
