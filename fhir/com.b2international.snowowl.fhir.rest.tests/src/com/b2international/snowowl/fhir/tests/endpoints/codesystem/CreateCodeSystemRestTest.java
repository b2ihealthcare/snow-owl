package com.b2international.snowowl.fhir.tests.endpoints.codesystem;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.tests.FhirRestTest;

public class CreateCodeSystemRestTest extends FhirRestTest {
	
	@Test
	public void createCodeSytem() throws Exception {
		
		
		URI uri = CreateCodeSystemRestTest.class.getResource("dd_codesystem.json").toURI();
		CodeSystem codeSystem = objectMapper.readValue(Paths.get(uri).toFile(), CodeSystem.class);
		
		System.out.println("Data dictionary code system loaded: " + codeSystem.getId());
	}

}
