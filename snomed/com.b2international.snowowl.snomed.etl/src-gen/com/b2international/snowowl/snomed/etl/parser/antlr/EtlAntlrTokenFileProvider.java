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
<<<<<<< HEAD:snomed/com.b2international.snowowl.snomed.scg/src-gen/com/b2international/snowowl/snomed/scg/parser/antlr/ScgAntlrTokenFileProvider.java
package com.b2international.snowowl.snomed.scg.parser.antlr;
=======
package com.b2international.snowowl.snomed.etl.parser.antlr;
>>>>>>> 9e3749a0a8ea09c4ad5a3025876203f3c39437fa:snomed/com.b2international.snowowl.snomed.etl/src-gen/com/b2international/snowowl/snomed/etl/parser/antlr/EtlAntlrTokenFileProvider.java

import java.io.InputStream;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;

<<<<<<< HEAD:snomed/com.b2international.snowowl.snomed.scg/src-gen/com/b2international/snowowl/snomed/scg/parser/antlr/ScgAntlrTokenFileProvider.java
public class ScgAntlrTokenFileProvider implements IAntlrTokenFileProvider {
=======
public class EtlAntlrTokenFileProvider implements IAntlrTokenFileProvider {
>>>>>>> 9e3749a0a8ea09c4ad5a3025876203f3c39437fa:snomed/com.b2international.snowowl.snomed.etl/src-gen/com/b2international/snowowl/snomed/etl/parser/antlr/EtlAntlrTokenFileProvider.java

	@Override
	public InputStream getAntlrTokenFile() {
		ClassLoader classLoader = getClass().getClassLoader();
<<<<<<< HEAD:snomed/com.b2international.snowowl.snomed.scg/src-gen/com/b2international/snowowl/snomed/scg/parser/antlr/ScgAntlrTokenFileProvider.java
		return classLoader.getResourceAsStream("com/b2international/snowowl/snomed/scg/parser/antlr/internal/InternalScgParser.tokens");
=======
		return classLoader.getResourceAsStream("com/b2international/snowowl/snomed/etl/parser/antlr/internal/InternalEtlParser.tokens");
>>>>>>> 9e3749a0a8ea09c4ad5a3025876203f3c39437fa:snomed/com.b2international.snowowl.snomed.etl/src-gen/com/b2international/snowowl/snomed/etl/parser/antlr/EtlAntlrTokenFileProvider.java
	}
}
