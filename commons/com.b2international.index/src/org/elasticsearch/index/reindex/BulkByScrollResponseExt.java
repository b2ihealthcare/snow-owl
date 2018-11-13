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

import static org.elasticsearch.common.xcontent.XContentParserUtils.ensureExpectedToken;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentParser.Token;
import org.elasticsearch.index.reindex.ScrollableHitSource.SearchFailure;
import org.elasticsearch.rest.RestStatus;

public class BulkByScrollResponseExt {
	
	private static final String TOOK_FIELD = "took";
    private static final String TIMED_OUT_FIELD = "timed_out";
    private static final String FAILURES_FIELD = "failures";
    
    // SearchFailure fields
    private static final String SEARCH_FAILURE_REASON_FIELD = "reason";
    private static final String SEARCH_FAILURE_NODE_FIELD = "node";
    private static final String SEARCH_FAILURE_SHARD_FIELD = "shard";
    
    // Failure fields, they are there but not public
    private static final String FAILURE_INDEX_FIELD = "index";
    private static final String FAILURE_CAUSE_FIELD = "cause";
    private static final String FAILURE_TYPE_FIELD = "type";
    private static final String FAILURE_ID_FIELD = "id";
    private static final String FAILURE_STATUS_FIELD = "status";
	
    private static final ObjectParser<BulkByScrollResponseBuilder, Void> PARSER =
        new ObjectParser<>(
            "bulk_by_scroll_response",
            true,
            BulkByScrollResponseBuilder::new
        );
    static {
        PARSER.declareLong(BulkByScrollResponseBuilder::setTook, new ParseField(TOOK_FIELD));
        PARSER.declareBoolean(BulkByScrollResponseBuilder::setTimedOut, new ParseField(TIMED_OUT_FIELD));
        PARSER.declareObjectArray(
            BulkByScrollResponseBuilder::setFailures, (p, c) -> parseFailure(p), new ParseField(FAILURES_FIELD)
        );
        // since the result of BulkByScrollResponse.Status are mixed we also parse that in this
        StatusExt.declareFields(PARSER);
    }
    
    public static BulkByScrollResponse fromXContent(XContentParser parser) {
        return PARSER.apply(parser, null).buildResponse();
    }

    private static Object parseFailure(XContentParser parser) throws IOException {
       ensureExpectedToken(Token.START_OBJECT, parser.currentToken(), parser::getTokenLocation);
       Token token;
       String index = null;
       String type = null;
       String id = null;
       Integer status = null;
       Integer shardId = null;
       String nodeId = null;
       ElasticsearchException bulkExc = null;
       ElasticsearchException searchExc = null;
       while ((token = parser.nextToken()) != Token.END_OBJECT) {
           ensureExpectedToken(Token.FIELD_NAME, token, parser::getTokenLocation);
           String name = parser.currentName();
           token = parser.nextToken();
           if (token == Token.START_ARRAY) {
               parser.skipChildren();
           } else if (token == Token.START_OBJECT) {
               switch (name) {
                   case SEARCH_FAILURE_REASON_FIELD:
                       bulkExc = ElasticsearchException.fromXContent(parser);
                       break;
                   case FAILURE_CAUSE_FIELD:
                       searchExc = ElasticsearchException.fromXContent(parser);
                       break;
                   default:
                       parser.skipChildren();
               }
           } else if (token == Token.VALUE_STRING) {
               switch (name) {
                   // This field is the same as SearchFailure.index
                   case FAILURE_INDEX_FIELD:
                       index = parser.text();
                       break;
                   case FAILURE_TYPE_FIELD:
                       type = parser.text();
                       break;
                   case FAILURE_ID_FIELD:
                       id = parser.text();
                       break;
                   case SEARCH_FAILURE_NODE_FIELD:
                       nodeId = parser.text();
                       break;
                   default:
                       // Do nothing
                       break;
               }
           } else if (token == Token.VALUE_NUMBER) {
               switch (name) {
                   case FAILURE_STATUS_FIELD:
                       status = parser.intValue();
                       break;
                   case SEARCH_FAILURE_SHARD_FIELD:
                       shardId = parser.intValue();
                       break;
                   default:
                       // Do nothing
                       break;
               }
           }
       }
       if (bulkExc != null) {
           return new Failure(index, type, id, bulkExc, RestStatus.fromCode(status));
       } else if (searchExc != null) {
           return new SearchFailure(searchExc, index, shardId, nodeId);
       } else {
           throw new ElasticsearchParseException("failed to parse failures array. At least one of {reason,cause} must be present");
       }
    }
	
}
