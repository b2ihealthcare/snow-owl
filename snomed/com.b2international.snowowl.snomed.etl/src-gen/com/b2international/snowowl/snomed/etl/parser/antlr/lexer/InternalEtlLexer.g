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
lexer grammar InternalEtlLexer;

@header {
package com.b2international.snowowl.snomed.etl.parser.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

False : ('F'|'f')('A'|'a')('L'|'l')('S'|'s')('E'|'e');

True : ('T'|'t')('R'|'r')('U'|'u')('E'|'e');

RULE_DOUBLE_SQUARE_OPEN : '[[';

RULE_DOUBLE_SQUARE_CLOSE : ']]';

RULE_TILDE : '~';

fragment RULE_AT : '@';

RULE_ID : 'id';

RULE_SCG : 'scg';

RULE_TOK : 'tok';

RULE_STR : 'str';

RULE_INT : 'int';

RULE_DEC : 'dec';

RULE_EQUIVALENT_TO : '===';

RULE_SUBTYPE_OF : '<<<';

RULE_SLOTNAME_STRING : RULE_AT (RULE_STRING|~(('\\'|'"'|'\''|RULE_WS|RULE_AT|RULE_SQUARE_OPEN|RULE_SQUARE_CLOSE))*);

RULE_TERM_STRING : '|' ~('|')* '|';

RULE_REVERSED : 'R';

RULE_TO : '..';

RULE_COMMA : ',';

RULE_CONJUNCTION : ('a'|'A') ('n'|'N') ('d'|'D');

RULE_DISJUNCTION : ('o'|'O') ('r'|'R');

RULE_EXCLUSION : ('m'|'M') ('i'|'I') ('n'|'N') ('u'|'U') ('s'|'S');

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
