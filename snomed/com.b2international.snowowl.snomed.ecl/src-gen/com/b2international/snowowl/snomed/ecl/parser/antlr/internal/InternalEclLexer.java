package com.b2international.snowowl.snomed.ecl.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalEclLexer extends Lexer {
    public static final int RULE_LTE=30;
    public static final int RULE_CURLY_OPEN=19;
    public static final int RULE_DIGIT_NONZERO=31;
    public static final int RULE_DBL_GT=13;
    public static final int RULE_ROUND_CLOSE=18;
    public static final int RULE_TO=23;
    public static final int RULE_GT=12;
    public static final int RULE_STRING=27;
    public static final int RULE_NOT=35;
    public static final int RULE_REVERSED=21;
    public static final int RULE_AND=34;
    public static final int RULE_GTE=29;
    public static final int RULE_SL_COMMENT=42;
    public static final int RULE_HASH=28;
    public static final int RULE_ROUND_OPEN=17;
    public static final int RULE_DASH=39;
    public static final int RULE_OTHER_CHARACTER=40;
    public static final int RULE_DBL_LT=10;
    public static final int RULE_PLUS=37;
    public static final int RULE_NOT_EQUAL=26;
    public static final int RULE_OR=4;
    public static final int RULE_DOT=7;
    public static final int EOF=-1;
    public static final int RULE_SQUARE_CLOSE=24;
    public static final int RULE_SQUARE_OPEN=22;
    public static final int RULE_EQUAL=25;
    public static final int RULE_LT_EM=8;
    public static final int RULE_GT_EM=11;
    public static final int RULE_WS=33;
    public static final int RULE_COMMA=38;
    public static final int RULE_CURLY_CLOSE=20;
    public static final int RULE_ZERO=32;
    public static final int RULE_COLON=6;
    public static final int RULE_MINUS=5;
    public static final int RULE_LETTER=36;
    public static final int RULE_LT=9;
    public static final int RULE_CARET=14;
    public static final int RULE_PIPE=15;
    public static final int RULE_ML_COMMENT=41;
    public static final int RULE_WILDCARD=16;

    // delegates
    // delegators

    public InternalEclLexer() {;} 
    public InternalEclLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalEclLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "InternalEcl.g"; }

    // $ANTLR start "RULE_REVERSED"
    public final void mRULE_REVERSED() throws RecognitionException {
        try {
            int _type = RULE_REVERSED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEcl.g:3600:15: ( 'R' )
            // InternalEcl.g:3600:17: 'R'
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
            // InternalEcl.g:3602:9: ( '..' )
            // InternalEcl.g:3602:11: '..'
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

    // $ANTLR start "RULE_AND"
    public final void mRULE_AND() throws RecognitionException {
        try {
            int _type = RULE_AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEcl.g:3604:10: ( 'AND' )
            // InternalEcl.g:3604:12: 'AND'
            {
            match("AND"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_AND"

    // $ANTLR start "RULE_OR"
    public final void mRULE_OR() throws RecognitionException {
        try {
            int _type = RULE_OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEcl.g:3606:9: ( 'OR' )
            // InternalEcl.g:3606:11: 'OR'
            {
            match("OR"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_OR"

    // $ANTLR start "RULE_MINUS"
    public final void mRULE_MINUS() throws RecognitionException {
        try {
            int _type = RULE_MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEcl.g:3608:12: ( 'MINUS' )
            // InternalEcl.g:3608:14: 'MINUS'
            {
            match("MINUS"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_MINUS"

    // $ANTLR start "RULE_ZERO"
    public final void mRULE_ZERO() throws RecognitionException {
        try {
            int _type = RULE_ZERO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEcl.g:3610:11: ( '0' )
            // InternalEcl.g:3610:13: '0'
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
            // InternalEcl.g:3612:20: ( '1' .. '9' )
            // InternalEcl.g:3612:22: '1' .. '9'
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
            // InternalEcl.g:3614:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) )
            // InternalEcl.g:3614:15: ( 'a' .. 'z' | 'A' .. 'Z' )
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

    // $ANTLR start "RULE_PIPE"
    public final void mRULE_PIPE() throws RecognitionException {
        try {
            int _type = RULE_PIPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEcl.g:3616:11: ( '|' )
            // InternalEcl.g:3616:13: '|'
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
            // InternalEcl.g:3618:12: ( ':' )
            // InternalEcl.g:3618:14: ':'
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
            // InternalEcl.g:3620:17: ( '{' )
            // InternalEcl.g:3620:19: '{'
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
            // InternalEcl.g:3622:18: ( '}' )
            // InternalEcl.g:3622:20: '}'
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
            // InternalEcl.g:3624:12: ( ',' )
            // InternalEcl.g:3624:14: ','
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

    // $ANTLR start "RULE_ROUND_OPEN"
    public final void mRULE_ROUND_OPEN() throws RecognitionException {
        try {
            int _type = RULE_ROUND_OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalEcl.g:3626:17: ( '(' )
            // InternalEcl.g:3626:19: '('
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
            // InternalEcl.g:3628:18: ( ')' )
            // InternalEcl.g:3628:20: ')'
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
            // InternalEcl.g:3630:18: ( '[' )
            // InternalEcl.g:3630:20: '['
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
            // InternalEcl.g:3632:19: ( ']' )
            // InternalEcl.g:3632:21: ']'
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
            // InternalEcl.g:3634:11: ( '+' )
            // InternalEcl.g:3634:13: '+'
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
            // InternalEcl.g:3636:11: ( '-' )
            // InternalEcl.g:3636:13: '-'
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
            // InternalEcl.g:3638:12: ( '^' )
            // InternalEcl.g:3638:14: '^'
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
            // InternalEcl.g:3640:10: ( '!' )
            // InternalEcl.g:3640:12: '!'
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
            // InternalEcl.g:3642:10: ( '.' )
            // InternalEcl.g:3642:12: '.'
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
            // InternalEcl.g:3644:15: ( '*' )
            // InternalEcl.g:3644:17: '*'
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
            // InternalEcl.g:3646:12: ( '=' )
            // InternalEcl.g:3646:14: '='
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
            // InternalEcl.g:3648:16: ( '!=' )
            // InternalEcl.g:3648:18: '!='
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
            // InternalEcl.g:3650:9: ( '<' )
            // InternalEcl.g:3650:11: '<'
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
            // InternalEcl.g:3652:9: ( '>' )
            // InternalEcl.g:3652:11: '>'
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
            // InternalEcl.g:3654:13: ( '<<' )
            // InternalEcl.g:3654:15: '<<'
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
            // InternalEcl.g:3656:13: ( '>>' )
            // InternalEcl.g:3656:15: '>>'
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
            // InternalEcl.g:3658:12: ( '<!' )
            // InternalEcl.g:3658:14: '<!'
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
            // InternalEcl.g:3660:12: ( '>!' )
            // InternalEcl.g:3660:14: '>!'
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
            // InternalEcl.g:3662:10: ( '>=' )
            // InternalEcl.g:3662:12: '>='
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
            // InternalEcl.g:3664:10: ( '<=' )
            // InternalEcl.g:3664:12: '<='
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
            // InternalEcl.g:3666:11: ( '#' )
            // InternalEcl.g:3666:13: '#'
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
            // InternalEcl.g:3668:9: ( ( ' ' | '\\t' | '\\n' | '\\r' ) )
            // InternalEcl.g:3668:11: ( ' ' | '\\t' | '\\n' | '\\r' )
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
            // InternalEcl.g:3670:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // InternalEcl.g:3670:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // InternalEcl.g:3670:24: ( options {greedy=false; } : . )*
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
            	    // InternalEcl.g:3670:52: .
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
            // InternalEcl.g:3672:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // InternalEcl.g:3672:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // InternalEcl.g:3672:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\u0000' && LA2_0<='\t')||(LA2_0>='\u000B' && LA2_0<='\f')||(LA2_0>='\u000E' && LA2_0<='\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // InternalEcl.g:3672:24: ~ ( ( '\\n' | '\\r' ) )
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

            // InternalEcl.g:3672:40: ( ( '\\r' )? '\\n' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='\n'||LA4_0=='\r') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // InternalEcl.g:3672:41: ( '\\r' )? '\\n'
                    {
                    // InternalEcl.g:3672:41: ( '\\r' )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0=='\r') ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // InternalEcl.g:3672:41: '\\r'
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
            // InternalEcl.g:3674:22: (~ ( '|' ) )
            // InternalEcl.g:3674:24: ~ ( '|' )
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
            // InternalEcl.g:3676:13: ( ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // InternalEcl.g:3676:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // InternalEcl.g:3676:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='\"') ) {
                alt7=1;
            }
            else if ( (LA7_0=='\'') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // InternalEcl.g:3676:16: '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // InternalEcl.g:3676:20: ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )*
                    loop5:
                    do {
                        int alt5=3;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0=='\\') ) {
                            alt5=1;
                        }
                        else if ( ((LA5_0>='\u0000' && LA5_0<='!')||(LA5_0>='#' && LA5_0<='[')||(LA5_0>=']' && LA5_0<='\uFFFF')) ) {
                            alt5=2;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // InternalEcl.g:3676:21: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalEcl.g:3676:28: ~ ( ( '\\\\' | '\"' ) )
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
                    	    break loop5;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 2 :
                    // InternalEcl.g:3676:48: '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // InternalEcl.g:3676:53: ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )*
                    loop6:
                    do {
                        int alt6=3;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0=='\\') ) {
                            alt6=1;
                        }
                        else if ( ((LA6_0>='\u0000' && LA6_0<='&')||(LA6_0>='(' && LA6_0<='[')||(LA6_0>=']' && LA6_0<='\uFFFF')) ) {
                            alt6=2;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // InternalEcl.g:3676:54: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalEcl.g:3676:61: ~ ( ( '\\\\' | '\\'' ) )
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
                    	    break loop6;
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
        // InternalEcl.g:1:8: ( RULE_REVERSED | RULE_TO | RULE_AND | RULE_OR | RULE_MINUS | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_LETTER | RULE_PIPE | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_COMMA | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_NOT | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_OTHER_CHARACTER | RULE_STRING )
        int alt8=39;
        alt8 = dfa8.predict(input);
        switch (alt8) {
            case 1 :
                // InternalEcl.g:1:10: RULE_REVERSED
                {
                mRULE_REVERSED(); 

                }
                break;
            case 2 :
                // InternalEcl.g:1:24: RULE_TO
                {
                mRULE_TO(); 

                }
                break;
            case 3 :
                // InternalEcl.g:1:32: RULE_AND
                {
                mRULE_AND(); 

                }
                break;
            case 4 :
                // InternalEcl.g:1:41: RULE_OR
                {
                mRULE_OR(); 

                }
                break;
            case 5 :
                // InternalEcl.g:1:49: RULE_MINUS
                {
                mRULE_MINUS(); 

                }
                break;
            case 6 :
                // InternalEcl.g:1:60: RULE_ZERO
                {
                mRULE_ZERO(); 

                }
                break;
            case 7 :
                // InternalEcl.g:1:70: RULE_DIGIT_NONZERO
                {
                mRULE_DIGIT_NONZERO(); 

                }
                break;
            case 8 :
                // InternalEcl.g:1:89: RULE_LETTER
                {
                mRULE_LETTER(); 

                }
                break;
            case 9 :
                // InternalEcl.g:1:101: RULE_PIPE
                {
                mRULE_PIPE(); 

                }
                break;
            case 10 :
                // InternalEcl.g:1:111: RULE_COLON
                {
                mRULE_COLON(); 

                }
                break;
            case 11 :
                // InternalEcl.g:1:122: RULE_CURLY_OPEN
                {
                mRULE_CURLY_OPEN(); 

                }
                break;
            case 12 :
                // InternalEcl.g:1:138: RULE_CURLY_CLOSE
                {
                mRULE_CURLY_CLOSE(); 

                }
                break;
            case 13 :
                // InternalEcl.g:1:155: RULE_COMMA
                {
                mRULE_COMMA(); 

                }
                break;
            case 14 :
                // InternalEcl.g:1:166: RULE_ROUND_OPEN
                {
                mRULE_ROUND_OPEN(); 

                }
                break;
            case 15 :
                // InternalEcl.g:1:182: RULE_ROUND_CLOSE
                {
                mRULE_ROUND_CLOSE(); 

                }
                break;
            case 16 :
                // InternalEcl.g:1:199: RULE_SQUARE_OPEN
                {
                mRULE_SQUARE_OPEN(); 

                }
                break;
            case 17 :
                // InternalEcl.g:1:216: RULE_SQUARE_CLOSE
                {
                mRULE_SQUARE_CLOSE(); 

                }
                break;
            case 18 :
                // InternalEcl.g:1:234: RULE_PLUS
                {
                mRULE_PLUS(); 

                }
                break;
            case 19 :
                // InternalEcl.g:1:244: RULE_DASH
                {
                mRULE_DASH(); 

                }
                break;
            case 20 :
                // InternalEcl.g:1:254: RULE_CARET
                {
                mRULE_CARET(); 

                }
                break;
            case 21 :
                // InternalEcl.g:1:265: RULE_NOT
                {
                mRULE_NOT(); 

                }
                break;
            case 22 :
                // InternalEcl.g:1:274: RULE_DOT
                {
                mRULE_DOT(); 

                }
                break;
            case 23 :
                // InternalEcl.g:1:283: RULE_WILDCARD
                {
                mRULE_WILDCARD(); 

                }
                break;
            case 24 :
                // InternalEcl.g:1:297: RULE_EQUAL
                {
                mRULE_EQUAL(); 

                }
                break;
            case 25 :
                // InternalEcl.g:1:308: RULE_NOT_EQUAL
                {
                mRULE_NOT_EQUAL(); 

                }
                break;
            case 26 :
                // InternalEcl.g:1:323: RULE_LT
                {
                mRULE_LT(); 

                }
                break;
            case 27 :
                // InternalEcl.g:1:331: RULE_GT
                {
                mRULE_GT(); 

                }
                break;
            case 28 :
                // InternalEcl.g:1:339: RULE_DBL_LT
                {
                mRULE_DBL_LT(); 

                }
                break;
            case 29 :
                // InternalEcl.g:1:351: RULE_DBL_GT
                {
                mRULE_DBL_GT(); 

                }
                break;
            case 30 :
                // InternalEcl.g:1:363: RULE_LT_EM
                {
                mRULE_LT_EM(); 

                }
                break;
            case 31 :
                // InternalEcl.g:1:374: RULE_GT_EM
                {
                mRULE_GT_EM(); 

                }
                break;
            case 32 :
                // InternalEcl.g:1:385: RULE_GTE
                {
                mRULE_GTE(); 

                }
                break;
            case 33 :
                // InternalEcl.g:1:394: RULE_LTE
                {
                mRULE_LTE(); 

                }
                break;
            case 34 :
                // InternalEcl.g:1:403: RULE_HASH
                {
                mRULE_HASH(); 

                }
                break;
            case 35 :
                // InternalEcl.g:1:413: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 36 :
                // InternalEcl.g:1:421: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 37 :
                // InternalEcl.g:1:437: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 38 :
                // InternalEcl.g:1:453: RULE_OTHER_CHARACTER
                {
                mRULE_OTHER_CHARACTER(); 

                }
                break;
            case 39 :
                // InternalEcl.g:1:474: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;

        }

    }


    protected DFA8 dfa8 = new DFA8(this);
    static final String DFA8_eotS =
        "\2\uffff\1\42\3\44\17\uffff\1\65\2\uffff\1\73\1\77\2\uffff\3\37\46\uffff";
    static final String DFA8_eofS =
        "\105\uffff";
    static final String DFA8_minS =
        "\1\0\1\uffff\1\56\1\116\1\122\1\111\17\uffff\1\75\2\uffff\2\41\2\uffff\1\52\2\0\46\uffff";
    static final String DFA8_maxS =
        "\1\uffff\1\uffff\1\56\1\116\1\122\1\111\17\uffff\1\75\2\uffff\1\75\1\76\2\uffff\1\57\2\uffff\46\uffff";
    static final String DFA8_acceptS =
        "\1\uffff\1\1\4\uffff\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\uffff\1\27\1\30\2\uffff\1\42\1\43\3\uffff\1\46\1\1\1\2\1\26\1\3\1\10\1\4\1\5\1\6\1\7\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\31\1\25\1\27\1\30\1\34\1\36\1\41\1\32\1\35\1\37\1\40\1\33\1\42\1\43\1\44\1\45\1\47";
    static final String DFA8_specialS =
        "\1\0\34\uffff\1\1\1\2\46\uffff}>";
    static final String[] DFA8_transitionS = {
            "\11\37\2\33\2\37\1\33\22\37\1\33\1\25\1\35\1\32\3\37\1\36\1\16\1\17\1\26\1\22\1\15\1\23\1\2\1\34\1\6\11\7\1\12\1\37\1\30\1\27\1\31\2\37\1\3\13\10\1\5\1\10\1\4\2\10\1\1\10\10\1\20\1\37\1\21\1\24\2\37\32\10\1\13\1\11\1\14\uff82\37",
            "",
            "\1\41",
            "\1\43",
            "\1\45",
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

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( RULE_REVERSED | RULE_TO | RULE_AND | RULE_OR | RULE_MINUS | RULE_ZERO | RULE_DIGIT_NONZERO | RULE_LETTER | RULE_PIPE | RULE_COLON | RULE_CURLY_OPEN | RULE_CURLY_CLOSE | RULE_COMMA | RULE_ROUND_OPEN | RULE_ROUND_CLOSE | RULE_SQUARE_OPEN | RULE_SQUARE_CLOSE | RULE_PLUS | RULE_DASH | RULE_CARET | RULE_NOT | RULE_DOT | RULE_WILDCARD | RULE_EQUAL | RULE_NOT_EQUAL | RULE_LT | RULE_GT | RULE_DBL_LT | RULE_DBL_GT | RULE_LT_EM | RULE_GT_EM | RULE_GTE | RULE_LTE | RULE_HASH | RULE_WS | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_OTHER_CHARACTER | RULE_STRING );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA8_0 = input.LA(1);

                        s = -1;
                        if ( (LA8_0=='R') ) {s = 1;}

                        else if ( (LA8_0=='.') ) {s = 2;}

                        else if ( (LA8_0=='A') ) {s = 3;}

                        else if ( (LA8_0=='O') ) {s = 4;}

                        else if ( (LA8_0=='M') ) {s = 5;}

                        else if ( (LA8_0=='0') ) {s = 6;}

                        else if ( ((LA8_0>='1' && LA8_0<='9')) ) {s = 7;}

                        else if ( ((LA8_0>='B' && LA8_0<='L')||LA8_0=='N'||(LA8_0>='P' && LA8_0<='Q')||(LA8_0>='S' && LA8_0<='Z')||(LA8_0>='a' && LA8_0<='z')) ) {s = 8;}

                        else if ( (LA8_0=='|') ) {s = 9;}

                        else if ( (LA8_0==':') ) {s = 10;}

                        else if ( (LA8_0=='{') ) {s = 11;}

                        else if ( (LA8_0=='}') ) {s = 12;}

                        else if ( (LA8_0==',') ) {s = 13;}

                        else if ( (LA8_0=='(') ) {s = 14;}

                        else if ( (LA8_0==')') ) {s = 15;}

                        else if ( (LA8_0=='[') ) {s = 16;}

                        else if ( (LA8_0==']') ) {s = 17;}

                        else if ( (LA8_0=='+') ) {s = 18;}

                        else if ( (LA8_0=='-') ) {s = 19;}

                        else if ( (LA8_0=='^') ) {s = 20;}

                        else if ( (LA8_0=='!') ) {s = 21;}

                        else if ( (LA8_0=='*') ) {s = 22;}

                        else if ( (LA8_0=='=') ) {s = 23;}

                        else if ( (LA8_0=='<') ) {s = 24;}

                        else if ( (LA8_0=='>') ) {s = 25;}

                        else if ( (LA8_0=='#') ) {s = 26;}

                        else if ( ((LA8_0>='\t' && LA8_0<='\n')||LA8_0=='\r'||LA8_0==' ') ) {s = 27;}

                        else if ( (LA8_0=='/') ) {s = 28;}

                        else if ( (LA8_0=='\"') ) {s = 29;}

                        else if ( (LA8_0=='\'') ) {s = 30;}

                        else if ( ((LA8_0>='\u0000' && LA8_0<='\b')||(LA8_0>='\u000B' && LA8_0<='\f')||(LA8_0>='\u000E' && LA8_0<='\u001F')||(LA8_0>='$' && LA8_0<='&')||LA8_0==';'||(LA8_0>='?' && LA8_0<='@')||LA8_0=='\\'||(LA8_0>='_' && LA8_0<='`')||(LA8_0>='~' && LA8_0<='\uFFFF')) ) {s = 31;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA8_29 = input.LA(1);

                        s = -1;
                        if ( ((LA8_29>='\u0000' && LA8_29<='\uFFFF')) ) {s = 68;}

                        else s = 31;

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA8_30 = input.LA(1);

                        s = -1;
                        if ( ((LA8_30>='\u0000' && LA8_30<='\uFFFF')) ) {s = 68;}

                        else s = 31;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 8, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}