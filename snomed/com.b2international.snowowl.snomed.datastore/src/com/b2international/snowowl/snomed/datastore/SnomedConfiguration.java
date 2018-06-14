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
package com.b2international.snowowl.snomed.datastore;

import java.io.File;

import org.osgi.service.prefs.PreferencesService;

import com.b2international.snowowl.core.api.preferences.ConfigNode;
import com.b2international.snowowl.core.api.preferences.PreferenceBase;
import com.b2international.snowowl.core.api.preferences.io.ConfigurationEntrySerializer;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

/**
 * Configuration for SNOMED CT editing.
 *
 */
public class SnomedConfiguration extends PreferenceBase {

	public static final String NODE_NAME = "Snomed";
	
	public static final String KEY_MODULE_IDS = "com.b2international.snowowl.snomed.moduleid"; //$NON-NLS-1$
	public static final String KEY_SNOMED_NAMESPACES = "com.b2international.snowowl.snomed.namespaces"; //$NON-NLS-1$ 
	public static final String KEY_HIDDEN_REFERENCE_SETS = "com.b2international.snowowl.snomed.refset.hidden"; //$NON-NLS-1$
	public static final String KEY_MAP_ADVICES= "com.b2international.snowowl.snomed.mapadvice"; //$NON-NLS-1$
	public static final String KEY_MAP_RULE= "com.b2international.snowowl.snomed.maprule"; //$NON-NLS-1$

	public ConfigurationEntrySerializer<ConfigNode<String, String>> moduleIdsSerializer;
	public ConfigurationEntrySerializer<ConfigNode<String, String>> namespacesSerializer;
	public ConfigurationEntrySerializer<ConfigNode<String, String>> hiddenReferenceSetsSerializer;
	public ConfigurationEntrySerializer<ConfigNode<String, IComplexMapAttribute>> mapAdviceSerializer;
	public ConfigurationEntrySerializer<ConfigNode<String, IComplexMapAttribute>> mapRuleSerializer;
	
	public SnomedConfiguration(PreferencesService preferencesService, File defaultsPath) {
		super(preferencesService, NODE_NAME);
		init(defaultsPath);
	}
	
