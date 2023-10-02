package org.linuxforhealth.fhir;

import java.io.InputStream;

import org.linuxforhealth.fhir.model.format.Format;
import org.linuxforhealth.fhir.model.generator.exception.FHIRGeneratorException;
import org.linuxforhealth.fhir.model.parser.exception.FHIRParserException;
import org.linuxforhealth.fhir.model.r5.generator.FHIRGenerator;
import org.linuxforhealth.fhir.model.r5.parser.FHIRParser;
import org.linuxforhealth.fhir.model.r5.resource.Resource;

public class FhirExerciser {

	public static void main(String[] args) throws FHIRParserException, FHIRGeneratorException {
		InputStream animals = FhirExerciser.class.getResourceAsStream("animals.json");
		FHIRParser parser = FHIRParser.parser(Format.JSON);
		Resource resource = parser.parse(animals);
		
		FHIRGenerator generator = FHIRGenerator.generator(Format.XML, true);
		generator.generate(resource, System.out);
	}
}
