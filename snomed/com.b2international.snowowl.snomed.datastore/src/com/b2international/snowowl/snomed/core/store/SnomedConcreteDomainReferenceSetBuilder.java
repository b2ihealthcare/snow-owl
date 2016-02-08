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
package com.b2international.snowowl.snomed.core.store;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.6
 */
public final class SnomedConcreteDomainReferenceSetBuilder extends SnomedReferenceSetBuilder<SnomedConcreteDomainReferenceSetBuilder, SnomedConcreteDataTypeRefSet> {

	private DataType dataType;
	
	protected SnomedConcreteDomainReferenceSetBuilder() {
		type = SnomedRefSetType.CONCRETE_DATA_TYPE;
		referencedComponentType = CoreTerminologyBroker.UNSPECIFIED;
	}
	
	/**
	 * Specifies the type ({@link DataType}) of the concrete domain reference set
	 * 
	 * @param dataType
	 * @return
	 */
	public SnomedConcreteDomainReferenceSetBuilder setDataType(final DataType dataType) {
		this.dataType = dataType;
		return getSelf();
	}
	
	@Override
	protected SnomedConcreteDataTypeRefSet create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedConcreteDataTypeRefSet();
	}
	
	@Override
	protected void init(final SnomedConcreteDataTypeRefSet component, final TransactionContext context) {
		super.init(component, context);
		checkNotNull(dataType, "Datatype must be specified");
		component.setDataType(dataType);
	}
}