	public void init(File defaultsPath) {
		
		hiddenReferenceSetsSerializer = new ConfigurationEntrySerializer<ConfigNode<String,String>>(preferences, KEY_HIDDEN_REFERENCE_SETS, new File(defaultsPath, "hiddenReferenceSets.xml")) {
			@Override
			public ConfigNode<String, String> computeDefault() {
				return new ConfigNode<String, String>(KEY_HIDDEN_REFERENCE_SETS)
						
					.addChild("900000000000530003", "ALTERNATIVE association reference set")
					.addChild("900000000000525002", "MOVED FROM association reference set")
					.addChild("900000000000524003", "MOVED TO association reference set")
					.addChild("900000000000523009", "POSSIBLY EQUIVALENT TO association reference set")
					.addChild("900000000000531004", "REFERS TO concept association reference set")
					.addChild("900000000000526001", "REPLACED BY association reference set")
					.addChild("900000000000527005", "SAME AS association reference set")
					.addChild("900000000000529008", "SIMILAR TO association reference set")
					.addChild("900000000000528000", "WAS A association reference set")
						
					.addChild("900000000000489007", "Concept inactivation indicator reference set")
					.addChild("900000000000490003", "Description inactivation indicator reference set")
					.addChild("900000000000547002", "Relationship inactivation indicator reference set")
					.addChild("900000000000488004", "Relationship refinability reference set")
					.addChild("900000000000508004", "Great Britain English language reference set")
					.addChild("900000000000509007", "United States of America English language reference set")
					.addChild(Concepts.REFSET_DRUG_TO_GROUPER_SIMPLE_MAP, "Ontology to grouper simple map")
					.addChild(Concepts.REFSET_DRUG_TO_PACKAGING_SIMPLE_MAP, "Ontology to packaging simple map")
					.addChild(Concepts.REFSET_DRUG_TO_SOURCE_DRUG_SIMPLE_MAP, "Ontology to product simple map");
			};
		};
		
		moduleIdsSerializer = new ConfigurationEntrySerializer<ConfigNode<String, String>>(preferences, KEY_MODULE_IDS, new File(defaultsPath, "moduleIds.xml")) {
			
			@Override
			public ConfigNode<String, String> computeDefault() {
				return new ConfigNode<String, String>(KEY_MODULE_IDS)
					.addChild("900000000000445007", "IHTSDO maintained module")
					.addChild(Concepts.MODULE_SCT_CORE, "SNOMED CT core module")
					.addChild("900000000000012004", "SNOMED CT model component module")
					.setDefaultChildKey(Concepts.MODULE_SCT_CORE);
			};
		};

		namespacesSerializer = new ConfigurationEntrySerializer<ConfigNode<String, String>>(preferences, KEY_SNOMED_NAMESPACES, new File(defaultsPath, "snomedNamespaces.xml")) {
			
			@Override
			public ConfigNode<String, String> computeDefault() {
				return new ConfigNode<String, String>(KEY_SNOMED_NAMESPACES)
					.addChild("", "IHTSDO core")
					.addChild("0000000", "IHTSDO model component")
					.setDefaultChildKey("");
			};
		};		

		mapAdviceSerializer = new ConfigurationEntrySerializer<ConfigNode<String, IComplexMapAttribute>>(preferences, KEY_MAP_ADVICES, new File(defaultsPath, "mapAdvice.xml")) {
			@Override public ConfigNode<String, IComplexMapAttribute> computeDefault() {
				return new ConfigNode<String, IComplexMapAttribute>(KEY_MAP_ADVICES)
					.addChild("A", new AlwalysComplexMapAttribute())	
					.addChild("B", new IfExistsComplexMapAttribute())
					.addChild("C", new PlainComplexMapAttribute("DESCENDANTS NOT EXHAUSTIVELY MAPPED"))
					.addChild("D", new PlainComplexMapAttribute("MAP OF SOURCE CONCEPT IS CONTEXT DEPENDENT"))
					.addChild("E", new PlainComplexMapAttribute("POSSIBLE REQUIREMENT FOR AN EXTERNAL CAUSE CODE"))
					.addChild("F", new PlainComplexMapAttribute("POSSIBLE REQUIREMENT FOR MORPHOLOGY CODE"))
					.addChild("G", new PlainComplexMapAttribute("MAP SOURCE CONCEPT CANNOT BE CLASSIFIED WITH AVAILABLE DATA"))
					.addChild("H", new PlainComplexMapAttribute("THIS IS AN EXTERNAL CAUSE CODE FOR USE IN A SECONDARY POSITION"))
					.addChild("I", new PlainComplexMapAttribute("FOURTH CHARACTER REQUIRED TO IDENTIFY PLACE OF OCCURRENCE"))
					.addChild("J", new PlainComplexMapAttribute("MAP CONCEPT IS OUTSIDE SCOPE OF TARGET CLASSIFICATION"))
					.addChild("K", new PlainComplexMapAttribute("WHO ADVISES TO ASSUME CLOSED FRACTURE"))
					.addChild("L", new PlainComplexMapAttribute("MAPPED WITH WHO GUIDANCE"))
					.addChild("L", new PlainComplexMapAttribute("POSSIBLE REQUIREMENT FOR CAUSATIVE DISEASE CODE"))
					.addChild("M", new PlainComplexMapAttribute("USE AS PRIMARY CODE ONLY IF SITE OF BURN UNSPECIFIED, OTHERWISE USE AS A SUPPLEMENTARY CODE WITH CATEGORIES T20-T29(Burns)"))
					.addChild("N", new PlainComplexMapAttribute("SOURCE SNOMED CONCEPT IS AMBIGUOUS"))
					.addChild("O", new PlainComplexMapAttribute("ADDITIONAL CODES NOT MAPPED"))
					.addChild("P", new PlainComplexMapAttribute("MAPPING GUIDANCE FROM WHO IS AMBIGUOUS"))
					.addChild("Q", new PlainComplexMapAttribute("ADDITIONAL CODES FROM XX, YY-ZZ NOT MAPPED"));
				}
		};
		mapRuleSerializer = new ConfigurationEntrySerializer<ConfigNode<String, IComplexMapAttribute>>(preferences, KEY_MAP_RULE, new File(defaultsPath, "mapRules.xml")) {
			@Override public ConfigNode<String, IComplexMapAttribute> computeDefault() {
				return new ConfigNode<String, IComplexMapAttribute>(KEY_MAP_RULE)
					.addChild("A", new PlainComplexMapAttribute("TRUE"))
					.addChild("B", new IfAvailableComplexMapAttribute())
					.addChild("C", new IfAvailableOrDescendantComplexMapAttribute())
					.addChild("D", new PlainComplexMapAttribute("OTHERWISE TRUE"));
				}
		};
	}

	public ConfigNode<String, String> getModuleIds() {
		return moduleIdsSerializer.deserialize();
	}
	
	public void setModuleIds(ConfigNode<String, String> moduleIds) {
		moduleIdsSerializer.serialize(moduleIds);
	}
	
	public ConfigNode<String, String> defaultModuleIds() {
		return moduleIdsSerializer.getDefault();
	}
	
	public ConfigurationEntrySerializer<ConfigNode<String, String>> getModuleIdsSerializer() {
		return moduleIdsSerializer;
	}
	
	public ConfigurationEntrySerializer<ConfigNode<String, String>> getHiddenRefSetSerializer() {
		return hiddenReferenceSetsSerializer;
	}
	
	public ConfigNode<String, String> getHiddenReferenceSets() {
		return hiddenReferenceSetsSerializer.deserialize();
	}
	
	public ConfigNode<String, IComplexMapAttribute> getMapAdvices() {
		return mapAdviceSerializer.deserialize();
	}
	
	public ConfigNode<String, IComplexMapAttribute> getMapRules() {
		return mapRuleSerializer.deserialize();
	}
	
	public ConfigNode<String, String> getNamespaces() {
		return namespacesSerializer.deserialize();
	}
	
	public void setNamespaces(ConfigNode<String, String> namespaces) {
		namespacesSerializer.serialize(namespaces);
	}
	
	public ConfigNode<String, String> defaultNamespaces() {
		return namespacesSerializer.getDefault();
	}
	
	public ConfigurationEntrySerializer<ConfigNode<String, String>> getNamespacesSerializer() {
		return namespacesSerializer;
	}		
}