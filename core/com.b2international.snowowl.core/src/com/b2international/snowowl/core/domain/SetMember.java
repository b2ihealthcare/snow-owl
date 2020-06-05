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
package com.b2international.snowowl.core.domain;

/**
 * @since 7.7
 */
public final class SetMember extends BaseComponent {

	private static final long serialVersionUID = 1L;

	private final String sourceCodeSystem;
	private final String sourceCode;
	private final String sourceTerm;
	
	private final String iconId;
	private final short terminologyComponentId;
	
	public SetMember(short memberTermionlogyComponentId, String codeSystem, String referencedComponentId, String term, String iconId) {
		this.sourceCodeSystem = codeSystem;
		this.sourceCode = referencedComponentId;
		this.sourceTerm = term;
		this.iconId = iconId;
		this.terminologyComponentId = memberTermionlogyComponentId;
	}
	
	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}

	public String getIconId() {
		return iconId;
	}

	public String getSourceCodeSystem() {
		return sourceCodeSystem;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public String getSourceTerm() {
		return sourceTerm;
	}
	
}
