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
package com.b2international.snowowl.snomed.reasoner.classification.entry;

/**
 * Represents a change entry of a concrete domain element related to a concept.
 */
public class ConceptConcreteDomainChangeEntry extends AbstractChangeEntry implements IConcreteDomainChangeEntry {

	private static final long serialVersionUID = -1070331640274138202L;

	private final ConcreteDomainElement concreteDomainElement;

	/**
	 * Creates a new concrete domain change entry with the specified arguments.
	 * @param nature the change nature
	 * @param source the component corresponding to the referenced concept
	 * @param concreteDomainElement the contained concrete domain element
	 */
	public ConceptConcreteDomainChangeEntry(final Nature nature, final LongComponent source, final ConcreteDomainElement concreteDomainElement) {
		super(nature, source);
		this.concreteDomainElement = concreteDomainElement;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.IConcreteDomainChangeEntry#getConcreteDomainElement()
	 */
	@Override public ConcreteDomainElement getConcreteDomainElement() {
		return concreteDomainElement;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.IConcreteDomainChangeEntry#getType()
	 */
	@Override public LongComponent getType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.IConcreteDomainChangeEntry#getDestination()
	 */
	@Override public LongComponent getDestination() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((concreteDomainElement == null) ? 0 : concreteDomainElement.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry#equals(java.lang.Object)
	 */
	@Override public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ConceptConcreteDomainChangeEntry other = (ConceptConcreteDomainChangeEntry) obj;
		if (concreteDomainElement == null) {
			if (other.concreteDomainElement != null)
				return false;
		} else if (!concreteDomainElement.equals(other.concreteDomainElement))
			return false;
		return true;
	}
}