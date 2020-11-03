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
package com.b2international.snowowl.core.scripts;

import java.io.Serializable;
import java.util.Objects;

/**
 * @since 7.11
 */
public final class ScriptSource implements Serializable {

	private final String scriptName;
	private final String script;

	public ScriptSource(final String scriptName, final String script) {
		this.scriptName = scriptName;
		this.script = script;
	}
	
	public String getScript() {
		return script;
	}
	
	public String getScriptName() {
		return scriptName;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(scriptName, script);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ScriptSource other = (ScriptSource) obj;
		return Objects.equals(scriptName, other.scriptName) && Objects.equals(script, other.script);
	}
	
}
