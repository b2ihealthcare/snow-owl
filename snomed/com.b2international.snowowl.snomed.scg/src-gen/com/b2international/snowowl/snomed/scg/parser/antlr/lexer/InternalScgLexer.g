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
lexer grammar InternalScgLexer;

@header {
package com.b2international.snowowl.snomed.scg.parser.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

RULE_EQUIVALENT_TO : '===';

RULE_SUBTYPE_OF : '<<<';

RULE_TERM_STRING : '|' ~('|')* '|';

RULE_ZERO : '0';

RULE_DIGIT_NONZERO : '1'..'9';

RULE_CURLY_OPEN : '{';

RULE_CURLY_CLOSE : '}';

RULE_COMMA : ',';

RULE_EQUAL_SIGN : '=';

RULE_COLON : ':';

RULE_PLUS : '+';

RULE_DASH : '-';

RULE_DOT : '.';

RULE_QUOTATION_MARK : '"';

RULE_OPENING_ROUND_BRACKET : '(';

RULE_CLOSING_ROUND_BRACKET : ')';

RULE_HASH : '#';

RULE_WS : (' '|'\t'|'\n'|'\r');

RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

RULE_STRING : ('"' ('\\' .|~(('\\'|'"')))* '"'|'\'' ('\\' .|~(('\\'|'\'')))* '\'');
