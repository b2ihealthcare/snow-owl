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
package com.b2international.snowowl.snomed.reasoner.net4j;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyFormat;

/**
 * Enumerates available OWL2 output formats.
 * 
 */
public enum SnomedOntologyExportType {
	
	/** OWL Functional syntax */
	FUNCTIONAL(new OWLFunctionalSyntaxOntologyFormat()),
	
	/** Manchester OWL syntax */
	MANCHESTER(new ManchesterOWLSyntaxOntologyFormat()),
	
	/** OWL/XML syntax */
	XML(new OWLXMLOntologyFormat());

	private final OWLOntologyFormat format;

	private SnomedOntologyExportType(final OWLOntologyFormat format) {
		this.format = format;
	}
	
	/**
	 * @return the wrapped {@link OWLOntologyFormat}
	 */
	public OWLOntologyFormat getFormat() {
		return format;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return format.toString();
	}
}