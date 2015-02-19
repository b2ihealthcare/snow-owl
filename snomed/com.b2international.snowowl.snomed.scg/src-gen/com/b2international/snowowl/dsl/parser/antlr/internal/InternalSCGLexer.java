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
public class InternalSCGLexer extends Lexer {
    public static final int RULE_ZERO=15;
    public static final int RULE_OPENING_CURLY_BRACKET=9;
    public static final int RULE_EQUAL_SIGN=11;
    public static final int RULE_COMMA=6;
    public static final int RULE_DIGIT_NONZERO=14;
    public static final int RULE_OPENING_ROUND_BRACKET=12;
    public static final int RULE_OTHER_ALLOWED_TERM_CHARACTER=17;
    public static final int EOF=-1;
    public static final int RULE_SL_COMMENT=19;
    public static final int RULE_LETTER=16;
    public static final int RULE_ML_COMMENT=18;
    public static final int RULE_CLOSING_CURLY_BRACKET=10;
    public static final int RULE_COLON=5;
    public static final int RULE_CLOSING_ROUND_BRACKET=13;
    public static final int RULE_PIPE=7;
    public static final int RULE_PLUS_SIGN=4;
    public static final int RULE_WS=8;

    // delegates
    // delegators

    public InternalSCGLexer() {;} 
    public InternalSCGLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalSCGLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g"; }

