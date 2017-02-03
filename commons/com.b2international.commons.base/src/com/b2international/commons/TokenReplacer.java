/**
 * Copyright (C) 2009-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.commons;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Single-class implementation of Marcel Sauer's TokenReplacer, using utility
 * methods from Guava.
 * <p>
 * Actualized documentation follows:
 * <p>
 * Toky is a token replacer for Strings. It will replace the found token with a
 * provided static value or a dynamically generated value created by a
 * {@link Function}. Toky itself <b>IS NOT THREAD SAFE</b>, so access to a
 * single instance in a multi-threaded environment should be synchronized by the
 * client.
 * </p>
 * 
 * <p>
 * Simplest use case, only <b>static values</b>:
 * </p>
 * 
 * <pre>
 * TokenReplacer toky = new TokenReplacer().register(&quot;number&quot;, &quot;123&quot;);
 * toky.substitute(&quot;i can count to {number}&quot;);
 * </pre>
 * 
 * <p>
 * We can also use a <b>{@link Function}</b> to <b>dynamically</b> get the
 * value:
 * </p>
 * 
 * <pre>
 * toky = new TokenReplacer().register(&quot;number&quot;, new Function&lt;String[], String&gt;() {
 * 	&#064;Override
 * 	public String apply(String[] input) {
 * 		return &quot;123&quot;;
 * 	}
 * });
 * </pre>
 * <p>
 * Here we use a generator and <b>pass the arguments</b> "a,b,c" to it, they
 * will be applied via {@link Function#apply(Object) Function#apply(String[])}.
 * This feature makes handling tokens pretty powerful because you can write very
 * dynamic generators.
 * </p>
 * 
 * <pre>
 * toky.substitute(&quot;i can count to {number(a,b,c)}&quot;);
 * </pre>
 * 
 * If you prefer to use <b>index based tokens</b>, you can also use this:
 * 
 * <pre>
 * toky.register(new String[] { &quot;one&quot;, &quot;two&quot;, &quot;three&quot; });
 * toky.substitute(&quot;abc {0} {1} {2} def&quot;)); // will produce &quot;abc one two three def&quot;
 * </pre>
 * 
 * <p>
 * Of course you can replace all default <b>delimiters</b> with your preferred
 * ones, just make sure start and end are different.
 * </p>
 * 
 * <pre>
 * toky.withTokenStart(&quot;*&quot;); // default is '{'
 * toky.withTokenEnd(&quot;#&quot;); // default is '}'
 * toky.withArgumentDelimiter(&quot;;&quot;); // default is ','
 * toky.withArgumentStart(&quot;[&quot;); // default is '('
 * toky.withArgumentEnd(&quot;]&quot;); // default is ')'
 * </pre>
 * 
 * <p>
 * By default, Toky will throw IllegalStateExceptions if there was no matching
 * value or generator found for a token. you can <b>enable generating
 * exceptions</b> by calling:
 * </p>
 * 
 * <pre>
 * toky.doNotIgnoreMissingValues(); // which is the DEFAULT
 * </pre>
 * 
 * <p>
 * This will turn error reporting for missing values <b>OFF</b>:
 * </p>
 * 
 * <pre>
 * toky.ignoreMissingValues();
 * </pre>
 * 
 * <p>
 * You can also <b>enable/disable generator caching</b>. If you enable caching
 * once a generator for a token returned a value this value will be used for all
 * subsequent tokens with the same name otherwise the generator will be called
 * once for every token. <br/>
 * <br/>
 * 
 * e.g. {counter}{counter}{counter}<br/>
 * <br/>
 * 
 * With a registered generator will result in 3 calls to the generator
 * (resulting in poorer performance), so if you know your generator will always
 * return the same value, enable caching.
 * </p>
 * 
 * <pre>
 * toky.enableGeneratorCaching();
 * toky.disableGeneratorCaching();
 * </pre>
 * 
 * @see {@link https://github.com/niesfisch/tokenreplacer}
 * 
 * @author Marcel Sauer (msauer)
 */
public class TokenReplacer {

	private static class LiteralFunction implements Function<String[], String> {
		
		private final String value;

		private LiteralFunction(String value) {
			this.value = value;
		}

		@Override
		public String apply(String[] input) {
			return value;
		}
	}
	
	private static final String[] EMPTY_STRING_ARRAY = new String[] {};
	
