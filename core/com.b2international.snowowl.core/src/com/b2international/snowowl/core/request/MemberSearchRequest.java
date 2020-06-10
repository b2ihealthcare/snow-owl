/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import java.io.IOException;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.SetMembers;
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
* @since 7.7
*/
public final class MemberSearchRequest extends SearchResourceRequest<BranchContext, SetMembers> {

	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected SetMembers createEmptyResult(int limit) {
		return new SetMembers(limit, 0);
	}
	
	@Override
	protected SetMembers doExecute(BranchContext context) throws IOException {
		Options options = Options.builder()
				.putAll(options())
				.put(SetMemberSearchRequestEvaluator.OptionKey.AFTER, searchAfter())
				.put(SetMemberSearchRequestEvaluator.OptionKey.LIMIT, limit())
				.build();
		
		return context.service(SetMemberSearchRequestEvaluator.class)
				.evaluate(context.service(CodeSystemURI.class), context, options);
	}
	
}
