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
package org.elasticsearch.index.reindex;

import static org.elasticsearch.common.xcontent.ConstructingObjectParser.constructorArg;
import static org.elasticsearch.common.xcontent.XContentParserUtils.ensureExpectedToken;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentParser.Token;
import org.elasticsearch.index.reindex.BulkByScrollTask.Status;

public class StatusExt {

	public static final String SLICE_ID_FIELD = "slice_id";
    public static final String TOTAL_FIELD = "total";
    public static final String UPDATED_FIELD = "updated";
    public static final String CREATED_FIELD = "created";
    public static final String DELETED_FIELD = "deleted";
    public static final String BATCHES_FIELD = "batches";
    public static final String VERSION_CONFLICTS_FIELD = "version_conflicts";
    public static final String NOOPS_FIELD = "noops";
    public static final String RETRIES_FIELD = "retries";
    public static final String RETRIES_BULK_FIELD = "bulk";
    public static final String RETRIES_SEARCH_FIELD = "search";
    public static final String THROTTLED_RAW_FIELD = "throttled_millis";
    public static final String THROTTLED_HR_FIELD = "throttled";
    public static final String REQUESTS_PER_SEC_FIELD = "requests_per_second";
    public static final String CANCELED_FIELD = "canceled";
    public static final String THROTTLED_UNTIL_RAW_FIELD = "throttled_until_millis";
    public static final String THROTTLED_UNTIL_HR_FIELD = "throttled_until";
    public static final String SLICES_FIELD = "slices";
    
    public static Set<String> FIELDS_SET = new HashSet<>();
    static {
        FIELDS_SET.add(SLICE_ID_FIELD);
        FIELDS_SET.add(TOTAL_FIELD);
        FIELDS_SET.add(UPDATED_FIELD);
        FIELDS_SET.add(CREATED_FIELD);
        FIELDS_SET.add(DELETED_FIELD);
        FIELDS_SET.add(BATCHES_FIELD);
        FIELDS_SET.add(VERSION_CONFLICTS_FIELD);
        FIELDS_SET.add(NOOPS_FIELD);
        FIELDS_SET.add(RETRIES_FIELD);
        // No need for inner level fields for retries in the set of outer level fields
        FIELDS_SET.add(THROTTLED_RAW_FIELD);
        FIELDS_SET.add(THROTTLED_HR_FIELD);
        FIELDS_SET.add(REQUESTS_PER_SEC_FIELD);
        FIELDS_SET.add(CANCELED_FIELD);
        FIELDS_SET.add(THROTTLED_UNTIL_RAW_FIELD);
        FIELDS_SET.add(THROTTLED_UNTIL_HR_FIELD);
        FIELDS_SET.add(SLICES_FIELD);
    }

	
	public static void declareFields(ObjectParser<? extends StatusBuilder, Void> parser) {
        parser.declareInt(StatusBuilder::setSliceId, new ParseField(SLICE_ID_FIELD));
        parser.declareLong(StatusBuilder::setTotal, new ParseField(TOTAL_FIELD));
        parser.declareLong(StatusBuilder::setUpdated, new ParseField(UPDATED_FIELD));
        parser.declareLong(StatusBuilder::setCreated, new ParseField(CREATED_FIELD));
        parser.declareLong(StatusBuilder::setDeleted, new ParseField(DELETED_FIELD));
        parser.declareInt(StatusBuilder::setBatches, new ParseField(BATCHES_FIELD));
        parser.declareLong(StatusBuilder::setVersionConflicts, new ParseField(VERSION_CONFLICTS_FIELD));
        parser.declareLong(StatusBuilder::setNoops, new ParseField(NOOPS_FIELD));
        parser.declareObject(StatusBuilder::setRetries, RETRIES_PARSER, new ParseField(RETRIES_FIELD));
        parser.declareLong(StatusBuilder::setThrottled, new ParseField(THROTTLED_RAW_FIELD));
        parser.declareFloat(StatusBuilder::setRequestsPerSecond, new ParseField(REQUESTS_PER_SEC_FIELD));
        parser.declareString(StatusBuilder::setReasonCancelled, new ParseField(CANCELED_FIELD));
        parser.declareLong(StatusBuilder::setThrottledUntil, new ParseField(THROTTLED_UNTIL_RAW_FIELD));
        parser.declareObjectArray(
            StatusBuilder::setSliceStatuses, (p, c) -> StatusOrExceptionExt.fromXContent(p), new ParseField(SLICES_FIELD)
        );
    }
	
