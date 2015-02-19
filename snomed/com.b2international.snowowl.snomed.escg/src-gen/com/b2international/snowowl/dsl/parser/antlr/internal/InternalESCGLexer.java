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
package com.b2international.snowowl.dsl.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalESCGLexer extends Lexer {
    public static final int RULE_EQUALS_OPERATOR=29;
    public static final int RULE_ZERO=25;
    public static final int RULE_NOT_TOKEN=7;
    public static final int RULE_CARET=8;
    public static final int RULE_PERIOD=27;
    public static final int RULE_SUBTYPE=10;
    public static final int RULE_OPENING_CURLY_BRACKET=13;
    public static final int RULE_EQUAL_SIGN=17;
    public static final int RULE_COMMA=15;
    public static final int RULE_GREATER_THAN_OPERATOR=30;
    public static final int RULE_DIGIT_NONZERO=24;
    public static final int RULE_AND_TOKEN=21;
    public static final int RULE_OTHER_ALLOWED_TERM_CHARACTER=28;
    public static final int RULE_OPENING_ROUND_BRACKET=22;
    public static final int RULE_OR_TOKEN=20;
    public static final int RULE_OPTIONAL=16;
    public static final int RULE_SL_COMMENT=35;
    public static final int EOF=-1;
    public static final int RULE_GREATER_EQUALS_OPERATOR=32;
    public static final int RULE_LESS_EQUALS_OPERATOR=31;
    public static final int RULE_NOT_EQUALS_OPERATOR=33;
    public static final int RULE_LETTER=26;
    public static final int RULE_ML_COMMENT=34;
    public static final int RULE_CLOSING_CURLY_BRACKET=14;
    public static final int RULE_COLON=6;
    public static final int RULE_CLOSING_ROUND_BRACKET=23;
    public static final int RULE_PIPE=9;
    public static final int T__36=36;
    public static final int RULE_INCLUSIVE_SUBTYPE=11;
    public static final int RULE_CLOSING_SQUARE_BRACKET=19;
    public static final int RULE_OPENING_SQUARE_BRACKET=18;
    public static final int RULE_PLUS_SIGN=5;
    public static final int RULE_WS=12;
    public static final int RULE_UNION_TOKEN=4;

    // delegates
    // delegators

    public InternalESCGLexer() {;} 
    public InternalESCGLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalESCGLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g"; }

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:11:7: ( 'mg' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:11:9: 'mg'
            {
            match("mg"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "RULE_ZERO"
    public final void mRULE_ZERO() throws RecognitionException {
        try {
            int _type = RULE_ZERO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1948:11: ( '0' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1948:13: '0'
            {
            match('0'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ZERO"

    // $ANTLR start "RULE_DIGIT_NONZERO"
    public final void mRULE_DIGIT_NONZERO() throws RecognitionException {
        try {
            int _type = RULE_DIGIT_NONZERO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1950:20: ( '1' .. '9' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1950:22: '1' .. '9'
            {
            matchRange('1','9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DIGIT_NONZERO"

    // $ANTLR start "RULE_LETTER"
    public final void mRULE_LETTER() throws RecognitionException {
        try {
            int _type = RULE_LETTER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1952:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1952:15: ( 'a' .. 'z' | 'A' .. 'Z' )
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_LETTER"

    // $ANTLR start "RULE_SUBTYPE"
    public final void mRULE_SUBTYPE() throws RecognitionException {
        try {
            int _type = RULE_SUBTYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1954:14: ( '<' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1954:16: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_SUBTYPE"

    // $ANTLR start "RULE_INCLUSIVE_SUBTYPE"
    public final void mRULE_INCLUSIVE_SUBTYPE() throws RecognitionException {
        try {
            int _type = RULE_INCLUSIVE_SUBTYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1956:24: ( '<<' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1956:26: '<<'
            {
            match("<<"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_INCLUSIVE_SUBTYPE"

    // $ANTLR start "RULE_EQUALS_OPERATOR"
    public final void mRULE_EQUALS_OPERATOR() throws RecognitionException {
        try {
            int _type = RULE_EQUALS_OPERATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1958:22: ( '==' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1958:24: '=='
            {
            match("=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_EQUALS_OPERATOR"

    // $ANTLR start "RULE_GREATER_THAN_OPERATOR"
    public final void mRULE_GREATER_THAN_OPERATOR() throws RecognitionException {
        try {
            int _type = RULE_GREATER_THAN_OPERATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1960:28: ( '>' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1960:30: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_GREATER_THAN_OPERATOR"

    // $ANTLR start "RULE_LESS_EQUALS_OPERATOR"
    public final void mRULE_LESS_EQUALS_OPERATOR() throws RecognitionException {
        try {
            int _type = RULE_LESS_EQUALS_OPERATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1962:27: ( '<=' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1962:29: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_LESS_EQUALS_OPERATOR"

    // $ANTLR start "RULE_GREATER_EQUALS_OPERATOR"
    public final void mRULE_GREATER_EQUALS_OPERATOR() throws RecognitionException {
        try {
            int _type = RULE_GREATER_EQUALS_OPERATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1964:30: ( '>=' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1964:32: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_GREATER_EQUALS_OPERATOR"

    // $ANTLR start "RULE_NOT_EQUALS_OPERATOR"
    public final void mRULE_NOT_EQUALS_OPERATOR() throws RecognitionException {
        try {
            int _type = RULE_NOT_EQUALS_OPERATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1966:26: ( '!=' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1966:28: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_NOT_EQUALS_OPERATOR"

    // $ANTLR start "RULE_WS"
    public final void mRULE_WS() throws RecognitionException {
        try {
            int _type = RULE_WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1968:9: ( ( ' ' | '\\t' | '\\n' | '\\r' ) )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1968:11: ( ' ' | '\\t' | '\\n' | '\\r' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_WS"

    // $ANTLR start "RULE_ML_COMMENT"
    public final void mRULE_ML_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1970:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1970:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1970:24: ( options {greedy=false; } : . )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='*') ) {
                    int LA1_1 = input.LA(2);

                    if ( (LA1_1=='/') ) {
                        alt1=2;
                    }
                    else if ( ((LA1_1>='\u0000' && LA1_1<='.')||(LA1_1>='0' && LA1_1<='\uFFFF')) ) {
                        alt1=1;
                    }


                }
                else if ( ((LA1_0>='\u0000' && LA1_0<=')')||(LA1_0>='+' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1970:52: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            match("*/"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ML_COMMENT"

    // $ANTLR start "RULE_SL_COMMENT"
    public final void mRULE_SL_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1972:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1972:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1972:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\u0000' && LA2_0<='\t')||(LA2_0>='\u000B' && LA2_0<='\f')||(LA2_0>='\u000E' && LA2_0<='\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1972:24: ~ ( ( '\\n' | '\\r' ) )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1972:40: ( ( '\\r' )? '\\n' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='\n'||LA4_0=='\r') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1972:41: ( '\\r' )? '\\n'
                    {
                    // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1972:41: ( '\\r' )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0=='\r') ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1972:41: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    match('\n'); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_SL_COMMENT"

    // $ANTLR start "RULE_PIPE"
    public final void mRULE_PIPE() throws RecognitionException {
        try {
            int _type = RULE_PIPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1974:11: ( '|' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1974:13: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_PIPE"

    // $ANTLR start "RULE_COLON"
    public final void mRULE_COLON() throws RecognitionException {
        try {
            int _type = RULE_COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1976:12: ( ':' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1976:14: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_COLON"

    // $ANTLR start "RULE_OPENING_CURLY_BRACKET"
    public final void mRULE_OPENING_CURLY_BRACKET() throws RecognitionException {
        try {
            int _type = RULE_OPENING_CURLY_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1978:28: ( '{' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1978:30: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_OPENING_CURLY_BRACKET"

    // $ANTLR start "RULE_CLOSING_CURLY_BRACKET"
    public final void mRULE_CLOSING_CURLY_BRACKET() throws RecognitionException {
        try {
            int _type = RULE_CLOSING_CURLY_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1980:28: ( '}' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1980:30: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_CLOSING_CURLY_BRACKET"

    // $ANTLR start "RULE_EQUAL_SIGN"
    public final void mRULE_EQUAL_SIGN() throws RecognitionException {
        try {
            int _type = RULE_EQUAL_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1982:17: ( '=' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1982:19: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_EQUAL_SIGN"

    // $ANTLR start "RULE_COMMA"
    public final void mRULE_COMMA() throws RecognitionException {
        try {
            int _type = RULE_COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1984:12: ( ',' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1984:14: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_COMMA"

    // $ANTLR start "RULE_OPENING_ROUND_BRACKET"
    public final void mRULE_OPENING_ROUND_BRACKET() throws RecognitionException {
        try {
            int _type = RULE_OPENING_ROUND_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1986:28: ( '(' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1986:30: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_OPENING_ROUND_BRACKET"

    // $ANTLR start "RULE_CLOSING_ROUND_BRACKET"
    public final void mRULE_CLOSING_ROUND_BRACKET() throws RecognitionException {
        try {
            int _type = RULE_CLOSING_ROUND_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1988:28: ( ')' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1988:30: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_CLOSING_ROUND_BRACKET"

    // $ANTLR start "RULE_OPENING_SQUARE_BRACKET"
    public final void mRULE_OPENING_SQUARE_BRACKET() throws RecognitionException {
        try {
            int _type = RULE_OPENING_SQUARE_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1990:29: ( '[' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1990:31: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_OPENING_SQUARE_BRACKET"

    // $ANTLR start "RULE_CLOSING_SQUARE_BRACKET"
    public final void mRULE_CLOSING_SQUARE_BRACKET() throws RecognitionException {
        try {
            int _type = RULE_CLOSING_SQUARE_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1992:29: ( ']' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1992:31: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_CLOSING_SQUARE_BRACKET"

    // $ANTLR start "RULE_PLUS_SIGN"
    public final void mRULE_PLUS_SIGN() throws RecognitionException {
        try {
            int _type = RULE_PLUS_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1994:16: ( '+' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1994:18: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_PLUS_SIGN"

    // $ANTLR start "RULE_CARET"
    public final void mRULE_CARET() throws RecognitionException {
        try {
            int _type = RULE_CARET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1996:12: ( '^' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1996:14: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_CARET"

    // $ANTLR start "RULE_NOT_TOKEN"
    public final void mRULE_NOT_TOKEN() throws RecognitionException {
        try {
            int _type = RULE_NOT_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1998:16: ( '!' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1998:18: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_NOT_TOKEN"

    // $ANTLR start "RULE_OPTIONAL"
    public final void mRULE_OPTIONAL() throws RecognitionException {
        try {
            int _type = RULE_OPTIONAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2000:15: ( '~' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2000:17: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_OPTIONAL"

    // $ANTLR start "RULE_PERIOD"
    public final void mRULE_PERIOD() throws RecognitionException {
        try {
            int _type = RULE_PERIOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2002:13: ( '.' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2002:15: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_PERIOD"

    // $ANTLR start "RULE_OTHER_ALLOWED_TERM_CHARACTER"
    public final void mRULE_OTHER_ALLOWED_TERM_CHARACTER() throws RecognitionException {
        try {
            int _type = RULE_OTHER_ALLOWED_TERM_CHARACTER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2004:35: ( . )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2004:37: .
            {
            matchAny(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_OTHER_ALLOWED_TERM_CHARACTER"

    // $ANTLR start "RULE_AND_TOKEN"
    public final void mRULE_AND_TOKEN() throws RecognitionException {
        try {
            int _type = RULE_AND_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2006:16: ( 'AND' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2006:18: 'AND'
            {
            match("AND"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_AND_TOKEN"

    // $ANTLR start "RULE_OR_TOKEN"
    public final void mRULE_OR_TOKEN() throws RecognitionException {
        try {
            int _type = RULE_OR_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2008:15: ( 'OR' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2008:17: 'OR'
            {
            match("OR"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_OR_TOKEN"

    // $ANTLR start "RULE_UNION_TOKEN"
    public final void mRULE_UNION_TOKEN() throws RecognitionException {
        try {
            int _type = RULE_UNION_TOKEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2010:18: ( 'UNION' )
            // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:2010:20: 'UNION'
            {
            match("UNION"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_UNION_TOKEN"

    public void mTokens() throws RecognitionException {
        // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:8: ( T__36 | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_LETTER | RULE_SUBTYPE | RULE_INCLUSIVE_SUBTYPE | RULE_EQUALS_OPERATOR | RULE_GREATER_THAN_OPERATOR | RULE_LESS_EQUALS_OPERATOR | RULE_GREATER_EQUALS_OPERATOR | RULE_NOT_EQUALS_OPERATOR | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_PIPE | RULE_COLON | RULE_OPENING_CURLY_BRACKET | RULE_CLOSING_CURLY_BRACKET | RULE_EQUAL_SIGN | RULE_COMMA | RULE_OPENING_ROUND_BRACKET | RULE_CLOSING_ROUND_BRACKET | RULE_OPENING_SQUARE_BRACKET | RULE_CLOSING_SQUARE_BRACKET | RULE_PLUS_SIGN | RULE_CARET | RULE_NOT_TOKEN | RULE_OPTIONAL | RULE_PERIOD | RULE_OTHER_ALLOWED_TERM_CHARACTER | RULE_AND_TOKEN | RULE_OR_TOKEN | RULE_UNION_TOKEN )
        int alt5=33;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:10: T__36
                {
                mT__36(); 

                }
                break;
            case 2 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:16: RULE_ZERO
                {
                mRULE_ZERO(); 

                }
                break;
            case 3 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:26: RULE_DIGIT_NONZERO
                {
                mRULE_DIGIT_NONZERO(); 

                }
                break;
            case 4 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:45: RULE_LETTER
                {
                mRULE_LETTER(); 

                }
                break;
            case 5 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:57: RULE_SUBTYPE
                {
                mRULE_SUBTYPE(); 

                }
                break;
            case 6 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:70: RULE_INCLUSIVE_SUBTYPE
                {
                mRULE_INCLUSIVE_SUBTYPE(); 

                }
                break;
            case 7 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:93: RULE_EQUALS_OPERATOR
                {
                mRULE_EQUALS_OPERATOR(); 

                }
                break;
            case 8 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:114: RULE_GREATER_THAN_OPERATOR
                {
                mRULE_GREATER_THAN_OPERATOR(); 

                }
                break;
            case 9 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:141: RULE_LESS_EQUALS_OPERATOR
                {
                mRULE_LESS_EQUALS_OPERATOR(); 

                }
                break;
            case 10 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:167: RULE_GREATER_EQUALS_OPERATOR
                {
                mRULE_GREATER_EQUALS_OPERATOR(); 

                }
                break;
            case 11 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:196: RULE_NOT_EQUALS_OPERATOR
                {
                mRULE_NOT_EQUALS_OPERATOR(); 

                }
                break;
            case 12 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:221: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 13 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:229: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 14 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:245: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 15 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:261: RULE_PIPE
                {
                mRULE_PIPE(); 

                }
                break;
            case 16 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:271: RULE_COLON
                {
                mRULE_COLON(); 

                }
                break;
            case 17 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:282: RULE_OPENING_CURLY_BRACKET
                {
                mRULE_OPENING_CURLY_BRACKET(); 

                }
                break;
            case 18 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:309: RULE_CLOSING_CURLY_BRACKET
                {
                mRULE_CLOSING_CURLY_BRACKET(); 

                }
                break;
            case 19 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:336: RULE_EQUAL_SIGN
                {
                mRULE_EQUAL_SIGN(); 

                }
                break;
            case 20 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:352: RULE_COMMA
                {
                mRULE_COMMA(); 

                }
                break;
            case 21 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:363: RULE_OPENING_ROUND_BRACKET
                {
                mRULE_OPENING_ROUND_BRACKET(); 

                }
                break;
            case 22 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:390: RULE_CLOSING_ROUND_BRACKET
                {
                mRULE_CLOSING_ROUND_BRACKET(); 

                }
                break;
            case 23 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:417: RULE_OPENING_SQUARE_BRACKET
                {
                mRULE_OPENING_SQUARE_BRACKET(); 

                }
                break;
            case 24 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:445: RULE_CLOSING_SQUARE_BRACKET
                {
                mRULE_CLOSING_SQUARE_BRACKET(); 

                }
                break;
            case 25 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:473: RULE_PLUS_SIGN
                {
                mRULE_PLUS_SIGN(); 

                }
                break;
            case 26 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:488: RULE_CARET
                {
                mRULE_CARET(); 

                }
                break;
            case 27 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:499: RULE_NOT_TOKEN
                {
                mRULE_NOT_TOKEN(); 

                }
                break;
            case 28 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:514: RULE_OPTIONAL
                {
                mRULE_OPTIONAL(); 

                }
                break;
            case 29 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:528: RULE_PERIOD
                {
                mRULE_PERIOD(); 

                }
                break;
            case 30 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:540: RULE_OTHER_ALLOWED_TERM_CHARACTER
                {
                mRULE_OTHER_ALLOWED_TERM_CHARACTER(); 

                }
                break;
            case 31 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:574: RULE_AND_TOKEN
                {
                mRULE_AND_TOKEN(); 

                }
                break;
            case 32 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:589: RULE_OR_TOKEN
                {
                mRULE_OR_TOKEN(); 

                }
                break;
            case 33 :
                // ../com.b2international.snowowl.dsl.escg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalESCG.g:1:603: RULE_UNION_TOKEN
                {
                mRULE_UNION_TOKEN(); 

                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\1\uffff\1\35\2\uffff\1\35\1\43\1\45\1\47\1\51\1\uffff\1\30\16"+
        "\uffff\2\35\41\uffff";
    static final String DFA5_eofS =
        "\74\uffff";
    static final String DFA5_minS =
        "\1\0\1\147\2\uffff\1\116\1\74\3\75\1\uffff\1\52\16\uffff\1\122"+
        "\1\116\41\uffff";
    static final String DFA5_maxS =
        "\1\uffff\1\147\2\uffff\1\116\4\75\1\uffff\1\57\16\uffff\1\122\1"+
        "\116\41\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\2\1\3\5\uffff\1\14\1\uffff\1\17\1\20\1\21\1\22\1\24"+
        "\1\25\1\26\1\27\1\30\1\31\1\32\1\34\1\35\1\36\2\uffff\1\4\1\1\1"+
        "\4\1\2\1\3\1\37\1\6\1\11\1\5\1\7\1\23\1\12\1\10\1\13\1\33\1\14\1"+
        "\15\1\16\1\17\1\20\1\21\1\22\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
        "\1\34\1\35\1\40\1\41";
    static final String DFA5_specialS =
        "\1\0\73\uffff}>";
    static final String[] DFA5_transitionS = {
            "\11\30\2\11\2\30\1\11\22\30\1\11\1\10\6\30\1\20\1\21\1\30\1"+
            "\24\1\17\1\30\1\27\1\12\1\2\11\3\1\14\1\30\1\5\1\6\1\7\2\30"+
            "\1\4\15\33\1\31\5\33\1\32\5\33\1\22\1\30\1\23\1\25\2\30\14\33"+
            "\1\1\15\33\1\15\1\13\1\16\1\26\uff81\30",
            "\1\34",
            "",
            "",
            "\1\40",
            "\1\41\1\42",
            "\1\44",
            "\1\46",
            "\1\50",
            "",
            "\1\53\4\uffff\1\54",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\72",
            "\1\73",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__36 | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_LETTER | RULE_SUBTYPE | RULE_INCLUSIVE_SUBTYPE | RULE_EQUALS_OPERATOR | RULE_GREATER_THAN_OPERATOR | RULE_LESS_EQUALS_OPERATOR | RULE_GREATER_EQUALS_OPERATOR | RULE_NOT_EQUALS_OPERATOR | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_PIPE | RULE_COLON | RULE_OPENING_CURLY_BRACKET | RULE_CLOSING_CURLY_BRACKET | RULE_EQUAL_SIGN | RULE_COMMA | RULE_OPENING_ROUND_BRACKET | RULE_CLOSING_ROUND_BRACKET | RULE_OPENING_SQUARE_BRACKET | RULE_CLOSING_SQUARE_BRACKET | RULE_PLUS_SIGN | RULE_CARET | RULE_NOT_TOKEN | RULE_OPTIONAL | RULE_PERIOD | RULE_OTHER_ALLOWED_TERM_CHARACTER | RULE_AND_TOKEN | RULE_OR_TOKEN | RULE_UNION_TOKEN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_0 = input.LA(1);

                        s = -1;
                        if ( (LA5_0=='m') ) {s = 1;}

                        else if ( (LA5_0=='0') ) {s = 2;}

                        else if ( ((LA5_0>='1' && LA5_0<='9')) ) {s = 3;}

                        else if ( (LA5_0=='A') ) {s = 4;}

                        else if ( (LA5_0=='<') ) {s = 5;}

                        else if ( (LA5_0=='=') ) {s = 6;}

                        else if ( (LA5_0=='>') ) {s = 7;}

                        else if ( (LA5_0=='!') ) {s = 8;}

                        else if ( ((LA5_0>='\t' && LA5_0<='\n')||LA5_0=='\r'||LA5_0==' ') ) {s = 9;}

                        else if ( (LA5_0=='/') ) {s = 10;}

                        else if ( (LA5_0=='|') ) {s = 11;}

                        else if ( (LA5_0==':') ) {s = 12;}

                        else if ( (LA5_0=='{') ) {s = 13;}

                        else if ( (LA5_0=='}') ) {s = 14;}

                        else if ( (LA5_0==',') ) {s = 15;}

                        else if ( (LA5_0=='(') ) {s = 16;}

                        else if ( (LA5_0==')') ) {s = 17;}

                        else if ( (LA5_0=='[') ) {s = 18;}

                        else if ( (LA5_0==']') ) {s = 19;}

                        else if ( (LA5_0=='+') ) {s = 20;}

                        else if ( (LA5_0=='^') ) {s = 21;}

                        else if ( (LA5_0=='~') ) {s = 22;}

                        else if ( (LA5_0=='.') ) {s = 23;}

                        else if ( ((LA5_0>='\u0000' && LA5_0<='\b')||(LA5_0>='\u000B' && LA5_0<='\f')||(LA5_0>='\u000E' && LA5_0<='\u001F')||(LA5_0>='\"' && LA5_0<='\'')||LA5_0=='*'||LA5_0=='-'||LA5_0==';'||(LA5_0>='?' && LA5_0<='@')||LA5_0=='\\'||(LA5_0>='_' && LA5_0<='`')||(LA5_0>='\u007F' && LA5_0<='\uFFFF')) ) {s = 24;}

                        else if ( (LA5_0=='O') ) {s = 25;}

                        else if ( (LA5_0=='U') ) {s = 26;}

                        else if ( ((LA5_0>='B' && LA5_0<='N')||(LA5_0>='P' && LA5_0<='T')||(LA5_0>='V' && LA5_0<='Z')||(LA5_0>='a' && LA5_0<='l')||(LA5_0>='n' && LA5_0<='z')) ) {s = 27;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}