	private static final char DEFAULT_TOKEN_START = '{';
	private static final char DEFAULT_TOKEN_END = '}';
	private static final char DEFAULT_ARGS_SEPARATOR = ',';
	private static final char DEFAULT_ARGS_START = '(';
	private static final char DEFAULT_ARGS_END = ')';
	
	protected char tokenStart = DEFAULT_TOKEN_START;
	protected char tokenEnd = DEFAULT_TOKEN_END;

	protected char argsStart = DEFAULT_ARGS_START;
	protected char argsEnd = DEFAULT_ARGS_END;
	protected char argsSep = DEFAULT_ARGS_SEPARATOR;

	protected boolean ignoreMissingValues = false;
	protected boolean generatorCachingEnabled = false;

	protected final Map<String, Function<String[], String>> tokens = Maps.newHashMap();

	protected enum State {
		READING_INPUT, 
		TOKEN_STARTED, 
		READING_TOKEN, 
		TOKEN_ARGS_STARTED, 
		READING_TOKEN_ARGS, 
		TOKEN_ARGS_END
	}

	/**
	 * Replaces all tokens in the specified input with one of the following:
	 * 
	 * <ul>
	 * <li>the provided static values set via {@link #register(String, String)}
	 * <li>the generator registered via {@link #register(String, Generator)}
	 * </ul>
	 * 
	 * @param toSubstitute
	 *            the string that contains the tokens, will be returned as-is in
	 *            case of {@code null} or empty string
	 * 
	 * @return the result after replacing all tokens with the proper values
	 * 
	 * @throws IllegalArgumentException
	 *             if a parse error occurs while processing the input string
	 */
	public String substitute(String toSubstitute) {

		if (toSubstitute == null || toSubstitute.isEmpty()) {
			return toSubstitute;
		}
		
		final StringBuilder tokenBuilder = new StringBuilder();
		final StringBuilder argsBuilder = new StringBuilder();
		final StringBuilder resultBuilder = new StringBuilder();
		
		final Map<String, String> generatorCache = new HashMap<String, String>();

		State state = State.READING_INPUT;

		for (int i = 0; i < toSubstitute.length(); ++i) {
			
			final char c = toSubstitute.charAt(i);
			
			switch (state) {
			
				case READING_INPUT:
					if (isTokenStart(c)) {
						state = State.TOKEN_STARTED;
						tokenBuilder.setLength(0);
					} else {
						resultBuilder.append(c);
					}
					
					break;
					
				case TOKEN_STARTED:
					
					if (isStdInput(c)) {
						state = State.READING_TOKEN;
						tokenBuilder.append(c);
					} else {
						error(toSubstitute);
					}
					
					break;
					
				case READING_TOKEN:
					
					if (isStdInput(c)) {
						tokenBuilder.append(c);
					} else if (isArgStart(c)) {
						state = State.TOKEN_ARGS_STARTED;
						argsBuilder.setLength(0);
					} else if (isTokenEnd(c)) {
						state = State.READING_INPUT;
						resultBuilder.append(
								evalToken(tokenBuilder.toString(), argsBuilder.toString(), generatorCache));
					} else {
						error(toSubstitute);
					}
					
					break;
					
				case TOKEN_ARGS_STARTED:
					
					if (isArgEnd(c)) {
						state = State.TOKEN_ARGS_END;
					} else if (isStdInput(c)) {
						state = State.READING_TOKEN_ARGS;
						argsBuilder.append(c);
					} else {
						error(toSubstitute);
					}
					
					break;
					
				case READING_TOKEN_ARGS:
					
					if (isArgEnd(c)) {
						state = State.TOKEN_ARGS_END;
					} else if (isStdInput(c)) {
						argsBuilder.append(c);
					} else {
						error(toSubstitute);
					}
					
					break;
					
				case TOKEN_ARGS_END:
					
					if (isTokenEnd(c)) {
						state = State.READING_INPUT;
						resultBuilder.append(
								evalToken(tokenBuilder.toString(), argsBuilder.toString(), generatorCache));
					} else {
						error(toSubstitute);
					}
					
					break;
			}
		}
		
		if (!isFinalStateReached(state)) {
			error(toSubstitute);
		}

		return resultBuilder.toString();
	}

	private void error(String toSubstitute) {
		throw new IllegalArgumentException("Couldn't parse string '" + toSubstitute + "'.");
	}