	@SuppressWarnings("unchecked")
    static ConstructingObjectParser<Tuple<Long, Long>, Void> RETRIES_PARSER = new ConstructingObjectParser<>(
        "bulk_by_scroll_task_status_retries",
        true,
        a -> new Tuple(a[0], a[1])
    );
    static {
        RETRIES_PARSER.declareLong(constructorArg(), new ParseField(RETRIES_BULK_FIELD));
        RETRIES_PARSER.declareLong(constructorArg(), new ParseField(RETRIES_SEARCH_FIELD));
    }
    
    public static Status fromXContent(XContentParser parser) throws IOException {
        XContentParser.Token token;
        if (parser.currentToken() == Token.START_OBJECT) {
             token = parser.nextToken();
        } else {
            token = parser.nextToken();
        }
        ensureExpectedToken(Token.START_OBJECT, token, parser::getTokenLocation);
        token = parser.nextToken();
        ensureExpectedToken(Token.FIELD_NAME, token, parser::getTokenLocation);
        return innerFromXContent(parser);
    }

    public static Status innerFromXContent(XContentParser parser) throws IOException {
        Token token = parser.currentToken();
        String fieldName = parser.currentName();
        ensureExpectedToken(XContentParser.Token.FIELD_NAME, token, parser::getTokenLocation);
        StatusBuilder builder = new StatusBuilder();
        while ((token = parser.nextToken()) != Token.END_OBJECT) {
            if (token == Token.FIELD_NAME) {
                fieldName = parser.currentName();
            } else if (token == Token.START_OBJECT) {
                if (fieldName.equals(RETRIES_FIELD)) {
                    builder.setRetries(RETRIES_PARSER.parse(parser, null));
                } else {
                    parser.skipChildren();
                }
            } else if (token == Token.START_ARRAY) {
                if (fieldName.equals(SLICES_FIELD)) {
                    while ((token = parser.nextToken()) != Token.END_ARRAY) {
                        builder.addToSliceStatuses(StatusOrExceptionExt.fromXContent(parser));
                    }
                } else {
                    parser.skipChildren();
                }
            } else { // else if it is a value
                switch (fieldName) {
                    case SLICE_ID_FIELD:
                        builder.setSliceId(parser.intValue());
                        break;
                    case TOTAL_FIELD:
                        builder.setTotal(parser.longValue());
                        break;
                    case UPDATED_FIELD:
                        builder.setUpdated(parser.longValue());
                        break;
                    case CREATED_FIELD:
                        builder.setCreated(parser.longValue());
                        break;
                    case DELETED_FIELD:
                        builder.setDeleted(parser.longValue());
                        break;
                    case BATCHES_FIELD:
                        builder.setBatches(parser.intValue());
                        break;
                    case VERSION_CONFLICTS_FIELD:
                        builder.setVersionConflicts(parser.longValue());
                        break;
                    case NOOPS_FIELD:
                        builder.setNoops(parser.longValue());
                        break;
                    case THROTTLED_RAW_FIELD:
                        builder.setThrottled(parser.longValue());
                        break;
                    case REQUESTS_PER_SEC_FIELD:
                        builder.setRequestsPerSecond(parser.floatValue());
                        break;
                    case CANCELED_FIELD:
                        builder.setReasonCancelled(parser.text());
                        break;
                    case THROTTLED_UNTIL_RAW_FIELD:
                        builder.setThrottledUntil(parser.longValue());
                        break;
                    default:
                        break;
                }
            }
        }
        return builder.buildStatus();
    }
	
}
