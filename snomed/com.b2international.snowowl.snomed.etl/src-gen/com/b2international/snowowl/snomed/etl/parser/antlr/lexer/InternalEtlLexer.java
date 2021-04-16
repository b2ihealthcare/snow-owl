package com.b2international.snowowl.snomed.etl.parser.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalEtlLexer extends Lexer {
    public static final int RULE_DIGIT_NONZERO=31;
    public static final int RULE_CURLY_OPEN=33;
    public static final int RULE_TO=25;
    public static final int RULE_ROUND_CLOSE=36;
    public static final int RULE_DBL_GT=47;
    public static final int True=5;
    public static final int RULE_GT=45;
    public static final int False=4;
    public static final int RULE_SLOTNAME_STRING=22;
    public static final int RULE_SCG=11;
    public static final int RULE_GTE=52;
    public static final int RULE_DOUBLE_SQUARE_OPEN=6;
    public static final int RULE_EQUIVALENT_TO=16;
    public static final int RULE_ROUND_OPEN=35;
    public static final int RULE_DBL_LT=46;
    public static final int RULE_NOT_EQUAL=43;
    public static final int RULE_SQUARE_CLOSE=21;
    public static final int RULE_ID=10;
    public static final int RULE_SQUARE_OPEN=20;
    public static final int RULE_EQUAL=42;
    public static final int RULE_DEC=15;
    public static final int RULE_COMMA=26;
    public static final int RULE_LT_EM=48;
    public static final int RULE_CURLY_CLOSE=34;
    public static final int RULE_ZERO=30;
    public static final int RULE_DBL_GT_EM=51;
    public static final int RULE_COLON=32;
    public static final int RULE_TILDE=8;
    public static final int RULE_LT=44;
    public static final int RULE_INT=14;
    public static final int RULE_ML_COMMENT=55;
    public static final int RULE_DOUBLE_SQUARE_CLOSE=7;
    public static final int RULE_LTE=53;
    public static final int RULE_STRING=18;
    public static final int RULE_AT=9;
    public static final int RULE_REVERSED=24;
    public static final int RULE_SL_COMMENT=56;
    public static final int RULE_HASH=54;
    public static final int RULE_TOK=12;
    public static final int RULE_DASH=38;
    public static final int RULE_PLUS=37;
    public static final int RULE_DOT=40;
    public static final int EOF=-1;
    public static final int RULE_SUBTYPE_OF=17;
    public static final int RULE_WS=19;
    public static final int RULE_GT_EM=50;
    public static final int RULE_EXCLUSION=29;
    public static final int RULE_CARET=39;
    public static final int RULE_CONJUNCTION=27;
    public static final int RULE_STR=13;
    public static final int RULE_WILDCARD=41;
    public static final int RULE_DISJUNCTION=28;
    public static final int RULE_TERM_STRING=23;
    public static final int RULE_DBL_LT_EM=49;

    // delegates
    // delegators

    public InternalEtlLexer() {;} 
    public InternalEtlLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalEtlLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "InternalEtlLexer.g"; }

    // $ANTLR start "False"
    public final void mFalse() throws RecognitionException {
        try {
            int _type = False;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:26:7: ( ( 'F' | 'f' ) ( 'A' | 'a' ) ( 'L' | 'l' ) ( 'S' | 's' ) ( 'E' | 'e' ) )
            // InternalEtlLexer.g:26:9: ( 'F' | 'f' ) ( 'A' | 'a' ) ( 'L' | 'l' ) ( 'S' | 's' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
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
    // $ANTLR end "False"

    // $ANTLR start "True"
    public final void mTrue() throws RecognitionException {
        try {
            int _type = True;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:28:6: ( ( 'T' | 't' ) ( 'R' | 'r' ) ( 'U' | 'u' ) ( 'E' | 'e' ) )
            // InternalEtlLexer.g:28:8: ( 'T' | 't' ) ( 'R' | 'r' ) ( 'U' | 'u' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
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
    // $ANTLR end "True"

    // $ANTLR start "RULE_DOUBLE_SQUARE_OPEN"
    public final void mRULE_DOUBLE_SQUARE_OPEN() throws RecognitionException {
        try {
            int _type = RULE_DOUBLE_SQUARE_OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:30:25: ( '[[' )
            // InternalEtlLexer.g:30:27: '[['
            {
            match("[["); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DOUBLE_SQUARE_OPEN"

    // $ANTLR start "RULE_DOUBLE_SQUARE_CLOSE"
    public final void mRULE_DOUBLE_SQUARE_CLOSE() throws RecognitionException {
        try {
            int _type = RULE_DOUBLE_SQUARE_CLOSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:32:26: ( ']]' )
            // InternalEtlLexer.g:32:28: ']]'
            {
            match("]]"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DOUBLE_SQUARE_CLOSE"

    // $ANTLR start "RULE_TILDE"
    public final void mRULE_TILDE() throws RecognitionException {
        try {
            int _type = RULE_TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:34:12: ( '~' )
            // InternalEtlLexer.g:34:14: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_TILDE"

    // $ANTLR start "RULE_AT"
    public final void mRULE_AT() throws RecognitionException {
        try {
            // InternalEtlLexer.g:36:18: ( '@' )
            // InternalEtlLexer.g:36:20: '@'
            {
            match('@'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "RULE_AT"

    // $ANTLR start "RULE_ID"
    public final void mRULE_ID() throws RecognitionException {
        try {
            int _type = RULE_ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:38:9: ( 'id' )
            // InternalEtlLexer.g:38:11: 'id'
            {
            match("id"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ID"

    // $ANTLR start "RULE_SCG"
    public final void mRULE_SCG() throws RecognitionException {
        try {
            int _type = RULE_SCG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:40:10: ( 'scg' )
            // InternalEtlLexer.g:40:12: 'scg'
            {
            match("scg"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_SCG"

    // $ANTLR start "RULE_TOK"
    public final void mRULE_TOK() throws RecognitionException {
        try {
            int _type = RULE_TOK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:42:10: ( 'tok' )
            // InternalEtlLexer.g:42:12: 'tok'
            {
            match("tok"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_TOK"

    // $ANTLR start "RULE_STR"
    public final void mRULE_STR() throws RecognitionException {
        try {
            int _type = RULE_STR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:44:10: ( 'str' )
            // InternalEtlLexer.g:44:12: 'str'
            {
            match("str"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_STR"

    // $ANTLR start "RULE_INT"
    public final void mRULE_INT() throws RecognitionException {
        try {
            int _type = RULE_INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:46:10: ( 'int' )
            // InternalEtlLexer.g:46:12: 'int'
            {
            match("int"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_INT"

    // $ANTLR start "RULE_DEC"
    public final void mRULE_DEC() throws RecognitionException {
        try {
            int _type = RULE_DEC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:48:10: ( 'dec' )
            // InternalEtlLexer.g:48:12: 'dec'
            {
            match("dec"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DEC"

    // $ANTLR start "RULE_EQUIVALENT_TO"
    public final void mRULE_EQUIVALENT_TO() throws RecognitionException {
        try {
            int _type = RULE_EQUIVALENT_TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:50:20: ( '===' )
            // InternalEtlLexer.g:50:22: '==='
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
            // InternalEtlLexer.g:52:17: ( '<<<' )
            // InternalEtlLexer.g:52:19: '<<<'
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

    // $ANTLR start "RULE_SLOTNAME_STRING"
    public final void mRULE_SLOTNAME_STRING() throws RecognitionException {
        try {
            int _type = RULE_SLOTNAME_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:54:22: ( RULE_AT ( RULE_STRING | (~ ( ( '\\\\' | '\"' | '\\'' | RULE_WS | RULE_AT | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE ) ) )* ) )
            // InternalEtlLexer.g:54:24: RULE_AT ( RULE_STRING | (~ ( ( '\\\\' | '\"' | '\\'' | RULE_WS | RULE_AT | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE ) ) )* )
            {
            mRULE_AT(); 
            // InternalEtlLexer.g:54:32: ( RULE_STRING | (~ ( ( '\\\\' | '\"' | '\\'' | RULE_WS | RULE_AT | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE ) ) )* )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\"'||LA2_0=='\'') ) {
                alt2=1;
            }
            else {
                alt2=2;}
            switch (alt2) {
                case 1 :
                    // InternalEtlLexer.g:54:33: RULE_STRING
                    {
                    mRULE_STRING(); 

                    }
                    break;
                case 2 :
                    // InternalEtlLexer.g:54:45: (~ ( ( '\\\\' | '\"' | '\\'' | RULE_WS | RULE_AT | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE ) ) )*
                    {
                    // InternalEtlLexer.g:54:45: (~ ( ( '\\\\' | '\"' | '\\'' | RULE_WS | RULE_AT | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE ) ) )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='\u0000' && LA1_0<='\b')||(LA1_0>='\u000B' && LA1_0<='\f')||(LA1_0>='\u000E' && LA1_0<='\u001F')||LA1_0=='!'||(LA1_0>='#' && LA1_0<='&')||(LA1_0>='(' && LA1_0<='?')||(LA1_0>='A' && LA1_0<='Z')||(LA1_0>='^' && LA1_0<='\uFFFF')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // InternalEtlLexer.g:54:45: ~ ( ( '\\\\' | '\"' | '\\'' | RULE_WS | RULE_AT | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\u001F')||input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='?')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='^' && input.LA(1)<='\uFFFF') ) {
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
    // $ANTLR end "RULE_SLOTNAME_STRING"

    // $ANTLR start "RULE_TERM_STRING"
    public final void mRULE_TERM_STRING() throws RecognitionException {
        try {
            int _type = RULE_TERM_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:56:18: ( '|' (~ ( '|' ) )* '|' )
            // InternalEtlLexer.g:56:20: '|' (~ ( '|' ) )* '|'
            {
            match('|'); 
            // InternalEtlLexer.g:56:24: (~ ( '|' ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='{')||(LA3_0>='}' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalEtlLexer.g:56:24: ~ ( '|' )
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
            	    break loop3;
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

    // $ANTLR start "RULE_REVERSED"
    public final void mRULE_REVERSED() throws RecognitionException {
        try {
            int _type = RULE_REVERSED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:58:15: ( 'R' )
            // InternalEtlLexer.g:58:17: 'R'
            {
            match('R'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_REVERSED"

    // $ANTLR start "RULE_TO"
    public final void mRULE_TO() throws RecognitionException {
        try {
            int _type = RULE_TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:60:9: ( '..' )
            // InternalEtlLexer.g:60:11: '..'
            {
            match(".."); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_TO"

    // $ANTLR start "RULE_COMMA"
    public final void mRULE_COMMA() throws RecognitionException {
        try {
            int _type = RULE_COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:62:12: ( ',' )
            // InternalEtlLexer.g:62:14: ','
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

    // $ANTLR start "RULE_CONJUNCTION"
    public final void mRULE_CONJUNCTION() throws RecognitionException {
        try {
            int _type = RULE_CONJUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:64:18: ( ( 'a' | 'A' ) ( 'n' | 'N' ) ( 'd' | 'D' ) )
            // InternalEtlLexer.g:64:20: ( 'a' | 'A' ) ( 'n' | 'N' ) ( 'd' | 'D' )
            {
            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
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
    // $ANTLR end "RULE_CONJUNCTION"

    // $ANTLR start "RULE_DISJUNCTION"
    public final void mRULE_DISJUNCTION() throws RecognitionException {
        try {
            int _type = RULE_DISJUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:66:18: ( ( 'o' | 'O' ) ( 'r' | 'R' ) )
            // InternalEtlLexer.g:66:20: ( 'o' | 'O' ) ( 'r' | 'R' )
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
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
    // $ANTLR end "RULE_DISJUNCTION"

    // $ANTLR start "RULE_EXCLUSION"
    public final void mRULE_EXCLUSION() throws RecognitionException {
        try {
            int _type = RULE_EXCLUSION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:68:16: ( ( 'm' | 'M' ) ( 'i' | 'I' ) ( 'n' | 'N' ) ( 'u' | 'U' ) ( 's' | 'S' ) )
            // InternalEtlLexer.g:68:18: ( 'm' | 'M' ) ( 'i' | 'I' ) ( 'n' | 'N' ) ( 'u' | 'U' ) ( 's' | 'S' )
            {
            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
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
    // $ANTLR end "RULE_EXCLUSION"

    // $ANTLR start "RULE_ZERO"
    public final void mRULE_ZERO() throws RecognitionException {
        try {
            int _type = RULE_ZERO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:70:11: ( '0' )
            // InternalEtlLexer.g:70:13: '0'
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
            // InternalEtlLexer.g:72:20: ( '1' .. '9' )
            // InternalEtlLexer.g:72:22: '1' .. '9'
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

    // $ANTLR start "RULE_COLON"
    public final void mRULE_COLON() throws RecognitionException {
        try {
            int _type = RULE_COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:74:12: ( ':' )
            // InternalEtlLexer.g:74:14: ':'
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

    // $ANTLR start "RULE_CURLY_OPEN"
    public final void mRULE_CURLY_OPEN() throws RecognitionException {
        try {
            int _type = RULE_CURLY_OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:76:17: ( '{' )
            // InternalEtlLexer.g:76:19: '{'
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
            // InternalEtlLexer.g:78:18: ( '}' )
            // InternalEtlLexer.g:78:20: '}'
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

    // $ANTLR start "RULE_ROUND_OPEN"
    public final void mRULE_ROUND_OPEN() throws RecognitionException {
        try {
            int _type = RULE_ROUND_OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:80:17: ( '(' )
            // InternalEtlLexer.g:80:19: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ROUND_OPEN"

    // $ANTLR start "RULE_ROUND_CLOSE"
    public final void mRULE_ROUND_CLOSE() throws RecognitionException {
        try {
            int _type = RULE_ROUND_CLOSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:82:18: ( ')' )
            // InternalEtlLexer.g:82:20: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ROUND_CLOSE"

    // $ANTLR start "RULE_SQUARE_OPEN"
    public final void mRULE_SQUARE_OPEN() throws RecognitionException {
        try {
            int _type = RULE_SQUARE_OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:84:18: ( '[' )
            // InternalEtlLexer.g:84:20: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_SQUARE_OPEN"

    // $ANTLR start "RULE_SQUARE_CLOSE"
    public final void mRULE_SQUARE_CLOSE() throws RecognitionException {
        try {
            int _type = RULE_SQUARE_CLOSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:86:19: ( ']' )
            // InternalEtlLexer.g:86:21: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_SQUARE_CLOSE"

    // $ANTLR start "RULE_PLUS"
    public final void mRULE_PLUS() throws RecognitionException {
        try {
            int _type = RULE_PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:88:11: ( '+' )
            // InternalEtlLexer.g:88:13: '+'
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
            // InternalEtlLexer.g:90:11: ( '-' )
            // InternalEtlLexer.g:90:13: '-'
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

    // $ANTLR start "RULE_CARET"
    public final void mRULE_CARET() throws RecognitionException {
        try {
            int _type = RULE_CARET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:92:12: ( '^' )
            // InternalEtlLexer.g:92:14: '^'
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

    // $ANTLR start "RULE_DOT"
    public final void mRULE_DOT() throws RecognitionException {
        try {
            int _type = RULE_DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:94:10: ( '.' )
            // InternalEtlLexer.g:94:12: '.'
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

    // $ANTLR start "RULE_WILDCARD"
    public final void mRULE_WILDCARD() throws RecognitionException {
        try {
            int _type = RULE_WILDCARD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:96:15: ( '*' )
            // InternalEtlLexer.g:96:17: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_WILDCARD"

    // $ANTLR start "RULE_EQUAL"
    public final void mRULE_EQUAL() throws RecognitionException {
        try {
            int _type = RULE_EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:98:12: ( '=' )
            // InternalEtlLexer.g:98:14: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_EQUAL"

    // $ANTLR start "RULE_NOT_EQUAL"
    public final void mRULE_NOT_EQUAL() throws RecognitionException {
        try {
            int _type = RULE_NOT_EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:100:16: ( '!=' )
            // InternalEtlLexer.g:100:18: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_NOT_EQUAL"

    // $ANTLR start "RULE_LT"
    public final void mRULE_LT() throws RecognitionException {
        try {
            int _type = RULE_LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:102:9: ( '<' )
            // InternalEtlLexer.g:102:11: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_LT"

    // $ANTLR start "RULE_GT"
    public final void mRULE_GT() throws RecognitionException {
        try {
            int _type = RULE_GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:104:9: ( '>' )
            // InternalEtlLexer.g:104:11: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_GT"

    // $ANTLR start "RULE_DBL_LT"
    public final void mRULE_DBL_LT() throws RecognitionException {
        try {
            int _type = RULE_DBL_LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:106:13: ( '<<' )
            // InternalEtlLexer.g:106:15: '<<'
            {
            match("<<"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DBL_LT"

    // $ANTLR start "RULE_DBL_GT"
    public final void mRULE_DBL_GT() throws RecognitionException {
        try {
            int _type = RULE_DBL_GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:108:13: ( '>>' )
            // InternalEtlLexer.g:108:15: '>>'
            {
            match(">>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DBL_GT"

    // $ANTLR start "RULE_LT_EM"
    public final void mRULE_LT_EM() throws RecognitionException {
        try {
            int _type = RULE_LT_EM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:110:12: ( '<!' )
            // InternalEtlLexer.g:110:14: '<!'
            {
            match("<!"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_LT_EM"

    // $ANTLR start "RULE_DBL_LT_EM"
    public final void mRULE_DBL_LT_EM() throws RecognitionException {
        try {
            int _type = RULE_DBL_LT_EM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:112:16: ( '<<!' )
            // InternalEtlLexer.g:112:18: '<<!'
            {
            match("<<!"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DBL_LT_EM"

    // $ANTLR start "RULE_GT_EM"
    public final void mRULE_GT_EM() throws RecognitionException {
        try {
            int _type = RULE_GT_EM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:114:12: ( '>!' )
            // InternalEtlLexer.g:114:14: '>!'
            {
            match(">!"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_GT_EM"

    // $ANTLR start "RULE_DBL_GT_EM"
    public final void mRULE_DBL_GT_EM() throws RecognitionException {
        try {
            int _type = RULE_DBL_GT_EM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:116:16: ( '>>!' )
            // InternalEtlLexer.g:116:18: '>>!'
            {
            match(">>!"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DBL_GT_EM"

    // $ANTLR start "RULE_GTE"
    public final void mRULE_GTE() throws RecognitionException {
        try {
            int _type = RULE_GTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:118:10: ( '>=' )
            // InternalEtlLexer.g:118:12: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_GTE"

    // $ANTLR start "RULE_LTE"
    public final void mRULE_LTE() throws RecognitionException {
        try {
            int _type = RULE_LTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:120:10: ( '<=' )
            // InternalEtlLexer.g:120:12: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_LTE"

    // $ANTLR start "RULE_HASH"
    public final void mRULE_HASH() throws RecognitionException {
        try {
            int _type = RULE_HASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEtlLexer.g:122:11: ( '#' )
            // InternalEtlLexer.g:122:13: '#'
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
            // InternalEtlLexer.g:124:9: ( ( ' ' | '\\t' | '\\n' | '\\r' ) )
            // InternalEtlLexer.g:124:11: ( ' ' | '\\t' | '\\n' | '\\r' )
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
            // InternalEtlLexer.g:126:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // InternalEtlLexer.g:126:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // InternalEtlLexer.g:126:24: ( options {greedy=false; } : . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='*') ) {
                    int LA4_1 = input.LA(2);

                    if ( (LA4_1=='/') ) {
                        alt4=2;
                    }
                    else if ( ((LA4_1>='\u0000' && LA4_1<='.')||(LA4_1>='0' && LA4_1<='\uFFFF')) ) {
                        alt4=1;
                    }


                }
                else if ( ((LA4_0>='\u0000' && LA4_0<=')')||(LA4_0>='+' && LA4_0<='\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // InternalEtlLexer.g:126:52: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop4;
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
            // InternalEtlLexer.g:128:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // InternalEtlLexer.g:128:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // InternalEtlLexer.g:128:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='\u0000' && LA5_0<='\t')||(LA5_0>='\u000B' && LA5_0<='\f')||(LA5_0>='\u000E' && LA5_0<='\uFFFF')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // InternalEtlLexer.g:128:24: ~ ( ( '\\n' | '\\r' ) )
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
            	    break loop5;
                }
            } while (true);

            // InternalEtlLexer.g:128:40: ( ( '\\r' )? '\\n' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='\n'||LA7_0=='\r') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // InternalEtlLexer.g:128:41: ( '\\r' )? '\\n'
                    {
                    // InternalEtlLexer.g:128:41: ( '\\r' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='\r') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // InternalEtlLexer.g:128:41: '\\r'
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
            // InternalEtlLexer.g:130:13: ( ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // InternalEtlLexer.g:130:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // InternalEtlLexer.g:130:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\"') ) {
                alt10=1;
            }
            else if ( (LA10_0=='\'') ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // InternalEtlLexer.g:130:16: '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // InternalEtlLexer.g:130:20: ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )*
                    loop8:
                    do {
                        int alt8=3;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0=='\\') ) {
                            alt8=1;
                        }
                        else if ( ((LA8_0>='\u0000' && LA8_0<='!')||(LA8_0>='#' && LA8_0<='[')||(LA8_0>=']' && LA8_0<='\uFFFF')) ) {
                            alt8=2;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // InternalEtlLexer.g:130:21: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalEtlLexer.g:130:28: ~ ( ( '\\\\' | '\"' ) )
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
                    	    break loop8;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 2 :
                    // InternalEtlLexer.g:130:48: '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // InternalEtlLexer.g:130:53: ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )*
                    loop9:
                    do {
                        int alt9=3;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0=='\\') ) {
                            alt9=1;
                        }
                        else if ( ((LA9_0>='\u0000' && LA9_0<='&')||(LA9_0>='(' && LA9_0<='[')||(LA9_0>=']' && LA9_0<='\uFFFF')) ) {
                            alt9=2;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // InternalEtlLexer.g:130:54: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalEtlLexer.g:130:61: ~ ( ( '\\\\' | '\\'' ) )
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
                    	    break loop9;
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
        // InternalEtlLexer.g:1:8: ( False | True | RULE_DOUBLE_SQUARE_OPEN | RULE_DOUBLE_SQUARE_CLOSE | RULE_TILDE | RULE_ID | RULE_SCG | RULE_TOK | RULE_STR | RULE_INT | RULE_DEC | RULE_EQUIVALENT_TO | RULE_SUBTYPE_OF | RULE_SLOTNAME_STRING | RULE_TERM_STRING | RULE_REVERSED | RULE_TO | RULE_COMMA | RULE_CONJUNCTION | RULE_DISJUNCTION | RULE_EXCLUSION | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_DBL_LT_EM | RULE_GT_EM | RULE_DBL_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_STRING )
        int alt11=52;
        alt11 = dfa11.predict(input);
        switch (alt11) {
            case 1 :
                // InternalEtlLexer.g:1:10: False
                {
                mFalse(); 

                }
                break;
            case 2 :
                // InternalEtlLexer.g:1:16: True
                {
                mTrue(); 

                }
                break;
            case 3 :
                // InternalEtlLexer.g:1:21: RULE_DOUBLE_SQUARE_OPEN
                {
                mRULE_DOUBLE_SQUARE_OPEN(); 

                }
                break;
            case 4 :
                // InternalEtlLexer.g:1:45: RULE_DOUBLE_SQUARE_CLOSE
                {
                mRULE_DOUBLE_SQUARE_CLOSE(); 

                }
                break;
            case 5 :
                // InternalEtlLexer.g:1:70: RULE_TILDE
                {
                mRULE_TILDE(); 

                }
                break;
            case 6 :
                // InternalEtlLexer.g:1:81: RULE_ID
                {
                mRULE_ID(); 

                }
                break;
            case 7 :
                // InternalEtlLexer.g:1:89: RULE_SCG
                {
                mRULE_SCG(); 

                }
                break;
            case 8 :
                // InternalEtlLexer.g:1:98: RULE_TOK
                {
                mRULE_TOK(); 

                }
                break;
            case 9 :
                // InternalEtlLexer.g:1:107: RULE_STR
                {
                mRULE_STR(); 

                }
                break;
            case 10 :
                // InternalEtlLexer.g:1:116: RULE_INT
                {
                mRULE_INT(); 

                }
                break;
            case 11 :
                // InternalEtlLexer.g:1:125: RULE_DEC
                {
                mRULE_DEC(); 

                }
                break;
            case 12 :
                // InternalEtlLexer.g:1:134: RULE_EQUIVALENT_TO
                {
                mRULE_EQUIVALENT_TO(); 

                }
                break;
            case 13 :
                // InternalEtlLexer.g:1:153: RULE_SUBTYPE_OF
                {
                mRULE_SUBTYPE_OF(); 

                }
                break;
            case 14 :
                // InternalEtlLexer.g:1:169: RULE_SLOTNAME_STRING
                {
                mRULE_SLOTNAME_STRING(); 

                }
                break;
            case 15 :
                // InternalEtlLexer.g:1:190: RULE_TERM_STRING
                {
                mRULE_TERM_STRING(); 

                }
                break;
            case 16 :
                // InternalEtlLexer.g:1:207: RULE_REVERSED
                {
                mRULE_REVERSED(); 

                }
                break;
            case 17 :
                // InternalEtlLexer.g:1:221: RULE_TO
                {
                mRULE_TO(); 

                }
                break;
            case 18 :
                // InternalEtlLexer.g:1:229: RULE_COMMA
                {
                mRULE_COMMA(); 

                }
                break;
            case 19 :
                // InternalEtlLexer.g:1:240: RULE_CONJUNCTION
                {
                mRULE_CONJUNCTION(); 

                }
                break;
            case 20 :
                // InternalEtlLexer.g:1:257: RULE_DISJUNCTION
                {
                mRULE_DISJUNCTION(); 

                }
                break;
            case 21 :
                // InternalEtlLexer.g:1:274: RULE_EXCLUSION
                {
                mRULE_EXCLUSION(); 

                }
                break;
            case 22 :
                // InternalEtlLexer.g:1:289: RULE_ZERO
                {
                mRULE_ZERO(); 

                }
                break;
            case 23 :
                // InternalEtlLexer.g:1:299: RULE_DIGIT_NONZERO
                {
                mRULE_DIGIT_NONZERO(); 

                }
                break;
            case 24 :
                // InternalEtlLexer.g:1:318: RULE_COLON
                {
                mRULE_COLON(); 

                }
                break;
            case 25 :
                // InternalEtlLexer.g:1:329: RULE_CURLY_OPEN
                {
                mRULE_CURLY_OPEN(); 

                }
                break;
            case 26 :
                // InternalEtlLexer.g:1:345: RULE_CURLY_CLOSE
                {
                mRULE_CURLY_CLOSE(); 

                }
                break;
            case 27 :
                // InternalEtlLexer.g:1:362: RULE_ROUND_OPEN
                {
                mRULE_ROUND_OPEN(); 

                }
                break;
            case 28 :
                // InternalEtlLexer.g:1:378: RULE_ROUND_CLOSE
                {
                mRULE_ROUND_CLOSE(); 

                }
                break;
            case 29 :
                // InternalEtlLexer.g:1:395: RULE_SQUARE_OPEN
                {
                mRULE_SQUARE_OPEN(); 

                }
                break;
            case 30 :
                // InternalEtlLexer.g:1:412: RULE_SQUARE_CLOSE
                {
                mRULE_SQUARE_CLOSE(); 

                }
                break;
            case 31 :
                // InternalEtlLexer.g:1:430: RULE_PLUS
                {
                mRULE_PLUS(); 

                }
                break;
            case 32 :
                // InternalEtlLexer.g:1:440: RULE_DASH
                {
                mRULE_DASH(); 

                }
                break;
            case 33 :
                // InternalEtlLexer.g:1:450: RULE_CARET
                {
                mRULE_CARET(); 

                }
                break;
            case 34 :
                // InternalEtlLexer.g:1:461: RULE_DOT
                {
                mRULE_DOT(); 

                }
                break;
            case 35 :
                // InternalEtlLexer.g:1:470: RULE_WILDCARD
                {
                mRULE_WILDCARD(); 

                }
                break;
            case 36 :
                // InternalEtlLexer.g:1:484: RULE_EQUAL
                {
                mRULE_EQUAL(); 

                }
                break;
            case 37 :
                // InternalEtlLexer.g:1:495: RULE_NOT_EQUAL
                {
                mRULE_NOT_EQUAL(); 

                }
                break;
            case 38 :
                // InternalEtlLexer.g:1:510: RULE_LT
                {
                mRULE_LT(); 

                }
                break;
            case 39 :
                // InternalEtlLexer.g:1:518: RULE_GT
                {
                mRULE_GT(); 

                }
                break;
            case 40 :
                // InternalEtlLexer.g:1:526: RULE_DBL_LT
                {
                mRULE_DBL_LT(); 

                }
                break;
            case 41 :
                // InternalEtlLexer.g:1:538: RULE_DBL_GT
                {
                mRULE_DBL_GT(); 

                }
                break;
            case 42 :
                // InternalEtlLexer.g:1:550: RULE_LT_EM
                {
                mRULE_LT_EM(); 

                }
                break;
            case 43 :
                // InternalEtlLexer.g:1:561: RULE_DBL_LT_EM
                {
                mRULE_DBL_LT_EM(); 

                }
                break;
            case 44 :
                // InternalEtlLexer.g:1:576: RULE_GT_EM
                {
                mRULE_GT_EM(); 

                }
                break;
            case 45 :
                // InternalEtlLexer.g:1:587: RULE_DBL_GT_EM
                {
                mRULE_DBL_GT_EM(); 

                }
                break;
            case 46 :
                // InternalEtlLexer.g:1:602: RULE_GTE
                {
                mRULE_GTE(); 

                }
                break;
            case 47 :
                // InternalEtlLexer.g:1:611: RULE_LTE
                {
                mRULE_LTE(); 

                }
                break;
            case 48 :
                // InternalEtlLexer.g:1:620: RULE_HASH
                {
                mRULE_HASH(); 

                }
                break;
            case 49 :
                // InternalEtlLexer.g:1:630: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 50 :
                // InternalEtlLexer.g:1:638: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 51 :
                // InternalEtlLexer.g:1:654: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 52 :
                // InternalEtlLexer.g:1:670: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;

        }

    }


    protected DFA11 dfa11 = new DFA11(this);
    static final String DFA11_eotS =
        "\3\uffff\1\47\1\51\5\uffff\1\57\1\63\3\uffff\1\65\20\uffff\1\71\17\uffff\1\76\5\uffff\1\100\12\uffff";
    static final String DFA11_eofS =
        "\101\uffff";
    static final String DFA11_minS =
        "\1\11\1\uffff\1\122\1\133\1\135\1\uffff\1\144\1\143\2\uffff\1\75\1\41\3\uffff\1\56\20\uffff\1\41\2\uffff\1\52\14\uffff\1\41\5\uffff\1\41\12\uffff";
    static final String DFA11_maxS =
        "\1\176\1\uffff\1\162\1\133\1\135\1\uffff\1\156\1\164\2\uffff\2\75\3\uffff\1\56\20\uffff\1\76\2\uffff\1\57\14\uffff\1\74\5\uffff\1\41\12\uffff";
    static final String DFA11_acceptS =
        "\1\uffff\1\1\3\uffff\1\5\2\uffff\1\2\1\13\2\uffff\1\16\1\17\1\20\1\uffff\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\37\1\40\1\41\1\43\1\45\1\uffff\1\60\1\61\1\uffff\1\64\1\10\1\3\1\35\1\4\1\36\1\6\1\12\1\7\1\11\1\14\1\44\1\uffff\1\52\1\57\1\46\1\21\1\42\1\uffff\1\54\1\56\1\47\1\62\1\63\1\15\1\53\1\50\1\55\1\51";
    static final String DFA11_specialS =
        "\101\uffff}>";
    static final String[] DFA11_transitionS = DFA11_transitionS_.DFA11_transitionS;
    private static final class DFA11_transitionS_ {
        static final String[] DFA11_transitionS = {
                "\2\42\2\uffff\1\42\22\uffff\1\42\1\37\1\44\1\41\3\uffff\1\44\1\31\1\32\1\36\1\33\1\20\1\34\1\17\1\43\1\24\11\25\1\26\1\uffff\1\13\1\12\1\40\1\uffff\1\14\1\21\4\uffff\1\1\6\uffff\1\23\1\uffff\1\22\2\uffff\1\16\1\uffff\1\10\6\uffff\1\3\1\uffff\1\4\1\35\2\uffff\1\21\2\uffff\1\11\1\uffff\1\1\2\uffff\1\6\3\uffff\1\23\1\uffff\1\22\3\uffff\1\7\1\2\6\uffff\1\27\1\15\1\30\1\5",
                "",
                "\1\10\34\uffff\1\45\2\uffff\1\10",
                "\1\46",
                "\1\50",
                "",
                "\1\52\11\uffff\1\53",
                "\1\54\20\uffff\1\55",
                "",
                "",
                "\1\56",
                "\1\61\32\uffff\1\60\1\62",
                "",
                "",
                "",
                "\1\64",
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
                "\1\67\33\uffff\1\70\1\66",
                "",
                "",
                "\1\72\4\uffff\1\73",
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
                "\1\75\32\uffff\1\74",
                "",
                "",
                "",
                "",
                "",
                "\1\77",
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
    }

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( False | True | RULE_DOUBLE_SQUARE_OPEN | RULE_DOUBLE_SQUARE_CLOSE | RULE_TILDE | RULE_ID | RULE_SCG | RULE_TOK | RULE_STR | RULE_INT | RULE_DEC | RULE_EQUIVALENT_TO | RULE_SUBTYPE_OF | RULE_SLOTNAME_STRING | RULE_TERM_STRING | RULE_REVERSED | RULE_TO | RULE_COMMA | RULE_CONJUNCTION | RULE_DISJUNCTION | RULE_EXCLUSION | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_DBL_LT_EM | RULE_GT_EM | RULE_DBL_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_STRING );";
        }
    }
 

}