	private boolean isFinalStateReached(State state) {
		return state == State.READING_INPUT;
	}

	protected boolean isArgStart(char c) {
		return argsStart == c;
	}

	protected boolean isArgEnd(char c) {
		return argsEnd == c;
	}
	
	protected boolean isTokenStart(char c) {
		return tokenStart == c;
	}

	protected boolean isTokenEnd(char c) {
		return tokenEnd == c;
	}

	protected boolean isStdInput(char c) {
		boolean isIdentifier = isTokenStart(c) || isTokenEnd(c) || isArgStart(c) || isArgEnd(c);
		return (!isIdentifier);
	}

	protected String[] extractArgs(final String tokenName, final String args) {
		
		if (args.isEmpty()) {
			return EMPTY_STRING_ARRAY;
		}
		
		checkArgumentsAreValid(tokenName, args);
		
		String[] argsResult = Iterables.toArray(Splitter.on(argsSep).trimResults().split(args), String.class);
		return argsResult;
	}

	protected void checkArgumentsAreValid(final String tokenName, final String args) {
		
		if (args.startsWith(",") || args.endsWith(",")) {
			
			throw new IllegalArgumentException(
					String.format("Invalid argument list '%s' for token '%s'.", args, tokenName));
		}
	}

	protected String evalToken(final String token, final String args, final Map<String, String> generatorCache) {
		
		final String[] argsResult = extractArgs(token, args);
		
		if (!this.tokens.containsKey(token)) {
			
			if (this.ignoreMissingValues) {
				return tokenWithPossibleArguments(token, args);
			} else {
				throw new IllegalArgumentException(
						String.format("No value registered for token '%s'.", token));
			}
		}
		
		return getGeneratorValue(token, argsResult, generatorCache);
	}

	private String getGeneratorValue(final String tokenName, final String[] args, final Map<String, String> generatorCache) {
		
		if (this.generatorCachingEnabled && generatorCache.containsKey(tokenName)) {
			return generatorCache.get(tokenName);
		}
		
		final Function<String[], String> generator = this.tokens.get(tokenName);
		final String value = generator.apply(args);
		
		if (this.generatorCachingEnabled) {
			generatorCache.put(tokenName, value);
		}
		
		return value;
	}

	private String tokenWithPossibleArguments(final String token, final String args) {
		
		if (args.length() > 0) {
			return this.tokenStart + token.toString() + this.argsStart + args + this.argsEnd + this.tokenEnd;
		} else {
			return this.tokenStart + token.toString() + this.tokenEnd;
		}
	}

	public TokenReplacer register(final String token, final String value) {
		
		checkNotNull(token);
		checkState(!token.isEmpty(), "token is empty.");
		checkNotNull(value);
		
		return register(token, new LiteralFunction(value));
	}

	public TokenReplacer register(String token, Function<String[], String> generator) {
		
		checkNotNull(token);
		checkNotNull(generator);

		tokens.put(token, generator);
		return this;
	}

	public TokenReplacer withTokenStart(char tokenStart) {
		this.tokenStart = tokenStart;
		return this;
	}

	public TokenReplacer withTokenEnd(char tokenEnd) {
		this.tokenEnd = tokenEnd;
		return this;
	}

	public TokenReplacer withArgumentDelimiter(char argsSep) {
		this.argsSep = argsSep;
		return this;
	}

	public TokenReplacer withArgumentStart(char argsStart) {
		this.argsStart = argsStart;
		return this;
	}

	public TokenReplacer withArgumentEnd(char argsEnd) {
		this.argsEnd = argsEnd;
		return this;
	}

	public TokenReplacer doNotIgnoreMissingValues() {
		this.ignoreMissingValues = false;
		return this;
	}

	public TokenReplacer ignoreMissingValues() {
		this.ignoreMissingValues = true;
		return this;
	}

	public TokenReplacer enableGeneratorCaching() {
		this.generatorCachingEnabled = true;
		return this;
	}

	public TokenReplacer disableGeneratorCaching() {
		this.generatorCachingEnabled = false;
		return this;
	}

	public TokenReplacer register(String[] replacements) {

		checkNotNull(replacements);
		
		int i = 0;
		
		for (String replacement : replacements) {
			this.register(String.valueOf(i), replacement);
			i++;
		}
		
		return this;
	}
}