    // $ANTLR start "RULE_ZERO"
    public final void mRULE_ZERO() throws RecognitionException {
        try {
            int _type = RULE_ZERO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:761:11: ( '0' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:761:13: '0'
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:763:20: ( '1' .. '9' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:763:22: '1' .. '9'
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:765:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:765:15: ( 'a' .. 'z' | 'A' .. 'Z' )
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

    // $ANTLR start "RULE_WS"
    public final void mRULE_WS() throws RecognitionException {
        try {
            int _type = RULE_WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:767:9: ( ( ' ' | '\\t' | '\\n' | '\\r' ) )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:767:11: ( ' ' | '\\t' | '\\n' | '\\r' )
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:769:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:769:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:769:24: ( options {greedy=false; } : . )*
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
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:769:52: .
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:771:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:771:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:771:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\u0000' && LA2_0<='\t')||(LA2_0>='\u000B' && LA2_0<='\f')||(LA2_0>='\u000E' && LA2_0<='\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:771:24: ~ ( ( '\\n' | '\\r' ) )
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

            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:771:40: ( ( '\\r' )? '\\n' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='\n'||LA4_0=='\r') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:771:41: ( '\\r' )? '\\n'
                    {
                    // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:771:41: ( '\\r' )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0=='\r') ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:771:41: '\\r'
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:773:11: ( '|' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:773:13: '|'
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:775:12: ( ':' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:775:14: ':'
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:777:28: ( '{' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:777:30: '{'
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:779:28: ( '}' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:779:30: '}'
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:781:17: ( '=' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:781:19: '='
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:783:12: ( ',' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:783:14: ','
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:785:28: ( '(' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:785:30: '('
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
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:787:28: ( ')' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:787:30: ')'
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

    // $ANTLR start "RULE_PLUS_SIGN"
    public final void mRULE_PLUS_SIGN() throws RecognitionException {
        try {
            int _type = RULE_PLUS_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:789:16: ( '+' )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:789:18: '+'
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

    // $ANTLR start "RULE_OTHER_ALLOWED_TERM_CHARACTER"
    public final void mRULE_OTHER_ALLOWED_TERM_CHARACTER() throws RecognitionException {
        try {
            int _type = RULE_OTHER_ALLOWED_TERM_CHARACTER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:791:35: ( . )
            // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:791:37: .
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

    public void mTokens() throws RecognitionException {
        // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:8: ( RULE_ZERO | RULE_DIGIT_NONZERO | RULE_LETTER | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_PIPE | RULE_COLON | RULE_OPENING_CURLY_BRACKET | RULE_CLOSING_CURLY_BRACKET | RULE_EQUAL_SIGN | RULE_COMMA | RULE_OPENING_ROUND_BRACKET | RULE_CLOSING_ROUND_BRACKET | RULE_PLUS_SIGN | RULE_OTHER_ALLOWED_TERM_CHARACTER )
        int alt5=16;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:10: RULE_ZERO
                {
                mRULE_ZERO(); 

                }
                break;
            case 2 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:20: RULE_DIGIT_NONZERO
                {
                mRULE_DIGIT_NONZERO(); 

                }
                break;
            case 3 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:39: RULE_LETTER
                {
                mRULE_LETTER(); 

                }
                break;
            case 4 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:51: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 5 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:59: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 6 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:75: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 7 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:91: RULE_PIPE
                {
                mRULE_PIPE(); 

                }
                break;
            case 8 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:101: RULE_COLON
                {
                mRULE_COLON(); 

                }
                break;
            case 9 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:112: RULE_OPENING_CURLY_BRACKET
                {
                mRULE_OPENING_CURLY_BRACKET(); 

                }
                break;
            case 10 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:139: RULE_CLOSING_CURLY_BRACKET
                {
                mRULE_CLOSING_CURLY_BRACKET(); 

                }
                break;
            case 11 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:166: RULE_EQUAL_SIGN
                {
                mRULE_EQUAL_SIGN(); 

                }
                break;
            case 12 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:182: RULE_COMMA
                {
                mRULE_COMMA(); 

                }
                break;
            case 13 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:193: RULE_OPENING_ROUND_BRACKET
                {
                mRULE_OPENING_ROUND_BRACKET(); 

                }
                break;
            case 14 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:220: RULE_CLOSING_ROUND_BRACKET
                {
                mRULE_CLOSING_ROUND_BRACKET(); 

                }
                break;
            case 15 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:247: RULE_PLUS_SIGN
                {
                mRULE_PLUS_SIGN(); 

                }
                break;
            case 16 :
                // ../com.b2international.snowowl.dsl.scg/src-gen/com/b2international/snowowl/dsl/parser/antlr/internal/InternalSCG.g:1:262: RULE_OTHER_ALLOWED_TERM_CHARACTER
                {
                mRULE_OTHER_ALLOWED_TERM_CHARACTER(); 

                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\5\uffff\1\17\31\uffff";
    static final String DFA5_eofS =
        "\37\uffff";
    static final String DFA5_minS =
        "\1\0\4\uffff\1\52\31\uffff";
    static final String DFA5_maxS =
        "\1\uffff\4\uffff\1\57\31\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\16\1\17\1\20\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1"+
        "\13\1\14\1\15\1\16\1\17";
    static final String DFA5_specialS =
        "\1\0\36\uffff}>";
    static final String[] DFA5_transitionS = {
            "\11\17\2\4\2\17\1\4\22\17\1\4\7\17\1\14\1\15\1\17\1\16\1\13"+
            "\2\17\1\5\1\1\11\2\1\7\2\17\1\12\3\17\32\3\6\17\32\3\1\10\1"+
            "\6\1\11\uff82\17",
            "",
            "",
            "",
            "",
            "\1\24\4\uffff\1\25",
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
            return "1:1: Tokens : ( RULE_ZERO | RULE_DIGIT_NONZERO | RULE_LETTER | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_PIPE | RULE_COLON | RULE_OPENING_CURLY_BRACKET | RULE_CLOSING_CURLY_BRACKET | RULE_EQUAL_SIGN | RULE_COMMA | RULE_OPENING_ROUND_BRACKET | RULE_CLOSING_ROUND_BRACKET | RULE_PLUS_SIGN | RULE_OTHER_ALLOWED_TERM_CHARACTER );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_0 = input.LA(1);

                        s = -1;
                        if ( (LA5_0=='0') ) {s = 1;}

                        else if ( ((LA5_0>='1' && LA5_0<='9')) ) {s = 2;}

                        else if ( ((LA5_0>='A' && LA5_0<='Z')||(LA5_0>='a' && LA5_0<='z')) ) {s = 3;}

                        else if ( ((LA5_0>='\t' && LA5_0<='\n')||LA5_0=='\r'||LA5_0==' ') ) {s = 4;}

                        else if ( (LA5_0=='/') ) {s = 5;}

                        else if ( (LA5_0=='|') ) {s = 6;}

                        else if ( (LA5_0==':') ) {s = 7;}

                        else if ( (LA5_0=='{') ) {s = 8;}

                        else if ( (LA5_0=='}') ) {s = 9;}

                        else if ( (LA5_0=='=') ) {s = 10;}

                        else if ( (LA5_0==',') ) {s = 11;}

                        else if ( (LA5_0=='(') ) {s = 12;}

                        else if ( (LA5_0==')') ) {s = 13;}

                        else if ( (LA5_0=='+') ) {s = 14;}

                        else if ( ((LA5_0>='\u0000' && LA5_0<='\b')||(LA5_0>='\u000B' && LA5_0<='\f')||(LA5_0>='\u000E' && LA5_0<='\u001F')||(LA5_0>='!' && LA5_0<='\'')||LA5_0=='*'||(LA5_0>='-' && LA5_0<='.')||(LA5_0>=';' && LA5_0<='<')||(LA5_0>='>' && LA5_0<='@')||(LA5_0>='[' && LA5_0<='`')||(LA5_0>='~' && LA5_0<='\uFFFF')) ) {s = 15;}

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