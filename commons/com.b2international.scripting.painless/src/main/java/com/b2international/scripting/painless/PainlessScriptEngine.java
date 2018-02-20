/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.scripting.painless;

import java.util.Map;

import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.painless.PainlessPlugin;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.ExecutableScript.Factory;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptModule;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.script.ScriptType;

import com.b2international.scripting.api.ScriptEngine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.3
 */
public final class PainlessScriptEngine implements ScriptEngine {

	private static final String LANGUAGE = "painless";

	private final ScriptModule scriptModule;

	public PainlessScriptEngine() {
		this.scriptModule = new ScriptModule(Settings.EMPTY, ImmutableList.of(new PainlessPlugin()));
	}

	@Override
	public <T> T run(final ClassLoader ctx, final String rawScript, final Map<String, Object> params) {
		if (Strings.isNullOrEmpty(rawScript)) {
			return null;
		}

		/*
		 * XXX: The class loader is ignored, as Painless was designed to have a limited
		 * set of available classes.
		 */
		final ScriptService scriptService = scriptModule.getScriptService();
		final Script script = new Script(ScriptType.INLINE, LANGUAGE, rawScript, ImmutableMap.of());
		final Factory scriptFactory = scriptService.compile(script, ExecutableScript.CONTEXT);

		final ExecutableScript executableScript = scriptFactory.newInstance(params);

		return (T) executableScript.run();
	}

	@Override
	public String getExtension() {
		return LANGUAGE;
	}
}
