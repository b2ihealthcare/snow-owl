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
package com.b2international.snowowl.dsl;

import java.io.StringReader;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;

import com.b2international.snowowl.dsl.parser.antlr.SCGParser;
import com.google.inject.Injector;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 * <br><br>
 * FIXME don't use it:
 * http://www.eclipse.org/forums/index.php/t/280252/
 * we debugged for a while, injecting instead of instantiating is not an easy task, might be worth upgrading XText instead of wasting lot of time
 * with figuring out how to do that.
 * <br>
 * we use this: http://trac.rtsys.informatik.uni-kiel.de/trac/kieler/ticket/1577 but the exception occurs
 */
public class SCGStandaloneSetup extends SCGStandaloneSetupGenerated{

	private static SCGParser parser;

	public static void doSetup() {
		new SCGStandaloneSetup().createInjectorAndDoEMFRegistration();
	}

	public static SCGParser createSCGParser() {
		final Injector injector = new SCGStandaloneSetup().createInjectorAndDoEMFRegistration();
		return injector.getInstance(SCGParser.class);
	}	
	
	public static EObject parse(final String expression) {
		if (parser == null) {
			parser = createSCGParser();
		}

		final IParseResult parseResult = parser.parse(new StringReader(expression));
		if (parseResult.hasSyntaxErrors()) {
			final ParseException exception = new ParseException("Error in SCG expression");
			final Iterator<INode> itr = parseResult.getSyntaxErrors().iterator();
			while (itr.hasNext()) {
				exception.getErrors().add(itr.next().getSyntaxErrorMessage().getMessage());
			}
			throw exception;
		}

		return parseResult.getRootASTElement();
	}
}