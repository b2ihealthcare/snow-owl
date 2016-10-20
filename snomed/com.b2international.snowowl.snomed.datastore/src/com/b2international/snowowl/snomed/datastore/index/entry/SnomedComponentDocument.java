/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static com.b2international.index.query.Expressions.exactMatch;

import java.util.Collection;

import com.b2international.index.query.Expression;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @since 4.7
 */
public abstract class SnomedComponentDocument extends SnomedDocument {

	public static abstract class Expressions extends SnomedDocument.Expressions {
		
		protected Expressions() {}
		
		public static final Expression namespace(String namespace) {
			return exactMatch(Fields.NAMESPACE, namespace);
		}
		
		public static Expression referringRefSet(String referringRefSet) {
			return exactMatch(Fields.REFERRING_REFSETS, referringRefSet);
		}
		
		public static Expression referringMappingRefSet(String referringMappingRefSet) {
			return exactMatch(Fields.REFERRING_MAPPING_REFSETS, referringMappingRefSet);
		}
		
	}

	public static class Fields extends SnomedDocument.Fields {
		public static final String NAMESPACE = "namespace";
		public static final String REFERRING_REFSETS = "referringRefSets";
		public static final String REFERRING_MAPPING_REFSETS = "referringMappingRefSets";
	}
	
	protected static abstract class SnomedComponentDocumentBuilder<B extends SnomedComponentDocumentBuilder<B>> extends SnomedDocumentBuilder<B> {
		
		protected String namespace;
		protected Collection<String> referringRefSets;
		protected Collection<String> referringMappingRefSets;

		@Override
		public B id(String id) {
			namespace(SnomedIdentifiers.create(id).getNamespace());
			return super.id(id);
		}
		
		B namespace(String namespace) {
			this.namespace = namespace;
			return getSelf();
		}
		
		public B referringRefSets(Collection<String> referringRefSets) {
			this.referringRefSets = referringRefSets;
			return getSelf();
		}
		
		public B referringMappingRefSets(Collection<String> referringMappingRefSets) {
			this.referringMappingRefSets = referringMappingRefSets;
			return getSelf();
		}
		
	}
	
	private final String namespace;
	private final Collection<String> referringRefSets;
	private final Collection<String> referringMappingRefSets;
	
	SnomedComponentDocument(String id, 
			String label, 
			String iconId, 
			String moduleId,
			boolean released, 
			boolean active,
			long effectiveTime,
			String namespace,
			Collection<String> referringRefSets,
			Collection<String> referringMappingRefSets) {
		super(id, label, iconId, moduleId, released, active, effectiveTime);
		this.namespace = namespace;
		this.referringRefSets = referringRefSets;
		this.referringMappingRefSets = referringMappingRefSets;
	}
	
	public final String getNamespace() {
		return namespace;
	}
	
	public Collection<String> getReferringRefSets() {
		return referringRefSets;
	}
	
	public Collection<String> getReferringMappingRefSets() {
		return referringMappingRefSets;
	}
	
	@Override
	protected ToStringHelper doToString() {
		return super.doToString()
				.add("namespace", namespace)
				.add("referringRefSets", referringRefSets)
				.add("referringMappingRefSets", referringMappingRefSets);
	}

}
