/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
lexer grammar InternalQLLexer;

@header {
package com.b2international.snowowl.snomed.parser.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

MINUS : ('M'|'m')('I'|'i')('N'|'n')('U'|'u')('S'|'s');

AND : ('A'|'a')('N'|'n')('D'|'d');

OR : ('O'|'o')('R'|'r');

Comma : ',';

RULE_TERM : 'term';

RULE_ECL : 'ecl';

RULE_TERM_STRING : '|' ~('|')* '|';

RULE_REVERSED : 'R';

RULE_TO : '..';

RULE_ZERO : '0';

RULE_DIGIT_NONZERO : '1'..'9';

RULE_COLON : ':';

RULE_CURLY_OPEN : '{';

RULE_CURLY_CLOSE : '}';

RULE_ROUND_OPEN : '(';

RULE_ROUND_CLOSE : ')';

RULE_SQUARE_OPEN : '[';

RULE_SQUARE_CLOSE : ']';

RULE_PLUS : '+';

RULE_DASH : '-';

RULE_CARET : '^';

RULE_NOT : '!';

RULE_DOT : '.';

RULE_WILDCARD : '*';

RULE_EQUAL : '=';

RULE_NOT_EQUAL : '!=';

RULE_LT : '<';

RULE_GT : '>';

RULE_DBL_LT : '<<';

RULE_DBL_GT : '>>';

RULE_LT_EM : '<!';

RULE_GT_EM : '>!';

RULE_GTE : '>=';

RULE_LTE : '<=';

RULE_HASH : '#';

RULE_WS : (' '|'\t'|'\n'|'\r');

RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

RULE_STRING : ('"' ('\\' .|~(('\\'|'"')))* '"'|'\'' ('\\' .|~(('\\'|'\'')))* '\'');
