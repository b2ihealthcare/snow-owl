package com.b2international.snowowl.snomed.ql.parser.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalQLLexer extends Lexer {
    public static final int RULE_DIGIT_NONZERO=24;
    public static final int RULE_CURLY_OPEN=26;
    public static final int RULE_TO=22;
    public static final int RULE_ROUND_CLOSE=29;
    public static final int RULE_DBL_GT=43;
    public static final int RULE_GT=41;
    public static final int MINUS=4;
    public static final int RULE_GTE=46;
    public static final int RULE_LANGREFSET=16;
    public static final int RULE_ECL=9;
    public static final int RULE_ROUND_OPEN=28;
    public static final int RULE_DBL_LT=42;
    public static final int RULE_NOT_EQUAL=39;
    public static final int RULE_SQUARE_CLOSE=31;
    public static final int RULE_DESCRIPTION_TYPE=17;
    public static final int RULE_SQUARE_OPEN=30;
    public static final int RULE_EQUAL=38;
    public static final int RULE_LT_EM=44;
    public static final int RULE_CURLY_CLOSE=27;
    public static final int RULE_ZERO=23;
    public static final int RULE_COLON=25;
    public static final int RULE_LT=40;
    public static final int AND=5;
    public static final int RULE_ML_COMMENT=50;
    public static final int RULE_FALSE=12;
    public static final int RULE_LTE=47;
    public static final int RULE_REGEX=13;
    public static final int RULE_STRING=52;
    public static final int RULE_NOT=35;
    public static final int RULE_REVERSED=21;
    public static final int RULE_SL_COMMENT=51;
    public static final int RULE_CLOSE_DOUBLE_BRACES=19;
    public static final int Comma=7;
    public static final int RULE_HASH=48;
    public static final int RULE_DASH=33;
    public static final int RULE_TRUE=11;
    public static final int RULE_ACCEPTABLE=14;
    public static final int RULE_PLUS=32;
    public static final int RULE_DOT=36;
    public static final int EOF=-1;
    public static final int OR=6;
    public static final int RULE_GT_EM=45;
    public static final int RULE_WS=49;
    public static final int RULE_TERM=8;
    public static final int RULE_OPEN_DOUBLE_BRACES=18;
    public static final int RULE_ACTIVE=10;
    public static final int RULE_CARET=34;
    public static final int RULE_WILDCARD=37;
    public static final int RULE_PREFERRED=15;
    public static final int RULE_TERM_STRING=20;

    // delegates
    // delegators

    public InternalQLLexer() {;} 
    public InternalQLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalQLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "InternalQLLexer.g"; }

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:26:7: ( ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'U' | 'u' ) ( 'S' | 's' ) )
            // InternalQLLexer.g:26:9: ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'U' | 'u' ) ( 'S' | 's' )
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
            // InternalQLLexer.g:28:5: ( ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' ) )
            // InternalQLLexer.g:28:7: ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' )
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
            // InternalQLLexer.g:30:4: ( ( 'O' | 'o' ) ( 'R' | 'r' ) )
            // InternalQLLexer.g:30:6: ( 'O' | 'o' ) ( 'R' | 'r' )
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
            // InternalQLLexer.g:32:7: ( ',' )
            // InternalQLLexer.g:32:9: ','
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

    // $ANTLR start "RULE_TERM"
    public final void mRULE_TERM() throws RecognitionException {
        try {
            int _type = RULE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:34:11: ( 'term' )
            // InternalQLLexer.g:34:13: 'term'
            {
            match("term"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_TERM"

    // $ANTLR start "RULE_ECL"
    public final void mRULE_ECL() throws RecognitionException {
        try {
            int _type = RULE_ECL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:36:10: ( 'ecl' )
            // InternalQLLexer.g:36:12: 'ecl'
            {
            match("ecl"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ECL"

    // $ANTLR start "RULE_ACTIVE"
    public final void mRULE_ACTIVE() throws RecognitionException {
        try {
            int _type = RULE_ACTIVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:38:13: ( 'active' )
            // InternalQLLexer.g:38:15: 'active'
            {
            match("active"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ACTIVE"

    // $ANTLR start "RULE_TRUE"
    public final void mRULE_TRUE() throws RecognitionException {
        try {
            int _type = RULE_TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:40:11: ( 'true' )
            // InternalQLLexer.g:40:13: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_TRUE"

    // $ANTLR start "RULE_FALSE"
    public final void mRULE_FALSE() throws RecognitionException {
        try {
            int _type = RULE_FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:42:12: ( 'false' )
            // InternalQLLexer.g:42:14: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_FALSE"

    // $ANTLR start "RULE_REGEX"
    public final void mRULE_REGEX() throws RecognitionException {
        try {
            int _type = RULE_REGEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:44:12: ( 'regex' )
            // InternalQLLexer.g:44:14: 'regex'
            {
            match("regex"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_REGEX"

    // $ANTLR start "RULE_ACCEPTABLE"
    public final void mRULE_ACCEPTABLE() throws RecognitionException {
        try {
            int _type = RULE_ACCEPTABLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:46:17: ( 'acceptableIn' )
            // InternalQLLexer.g:46:19: 'acceptableIn'
            {
            match("acceptableIn"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ACCEPTABLE"

    // $ANTLR start "RULE_PREFERRED"
    public final void mRULE_PREFERRED() throws RecognitionException {
        try {
            int _type = RULE_PREFERRED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:48:16: ( 'preferredIn' )
            // InternalQLLexer.g:48:18: 'preferredIn'
            {
            match("preferredIn"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_PREFERRED"

    // $ANTLR start "RULE_LANGREFSET"
    public final void mRULE_LANGREFSET() throws RecognitionException {
        try {
            int _type = RULE_LANGREFSET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:50:17: ( 'languageRefSet' )
            // InternalQLLexer.g:50:19: 'languageRefSet'
            {
            match("languageRefSet"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_LANGREFSET"

    // $ANTLR start "RULE_DESCRIPTION_TYPE"
    public final void mRULE_DESCRIPTION_TYPE() throws RecognitionException {
        try {
            int _type = RULE_DESCRIPTION_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:52:23: ( 'type' )
            // InternalQLLexer.g:52:25: 'type'
            {
            match("type"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DESCRIPTION_TYPE"

    // $ANTLR start "RULE_OPEN_DOUBLE_BRACES"
    public final void mRULE_OPEN_DOUBLE_BRACES() throws RecognitionException {
        try {
            int _type = RULE_OPEN_DOUBLE_BRACES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:54:25: ( '{{' )
            // InternalQLLexer.g:54:27: '{{'
            {
            match("{{"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_OPEN_DOUBLE_BRACES"

    // $ANTLR start "RULE_CLOSE_DOUBLE_BRACES"
    public final void mRULE_CLOSE_DOUBLE_BRACES() throws RecognitionException {
        try {
            int _type = RULE_CLOSE_DOUBLE_BRACES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:56:26: ( '}}' )
            // InternalQLLexer.g:56:28: '}}'
            {
            match("}}"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_CLOSE_DOUBLE_BRACES"

    // $ANTLR start "RULE_TERM_STRING"
    public final void mRULE_TERM_STRING() throws RecognitionException {
        try {
            int _type = RULE_TERM_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:58:18: ( '|' (~ ( '|' ) )* '|' )
            // InternalQLLexer.g:58:20: '|' (~ ( '|' ) )* '|'
            {
            match('|'); 
            // InternalQLLexer.g:58:24: (~ ( '|' ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='\u0000' && LA1_0<='{')||(LA1_0>='}' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalQLLexer.g:58:24: ~ ( '|' )
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
            // InternalQLLexer.g:60:15: ( 'R' )
            // InternalQLLexer.g:60:17: 'R'
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
            // InternalQLLexer.g:62:9: ( '..' )
            // InternalQLLexer.g:62:11: '..'
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
            // InternalQLLexer.g:64:11: ( '0' )
            // InternalQLLexer.g:64:13: '0'
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
            // InternalQLLexer.g:66:20: ( '1' .. '9' )
            // InternalQLLexer.g:66:22: '1' .. '9'
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
            // InternalQLLexer.g:68:12: ( ':' )
            // InternalQLLexer.g:68:14: ':'
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
            // InternalQLLexer.g:70:17: ( '{' )
            // InternalQLLexer.g:70:19: '{'
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
            // InternalQLLexer.g:72:18: ( '}' )
            // InternalQLLexer.g:72:20: '}'
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
            // InternalQLLexer.g:74:17: ( '(' )
            // InternalQLLexer.g:74:19: '('
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
            // InternalQLLexer.g:76:18: ( ')' )
            // InternalQLLexer.g:76:20: ')'
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
            // InternalQLLexer.g:78:18: ( '[' )
            // InternalQLLexer.g:78:20: '['
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
            // InternalQLLexer.g:80:19: ( ']' )
            // InternalQLLexer.g:80:21: ']'
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
            // InternalQLLexer.g:82:11: ( '+' )
            // InternalQLLexer.g:82:13: '+'
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
            // InternalQLLexer.g:84:11: ( '-' )
            // InternalQLLexer.g:84:13: '-'
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
            // InternalQLLexer.g:86:12: ( '^' )
            // InternalQLLexer.g:86:14: '^'
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
            // InternalQLLexer.g:88:10: ( '!' )
            // InternalQLLexer.g:88:12: '!'
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
            // InternalQLLexer.g:90:10: ( '.' )
            // InternalQLLexer.g:90:12: '.'
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
            // InternalQLLexer.g:92:15: ( '*' )
            // InternalQLLexer.g:92:17: '*'
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
            // InternalQLLexer.g:94:12: ( '=' )
            // InternalQLLexer.g:94:14: '='
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
            // InternalQLLexer.g:96:16: ( '!=' )
            // InternalQLLexer.g:96:18: '!='
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
            // InternalQLLexer.g:98:9: ( '<' )
            // InternalQLLexer.g:98:11: '<'
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
            // InternalQLLexer.g:100:9: ( '>' )
            // InternalQLLexer.g:100:11: '>'
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
            // InternalQLLexer.g:102:13: ( '<<' )
            // InternalQLLexer.g:102:15: '<<'
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
            // InternalQLLexer.g:104:13: ( '>>' )
            // InternalQLLexer.g:104:15: '>>'
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
            // InternalQLLexer.g:106:12: ( '<!' )
            // InternalQLLexer.g:106:14: '<!'
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
            // InternalQLLexer.g:108:12: ( '>!' )
            // InternalQLLexer.g:108:14: '>!'
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
            // InternalQLLexer.g:110:10: ( '>=' )
            // InternalQLLexer.g:110:12: '>='
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
            // InternalQLLexer.g:112:10: ( '<=' )
            // InternalQLLexer.g:112:12: '<='
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
            // InternalQLLexer.g:114:11: ( '#' )
            // InternalQLLexer.g:114:13: '#'
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
            // InternalQLLexer.g:116:9: ( ( ' ' | '\\t' | '\\n' | '\\r' ) )
            // InternalQLLexer.g:116:11: ( ' ' | '\\t' | '\\n' | '\\r' )
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
            // InternalQLLexer.g:118:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // InternalQLLexer.g:118:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // InternalQLLexer.g:118:24: ( options {greedy=false; } : . )*
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
            	    // InternalQLLexer.g:118:52: .
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
            // InternalQLLexer.g:120:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // InternalQLLexer.g:120:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // InternalQLLexer.g:120:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='\t')||(LA3_0>='\u000B' && LA3_0<='\f')||(LA3_0>='\u000E' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalQLLexer.g:120:24: ~ ( ( '\\n' | '\\r' ) )
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

            // InternalQLLexer.g:120:40: ( ( '\\r' )? '\\n' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='\n'||LA5_0=='\r') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalQLLexer.g:120:41: ( '\\r' )? '\\n'
                    {
                    // InternalQLLexer.g:120:41: ( '\\r' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='\r') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // InternalQLLexer.g:120:41: '\\r'
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
            // InternalQLLexer.g:122:13: ( ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // InternalQLLexer.g:122:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // InternalQLLexer.g:122:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
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
                    // InternalQLLexer.g:122:16: '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // InternalQLLexer.g:122:20: ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )*
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
                    	    // InternalQLLexer.g:122:21: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalQLLexer.g:122:28: ~ ( ( '\\\\' | '\"' ) )
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
                    // InternalQLLexer.g:122:48: '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // InternalQLLexer.g:122:53: ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )*
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
                    	    // InternalQLLexer.g:122:54: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalQLLexer.g:122:61: ~ ( ( '\\\\' | '\\'' ) )
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
        // InternalQLLexer.g:1:8: ( MINUS | AND | OR | Comma | RULE_TERM | RULE_ECL | RULE_ACTIVE | RULE_TRUE | RULE_FALSE | RULE_REGEX | RULE_ACCEPTABLE | RULE_PREFERRED | RULE_LANGREFSET | RULE_DESCRIPTION_TYPE | RULE_OPEN_DOUBLE_BRACES | RULE_CLOSE_DOUBLE_BRACES | RULE_TERM_STRING | RULE_REVERSED | RULE_TO | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_NOT | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_STRING )
        int alt9=49;
        alt9 = dfa9.predict(input);
        switch (alt9) {
            case 1 :
                // InternalQLLexer.g:1:10: MINUS
                {
                mMINUS(); 

                }
                break;
            case 2 :
                // InternalQLLexer.g:1:16: AND
                {
                mAND(); 

                }
                break;
            case 3 :
                // InternalQLLexer.g:1:20: OR
                {
                mOR(); 

                }
                break;
            case 4 :
                // InternalQLLexer.g:1:23: Comma
                {
                mComma(); 

                }
                break;
            case 5 :
                // InternalQLLexer.g:1:29: RULE_TERM
                {
                mRULE_TERM(); 

                }
                break;
            case 6 :
                // InternalQLLexer.g:1:39: RULE_ECL
                {
                mRULE_ECL(); 

                }
                break;
            case 7 :
                // InternalQLLexer.g:1:48: RULE_ACTIVE
                {
                mRULE_ACTIVE(); 

                }
                break;
            case 8 :
                // InternalQLLexer.g:1:60: RULE_TRUE
                {
                mRULE_TRUE(); 

                }
                break;
            case 9 :
                // InternalQLLexer.g:1:70: RULE_FALSE
                {
                mRULE_FALSE(); 

                }
                break;
            case 10 :
                // InternalQLLexer.g:1:81: RULE_REGEX
                {
                mRULE_REGEX(); 

                }
                break;
            case 11 :
                // InternalQLLexer.g:1:92: RULE_ACCEPTABLE
                {
                mRULE_ACCEPTABLE(); 

                }
                break;
            case 12 :
                // InternalQLLexer.g:1:108: RULE_PREFERRED
                {
                mRULE_PREFERRED(); 

                }
                break;
            case 13 :
                // InternalQLLexer.g:1:123: RULE_LANGREFSET
                {
                mRULE_LANGREFSET(); 

                }
                break;
            case 14 :
                // InternalQLLexer.g:1:139: RULE_DESCRIPTION_TYPE
                {
                mRULE_DESCRIPTION_TYPE(); 

                }
                break;
            case 15 :
                // InternalQLLexer.g:1:161: RULE_OPEN_DOUBLE_BRACES
                {
                mRULE_OPEN_DOUBLE_BRACES(); 

                }
                break;
            case 16 :
                // InternalQLLexer.g:1:185: RULE_CLOSE_DOUBLE_BRACES
                {
                mRULE_CLOSE_DOUBLE_BRACES(); 

                }
                break;
            case 17 :
                // InternalQLLexer.g:1:210: RULE_TERM_STRING
                {
                mRULE_TERM_STRING(); 

                }
                break;
            case 18 :
                // InternalQLLexer.g:1:227: RULE_REVERSED
                {
                mRULE_REVERSED(); 

                }
                break;
            case 19 :
                // InternalQLLexer.g:1:241: RULE_TO
                {
                mRULE_TO(); 

                }
                break;
            case 20 :
                // InternalQLLexer.g:1:249: RULE_ZERO
                {
                mRULE_ZERO(); 

                }
                break;
            case 21 :
                // InternalQLLexer.g:1:259: RULE_DIGIT_NONZERO
                {
                mRULE_DIGIT_NONZERO(); 

                }
                break;
            case 22 :
                // InternalQLLexer.g:1:278: RULE_COLON
                {
                mRULE_COLON(); 

                }
                break;
            case 23 :
                // InternalQLLexer.g:1:289: RULE_CURLY_OPEN
                {
                mRULE_CURLY_OPEN(); 

                }
                break;
            case 24 :
                // InternalQLLexer.g:1:305: RULE_CURLY_CLOSE
                {
                mRULE_CURLY_CLOSE(); 

                }
                break;
            case 25 :
                // InternalQLLexer.g:1:322: RULE_ROUND_OPEN
                {
                mRULE_ROUND_OPEN(); 

                }
                break;
            case 26 :
                // InternalQLLexer.g:1:338: RULE_ROUND_CLOSE
                {
                mRULE_ROUND_CLOSE(); 

                }
                break;
            case 27 :
                // InternalQLLexer.g:1:355: RULE_SQUARE_OPEN
                {
                mRULE_SQUARE_OPEN(); 

                }
                break;
            case 28 :
                // InternalQLLexer.g:1:372: RULE_SQUARE_CLOSE
                {
                mRULE_SQUARE_CLOSE(); 

                }
                break;
            case 29 :
                // InternalQLLexer.g:1:390: RULE_PLUS
                {
                mRULE_PLUS(); 

                }
                break;
            case 30 :
                // InternalQLLexer.g:1:400: RULE_DASH
                {
                mRULE_DASH(); 

                }
                break;
            case 31 :
                // InternalQLLexer.g:1:410: RULE_CARET
                {
                mRULE_CARET(); 

                }
                break;
            case 32 :
                // InternalQLLexer.g:1:421: RULE_NOT
                {
                mRULE_NOT(); 

                }
                break;
            case 33 :
                // InternalQLLexer.g:1:430: RULE_DOT
                {
                mRULE_DOT(); 

                }
                break;
            case 34 :
                // InternalQLLexer.g:1:439: RULE_WILDCARD
                {
                mRULE_WILDCARD(); 

                }
                break;
            case 35 :
                // InternalQLLexer.g:1:453: RULE_EQUAL
                {
                mRULE_EQUAL(); 

                }
                break;
            case 36 :
                // InternalQLLexer.g:1:464: RULE_NOT_EQUAL
                {
                mRULE_NOT_EQUAL(); 

                }
                break;
            case 37 :
                // InternalQLLexer.g:1:479: RULE_LT
                {
                mRULE_LT(); 

                }
                break;
            case 38 :
                // InternalQLLexer.g:1:487: RULE_GT
                {
                mRULE_GT(); 

                }
                break;
            case 39 :
                // InternalQLLexer.g:1:495: RULE_DBL_LT
                {
                mRULE_DBL_LT(); 

                }
                break;
            case 40 :
                // InternalQLLexer.g:1:507: RULE_DBL_GT
                {
                mRULE_DBL_GT(); 

                }
                break;
            case 41 :
                // InternalQLLexer.g:1:519: RULE_LT_EM
                {
                mRULE_LT_EM(); 

                }
                break;
            case 42 :
                // InternalQLLexer.g:1:530: RULE_GT_EM
                {
                mRULE_GT_EM(); 

                }
                break;
            case 43 :
                // InternalQLLexer.g:1:541: RULE_GTE
                {
                mRULE_GTE(); 

                }
                break;
            case 44 :
                // InternalQLLexer.g:1:550: RULE_LTE
                {
                mRULE_LTE(); 

                }
                break;
            case 45 :
                // InternalQLLexer.g:1:559: RULE_HASH
                {
                mRULE_HASH(); 

                }
                break;
            case 46 :
                // InternalQLLexer.g:1:569: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 47 :
                // InternalQLLexer.g:1:577: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 48 :
                // InternalQLLexer.g:1:593: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 49 :
                // InternalQLLexer.g:1:609: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
        "\14\uffff\1\51\1\53\2\uffff\1\55\12\uffff\1\57\2\uffff\1\63\1\67\34\uffff";
    static final String DFA9_eofS =
        "\74\uffff";
    static final String DFA9_minS =
        "\1\11\1\uffff\1\116\2\uffff\1\145\6\uffff\1\173\1\175\2\uffff\1\56\12\uffff\1\75\2\uffff\2\41\2\uffff\1\52\1\uffff\1\143\27\uffff";
    static final String DFA9_maxS =
        "\1\175\1\uffff\1\156\2\uffff\1\171\6\uffff\1\173\1\175\2\uffff\1\56\12\uffff\1\75\2\uffff\1\75\1\76\2\uffff\1\57\1\uffff\1\164\27\uffff";
    static final String DFA9_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\1\4\1\uffff\1\6\1\2\1\11\1\12\1\14\1\15\2\uffff\1\21\1\22\1\uffff\1\24\1\25\1\26\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\uffff\1\42\1\43\2\uffff\1\55\1\56\1\uffff\1\61\1\uffff\1\5\1\10\1\16\1\17\1\27\1\20\1\30\1\23\1\41\1\44\1\40\1\47\1\51\1\54\1\45\1\50\1\52\1\53\1\46\1\57\1\60\1\7\1\13";
    static final String DFA9_specialS =
        "\74\uffff}>";
    static final String[] DFA9_transitionS = {
            "\2\41\2\uffff\1\41\22\uffff\1\41\1\33\1\43\1\40\3\uffff\1\43\1\24\1\25\1\34\1\30\1\4\1\31\1\20\1\42\1\21\11\22\1\23\1\uffff\1\36\1\35\1\37\2\uffff\1\7\13\uffff\1\1\1\uffff\1\3\2\uffff\1\17\10\uffff\1\26\1\uffff\1\27\1\32\2\uffff\1\2\3\uffff\1\6\1\10\5\uffff\1\13\1\1\1\uffff\1\3\1\12\1\uffff\1\11\1\uffff\1\5\6\uffff\1\14\1\16\1\15",
            "",
            "\1\7\24\uffff\1\44\12\uffff\1\7",
            "",
            "",
            "\1\45\14\uffff\1\46\6\uffff\1\47",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\50",
            "\1\52",
            "",
            "",
            "\1\54",
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
            "\1\56",
            "",
            "",
            "\1\61\32\uffff\1\60\1\62",
            "\1\65\33\uffff\1\66\1\64",
            "",
            "",
            "\1\70\4\uffff\1\71",
            "",
            "\1\73\20\uffff\1\72",
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

    class DFA9 extends DFA {

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
            return "1:1: Tokens : ( MINUS | AND | OR | Comma | RULE_TERM | RULE_ECL | RULE_ACTIVE | RULE_TRUE | RULE_FALSE | RULE_REGEX | RULE_ACCEPTABLE | RULE_PREFERRED | RULE_LANGREFSET | RULE_DESCRIPTION_TYPE | RULE_OPEN_DOUBLE_BRACES | RULE_CLOSE_DOUBLE_BRACES | RULE_TERM_STRING | RULE_REVERSED | RULE_TO | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_NOT | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_STRING );";
        }
    }
 

}