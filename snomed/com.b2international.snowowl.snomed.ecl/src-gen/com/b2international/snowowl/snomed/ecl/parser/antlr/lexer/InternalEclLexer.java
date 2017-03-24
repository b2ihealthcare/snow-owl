package com.b2international.snowowl.snomed.ecl.parser.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalEclLexer extends Lexer {
    public static final int RULE_LTE=36;
    public static final int RULE_DIGIT_NONZERO=12;
    public static final int RULE_CURLY_OPEN=15;
    public static final int RULE_TO=10;
    public static final int RULE_ROUND_CLOSE=18;
    public static final int RULE_DBL_GT=32;
    public static final int RULE_GT=30;
    public static final int RULE_STRING=42;
    public static final int RULE_NOT=24;
    public static final int RULE_REVERSED=9;
    public static final int MINUS=4;
    public static final int RULE_GTE=35;
    public static final int RULE_SL_COMMENT=40;
    public static final int Comma=7;
    public static final int RULE_HASH=37;
    public static final int RULE_ROUND_OPEN=17;
    public static final int RULE_DASH=22;
    public static final int RULE_OTHER_CHARACTER=41;
    public static final int RULE_DBL_LT=31;
    public static final int RULE_PLUS=21;
    public static final int RULE_NOT_EQUAL=28;
    public static final int RULE_DOT=25;
    public static final int EOF=-1;
    public static final int RULE_SQUARE_CLOSE=20;
    public static final int OR=6;
    public static final int RULE_SQUARE_OPEN=19;
    public static final int RULE_EQUAL=27;
    public static final int RULE_LT_EM=33;
    public static final int RULE_GT_EM=34;
    public static final int RULE_WS=38;
    public static final int RULE_CURLY_CLOSE=16;
    public static final int RULE_ZERO=11;
    public static final int RULE_COLON=14;
    public static final int RULE_LETTER=13;
    public static final int RULE_CARET=23;
    public static final int RULE_LT=29;
    public static final int AND=5;
    public static final int RULE_ML_COMMENT=39;
    public static final int RULE_WILDCARD=26;
    public static final int RULE_TERM_STRING=8;

    // delegates
    // delegators

    public InternalEclLexer() {;} 
    public InternalEclLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalEclLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "InternalEclLexer.g"; }

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:26:7: ( ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'U' | 'u' ) ( 'S' | 's' ) )
            // InternalEclLexer.g:26:9: ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'U' | 'u' ) ( 'S' | 's' )
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
    // $ANTLR end "MINUS"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:28:5: ( ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' ) )
            // InternalEclLexer.g:28:7: ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' )
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
    // $ANTLR end "AND"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:30:4: ( ( 'O' | 'o' ) ( 'R' | 'r' ) )
            // InternalEclLexer.g:30:6: ( 'O' | 'o' ) ( 'R' | 'r' )
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
    // $ANTLR end "OR"

    // $ANTLR start "Comma"
    public final void mComma() throws RecognitionException {
        try {
            int _type = Comma;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:32:7: ( ',' )
            // InternalEclLexer.g:32:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "Comma"

    // $ANTLR start "RULE_TERM_STRING"
    public final void mRULE_TERM_STRING() throws RecognitionException {
        try {
            int _type = RULE_TERM_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:34:18: ( '|' (~ ( '|' ) )* '|' )
            // InternalEclLexer.g:34:20: '|' (~ ( '|' ) )* '|'
            {
            match('|'); 
            // InternalEclLexer.g:34:24: (~ ( '|' ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='\u0000' && LA1_0<='{')||(LA1_0>='}' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalEclLexer.g:34:24: ~ ( '|' )
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

    // $ANTLR start "RULE_REVERSED"
    public final void mRULE_REVERSED() throws RecognitionException {
        try {
            int _type = RULE_REVERSED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:36:15: ( 'R' )
            // InternalEclLexer.g:36:17: 'R'
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
            // InternalEclLexer.g:38:9: ( '..' )
            // InternalEclLexer.g:38:11: '..'
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

    // $ANTLR start "RULE_ZERO"
    public final void mRULE_ZERO() throws RecognitionException {
        try {
            int _type = RULE_ZERO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:40:11: ( '0' )
            // InternalEclLexer.g:40:13: '0'
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
            // InternalEclLexer.g:42:20: ( '1' .. '9' )
            // InternalEclLexer.g:42:22: '1' .. '9'
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
            // InternalEclLexer.g:44:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) )
            // InternalEclLexer.g:44:15: ( 'a' .. 'z' | 'A' .. 'Z' )
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

    // $ANTLR start "RULE_COLON"
    public final void mRULE_COLON() throws RecognitionException {
        try {
            int _type = RULE_COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:46:12: ( ':' )
            // InternalEclLexer.g:46:14: ':'
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
            // InternalEclLexer.g:48:17: ( '{' )
            // InternalEclLexer.g:48:19: '{'
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
            // InternalEclLexer.g:50:18: ( '}' )
            // InternalEclLexer.g:50:20: '}'
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
            // InternalEclLexer.g:52:17: ( '(' )
            // InternalEclLexer.g:52:19: '('
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
            // InternalEclLexer.g:54:18: ( ')' )
            // InternalEclLexer.g:54:20: ')'
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
            // InternalEclLexer.g:56:18: ( '[' )
            // InternalEclLexer.g:56:20: '['
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
            // InternalEclLexer.g:58:19: ( ']' )
            // InternalEclLexer.g:58:21: ']'
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
            // InternalEclLexer.g:60:11: ( '+' )
            // InternalEclLexer.g:60:13: '+'
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
            // InternalEclLexer.g:62:11: ( '-' )
            // InternalEclLexer.g:62:13: '-'
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
            // InternalEclLexer.g:64:12: ( '^' )
            // InternalEclLexer.g:64:14: '^'
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

    // $ANTLR start "RULE_NOT"
    public final void mRULE_NOT() throws RecognitionException {
        try {
            int _type = RULE_NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:66:10: ( '!' )
            // InternalEclLexer.g:66:12: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_NOT"

    // $ANTLR start "RULE_DOT"
    public final void mRULE_DOT() throws RecognitionException {
        try {
            int _type = RULE_DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:68:10: ( '.' )
            // InternalEclLexer.g:68:12: '.'
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
            // InternalEclLexer.g:70:15: ( '*' )
            // InternalEclLexer.g:70:17: '*'
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
            // InternalEclLexer.g:72:12: ( '=' )
            // InternalEclLexer.g:72:14: '='
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
            // InternalEclLexer.g:74:16: ( '!=' )
            // InternalEclLexer.g:74:18: '!='
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
            // InternalEclLexer.g:76:9: ( '<' )
            // InternalEclLexer.g:76:11: '<'
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
            // InternalEclLexer.g:78:9: ( '>' )
            // InternalEclLexer.g:78:11: '>'
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
            // InternalEclLexer.g:80:13: ( '<<' )
            // InternalEclLexer.g:80:15: '<<'
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
            // InternalEclLexer.g:82:13: ( '>>' )
            // InternalEclLexer.g:82:15: '>>'
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
            // InternalEclLexer.g:84:12: ( '<!' )
            // InternalEclLexer.g:84:14: '<!'
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

    // $ANTLR start "RULE_GT_EM"
    public final void mRULE_GT_EM() throws RecognitionException {
        try {
            int _type = RULE_GT_EM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:86:12: ( '>!' )
            // InternalEclLexer.g:86:14: '>!'
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

    // $ANTLR start "RULE_GTE"
    public final void mRULE_GTE() throws RecognitionException {
        try {
            int _type = RULE_GTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:88:10: ( '>=' )
            // InternalEclLexer.g:88:12: '>='
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
            // InternalEclLexer.g:90:10: ( '<=' )
            // InternalEclLexer.g:90:12: '<='
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
            // InternalEclLexer.g:92:11: ( '#' )
            // InternalEclLexer.g:92:13: '#'
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
            // InternalEclLexer.g:94:9: ( ( ' ' | '\\t' | '\\n' | '\\r' ) )
            // InternalEclLexer.g:94:11: ( ' ' | '\\t' | '\\n' | '\\r' )
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
            // InternalEclLexer.g:96:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // InternalEclLexer.g:96:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // InternalEclLexer.g:96:24: ( options {greedy=false; } : . )*
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
            	    // InternalEclLexer.g:96:52: .
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
            // InternalEclLexer.g:98:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // InternalEclLexer.g:98:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // InternalEclLexer.g:98:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='\t')||(LA3_0>='\u000B' && LA3_0<='\f')||(LA3_0>='\u000E' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalEclLexer.g:98:24: ~ ( ( '\\n' | '\\r' ) )
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

            // InternalEclLexer.g:98:40: ( ( '\\r' )? '\\n' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='\n'||LA5_0=='\r') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalEclLexer.g:98:41: ( '\\r' )? '\\n'
                    {
                    // InternalEclLexer.g:98:41: ( '\\r' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='\r') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // InternalEclLexer.g:98:41: '\\r'
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

    // $ANTLR start "RULE_OTHER_CHARACTER"
    public final void mRULE_OTHER_CHARACTER() throws RecognitionException {
        try {
            int _type = RULE_OTHER_CHARACTER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:100:22: (~ ( '|' ) )
            // InternalEclLexer.g:100:24: ~ ( '|' )
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='{')||(input.LA(1)>='}' && input.LA(1)<='\uFFFF') ) {
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
    // $ANTLR end "RULE_OTHER_CHARACTER"

    // $ANTLR start "RULE_STRING"
    public final void mRULE_STRING() throws RecognitionException {
        try {
            int _type = RULE_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEclLexer.g:102:13: ( ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // InternalEclLexer.g:102:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // InternalEclLexer.g:102:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
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
                    // InternalEclLexer.g:102:16: '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // InternalEclLexer.g:102:20: ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )*
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
                    	    // InternalEclLexer.g:102:21: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalEclLexer.g:102:28: ~ ( ( '\\\\' | '\"' ) )
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
                    // InternalEclLexer.g:102:48: '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // InternalEclLexer.g:102:53: ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )*
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
                    	    // InternalEclLexer.g:102:54: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalEclLexer.g:102:61: ~ ( ( '\\\\' | '\\'' ) )
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
        // InternalEclLexer.g:1:8: ( MINUS | AND | OR | Comma | RULE_TERM_STRING | RULE_REVERSED | RULE_TO | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_LETTER | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_NOT | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_OTHER_CHARACTER | RULE_STRING )
        int alt9=39;
        alt9 = dfa9.predict(input);
        switch (alt9) {
            case 1 :
                // InternalEclLexer.g:1:10: MINUS
                {
                mMINUS(); 

                }
                break;
            case 2 :
                // InternalEclLexer.g:1:16: AND
                {
                mAND(); 

                }
                break;
            case 3 :
                // InternalEclLexer.g:1:20: OR
                {
                mOR(); 

                }
                break;
            case 4 :
                // InternalEclLexer.g:1:23: Comma
                {
                mComma(); 

                }
                break;
            case 5 :
                // InternalEclLexer.g:1:29: RULE_TERM_STRING
                {
                mRULE_TERM_STRING(); 

                }
                break;
            case 6 :
                // InternalEclLexer.g:1:46: RULE_REVERSED
                {
                mRULE_REVERSED(); 

                }
                break;
            case 7 :
                // InternalEclLexer.g:1:60: RULE_TO
                {
                mRULE_TO(); 

                }
                break;
            case 8 :
                // InternalEclLexer.g:1:68: RULE_ZERO
                {
                mRULE_ZERO(); 

                }
                break;
            case 9 :
                // InternalEclLexer.g:1:78: RULE_DIGIT_NONZERO
                {
                mRULE_DIGIT_NONZERO(); 

                }
                break;
            case 10 :
                // InternalEclLexer.g:1:97: RULE_LETTER
                {
                mRULE_LETTER(); 

                }
                break;
            case 11 :
                // InternalEclLexer.g:1:109: RULE_COLON
                {
                mRULE_COLON(); 

                }
                break;
            case 12 :
                // InternalEclLexer.g:1:120: RULE_CURLY_OPEN
                {
                mRULE_CURLY_OPEN(); 

                }
                break;
            case 13 :
                // InternalEclLexer.g:1:136: RULE_CURLY_CLOSE
                {
                mRULE_CURLY_CLOSE(); 

                }
                break;
            case 14 :
                // InternalEclLexer.g:1:153: RULE_ROUND_OPEN
                {
                mRULE_ROUND_OPEN(); 

                }
                break;
            case 15 :
                // InternalEclLexer.g:1:169: RULE_ROUND_CLOSE
                {
                mRULE_ROUND_CLOSE(); 

                }
                break;
            case 16 :
                // InternalEclLexer.g:1:186: RULE_SQUARE_OPEN
                {
                mRULE_SQUARE_OPEN(); 

                }
                break;
            case 17 :
                // InternalEclLexer.g:1:203: RULE_SQUARE_CLOSE
                {
                mRULE_SQUARE_CLOSE(); 

                }
                break;
            case 18 :
                // InternalEclLexer.g:1:221: RULE_PLUS
                {
                mRULE_PLUS(); 

                }
                break;
            case 19 :
                // InternalEclLexer.g:1:231: RULE_DASH
                {
                mRULE_DASH(); 

                }
                break;
            case 20 :
                // InternalEclLexer.g:1:241: RULE_CARET
                {
                mRULE_CARET(); 

                }
                break;
            case 21 :
                // InternalEclLexer.g:1:252: RULE_NOT
                {
                mRULE_NOT(); 

                }
                break;
            case 22 :
                // InternalEclLexer.g:1:261: RULE_DOT
                {
                mRULE_DOT(); 

                }
                break;
            case 23 :
                // InternalEclLexer.g:1:270: RULE_WILDCARD
                {
                mRULE_WILDCARD(); 

                }
                break;
            case 24 :
                // InternalEclLexer.g:1:284: RULE_EQUAL
                {
                mRULE_EQUAL(); 

                }
                break;
            case 25 :
                // InternalEclLexer.g:1:295: RULE_NOT_EQUAL
                {
                mRULE_NOT_EQUAL(); 

                }
                break;
            case 26 :
                // InternalEclLexer.g:1:310: RULE_LT
                {
                mRULE_LT(); 

                }
                break;
            case 27 :
                // InternalEclLexer.g:1:318: RULE_GT
                {
                mRULE_GT(); 

                }
                break;
            case 28 :
                // InternalEclLexer.g:1:326: RULE_DBL_LT
                {
                mRULE_DBL_LT(); 

                }
                break;
            case 29 :
                // InternalEclLexer.g:1:338: RULE_DBL_GT
                {
                mRULE_DBL_GT(); 

                }
                break;
            case 30 :
                // InternalEclLexer.g:1:350: RULE_LT_EM
                {
                mRULE_LT_EM(); 

                }
                break;
            case 31 :
                // InternalEclLexer.g:1:361: RULE_GT_EM
                {
                mRULE_GT_EM(); 

                }
                break;
            case 32 :
                // InternalEclLexer.g:1:372: RULE_GTE
                {
                mRULE_GTE(); 

                }
                break;
            case 33 :
                // InternalEclLexer.g:1:381: RULE_LTE
                {
                mRULE_LTE(); 

                }
                break;
            case 34 :
                // InternalEclLexer.g:1:390: RULE_HASH
                {
                mRULE_HASH(); 

                }
                break;
            case 35 :
                // InternalEclLexer.g:1:400: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 36 :
                // InternalEclLexer.g:1:408: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 37 :
                // InternalEclLexer.g:1:424: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 38 :
                // InternalEclLexer.g:1:440: RULE_OTHER_CHARACTER
                {
                mRULE_OTHER_CHARACTER(); 

                }
                break;
            case 39 :
                // InternalEclLexer.g:1:461: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
        "\1\uffff\3\41\3\uffff\1\47\15\uffff\1\65\2\uffff\1\73\1\77\2\uffff\3\37\46\uffff";
    static final String DFA9_eofS =
        "\105\uffff";
    static final String DFA9_minS =
        "\1\0\1\111\1\116\1\122\3\uffff\1\56\15\uffff\1\75\2\uffff\2\41\2\uffff\1\52\2\0\46\uffff";
    static final String DFA9_maxS =
        "\1\uffff\1\151\1\156\1\162\3\uffff\1\56\15\uffff\1\75\2\uffff\1\75\1\76\2\uffff\1\57\2\uffff\46\uffff";
    static final String DFA9_acceptS =
        "\4\uffff\1\4\1\5\1\6\1\uffff\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\uffff\1\27\1\30\2\uffff\1\42\1\43\3\uffff\1\46\1\1\1\12\1\2\1\3\1\4\1\6\1\7\1\26\1\10\1\11\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\31\1\25\1\27\1\30\1\34\1\36\1\41\1\32\1\35\1\37\1\40\1\33\1\42\1\43\1\44\1\45\1\47";
    static final String DFA9_specialS =
        "\1\1\34\uffff\1\2\1\0\46\uffff}>";
    static final String[] DFA9_transitionS = {
            "\11\37\2\33\2\37\1\33\22\37\1\33\1\25\1\35\1\32\3\37\1\36\1\16\1\17\1\26\1\22\1\4\1\23\1\7\1\34\1\10\11\11\1\13\1\37\1\30\1\27\1\31\2\37\1\2\13\12\1\1\1\12\1\3\2\12\1\6\10\12\1\20\1\37\1\21\1\24\2\37\1\2\13\12\1\1\1\12\1\3\13\12\1\14\1\5\1\15\uff82\37",
            "\1\40\37\uffff\1\40",
            "\1\42\37\uffff\1\42",
            "\1\43\37\uffff\1\43",
            "",
            "",
            "",
            "\1\46",
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
            "\1\64",
            "",
            "",
            "\1\71\32\uffff\1\70\1\72",
            "\1\75\33\uffff\1\76\1\74",
            "",
            "",
            "\1\102\4\uffff\1\103",
            "\0\104",
            "\0\104",
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
            "",
            "",
            "",
            "",
            "",
            ""
    };

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
            return "1:1: Tokens : ( MINUS | AND | OR | Comma | RULE_TERM_STRING | RULE_REVERSED | RULE_TO | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_LETTER | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_NOT | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_OTHER_CHARACTER | RULE_STRING );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA9_30 = input.LA(1);

                        s = -1;
                        if ( ((LA9_30>='\u0000' && LA9_30<='\uFFFF')) ) {s = 68;}

                        else s = 31;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA9_0 = input.LA(1);

                        s = -1;
                        if ( (LA9_0=='M'||LA9_0=='m') ) {s = 1;}

                        else if ( (LA9_0=='A'||LA9_0=='a') ) {s = 2;}

                        else if ( (LA9_0=='O'||LA9_0=='o') ) {s = 3;}

                        else if ( (LA9_0==',') ) {s = 4;}

                        else if ( (LA9_0=='|') ) {s = 5;}

                        else if ( (LA9_0=='R') ) {s = 6;}

                        else if ( (LA9_0=='.') ) {s = 7;}

                        else if ( (LA9_0=='0') ) {s = 8;}

                        else if ( ((LA9_0>='1' && LA9_0<='9')) ) {s = 9;}

                        else if ( ((LA9_0>='B' && LA9_0<='L')||LA9_0=='N'||(LA9_0>='P' && LA9_0<='Q')||(LA9_0>='S' && LA9_0<='Z')||(LA9_0>='b' && LA9_0<='l')||LA9_0=='n'||(LA9_0>='p' && LA9_0<='z')) ) {s = 10;}

                        else if ( (LA9_0==':') ) {s = 11;}

                        else if ( (LA9_0=='{') ) {s = 12;}

                        else if ( (LA9_0=='}') ) {s = 13;}

                        else if ( (LA9_0=='(') ) {s = 14;}

                        else if ( (LA9_0==')') ) {s = 15;}

                        else if ( (LA9_0=='[') ) {s = 16;}

                        else if ( (LA9_0==']') ) {s = 17;}

                        else if ( (LA9_0=='+') ) {s = 18;}

                        else if ( (LA9_0=='-') ) {s = 19;}

                        else if ( (LA9_0=='^') ) {s = 20;}

                        else if ( (LA9_0=='!') ) {s = 21;}

                        else if ( (LA9_0=='*') ) {s = 22;}

                        else if ( (LA9_0=='=') ) {s = 23;}

                        else if ( (LA9_0=='<') ) {s = 24;}

                        else if ( (LA9_0=='>') ) {s = 25;}

                        else if ( (LA9_0=='#') ) {s = 26;}

                        else if ( ((LA9_0>='\t' && LA9_0<='\n')||LA9_0=='\r'||LA9_0==' ') ) {s = 27;}

                        else if ( (LA9_0=='/') ) {s = 28;}

                        else if ( (LA9_0=='\"') ) {s = 29;}

                        else if ( (LA9_0=='\'') ) {s = 30;}

                        else if ( ((LA9_0>='\u0000' && LA9_0<='\b')||(LA9_0>='\u000B' && LA9_0<='\f')||(LA9_0>='\u000E' && LA9_0<='\u001F')||(LA9_0>='$' && LA9_0<='&')||LA9_0==';'||(LA9_0>='?' && LA9_0<='@')||LA9_0=='\\'||(LA9_0>='_' && LA9_0<='`')||(LA9_0>='~' && LA9_0<='\uFFFF')) ) {s = 31;}

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA9_29 = input.LA(1);

                        s = -1;
                        if ( ((LA9_29>='\u0000' && LA9_29<='\uFFFF')) ) {s = 68;}

                        else s = 31;

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