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

import com.b2international.snowowl.dsl.parser.antlr.ESCGParser;

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
public class ESCGStandaloneSetup extends ESCGStandaloneSetupGenerated {

	private static ESCGStandaloneSetup instance;
	
	public static ESCGStandaloneSetup getInstance() {
		if (null == instance) {
			synchronized (ESCGStandaloneSetup.class) {
				if (null == instance) {
					instance = new ESCGStandaloneSetup(); 
				}
			}
		}
		return instance;
	}

	private ESCGParser parser;
	
	private ESCGStandaloneSetup() {
		parser = createInjectorAndDoEMFRegistration().getInstance(ESCGParser.class); 
	}
	
	public ESCGParser getParser() {
		return parser;
	}
}