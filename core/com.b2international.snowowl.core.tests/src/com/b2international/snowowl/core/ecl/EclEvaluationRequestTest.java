/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.ecl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.revision.Revision;
import com.b2international.snomed.ecl.EclStandaloneSetup;
import com.b2international.snomed.ecl.validation.EclValidator;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.ecl.EclEvaluationRequest;
import com.google.inject.Injector;

/**
 * @since 8.2.0
 */
public class EclEvaluationRequestTest {

	private static final Injector INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
	
	private static class IdOnlyEclEvaluationRequest extends EclEvaluationRequest<ServiceProvider> {

		@Override
		protected Expression parentsExpression(Set<String> ids) {
			return throwUnsupported("Unable to provide parentsExpression for ID Set: " + ids);
		}

		@Override
		protected Expression ancestorsExpression(Set<String> ids) {
			return throwUnsupported("Unable to provide ancestorsExpression for ID Set: " + ids);
		}
		
		@Override
		protected Class<?> getDocumentType() {
			return throwUnsupported("Unable to provide document type");
		}
		
	}

	private ServiceProvider context;
	
	@Before
	public void before() {
		this.context = ServiceProvider.EMPTY.inject()
				.bind(EclParser.class, new DefaultEclParser(INJECTOR.getInstance(IParser.class), INJECTOR.getInstance(IResourceValidator.class)))
				.bind(EclSerializer.class, new DefaultEclSerializer(INJECTOR.getInstance(ISerializer.class)))
				.build();
	}
	
	@Test
	public void ignoreSctIdSyntaxError() throws Exception {
		String nonSnomedIdentifier = UUID.randomUUID().toString();
		
		// using a UUID as ID, should parse successfully without errors when ignoring syntax errors
		var req = new IdOnlyEclEvaluationRequest();
		req.setIgnoredSyntaxErrorCodes(Set.of(EclValidator.SCTID_ERROR_CODE));
		req.setExpression(nonSnomedIdentifier);
		
		assertThat(req.execute(context).getSync()).isEqualTo(Expressions.exactMatch(Revision.Fields.ID, nonSnomedIdentifier));
	}
	
}
