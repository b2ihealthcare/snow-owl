package com.b2international.snowowl.snomed.parser.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalQLLexer extends Lexer {
    public static final int RULE_LTE=37;
    public static final int RULE_DIGIT_NONZERO=14;
    public static final int RULE_CURLY_OPEN=16;
    public static final int RULE_TO=12;
    public static final int RULE_ROUND_CLOSE=19;
    public static final int RULE_DBL_GT=33;
    public static final int RULE_GT=31;
    public static final int RULE_STRING=42;
    public static final int RULE_NOT=25;
    public static final int RULE_REVERSED=11;
    public static final int MINUS=4;
    public static final int RULE_GTE=36;
    public static final int RULE_SL_COMMENT=41;
    public static final int Comma=7;
    public static final int RULE_ECL=9;
    public static final int RULE_HASH=38;
    public static final int RULE_ROUND_OPEN=18;
    public static final int RULE_DASH=23;
    public static final int RULE_DBL_LT=32;
    public static final int RULE_PLUS=22;
    public static final int RULE_NOT_EQUAL=29;
    public static final int RULE_DOT=26;
    public static final int EOF=-1;
    public static final int RULE_SQUARE_CLOSE=21;
    public static final int OR=6;
    public static final int RULE_SQUARE_OPEN=20;
    public static final int RULE_EQUAL=28;
    public static final int RULE_LT_EM=34;
    public static final int RULE_GT_EM=35;
    public static final int RULE_WS=39;
    public static final int RULE_TERM=8;
    public static final int RULE_CURLY_CLOSE=17;
    public static final int RULE_ZERO=13;
    public static final int RULE_COLON=15;
    public static final int RULE_CARET=24;
    public static final int RULE_LT=30;
    public static final int AND=5;
    public static final int RULE_ML_COMMENT=40;
    public static final int RULE_WILDCARD=27;
    public static final int RULE_TERM_STRING=10;

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

    // $ANTLR start "RULE_TERM_STRING"
    public final void mRULE_TERM_STRING() throws RecognitionException {
        try {
            int _type = RULE_TERM_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalQLLexer.g:38:18: ( '|' (~ ( '|' ) )* '|' )
            // InternalQLLexer.g:38:20: '|' (~ ( '|' ) )* '|'
            {
            match('|'); 
            // InternalQLLexer.g:38:24: (~ ( '|' ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='\u0000' && LA1_0<='{')||(LA1_0>='}' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalQLLexer.g:38:24: ~ ( '|' )
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
            // InternalQLLexer.g:40:15: ( 'R' )
            // InternalQLLexer.g:40:17: 'R'
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
            // InternalQLLexer.g:42:9: ( '..' )
            // InternalQLLexer.g:42:11: '..'
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
            // InternalQLLexer.g:44:11: ( '0' )
            // InternalQLLexer.g:44:13: '0'
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
            // InternalQLLexer.g:46:20: ( '1' .. '9' )
            // InternalQLLexer.g:46:22: '1' .. '9'
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
            // InternalQLLexer.g:48:12: ( ':' )
            // InternalQLLexer.g:48:14: ':'
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
            // InternalQLLexer.g:50:17: ( '{' )
            // InternalQLLexer.g:50:19: '{'
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
            // InternalQLLexer.g:52:18: ( '}' )
            // InternalQLLexer.g:52:20: '}'
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
            // InternalQLLexer.g:54:17: ( '(' )
            // InternalQLLexer.g:54:19: '('
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
            // InternalQLLexer.g:56:18: ( ')' )
            // InternalQLLexer.g:56:20: ')'
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
            // InternalQLLexer.g:58:18: ( '[' )
            // InternalQLLexer.g:58:20: '['
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
            // InternalQLLexer.g:60:19: ( ']' )
            // InternalQLLexer.g:60:21: ']'
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
            // InternalQLLexer.g:62:11: ( '+' )
            // InternalQLLexer.g:62:13: '+'
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
            // InternalQLLexer.g:64:11: ( '-' )
            // InternalQLLexer.g:64:13: '-'
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
            // InternalQLLexer.g:66:12: ( '^' )
            // InternalQLLexer.g:66:14: '^'
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
            // InternalQLLexer.g:68:10: ( '!' )
            // InternalQLLexer.g:68:12: '!'
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
            // InternalQLLexer.g:70:10: ( '.' )
            // InternalQLLexer.g:70:12: '.'
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
            // InternalQLLexer.g:72:15: ( '*' )
            // InternalQLLexer.g:72:17: '*'
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
            // InternalQLLexer.g:74:12: ( '=' )
            // InternalQLLexer.g:74:14: '='
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
            // InternalQLLexer.g:76:16: ( '!=' )
            // InternalQLLexer.g:76:18: '!='
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
            // InternalQLLexer.g:78:9: ( '<' )
            // InternalQLLexer.g:78:11: '<'
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
            // InternalQLLexer.g:80:9: ( '>' )
            // InternalQLLexer.g:80:11: '>'
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
            // InternalQLLexer.g:82:13: ( '<<' )
            // InternalQLLexer.g:82:15: '<<'
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
            // InternalQLLexer.g:84:13: ( '>>' )
            // InternalQLLexer.g:84:15: '>>'
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
            // InternalQLLexer.g:86:12: ( '<!' )
            // InternalQLLexer.g:86:14: '<!'
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
            // InternalQLLexer.g:88:12: ( '>!' )
            // InternalQLLexer.g:88:14: '>!'
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
            // InternalQLLexer.g:90:10: ( '>=' )
            // InternalQLLexer.g:90:12: '>='
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
            // InternalQLLexer.g:92:10: ( '<=' )
            // InternalQLLexer.g:92:12: '<='
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
            // InternalQLLexer.g:94:11: ( '#' )
            // InternalQLLexer.g:94:13: '#'
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
            // InternalQLLexer.g:96:9: ( ( ' ' | '\\t' | '\\n' | '\\r' ) )
            // InternalQLLexer.g:96:11: ( ' ' | '\\t' | '\\n' | '\\r' )
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
            // InternalQLLexer.g:98:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // InternalQLLexer.g:98:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // InternalQLLexer.g:98:24: ( options {greedy=false; } : . )*
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
            	    // InternalQLLexer.g:98:52: .
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
            // InternalQLLexer.g:100:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // InternalQLLexer.g:100:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // InternalQLLexer.g:100:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='\t')||(LA3_0>='\u000B' && LA3_0<='\f')||(LA3_0>='\u000E' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalQLLexer.g:100:24: ~ ( ( '\\n' | '\\r' ) )
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

            // InternalQLLexer.g:100:40: ( ( '\\r' )? '\\n' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='\n'||LA5_0=='\r') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalQLLexer.g:100:41: ( '\\r' )? '\\n'
                    {
                    // InternalQLLexer.g:100:41: ( '\\r' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='\r') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // InternalQLLexer.g:100:41: '\\r'
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
            // InternalQLLexer.g:102:13: ( ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // InternalQLLexer.g:102:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // InternalQLLexer.g:102:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
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
                    // InternalQLLexer.g:102:16: '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // InternalQLLexer.g:102:20: ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )*
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
                    	    // InternalQLLexer.g:102:21: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalQLLexer.g:102:28: ~ ( ( '\\\\' | '\"' ) )
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
                    // InternalQLLexer.g:102:48: '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // InternalQLLexer.g:102:53: ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )*
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
                    	    // InternalQLLexer.g:102:54: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalQLLexer.g:102:61: ~ ( ( '\\\\' | '\\'' ) )
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
        // InternalQLLexer.g:1:8: ( MINUS | AND | OR | Comma | RULE_TERM | RULE_ECL | RULE_TERM_STRING | RULE_REVERSED | RULE_TO | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_NOT | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_STRING )
        int alt9=39;
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
                // InternalQLLexer.g:1:48: RULE_TERM_STRING
                {
                mRULE_TERM_STRING(); 

                }
                break;
            case 8 :
                // InternalQLLexer.g:1:65: RULE_REVERSED
                {
                mRULE_REVERSED(); 

                }
                break;
            case 9 :
                // InternalQLLexer.g:1:79: RULE_TO
                {
                mRULE_TO(); 

                }
                break;
            case 10 :
                // InternalQLLexer.g:1:87: RULE_ZERO
                {
                mRULE_ZERO(); 

                }
                break;
            case 11 :
                // InternalQLLexer.g:1:97: RULE_DIGIT_NONZERO
                {
                mRULE_DIGIT_NONZERO(); 

                }
                break;
            case 12 :
                // InternalQLLexer.g:1:116: RULE_COLON
                {
                mRULE_COLON(); 

                }
                break;
            case 13 :
                // InternalQLLexer.g:1:127: RULE_CURLY_OPEN
                {
                mRULE_CURLY_OPEN(); 

                }
                break;
            case 14 :
                // InternalQLLexer.g:1:143: RULE_CURLY_CLOSE
                {
                mRULE_CURLY_CLOSE(); 

                }
                break;
            case 15 :
                // InternalQLLexer.g:1:160: RULE_ROUND_OPEN
                {
                mRULE_ROUND_OPEN(); 

                }
                break;
            case 16 :
                // InternalQLLexer.g:1:176: RULE_ROUND_CLOSE
                {
                mRULE_ROUND_CLOSE(); 

                }
                break;
            case 17 :
                // InternalQLLexer.g:1:193: RULE_SQUARE_OPEN
                {
                mRULE_SQUARE_OPEN(); 

                }
                break;
            case 18 :
                // InternalQLLexer.g:1:210: RULE_SQUARE_CLOSE
                {
                mRULE_SQUARE_CLOSE(); 

                }
                break;
            case 19 :
                // InternalQLLexer.g:1:228: RULE_PLUS
                {
                mRULE_PLUS(); 

                }
                break;
            case 20 :
                // InternalQLLexer.g:1:238: RULE_DASH
                {
                mRULE_DASH(); 

                }
                break;
            case 21 :
                // InternalQLLexer.g:1:248: RULE_CARET
                {
                mRULE_CARET(); 

                }
                break;
            case 22 :
                // InternalQLLexer.g:1:259: RULE_NOT
                {
                mRULE_NOT(); 

                }
                break;
            case 23 :
                // InternalQLLexer.g:1:268: RULE_DOT
                {
                mRULE_DOT(); 

                }
                break;
            case 24 :
                // InternalQLLexer.g:1:277: RULE_WILDCARD
                {
                mRULE_WILDCARD(); 

                }
                break;
            case 25 :
                // InternalQLLexer.g:1:291: RULE_EQUAL
                {
                mRULE_EQUAL(); 

                }
                break;
            case 26 :
                // InternalQLLexer.g:1:302: RULE_NOT_EQUAL
                {
                mRULE_NOT_EQUAL(); 

                }
                break;
            case 27 :
                // InternalQLLexer.g:1:317: RULE_LT
                {
                mRULE_LT(); 

                }
                break;
            case 28 :
                // InternalQLLexer.g:1:325: RULE_GT
                {
                mRULE_GT(); 

                }
                break;
            case 29 :
                // InternalQLLexer.g:1:333: RULE_DBL_LT
                {
                mRULE_DBL_LT(); 

                }
                break;
            case 30 :
                // InternalQLLexer.g:1:345: RULE_DBL_GT
                {
                mRULE_DBL_GT(); 

                }
                break;
            case 31 :
                // InternalQLLexer.g:1:357: RULE_LT_EM
                {
                mRULE_LT_EM(); 

                }
                break;
            case 32 :
                // InternalQLLexer.g:1:368: RULE_GT_EM
                {
                mRULE_GT_EM(); 

                }
                break;
            case 33 :
                // InternalQLLexer.g:1:379: RULE_GTE
                {
                mRULE_GTE(); 

                }
                break;
            case 34 :
                // InternalQLLexer.g:1:388: RULE_LTE
                {
                mRULE_LTE(); 

                }
                break;
            case 35 :
                // InternalQLLexer.g:1:397: RULE_HASH
                {
                mRULE_HASH(); 

                }
                break;
            case 36 :
                // InternalQLLexer.g:1:407: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 37 :
                // InternalQLLexer.g:1:415: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 38 :
                // InternalQLLexer.g:1:431: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 39 :
                // InternalQLLexer.g:1:447: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
        "\11\uffff\1\40\14\uffff\1\42\2\uffff\1\46\1\52\22\uffff";
    static final String DFA9_eofS =
        "\55\uffff";
    static final String DFA9_minS =
        "\1\11\10\uffff\1\56\14\uffff\1\75\2\uffff\2\41\2\uffff\1\52\17\uffff";
    static final String DFA9_maxS =
        "\1\175\10\uffff\1\56\14\uffff\1\75\2\uffff\1\75\1\76\2\uffff\1\57\17\uffff";
    static final String DFA9_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\uffff\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\uffff\1\30\1\31\2\uffff\1\43\1\44\1\uffff\1\47\1\11\1\27\1\32\1\26\1\35\1\37\1\42\1\33\1\36\1\40\1\41\1\34\1\45\1\46";
    static final String DFA9_specialS =
        "\55\uffff}>";
    static final String[] DFA9_transitionS = {
            "\2\34\2\uffff\1\34\22\uffff\1\34\1\26\1\36\1\33\3\uffff\1\36\1\17\1\20\1\27\1\23\1\4\1\24\1\11\1\35\1\12\11\13\1\14\1\uffff\1\31\1\30\1\32\2\uffff\1\2\13\uffff\1\1\1\uffff\1\3\2\uffff\1\10\10\uffff\1\21\1\uffff\1\22\1\25\2\uffff\1\2\3\uffff\1\6\7\uffff\1\1\1\uffff\1\3\4\uffff\1\5\6\uffff\1\15\1\7\1\16",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\37",
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
            "\1\41",
            "",
            "",
            "\1\44\32\uffff\1\43\1\45",
            "\1\50\33\uffff\1\51\1\47",
            "",
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
            return "1:1: Tokens : ( MINUS | AND | OR | Comma | RULE_TERM | RULE_ECL | RULE_TERM_STRING | RULE_REVERSED | RULE_TO | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_NOT | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_STRING );";
        }
    }
 

}