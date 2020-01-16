package com.b2international.snowowl.snomed.scg.parser.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalScgLexer extends Lexer {
    public static final int RULE_DIGIT_NONZERO=8;
    public static final int RULE_CURLY_OPEN=9;
    public static final int RULE_STRING=24;
    public static final int RULE_SL_COMMENT=23;
    public static final int RULE_HASH=20;
    public static final int RULE_EQUIVALENT_TO=4;
    public static final int RULE_DASH=15;
    public static final int RULE_PLUS=14;
    public static final int RULE_DOT=16;
    public static final int EOF=-1;
    public static final int RULE_EQUAL_SIGN=12;
    public static final int RULE_SUBTYPE_OF=5;
    public static final int RULE_COMMA=11;
    public static final int RULE_WS=21;
    public static final int RULE_CURLY_CLOSE=10;
    public static final int RULE_ZERO=7;
    public static final int RULE_CLOSING_ROUND_BRACKET=19;
    public static final int RULE_COLON=13;
    public static final int RULE_QUOTATION_MARK=17;
    public static final int RULE_OPENING_ROUND_BRACKET=18;
    public static final int RULE_ML_COMMENT=22;
    public static final int RULE_TERM_STRING=6;

    // delegates
    // delegators

    public InternalScgLexer() {;} 
    public InternalScgLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalScgLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "InternalScgLexer.g"; }

    // $ANTLR start "RULE_EQUIVALENT_TO"
    public final void mRULE_EQUIVALENT_TO() throws RecognitionException {
        try {
            int _type = RULE_EQUIVALENT_TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:26:20: ( '===' )
            // InternalScgLexer.g:26:22: '==='
            {
            match("==="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_EQUIVALENT_TO"

    // $ANTLR start "RULE_SUBTYPE_OF"
    public final void mRULE_SUBTYPE_OF() throws RecognitionException {
        try {
            int _type = RULE_SUBTYPE_OF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:28:17: ( '<<<' )
            // InternalScgLexer.g:28:19: '<<<'
            {
            match("<<<"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_SUBTYPE_OF"

    // $ANTLR start "RULE_TERM_STRING"
    public final void mRULE_TERM_STRING() throws RecognitionException {
        try {
            int _type = RULE_TERM_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:30:18: ( '|' (~ ( '|' ) )* '|' )
            // InternalScgLexer.g:30:20: '|' (~ ( '|' ) )* '|'
            {
            match('|'); 
            // InternalScgLexer.g:30:24: (~ ( '|' ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='\u0000' && LA1_0<='{')||(LA1_0>='}' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalScgLexer.g:30:24: ~ ( '|' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='{')||(input.LA(1)>='}' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_TERM_STRING"

    // $ANTLR start "RULE_ZERO"
    public final void mRULE_ZERO() throws RecognitionException {
        try {
            int _type = RULE_ZERO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:32:11: ( '0' )
            // InternalScgLexer.g:32:13: '0'
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
            // InternalScgLexer.g:34:20: ( '1' .. '9' )
            // InternalScgLexer.g:34:22: '1' .. '9'
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

    // $ANTLR start "RULE_CURLY_OPEN"
    public final void mRULE_CURLY_OPEN() throws RecognitionException {
        try {
            int _type = RULE_CURLY_OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:36:17: ( '{' )
            // InternalScgLexer.g:36:19: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_CURLY_OPEN"

    // $ANTLR start "RULE_CURLY_CLOSE"
    public final void mRULE_CURLY_CLOSE() throws RecognitionException {
        try {
            int _type = RULE_CURLY_CLOSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:38:18: ( '}' )
            // InternalScgLexer.g:38:20: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_CURLY_CLOSE"

    // $ANTLR start "RULE_COMMA"
    public final void mRULE_COMMA() throws RecognitionException {
        try {
            int _type = RULE_COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:40:12: ( ',' )
            // InternalScgLexer.g:40:14: ','
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

    // $ANTLR start "RULE_EQUAL_SIGN"
    public final void mRULE_EQUAL_SIGN() throws RecognitionException {
        try {
            int _type = RULE_EQUAL_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:42:17: ( '=' )
            // InternalScgLexer.g:42:19: '='
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

    // $ANTLR start "RULE_COLON"
    public final void mRULE_COLON() throws RecognitionException {
        try {
            int _type = RULE_COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:44:12: ( ':' )
            // InternalScgLexer.g:44:14: ':'
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

    // $ANTLR start "RULE_PLUS"
    public final void mRULE_PLUS() throws RecognitionException {
        try {
            int _type = RULE_PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:46:11: ( '+' )
            // InternalScgLexer.g:46:13: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_PLUS"

    // $ANTLR start "RULE_DASH"
    public final void mRULE_DASH() throws RecognitionException {
        try {
            int _type = RULE_DASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:48:11: ( '-' )
            // InternalScgLexer.g:48:13: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DASH"

    // $ANTLR start "RULE_DOT"
    public final void mRULE_DOT() throws RecognitionException {
        try {
            int _type = RULE_DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:50:10: ( '.' )
            // InternalScgLexer.g:50:12: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DOT"

    // $ANTLR start "RULE_QUOTATION_MARK"
    public final void mRULE_QUOTATION_MARK() throws RecognitionException {
        try {
            int _type = RULE_QUOTATION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:52:21: ( '\"' )
            // InternalScgLexer.g:52:23: '\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_QUOTATION_MARK"

    // $ANTLR start "RULE_OPENING_ROUND_BRACKET"
    public final void mRULE_OPENING_ROUND_BRACKET() throws RecognitionException {
        try {
            int _type = RULE_OPENING_ROUND_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:54:28: ( '(' )
            // InternalScgLexer.g:54:30: '('
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
            // InternalScgLexer.g:56:28: ( ')' )
            // InternalScgLexer.g:56:30: ')'
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

    // $ANTLR start "RULE_HASH"
    public final void mRULE_HASH() throws RecognitionException {
        try {
            int _type = RULE_HASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:58:11: ( '#' )
            // InternalScgLexer.g:58:13: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_HASH"

    // $ANTLR start "RULE_WS"
    public final void mRULE_WS() throws RecognitionException {
        try {
            int _type = RULE_WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:60:9: ( ( ' ' | '\\t' | '\\n' | '\\r' ) )
            // InternalScgLexer.g:60:11: ( ' ' | '\\t' | '\\n' | '\\r' )
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
            // InternalScgLexer.g:62:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // InternalScgLexer.g:62:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // InternalScgLexer.g:62:24: ( options {greedy=false; } : . )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='*') ) {
                    int LA2_1 = input.LA(2);

                    if ( (LA2_1=='/') ) {
                        alt2=2;
                    }
                    else if ( ((LA2_1>='\u0000' && LA2_1<='.')||(LA2_1>='0' && LA2_1<='\uFFFF')) ) {
                        alt2=1;
                    }


                }
                else if ( ((LA2_0>='\u0000' && LA2_0<=')')||(LA2_0>='+' && LA2_0<='\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // InternalScgLexer.g:62:52: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop2;
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
            // InternalScgLexer.g:64:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // InternalScgLexer.g:64:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // InternalScgLexer.g:64:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='\t')||(LA3_0>='\u000B' && LA3_0<='\f')||(LA3_0>='\u000E' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalScgLexer.g:64:24: ~ ( ( '\\n' | '\\r' ) )
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
            	    break loop3;
                }
            } while (true);

            // InternalScgLexer.g:64:40: ( ( '\\r' )? '\\n' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='\n'||LA5_0=='\r') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalScgLexer.g:64:41: ( '\\r' )? '\\n'
                    {
                    // InternalScgLexer.g:64:41: ( '\\r' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='\r') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // InternalScgLexer.g:64:41: '\\r'
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

    // $ANTLR start "RULE_STRING"
    public final void mRULE_STRING() throws RecognitionException {
        try {
            int _type = RULE_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalScgLexer.g:66:13: ( ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // InternalScgLexer.g:66:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // InternalScgLexer.g:66:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0=='\"') ) {
                alt8=1;
            }
            else if ( (LA8_0=='\'') ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // InternalScgLexer.g:66:16: '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // InternalScgLexer.g:66:20: ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )*
                    loop6:
                    do {
                        int alt6=3;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0=='\\') ) {
                            alt6=1;
                        }
                        else if ( ((LA6_0>='\u0000' && LA6_0<='!')||(LA6_0>='#' && LA6_0<='[')||(LA6_0>=']' && LA6_0<='\uFFFF')) ) {
                            alt6=2;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // InternalScgLexer.g:66:21: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalScgLexer.g:66:28: ~ ( ( '\\\\' | '\"' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 2 :
                    // InternalScgLexer.g:66:48: '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // InternalScgLexer.g:66:53: ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )*
                    loop7:
                    do {
                        int alt7=3;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0=='\\') ) {
                            alt7=1;
                        }
                        else if ( ((LA7_0>='\u0000' && LA7_0<='&')||(LA7_0>='(' && LA7_0<='[')||(LA7_0>=']' && LA7_0<='\uFFFF')) ) {
                            alt7=2;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // InternalScgLexer.g:66:54: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalScgLexer.g:66:61: ~ ( ( '\\\\' | '\\'' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    match('\''); 

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
    // $ANTLR end "RULE_STRING"

    public void mTokens() throws RecognitionException {
        // InternalScgLexer.g:1:8: ( RULE_EQUIVALENT_TO | RULE_SUBTYPE_OF | RULE_TERM_STRING | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_COMMA | RULE_EQUAL_SIGN | RULE_COLON | RULE_PLUS | RULE_DASH | RULE_DOT | RULE_QUOTATION_MARK | RULE_OPENING_ROUND_BRACKET | RULE_CLOSING_ROUND_BRACKET | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_STRING )
        int alt9=21;
        alt9 = dfa9.predict(input);
        switch (alt9) {
            case 1 :
                // InternalScgLexer.g:1:10: RULE_EQUIVALENT_TO
                {
                mRULE_EQUIVALENT_TO(); 

                }
                break;
            case 2 :
                // InternalScgLexer.g:1:29: RULE_SUBTYPE_OF
                {
                mRULE_SUBTYPE_OF(); 

                }
                break;
            case 3 :
                // InternalScgLexer.g:1:45: RULE_TERM_STRING
                {
                mRULE_TERM_STRING(); 

                }
                break;
            case 4 :
                // InternalScgLexer.g:1:62: RULE_ZERO
                {
                mRULE_ZERO(); 

                }
                break;
            case 5 :
                // InternalScgLexer.g:1:72: RULE_DIGIT_NONZERO
                {
                mRULE_DIGIT_NONZERO(); 

                }
                break;
            case 6 :
                // InternalScgLexer.g:1:91: RULE_CURLY_OPEN
                {
                mRULE_CURLY_OPEN(); 

                }
                break;
            case 7 :
                // InternalScgLexer.g:1:107: RULE_CURLY_CLOSE
                {
                mRULE_CURLY_CLOSE(); 

                }
                break;
            case 8 :
                // InternalScgLexer.g:1:124: RULE_COMMA
                {
                mRULE_COMMA(); 

                }
                break;
            case 9 :
                // InternalScgLexer.g:1:135: RULE_EQUAL_SIGN
                {
                mRULE_EQUAL_SIGN(); 

                }
                break;
            case 10 :
                // InternalScgLexer.g:1:151: RULE_COLON
                {
                mRULE_COLON(); 

                }
                break;
            case 11 :
                // InternalScgLexer.g:1:162: RULE_PLUS
                {
                mRULE_PLUS(); 

                }
                break;
            case 12 :
                // InternalScgLexer.g:1:172: RULE_DASH
                {
                mRULE_DASH(); 

                }
                break;
            case 13 :
                // InternalScgLexer.g:1:182: RULE_DOT
                {
                mRULE_DOT(); 

                }
                break;
            case 14 :
                // InternalScgLexer.g:1:191: RULE_QUOTATION_MARK
                {
                mRULE_QUOTATION_MARK(); 

                }
                break;
            case 15 :
                // InternalScgLexer.g:1:211: RULE_OPENING_ROUND_BRACKET
                {
                mRULE_OPENING_ROUND_BRACKET(); 

                }
                break;
            case 16 :
                // InternalScgLexer.g:1:238: RULE_CLOSING_ROUND_BRACKET
                {
                mRULE_CLOSING_ROUND_BRACKET(); 

                }
                break;
            case 17 :
                // InternalScgLexer.g:1:265: RULE_HASH
                {
                mRULE_HASH(); 

                }
                break;
            case 18 :
                // InternalScgLexer.g:1:275: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 19 :
                // InternalScgLexer.g:1:283: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 20 :
                // InternalScgLexer.g:1:299: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 21 :
                // InternalScgLexer.g:1:315: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
        "\1\uffff\1\25\13\uffff\1\26\13\uffff";
    static final String DFA9_eofS =
        "\31\uffff";
    static final String DFA9_minS =
        "\1\11\1\75\13\uffff\1\0\4\uffff\1\52\6\uffff";
    static final String DFA9_maxS =
        "\1\175\1\75\13\uffff\1\uffff\4\uffff\1\57\6\uffff";
    static final String DFA9_acceptS =
        "\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\12\1\13\1\14\1\15\1\uffff\1\17\1\20\1\21\1\22\1\uffff\1\25\1\1\1\11\1\16\1\23\1\24";
    static final String DFA9_specialS =
        "\15\uffff\1\0\13\uffff}>";
    static final String[] DFA9_transitionS = DFA9_transitionS_.DFA9_transitionS;
    private static final class DFA9_transitionS_ {
        static final String[] DFA9_transitionS = {
                "\2\21\2\uffff\1\21\22\uffff\1\21\1\uffff\1\15\1\20\3\uffff\1\23\1\16\1\17\1\uffff\1\12\1\10\1\13\1\14\1\22\1\4\11\5\1\11\1\uffff\1\2\1\1\75\uffff\1\6\1\3\1\7",
                "\1\24",
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
                "\0\23",
                "",
                "",
                "",
                "",
                "\1\27\4\uffff\1\30",
                "",
                "",
                "",
                "",
                "",
                ""
        };
    }

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    static class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( RULE_EQUIVALENT_TO | RULE_SUBTYPE_OF | RULE_TERM_STRING | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_COMMA | RULE_EQUAL_SIGN | RULE_COLON | RULE_PLUS | RULE_DASH | RULE_DOT | RULE_QUOTATION_MARK | RULE_OPENING_ROUND_BRACKET | RULE_CLOSING_ROUND_BRACKET | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_STRING );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA9_13 = input.LA(1);

                        s = -1;
                        if ( ((LA9_13>='\u0000' && LA9_13<='\uFFFF')) ) {s = 19;}

                        else s = 22;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 9, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}