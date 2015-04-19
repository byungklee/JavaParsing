// $ANTLR 3.5.1 /Users/byung/workspace/antlr2/Java.g 2015-04-05 02:31:24

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created
 *          elementValuePair and elementValuePairs rules, then used them in the
 *          annotation rule.  Allows it to recognize annotation references with
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which
 *          has the Identifier portion in it, the parser would fail on constants in
 *          annotation definitions because it expected two identifiers.
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *         
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and
 *          normalInterfaceDeclaration rather than classDeclaration and
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation,
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java
 *      letter-or-digit is a character for which the method
 *      Character.isJavaIdentifierPart(int) returns true."
 */
@SuppressWarnings("all")
public class JavaParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABSTRACT", "AMP", "AMPAMP", "AMPEQ", 
		"ASSERT", "BANG", "BANGEQ", "BAR", "BARBAR", "BAREQ", "BOOLEAN", "BREAK", 
		"BYTE", "CARET", "CARETEQ", "CASE", "CATCH", "CHAR", "CHARLITERAL", "CLASS", 
		"COLON", "COMMA", "COMMENT", "CONST", "CONTINUE", "DEFAULT", "DO", "DOT", 
		"DOUBLE", "DOUBLELITERAL", "DoubleSuffix", "ELLIPSIS", "ELSE", "ENUM", 
		"EQ", "EQEQ", "EXTENDS", "EscapeSequence", "Exponent", "FALSE", "FINAL", 
		"FINALLY", "FLOAT", "FLOATLITERAL", "FOR", "FloatSuffix", "GOTO", "GT", 
		"HexDigit", "HexPrefix", "IDENTIFIER", "IF", "IMPLEMENTS", "IMPORT", "INSTANCEOF", 
		"INT", "INTERFACE", "INTLITERAL", "IdentifierPart", "IdentifierStart", 
		"IntegerNumber", "LBRACE", "LBRACKET", "LINE_COMMENT", "LONG", "LONGLITERAL", 
		"LPAREN", "LT", "LongSuffix", "MONKEYS_AT", "NATIVE", "NEW", "NULL", "NonIntegerNumber", 
		"PACKAGE", "PERCENT", "PERCENTEQ", "PLUS", "PLUSEQ", "PLUSPLUS", "PRIVATE", 
		"PROTECTED", "PUBLIC", "QUES", "RBRACE", "RBRACKET", "RETURN", "RPAREN", 
		"SEMI", "SHORT", "SLASH", "SLASHEQ", "STAR", "STAREQ", "STATIC", "STRICTFP", 
		"STRINGLITERAL", "SUB", "SUBEQ", "SUBSUB", "SUPER", "SWITCH", "SYNCHRONIZED", 
		"SurrogateIdentifer", "THIS", "THROW", "THROWS", "TILDE", "TRANSIENT", 
		"TRUE", "TRY", "VOID", "VOLATILE", "WHILE", "WS"
	};
	public static final int EOF=-1;
	public static final int ABSTRACT=4;
	public static final int AMP=5;
	public static final int AMPAMP=6;
	public static final int AMPEQ=7;
	public static final int ASSERT=8;
	public static final int BANG=9;
	public static final int BANGEQ=10;
	public static final int BAR=11;
	public static final int BARBAR=12;
	public static final int BAREQ=13;
	public static final int BOOLEAN=14;
	public static final int BREAK=15;
	public static final int BYTE=16;
	public static final int CARET=17;
	public static final int CARETEQ=18;
	public static final int CASE=19;
	public static final int CATCH=20;
	public static final int CHAR=21;
	public static final int CHARLITERAL=22;
	public static final int CLASS=23;
	public static final int COLON=24;
	public static final int COMMA=25;
	public static final int COMMENT=26;
	public static final int CONST=27;
	public static final int CONTINUE=28;
	public static final int DEFAULT=29;
	public static final int DO=30;
	public static final int DOT=31;
	public static final int DOUBLE=32;
	public static final int DOUBLELITERAL=33;
	public static final int DoubleSuffix=34;
	public static final int ELLIPSIS=35;
	public static final int ELSE=36;
	public static final int ENUM=37;
	public static final int EQ=38;
	public static final int EQEQ=39;
	public static final int EXTENDS=40;
	public static final int EscapeSequence=41;
	public static final int Exponent=42;
	public static final int FALSE=43;
	public static final int FINAL=44;
	public static final int FINALLY=45;
	public static final int FLOAT=46;
	public static final int FLOATLITERAL=47;
	public static final int FOR=48;
	public static final int FloatSuffix=49;
	public static final int GOTO=50;
	public static final int GT=51;
	public static final int HexDigit=52;
	public static final int HexPrefix=53;
	public static final int IDENTIFIER=54;
	public static final int IF=55;
	public static final int IMPLEMENTS=56;
	public static final int IMPORT=57;
	public static final int INSTANCEOF=58;
	public static final int INT=59;
	public static final int INTERFACE=60;
	public static final int INTLITERAL=61;
	public static final int IdentifierPart=62;
	public static final int IdentifierStart=63;
	public static final int IntegerNumber=64;
	public static final int LBRACE=65;
	public static final int LBRACKET=66;
	public static final int LINE_COMMENT=67;
	public static final int LONG=68;
	public static final int LONGLITERAL=69;
	public static final int LPAREN=70;
	public static final int LT=71;
	public static final int LongSuffix=72;
	public static final int MONKEYS_AT=73;
	public static final int NATIVE=74;
	public static final int NEW=75;
	public static final int NULL=76;
	public static final int NonIntegerNumber=77;
	public static final int PACKAGE=78;
	public static final int PERCENT=79;
	public static final int PERCENTEQ=80;
	public static final int PLUS=81;
	public static final int PLUSEQ=82;
	public static final int PLUSPLUS=83;
	public static final int PRIVATE=84;
	public static final int PROTECTED=85;
	public static final int PUBLIC=86;
	public static final int QUES=87;
	public static final int RBRACE=88;
	public static final int RBRACKET=89;
	public static final int RETURN=90;
	public static final int RPAREN=91;
	public static final int SEMI=92;
	public static final int SHORT=93;
	public static final int SLASH=94;
	public static final int SLASHEQ=95;
	public static final int STAR=96;
	public static final int STAREQ=97;
	public static final int STATIC=98;
	public static final int STRICTFP=99;
	public static final int STRINGLITERAL=100;
	public static final int SUB=101;
	public static final int SUBEQ=102;
	public static final int SUBSUB=103;
	public static final int SUPER=104;
	public static final int SWITCH=105;
	public static final int SYNCHRONIZED=106;
	public static final int SurrogateIdentifer=107;
	public static final int THIS=108;
	public static final int THROW=109;
	public static final int THROWS=110;
	public static final int TILDE=111;
	public static final int TRANSIENT=112;
	public static final int TRUE=113;
	public static final int TRY=114;
	public static final int VOID=115;
	public static final int VOLATILE=116;
	public static final int WHILE=117;
	public static final int WS=118;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public JavaParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public JavaParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
		this.state.ruleMemo = new HashMap[381+1];


	}

	@Override public String[] getTokenNames() { return JavaParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/byung/workspace/antlr2/Java.g"; }


		int classCount = 0;

		String lt = "";

		ArrayList<ClassInfo> classInfos = new ArrayList<ClassInfo>();
		ClassInfo ci = new ClassInfo();
		Stack<String> classStack = new Stack();
		Stack<String> methodStack = new Stack();
		
		void kAdd(String s) { ci.keywords.add(s);ci.keywordsSet.add(s);}
		void uAdd(String s) { 
			String temp = "";
			if(!classStack.isEmpty()) {
				temp = temp+classStack.peek();
			}
			if(!methodStack.isEmpty()) {
				temp = temp+":"+methodStack.peek();
			}
			temp = temp+":"+s;
			
			ci.udis.add(temp);
			ci.udisSet.add(temp); 
		}
		void cAdd(String s) { ci.constants.add(s);ci.constantsSet.add(s);}
		void sAdd(String s) { ci.specialChars.add(s);ci.specialCharsSet.add(s);}
		boolean containsIdentifier(String s) { return ci.udis.contains(s); }
		boolean isPrimitive(String s) {
			if(s.equals("int") || s.equals("float") || s.equals("double") || s.equals("long") || s.equals("boolean")
				|| s.equals("char") || s.equals("short") || s.equals("byte")) {
				return true;
			}
			return false;
		}
		



	// $ANTLR start "compilationUnit"
	// /Users/byung/workspace/antlr2/Java.g:334:1: compilationUnit : ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* ;
	public final void compilationUnit() throws RecognitionException {
		int compilationUnit_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:335:5: ( ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* )
			// /Users/byung/workspace/antlr2/Java.g:335:9: ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )*
			{
			// /Users/byung/workspace/antlr2/Java.g:335:9: ( ( annotations )? packageDeclaration )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==MONKEYS_AT) ) {
				int LA2_1 = input.LA(2);
				if ( (synpred2_Java()) ) {
					alt2=1;
				}
			}
			else if ( (LA2_0==PACKAGE) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:335:13: ( annotations )? packageDeclaration
					{
					// /Users/byung/workspace/antlr2/Java.g:335:13: ( annotations )?
					int alt1=2;
					int LA1_0 = input.LA(1);
					if ( (LA1_0==MONKEYS_AT) ) {
						alt1=1;
					}
					switch (alt1) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:335:14: annotations
							{
							pushFollow(FOLLOW_annotations_in_compilationUnit91);
							annotations();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					pushFollow(FOLLOW_packageDeclaration_in_compilationUnit120);
					packageDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:339:9: ( importDeclaration )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==IMPORT) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:339:10: importDeclaration
					{
					pushFollow(FOLLOW_importDeclaration_in_compilationUnit142);
					importDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop3;
				}
			}

			// /Users/byung/workspace/antlr2/Java.g:341:9: ( typeDeclaration )*
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0==ABSTRACT||LA4_0==BOOLEAN||LA4_0==BYTE||LA4_0==CHAR||LA4_0==CLASS||LA4_0==DOUBLE||LA4_0==ENUM||LA4_0==FINAL||LA4_0==FLOAT||LA4_0==IDENTIFIER||(LA4_0 >= INT && LA4_0 <= INTERFACE)||LA4_0==LONG||LA4_0==LT||(LA4_0 >= MONKEYS_AT && LA4_0 <= NATIVE)||(LA4_0 >= PRIVATE && LA4_0 <= PUBLIC)||(LA4_0 >= SEMI && LA4_0 <= SHORT)||(LA4_0 >= STATIC && LA4_0 <= STRICTFP)||LA4_0==SYNCHRONIZED||LA4_0==TRANSIENT||(LA4_0 >= VOID && LA4_0 <= VOLATILE)) ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:341:10: typeDeclaration
					{
					pushFollow(FOLLOW_typeDeclaration_in_compilationUnit164);
					typeDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop4;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 1, compilationUnit_StartIndex); }

		}
	}
	// $ANTLR end "compilationUnit"



	// $ANTLR start "packageDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:345:1: packageDeclaration : 'package' qualifiedName ';' ;
	public final void packageDeclaration() throws RecognitionException {
		int packageDeclaration_StartIndex = input.index();

		ParserRuleReturnScope qualifiedName1 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:346:5: ( 'package' qualifiedName ';' )
			// /Users/byung/workspace/antlr2/Java.g:346:9: 'package' qualifiedName ';'
			{
			match(input,PACKAGE,FOLLOW_PACKAGE_in_packageDeclaration195); if (state.failed) return;
			pushFollow(FOLLOW_qualifiedName_in_packageDeclaration197);
			qualifiedName1=qualifiedName();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) { ci.akip("package"); System.out.println("Package Name:" + (qualifiedName1!=null?input.toString(qualifiedName1.start,qualifiedName1.stop):null));ci.packageName = (qualifiedName1!=null?input.toString(qualifiedName1.start,qualifiedName1.stop):null); }
			match(input,SEMI,FOLLOW_SEMI_in_packageDeclaration209); if (state.failed) return;
			if ( state.backtracking==0 ) { ci.ascip(";");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 2, packageDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "packageDeclaration"



	// $ANTLR start "importDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:350:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );
	public final void importDeclaration() throws RecognitionException {
		int importDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:351:5: ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' )
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==IMPORT) ) {
				int LA9_1 = input.LA(2);
				if ( (LA9_1==STATIC) ) {
					int LA9_2 = input.LA(3);
					if ( (LA9_2==IDENTIFIER) ) {
						int LA9_3 = input.LA(4);
						if ( (LA9_3==DOT) ) {
							int LA9_4 = input.LA(5);
							if ( (LA9_4==STAR) ) {
								alt9=1;
							}
							else if ( (LA9_4==IDENTIFIER) ) {
								alt9=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 9, 4, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							if (state.backtracking>0) {state.failed=true; return;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 9, 3, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 9, 2, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA9_1==IDENTIFIER) ) {
					int LA9_3 = input.LA(3);
					if ( (LA9_3==DOT) ) {
						int LA9_4 = input.LA(4);
						if ( (LA9_4==STAR) ) {
							alt9=1;
						}
						else if ( (LA9_4==IDENTIFIER) ) {
							alt9=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 9, 4, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 9, 3, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 9, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:351:9: 'import' ( 'static' )? IDENTIFIER '.' '*' ';'
					{
					match(input,IMPORT,FOLLOW_IMPORT_in_importDeclaration232); if (state.failed) return;
					if ( state.backtracking==0 ) { ci.akip("import");}
					// /Users/byung/workspace/antlr2/Java.g:352:9: ( 'static' )?
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0==STATIC) ) {
						alt5=1;
					}
					switch (alt5) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:352:10: 'static'
							{
							match(input,STATIC,FOLLOW_STATIC_in_importDeclaration245); if (state.failed) return;
							if ( state.backtracking==0 ) {ci.akip("static");}
							}
							break;

					}

					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclaration268); if (state.failed) return;
					match(input,DOT,FOLLOW_DOT_in_importDeclaration270); if (state.failed) return;
					match(input,STAR,FOLLOW_STAR_in_importDeclaration272); if (state.failed) return;
					if ( state.backtracking==0 ) {  ci.ascip("."); ci.ascip("*");}
					match(input,SEMI,FOLLOW_SEMI_in_importDeclaration285); if (state.failed) return;
					if ( state.backtracking==0 ) { sAdd(":");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:356:9: 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';'
					{
					match(input,IMPORT,FOLLOW_IMPORT_in_importDeclaration302); if (state.failed) return;
					if ( state.backtracking==0 ) { ci.akip("import");}
					// /Users/byung/workspace/antlr2/Java.g:357:9: ( 'static' )?
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0==STATIC) ) {
						alt6=1;
					}
					switch (alt6) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:357:10: 'static'
							{
							match(input,STATIC,FOLLOW_STATIC_in_importDeclaration315); if (state.failed) return;
							if ( state.backtracking==0 ) { ci.akip("static");}
							}
							break;

					}

					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclaration339); if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:360:9: ( '.' IDENTIFIER )+
					int cnt7=0;
					loop7:
					while (true) {
						int alt7=2;
						int LA7_0 = input.LA(1);
						if ( (LA7_0==DOT) ) {
							int LA7_1 = input.LA(2);
							if ( (LA7_1==IDENTIFIER) ) {
								alt7=1;
							}

						}

						switch (alt7) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:360:10: '.' IDENTIFIER
							{
							match(input,DOT,FOLLOW_DOT_in_importDeclaration350); if (state.failed) return;
							match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclaration352); if (state.failed) return;
							if ( state.backtracking==0 ) {  ci.ascip(".");}
							}
							break;

						default :
							if ( cnt7 >= 1 ) break loop7;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(7, input);
							throw eee;
						}
						cnt7++;
					}

					// /Users/byung/workspace/antlr2/Java.g:362:9: ( '.' '*' )?
					int alt8=2;
					int LA8_0 = input.LA(1);
					if ( (LA8_0==DOT) ) {
						alt8=1;
					}
					switch (alt8) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:362:10: '.' '*'
							{
							match(input,DOT,FOLLOW_DOT_in_importDeclaration376); if (state.failed) return;
							match(input,STAR,FOLLOW_STAR_in_importDeclaration378); if (state.failed) return;
							if ( state.backtracking==0 ) {  ci.ascip("."); ci.ascip("*");}
							}
							break;

					}

					match(input,SEMI,FOLLOW_SEMI_in_importDeclaration402); if (state.failed) return;
					if ( state.backtracking==0 ) {  ci.ascip(";");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 3, importDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "importDeclaration"



	// $ANTLR start "qualifiedImportName"
	// /Users/byung/workspace/antlr2/Java.g:367:1: qualifiedImportName : IDENTIFIER ( '.' IDENTIFIER )* ;
	public final void qualifiedImportName() throws RecognitionException {
		int qualifiedImportName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:368:5: ( IDENTIFIER ( '.' IDENTIFIER )* )
			// /Users/byung/workspace/antlr2/Java.g:368:9: IDENTIFIER ( '.' IDENTIFIER )*
			{
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedImportName425); if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:369:9: ( '.' IDENTIFIER )*
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0==DOT) ) {
					alt10=1;
				}

				switch (alt10) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:369:10: '.' IDENTIFIER
					{
					match(input,DOT,FOLLOW_DOT_in_qualifiedImportName437); if (state.failed) return;
					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedImportName439); if (state.failed) return;
					}
					break;

				default :
					break loop10;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 4, qualifiedImportName_StartIndex); }

		}
	}
	// $ANTLR end "qualifiedImportName"



	// $ANTLR start "typeDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:373:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );
	public final void typeDeclaration() throws RecognitionException {
		int typeDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:374:5: ( classOrInterfaceDeclaration | ';' )
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==ABSTRACT||LA11_0==BOOLEAN||LA11_0==BYTE||LA11_0==CHAR||LA11_0==CLASS||LA11_0==DOUBLE||LA11_0==ENUM||LA11_0==FINAL||LA11_0==FLOAT||LA11_0==IDENTIFIER||(LA11_0 >= INT && LA11_0 <= INTERFACE)||LA11_0==LONG||LA11_0==LT||(LA11_0 >= MONKEYS_AT && LA11_0 <= NATIVE)||(LA11_0 >= PRIVATE && LA11_0 <= PUBLIC)||LA11_0==SHORT||(LA11_0 >= STATIC && LA11_0 <= STRICTFP)||LA11_0==SYNCHRONIZED||LA11_0==TRANSIENT||(LA11_0 >= VOID && LA11_0 <= VOLATILE)) ) {
				alt11=1;
			}
			else if ( (LA11_0==SEMI) ) {
				alt11=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}

			switch (alt11) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:374:9: classOrInterfaceDeclaration
					{
					pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration470);
					classOrInterfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:375:9: ';'
					{
					match(input,SEMI,FOLLOW_SEMI_in_typeDeclaration480); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 5, typeDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "typeDeclaration"



	// $ANTLR start "classOrInterfaceDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:378:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );
	public final void classOrInterfaceDeclaration() throws RecognitionException {
		int classOrInterfaceDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:379:5: ( classDeclaration | interfaceDeclaration )
			int alt12=2;
			switch ( input.LA(1) ) {
			case MONKEYS_AT:
				{
				int LA12_1 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case PUBLIC:
				{
				int LA12_2 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case PROTECTED:
				{
				int LA12_3 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case PRIVATE:
				{
				int LA12_4 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case STATIC:
				{
				int LA12_5 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case ABSTRACT:
				{
				int LA12_6 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case FINAL:
				{
				int LA12_7 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case NATIVE:
				{
				int LA12_8 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case SYNCHRONIZED:
				{
				int LA12_9 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case TRANSIENT:
				{
				int LA12_10 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case VOLATILE:
				{
				int LA12_11 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case STRICTFP:
				{
				int LA12_12 = input.LA(2);
				if ( (synpred12_Java()) ) {
					alt12=1;
				}
				else if ( (true) ) {
					alt12=2;
				}

				}
				break;
			case CLASS:
			case ENUM:
				{
				alt12=1;
				}
				break;
			case INTERFACE:
				{
				alt12=2;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}
			switch (alt12) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:379:10: classDeclaration
					{
					pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration501);
					classDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:380:9: interfaceDeclaration
					{
					pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration511);
					interfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 6, classOrInterfaceDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "classOrInterfaceDeclaration"


	public static class modifiers_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "modifiers"
	// /Users/byung/workspace/antlr2/Java.g:384:1: modifiers : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )* ;
	public final JavaParser.modifiers_return modifiers() throws RecognitionException {
		JavaParser.modifiers_return retval = new JavaParser.modifiers_return();
		retval.start = input.LT(1);
		int modifiers_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }

			// /Users/byung/workspace/antlr2/Java.g:385:5: ( ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )* )
			// /Users/byung/workspace/antlr2/Java.g:386:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )*
			{
			// /Users/byung/workspace/antlr2/Java.g:386:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )*
			loop13:
			while (true) {
				int alt13=13;
				switch ( input.LA(1) ) {
				case MONKEYS_AT:
					{
					int LA13_2 = input.LA(2);
					if ( (LA13_2==IDENTIFIER) ) {
						alt13=1;
					}

					}
					break;
				case PUBLIC:
					{
					alt13=2;
					}
					break;
				case PROTECTED:
					{
					alt13=3;
					}
					break;
				case PRIVATE:
					{
					alt13=4;
					}
					break;
				case STATIC:
					{
					alt13=5;
					}
					break;
				case ABSTRACT:
					{
					alt13=6;
					}
					break;
				case FINAL:
					{
					alt13=7;
					}
					break;
				case NATIVE:
					{
					alt13=8;
					}
					break;
				case SYNCHRONIZED:
					{
					alt13=9;
					}
					break;
				case TRANSIENT:
					{
					alt13=10;
					}
					break;
				case VOLATILE:
					{
					alt13=11;
					}
					break;
				case STRICTFP:
					{
					alt13=12;
					}
					break;
				}
				switch (alt13) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:386:10: annotation
					{
					pushFollow(FOLLOW_annotation_in_modifiers546);
					annotation();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:387:9: 'public'
					{
					match(input,PUBLIC,FOLLOW_PUBLIC_in_modifiers556); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("public");}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:388:9: 'protected'
					{
					match(input,PROTECTED,FOLLOW_PROTECTED_in_modifiers568); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("protected");}
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:389:9: 'private'
					{
					match(input,PRIVATE,FOLLOW_PRIVATE_in_modifiers580); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("private");}
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:390:9: 'static'
					{
					match(input,STATIC,FOLLOW_STATIC_in_modifiers591); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("static");}
					if ( state.backtracking==0 ) {System.out.println("Hello from static");}
					}
					break;
				case 6 :
					// /Users/byung/workspace/antlr2/Java.g:391:9: 'abstract'
					{
					match(input,ABSTRACT,FOLLOW_ABSTRACT_in_modifiers605); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("abstract");}
					}
					break;
				case 7 :
					// /Users/byung/workspace/antlr2/Java.g:392:9: 'final'
					{
					match(input,FINAL,FOLLOW_FINAL_in_modifiers616); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("final");}
					}
					break;
				case 8 :
					// /Users/byung/workspace/antlr2/Java.g:393:9: 'native'
					{
					match(input,NATIVE,FOLLOW_NATIVE_in_modifiers627); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("native");}
					}
					break;
				case 9 :
					// /Users/byung/workspace/antlr2/Java.g:394:9: 'synchronized'
					{
					match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_modifiers638); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("synchronized");}
					}
					break;
				case 10 :
					// /Users/byung/workspace/antlr2/Java.g:395:9: 'transient'
					{
					match(input,TRANSIENT,FOLLOW_TRANSIENT_in_modifiers649); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("transient");}
					}
					break;
				case 11 :
					// /Users/byung/workspace/antlr2/Java.g:396:9: 'volatile'
					{
					match(input,VOLATILE,FOLLOW_VOLATILE_in_modifiers660); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("volatile");}
					}
					break;
				case 12 :
					// /Users/byung/workspace/antlr2/Java.g:397:9: 'strictfp'
					{
					match(input,STRICTFP,FOLLOW_STRICTFP_in_modifiers671); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("strictfp");}
					}
					break;

				default :
					break loop13;
				}
			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 7, modifiers_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "modifiers"



	// $ANTLR start "variableModifiers"
	// /Users/byung/workspace/antlr2/Java.g:402:1: variableModifiers : ( 'final' | annotation )* ;
	public final void variableModifiers() throws RecognitionException {
		int variableModifiers_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:403:5: ( ( 'final' | annotation )* )
			// /Users/byung/workspace/antlr2/Java.g:403:9: ( 'final' | annotation )*
			{
			// /Users/byung/workspace/antlr2/Java.g:403:9: ( 'final' | annotation )*
			loop14:
			while (true) {
				int alt14=3;
				int LA14_0 = input.LA(1);
				if ( (LA14_0==FINAL) ) {
					alt14=1;
				}
				else if ( (LA14_0==MONKEYS_AT) ) {
					alt14=2;
				}

				switch (alt14) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:403:13: 'final'
					{
					match(input,FINAL,FOLLOW_FINAL_in_variableModifiers704); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("final");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:404:13: annotation
					{
					pushFollow(FOLLOW_annotation_in_variableModifiers719);
					annotation();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop14;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 8, variableModifiers_StartIndex); }

		}
	}
	// $ANTLR end "variableModifiers"



	// $ANTLR start "classDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:409:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
	public final void classDeclaration() throws RecognitionException {
		int classDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:410:5: ( normalClassDeclaration | enumDeclaration )
			int alt15=2;
			switch ( input.LA(1) ) {
			case MONKEYS_AT:
				{
				int LA15_1 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case PUBLIC:
				{
				int LA15_2 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case PROTECTED:
				{
				int LA15_3 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case PRIVATE:
				{
				int LA15_4 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case STATIC:
				{
				int LA15_5 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case ABSTRACT:
				{
				int LA15_6 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case FINAL:
				{
				int LA15_7 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case NATIVE:
				{
				int LA15_8 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case SYNCHRONIZED:
				{
				int LA15_9 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case TRANSIENT:
				{
				int LA15_10 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case VOLATILE:
				{
				int LA15_11 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case STRICTFP:
				{
				int LA15_12 = input.LA(2);
				if ( (synpred27_Java()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

				}
				break;
			case CLASS:
				{
				alt15=1;
				}
				break;
			case ENUM:
				{
				alt15=2;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}
			switch (alt15) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:410:9: normalClassDeclaration
					{
					pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration755);
					normalClassDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:411:9: enumDeclaration
					{
					pushFollow(FOLLOW_enumDeclaration_in_classDeclaration765);
					enumDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 9, classDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "classDeclaration"



	// $ANTLR start "normalClassDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:414:1: normalClassDeclaration : modifiers 'class' IDENTIFIER ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody ;
	public final void normalClassDeclaration() throws RecognitionException {
		int normalClassDeclaration_StartIndex = input.index();

		Token IDENTIFIER2=null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:415:5: ( modifiers 'class' IDENTIFIER ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody )
			// /Users/byung/workspace/antlr2/Java.g:415:9: modifiers 'class' IDENTIFIER ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody
			{
			pushFollow(FOLLOW_modifiers_in_normalClassDeclaration785);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			match(input,CLASS,FOLLOW_CLASS_in_normalClassDeclaration788); if (state.failed) return;
			IDENTIFIER2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalClassDeclaration790); if (state.failed) return;
			if ( state.backtracking==0 ) { kAdd("class"); ci.className = (IDENTIFIER2!=null?IDENTIFIER2.getText():null); classStack.add(ci.className); }
			// /Users/byung/workspace/antlr2/Java.g:416:9: ( typeParameters )?
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==LT) ) {
				alt16=1;
			}
			switch (alt16) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:416:10: typeParameters
					{
					pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration803);
					typeParameters();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:418:9: ( 'extends' type )?
			int alt17=2;
			int LA17_0 = input.LA(1);
			if ( (LA17_0==EXTENDS) ) {
				alt17=1;
			}
			switch (alt17) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:418:10: 'extends' type
					{
					match(input,EXTENDS,FOLLOW_EXTENDS_in_normalClassDeclaration825); if (state.failed) return;
					pushFollow(FOLLOW_type_in_normalClassDeclaration827);
					type();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("extends"); }
					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:420:9: ( 'implements' typeList )?
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==IMPLEMENTS) ) {
				alt18=1;
			}
			switch (alt18) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:420:10: 'implements' typeList
					{
					match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_normalClassDeclaration851); if (state.failed) return;
					pushFollow(FOLLOW_typeList_in_normalClassDeclaration853);
					typeList();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("implements"); }
					}
					break;

			}

			pushFollow(FOLLOW_classBody_in_normalClassDeclaration888);
			classBody();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {classStack.pop();}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 10, normalClassDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "normalClassDeclaration"



	// $ANTLR start "typeParameters"
	// /Users/byung/workspace/antlr2/Java.g:427:1: typeParameters : '<' typeParameter ( ',' typeParameter )* '>' ;
	public final void typeParameters() throws RecognitionException {
		int typeParameters_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:428:5: ( '<' typeParameter ( ',' typeParameter )* '>' )
			// /Users/byung/workspace/antlr2/Java.g:428:9: '<' typeParameter ( ',' typeParameter )* '>'
			{
			match(input,LT,FOLLOW_LT_in_typeParameters919); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("<");}
			pushFollow(FOLLOW_typeParameter_in_typeParameters935);
			typeParameter();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:430:13: ( ',' typeParameter )*
			loop19:
			while (true) {
				int alt19=2;
				int LA19_0 = input.LA(1);
				if ( (LA19_0==COMMA) ) {
					alt19=1;
				}

				switch (alt19) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:430:14: ',' typeParameter
					{
					match(input,COMMA,FOLLOW_COMMA_in_typeParameters950); if (state.failed) return;
					pushFollow(FOLLOW_typeParameter_in_typeParameters952);
					typeParameter();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(",");}
					}
					break;

				default :
					break loop19;
				}
			}

			match(input,GT,FOLLOW_GT_in_typeParameters979); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd(">");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 11, typeParameters_StartIndex); }

		}
	}
	// $ANTLR end "typeParameters"



	// $ANTLR start "typeParameter"
	// /Users/byung/workspace/antlr2/Java.g:435:1: typeParameter : IDENTIFIER ( 'extends' typeBound )? ;
	public final void typeParameter() throws RecognitionException {
		int typeParameter_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:436:5: ( IDENTIFIER ( 'extends' typeBound )? )
			// /Users/byung/workspace/antlr2/Java.g:436:9: IDENTIFIER ( 'extends' typeBound )?
			{
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_typeParameter1001); if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:437:9: ( 'extends' typeBound )?
			int alt20=2;
			int LA20_0 = input.LA(1);
			if ( (LA20_0==EXTENDS) ) {
				alt20=1;
			}
			switch (alt20) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:437:10: 'extends' typeBound
					{
					match(input,EXTENDS,FOLLOW_EXTENDS_in_typeParameter1013); if (state.failed) return;
					pushFollow(FOLLOW_typeBound_in_typeParameter1015);
					typeBound();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("extends"); }
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 12, typeParameter_StartIndex); }

		}
	}
	// $ANTLR end "typeParameter"



	// $ANTLR start "typeBound"
	// /Users/byung/workspace/antlr2/Java.g:442:1: typeBound : type ( '&' type )* ;
	public final void typeBound() throws RecognitionException {
		int typeBound_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:443:5: ( type ( '&' type )* )
			// /Users/byung/workspace/antlr2/Java.g:443:9: type ( '&' type )*
			{
			pushFollow(FOLLOW_type_in_typeBound1050);
			type();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:444:9: ( '&' type )*
			loop21:
			while (true) {
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( (LA21_0==AMP) ) {
					alt21=1;
				}

				switch (alt21) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:444:10: '&' type
					{
					match(input,AMP,FOLLOW_AMP_in_typeBound1061); if (state.failed) return;
					pushFollow(FOLLOW_type_in_typeBound1063);
					type();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { sAdd("&"); }
					}
					break;

				default :
					break loop21;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 13, typeBound_StartIndex); }

		}
	}
	// $ANTLR end "typeBound"



	// $ANTLR start "enumDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:449:1: enumDeclaration : modifiers ( 'enum' ) IDENTIFIER ( 'implements' typeList )? enumBody ;
	public final void enumDeclaration() throws RecognitionException {
		int enumDeclaration_StartIndex = input.index();

		Token IDENTIFIER3=null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:450:5: ( modifiers ( 'enum' ) IDENTIFIER ( 'implements' typeList )? enumBody )
			// /Users/byung/workspace/antlr2/Java.g:450:9: modifiers ( 'enum' ) IDENTIFIER ( 'implements' typeList )? enumBody
			{
			pushFollow(FOLLOW_modifiers_in_enumDeclaration1098);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:451:9: ( 'enum' )
			// /Users/byung/workspace/antlr2/Java.g:451:10: 'enum'
			{
			match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration1110); if (state.failed) return;
			if ( state.backtracking==0 ) { kAdd("enum"); }
			}

			IDENTIFIER3=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumDeclaration1133); if (state.failed) return;
			if ( state.backtracking==0 ) {ci.className = (IDENTIFIER3!=null?IDENTIFIER3.getText():null);classStack.add((IDENTIFIER3!=null?IDENTIFIER3.getText():null));}
			// /Users/byung/workspace/antlr2/Java.g:454:9: ( 'implements' typeList )?
			int alt22=2;
			int LA22_0 = input.LA(1);
			if ( (LA22_0==IMPLEMENTS) ) {
				alt22=1;
			}
			switch (alt22) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:454:10: 'implements' typeList
					{
					match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_enumDeclaration1146); if (state.failed) return;
					pushFollow(FOLLOW_typeList_in_enumDeclaration1148);
					typeList();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("implements"); }
					}
					break;

			}

			pushFollow(FOLLOW_enumBody_in_enumDeclaration1171);
			enumBody();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {
			        	classStack.pop();
			        }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 14, enumDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "enumDeclaration"



	// $ANTLR start "enumBody"
	// /Users/byung/workspace/antlr2/Java.g:463:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
	public final void enumBody() throws RecognitionException {
		int enumBody_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:464:5: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
			// /Users/byung/workspace/antlr2/Java.g:464:9: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
			{
			match(input,LBRACE,FOLLOW_LBRACE_in_enumBody1206); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("{"); }
			// /Users/byung/workspace/antlr2/Java.g:465:9: ( enumConstants )?
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==IDENTIFIER||LA23_0==MONKEYS_AT) ) {
				alt23=1;
			}
			switch (alt23) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:465:10: enumConstants
					{
					pushFollow(FOLLOW_enumConstants_in_enumBody1219);
					enumConstants();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:467:9: ( ',' )?
			int alt24=2;
			int LA24_0 = input.LA(1);
			if ( (LA24_0==COMMA) ) {
				alt24=1;
			}
			switch (alt24) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:467:9: ','
					{
					match(input,COMMA,FOLLOW_COMMA_in_enumBody1241); if (state.failed) return;
					}
					break;

			}

			if ( state.backtracking==0 ) {sAdd(",");}
			// /Users/byung/workspace/antlr2/Java.g:468:9: ( enumBodyDeclarations )?
			int alt25=2;
			int LA25_0 = input.LA(1);
			if ( (LA25_0==SEMI) ) {
				alt25=1;
			}
			switch (alt25) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:468:10: enumBodyDeclarations
					{
					pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody1255);
					enumBodyDeclarations();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,RBRACE,FOLLOW_RBRACE_in_enumBody1277); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("}");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 15, enumBody_StartIndex); }

		}
	}
	// $ANTLR end "enumBody"



	// $ANTLR start "enumConstants"
	// /Users/byung/workspace/antlr2/Java.g:473:1: enumConstants : enumConstant ( ',' enumConstant )* ;
	public final void enumConstants() throws RecognitionException {
		int enumConstants_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:474:5: ( enumConstant ( ',' enumConstant )* )
			// /Users/byung/workspace/antlr2/Java.g:474:9: enumConstant ( ',' enumConstant )*
			{
			pushFollow(FOLLOW_enumConstant_in_enumConstants1298);
			enumConstant();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:475:9: ( ',' enumConstant )*
			loop26:
			while (true) {
				int alt26=2;
				int LA26_0 = input.LA(1);
				if ( (LA26_0==COMMA) ) {
					int LA26_1 = input.LA(2);
					if ( (LA26_1==IDENTIFIER||LA26_1==MONKEYS_AT) ) {
						alt26=1;
					}

				}

				switch (alt26) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:475:10: ',' enumConstant
					{
					match(input,COMMA,FOLLOW_COMMA_in_enumConstants1309); if (state.failed) return;
					pushFollow(FOLLOW_enumConstant_in_enumConstants1311);
					enumConstant();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { sAdd(","); }
					}
					break;

				default :
					break loop26;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 16, enumConstants_StartIndex); }

		}
	}
	// $ANTLR end "enumConstants"



	// $ANTLR start "enumConstant"
	// /Users/byung/workspace/antlr2/Java.g:483:1: enumConstant : ( annotations )? IDENTIFIER ( arguments )? ( classBody )? ;
	public final void enumConstant() throws RecognitionException {
		int enumConstant_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:484:5: ( ( annotations )? IDENTIFIER ( arguments )? ( classBody )? )
			// /Users/byung/workspace/antlr2/Java.g:484:9: ( annotations )? IDENTIFIER ( arguments )? ( classBody )?
			{
			// /Users/byung/workspace/antlr2/Java.g:484:9: ( annotations )?
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==MONKEYS_AT) ) {
				alt27=1;
			}
			switch (alt27) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:484:10: annotations
					{
					pushFollow(FOLLOW_annotations_in_enumConstant1347);
					annotations();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumConstant1368); if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:487:9: ( arguments )?
			int alt28=2;
			int LA28_0 = input.LA(1);
			if ( (LA28_0==LPAREN) ) {
				alt28=1;
			}
			switch (alt28) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:487:10: arguments
					{
					pushFollow(FOLLOW_arguments_in_enumConstant1379);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:489:9: ( classBody )?
			int alt29=2;
			int LA29_0 = input.LA(1);
			if ( (LA29_0==LBRACE) ) {
				alt29=1;
			}
			switch (alt29) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:489:10: classBody
					{
					pushFollow(FOLLOW_classBody_in_enumConstant1401);
					classBody();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 17, enumConstant_StartIndex); }

		}
	}
	// $ANTLR end "enumConstant"



	// $ANTLR start "enumBodyDeclarations"
	// /Users/byung/workspace/antlr2/Java.g:495:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
	public final void enumBodyDeclarations() throws RecognitionException {
		int enumBodyDeclarations_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:496:5: ( ';' ( classBodyDeclaration )* )
			// /Users/byung/workspace/antlr2/Java.g:496:9: ';' ( classBodyDeclaration )*
			{
			match(input,SEMI,FOLLOW_SEMI_in_enumBodyDeclarations1442); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd(";"); }
			// /Users/byung/workspace/antlr2/Java.g:497:9: ( classBodyDeclaration )*
			loop30:
			while (true) {
				int alt30=2;
				int LA30_0 = input.LA(1);
				if ( (LA30_0==ABSTRACT||LA30_0==BOOLEAN||LA30_0==BYTE||LA30_0==CHAR||LA30_0==CLASS||LA30_0==DOUBLE||LA30_0==ENUM||LA30_0==FINAL||LA30_0==FLOAT||LA30_0==IDENTIFIER||(LA30_0 >= INT && LA30_0 <= INTERFACE)||LA30_0==LBRACE||LA30_0==LONG||LA30_0==LT||(LA30_0 >= MONKEYS_AT && LA30_0 <= NATIVE)||(LA30_0 >= PRIVATE && LA30_0 <= PUBLIC)||(LA30_0 >= SEMI && LA30_0 <= SHORT)||(LA30_0 >= STATIC && LA30_0 <= STRICTFP)||LA30_0==SYNCHRONIZED||LA30_0==TRANSIENT||(LA30_0 >= VOID && LA30_0 <= VOLATILE)) ) {
					alt30=1;
				}

				switch (alt30) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:497:10: classBodyDeclaration
					{
					pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1455);
					classBodyDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop30;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 18, enumBodyDeclarations_StartIndex); }

		}
	}
	// $ANTLR end "enumBodyDeclarations"



	// $ANTLR start "interfaceDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:501:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
	public final void interfaceDeclaration() throws RecognitionException {
		int interfaceDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:502:5: ( normalInterfaceDeclaration | annotationTypeDeclaration )
			int alt31=2;
			switch ( input.LA(1) ) {
			case MONKEYS_AT:
				{
				int LA31_1 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case PUBLIC:
				{
				int LA31_2 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case PROTECTED:
				{
				int LA31_3 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case PRIVATE:
				{
				int LA31_4 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case STATIC:
				{
				int LA31_5 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case ABSTRACT:
				{
				int LA31_6 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case FINAL:
				{
				int LA31_7 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case NATIVE:
				{
				int LA31_8 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case SYNCHRONIZED:
				{
				int LA31_9 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case TRANSIENT:
				{
				int LA31_10 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case VOLATILE:
				{
				int LA31_11 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case STRICTFP:
				{
				int LA31_12 = input.LA(2);
				if ( (synpred43_Java()) ) {
					alt31=1;
				}
				else if ( (true) ) {
					alt31=2;
				}

				}
				break;
			case INTERFACE:
				{
				alt31=1;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 31, 0, input);
				throw nvae;
			}
			switch (alt31) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:502:9: normalInterfaceDeclaration
					{
					pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1486);
					normalInterfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:503:9: annotationTypeDeclaration
					{
					pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1496);
					annotationTypeDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 19, interfaceDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "interfaceDeclaration"



	// $ANTLR start "normalInterfaceDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:506:1: normalInterfaceDeclaration : modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
	public final void normalInterfaceDeclaration() throws RecognitionException {
		int normalInterfaceDeclaration_StartIndex = input.index();

		Token IDENTIFIER4=null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:507:5: ( modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody )
			// /Users/byung/workspace/antlr2/Java.g:507:9: modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody
			{
			pushFollow(FOLLOW_modifiers_in_normalInterfaceDeclaration1520);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			match(input,INTERFACE,FOLLOW_INTERFACE_in_normalInterfaceDeclaration1522); if (state.failed) return;
			IDENTIFIER4=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalInterfaceDeclaration1524); if (state.failed) return;
			if ( state.backtracking==0 ) { kAdd("interface"); ci.className = (IDENTIFIER4!=null?IDENTIFIER4.getText():null); classStack.push((IDENTIFIER4!=null?IDENTIFIER4.getText():null));}
			// /Users/byung/workspace/antlr2/Java.g:508:9: ( typeParameters )?
			int alt32=2;
			int LA32_0 = input.LA(1);
			if ( (LA32_0==LT) ) {
				alt32=1;
			}
			switch (alt32) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:508:10: typeParameters
					{
					pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration1538);
					typeParameters();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:510:9: ( 'extends' typeList )?
			int alt33=2;
			int LA33_0 = input.LA(1);
			if ( (LA33_0==EXTENDS) ) {
				alt33=1;
			}
			switch (alt33) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:510:10: 'extends' typeList
					{
					match(input,EXTENDS,FOLLOW_EXTENDS_in_normalInterfaceDeclaration1560); if (state.failed) return;
					pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration1562);
					typeList();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("extends"); }
					}
					break;

			}

			pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration1585);
			interfaceBody();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {
			        	classStack.pop();
			        }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 20, normalInterfaceDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "normalInterfaceDeclaration"


	public static class typeList_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "typeList"
	// /Users/byung/workspace/antlr2/Java.g:518:1: typeList : type ( ',' type )* ;
	public final JavaParser.typeList_return typeList() throws RecognitionException {
		JavaParser.typeList_return retval = new JavaParser.typeList_return();
		retval.start = input.LT(1);
		int typeList_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }

			// /Users/byung/workspace/antlr2/Java.g:519:5: ( type ( ',' type )* )
			// /Users/byung/workspace/antlr2/Java.g:519:9: type ( ',' type )*
			{
			pushFollow(FOLLOW_type_in_typeList1615);
			type();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) {System.out.println(input.toString(retval.start,input.LT(-1)));}
			// /Users/byung/workspace/antlr2/Java.g:520:9: ( ',' type )*
			loop34:
			while (true) {
				int alt34=2;
				int LA34_0 = input.LA(1);
				if ( (LA34_0==COMMA) ) {
					alt34=1;
				}

				switch (alt34) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:520:10: ',' type
					{
					match(input,COMMA,FOLLOW_COMMA_in_typeList1628); if (state.failed) return retval;
					pushFollow(FOLLOW_type_in_typeList1630);
					type();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) { sAdd(","); }
					}
					break;

				default :
					break loop34;
				}
			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 21, typeList_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "typeList"



	// $ANTLR start "classBody"
	// /Users/byung/workspace/antlr2/Java.g:524:1: classBody : '{' ( classBodyDeclaration )* '}' ;
	public final void classBody() throws RecognitionException {
		int classBody_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:525:5: ( '{' ( classBodyDeclaration )* '}' )
			// /Users/byung/workspace/antlr2/Java.g:525:9: '{' ( classBodyDeclaration )* '}'
			{
			match(input,LBRACE,FOLLOW_LBRACE_in_classBody1665); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("{"); }
			// /Users/byung/workspace/antlr2/Java.g:526:9: ( classBodyDeclaration )*
			loop35:
			while (true) {
				int alt35=2;
				int LA35_0 = input.LA(1);
				if ( (LA35_0==ABSTRACT||LA35_0==BOOLEAN||LA35_0==BYTE||LA35_0==CHAR||LA35_0==CLASS||LA35_0==DOUBLE||LA35_0==ENUM||LA35_0==FINAL||LA35_0==FLOAT||LA35_0==IDENTIFIER||(LA35_0 >= INT && LA35_0 <= INTERFACE)||LA35_0==LBRACE||LA35_0==LONG||LA35_0==LT||(LA35_0 >= MONKEYS_AT && LA35_0 <= NATIVE)||(LA35_0 >= PRIVATE && LA35_0 <= PUBLIC)||(LA35_0 >= SEMI && LA35_0 <= SHORT)||(LA35_0 >= STATIC && LA35_0 <= STRICTFP)||LA35_0==SYNCHRONIZED||LA35_0==TRANSIENT||(LA35_0 >= VOID && LA35_0 <= VOLATILE)) ) {
					alt35=1;
				}

				switch (alt35) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:526:10: classBodyDeclaration
					{
					pushFollow(FOLLOW_classBodyDeclaration_in_classBody1678);
					classBodyDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop35;
				}
			}

			match(input,RBRACE,FOLLOW_RBRACE_in_classBody1700); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("}"); }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 22, classBody_StartIndex); }

		}
	}
	// $ANTLR end "classBody"



	// $ANTLR start "interfaceBody"
	// /Users/byung/workspace/antlr2/Java.g:531:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
	public final void interfaceBody() throws RecognitionException {
		int interfaceBody_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:532:5: ( '{' ( interfaceBodyDeclaration )* '}' )
			// /Users/byung/workspace/antlr2/Java.g:532:9: '{' ( interfaceBodyDeclaration )* '}'
			{
			match(input,LBRACE,FOLLOW_LBRACE_in_interfaceBody1721); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("{"); }
			// /Users/byung/workspace/antlr2/Java.g:533:9: ( interfaceBodyDeclaration )*
			loop36:
			while (true) {
				int alt36=2;
				int LA36_0 = input.LA(1);
				if ( (LA36_0==ABSTRACT||LA36_0==BOOLEAN||LA36_0==BYTE||LA36_0==CHAR||LA36_0==CLASS||LA36_0==DOUBLE||LA36_0==ENUM||LA36_0==FINAL||LA36_0==FLOAT||LA36_0==IDENTIFIER||(LA36_0 >= INT && LA36_0 <= INTERFACE)||LA36_0==LONG||LA36_0==LT||(LA36_0 >= MONKEYS_AT && LA36_0 <= NATIVE)||(LA36_0 >= PRIVATE && LA36_0 <= PUBLIC)||(LA36_0 >= SEMI && LA36_0 <= SHORT)||(LA36_0 >= STATIC && LA36_0 <= STRICTFP)||LA36_0==SYNCHRONIZED||LA36_0==TRANSIENT||(LA36_0 >= VOID && LA36_0 <= VOLATILE)) ) {
					alt36=1;
				}

				switch (alt36) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:533:10: interfaceBodyDeclaration
					{
					pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody1734);
					interfaceBodyDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop36;
				}
			}

			match(input,RBRACE,FOLLOW_RBRACE_in_interfaceBody1756); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("}"); }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 23, interfaceBody_StartIndex); }

		}
	}
	// $ANTLR end "interfaceBody"



	// $ANTLR start "classBodyDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:538:1: classBodyDeclaration : ( ';' | ( 'static' )? block | memberDecl );
	public final void classBodyDeclaration() throws RecognitionException {
		int classBodyDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:539:5: ( ';' | ( 'static' )? block | memberDecl )
			int alt38=3;
			switch ( input.LA(1) ) {
			case SEMI:
				{
				alt38=1;
				}
				break;
			case STATIC:
				{
				int LA38_2 = input.LA(2);
				if ( (LA38_2==LBRACE) ) {
					alt38=2;
				}
				else if ( (LA38_2==ABSTRACT||LA38_2==BOOLEAN||LA38_2==BYTE||LA38_2==CHAR||LA38_2==CLASS||LA38_2==DOUBLE||LA38_2==ENUM||LA38_2==FINAL||LA38_2==FLOAT||LA38_2==IDENTIFIER||(LA38_2 >= INT && LA38_2 <= INTERFACE)||LA38_2==LONG||LA38_2==LT||(LA38_2 >= MONKEYS_AT && LA38_2 <= NATIVE)||(LA38_2 >= PRIVATE && LA38_2 <= PUBLIC)||LA38_2==SHORT||(LA38_2 >= STATIC && LA38_2 <= STRICTFP)||LA38_2==SYNCHRONIZED||LA38_2==TRANSIENT||(LA38_2 >= VOID && LA38_2 <= VOLATILE)) ) {
					alt38=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 38, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LBRACE:
				{
				alt38=2;
				}
				break;
			case ABSTRACT:
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case CLASS:
			case DOUBLE:
			case ENUM:
			case FINAL:
			case FLOAT:
			case IDENTIFIER:
			case INT:
			case INTERFACE:
			case LONG:
			case LT:
			case MONKEYS_AT:
			case NATIVE:
			case PRIVATE:
			case PROTECTED:
			case PUBLIC:
			case SHORT:
			case STRICTFP:
			case SYNCHRONIZED:
			case TRANSIENT:
			case VOID:
			case VOLATILE:
				{
				alt38=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 38, 0, input);
				throw nvae;
			}
			switch (alt38) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:539:9: ';'
					{
					match(input,SEMI,FOLLOW_SEMI_in_classBodyDeclaration1777); if (state.failed) return;
					if ( state.backtracking==0 ) { sAdd(";"); }
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:540:9: ( 'static' )? block
					{
					// /Users/byung/workspace/antlr2/Java.g:540:9: ( 'static' )?
					int alt37=2;
					int LA37_0 = input.LA(1);
					if ( (LA37_0==STATIC) ) {
						alt37=1;
					}
					switch (alt37) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:540:10: 'static'
							{
							match(input,STATIC,FOLLOW_STATIC_in_classBodyDeclaration1790); if (state.failed) return;
							if ( state.backtracking==0 ) { kAdd("static"); }
							}
							break;

					}

					pushFollow(FOLLOW_block_in_classBodyDeclaration1813);
					block();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:543:9: memberDecl
					{
					pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration1823);
					memberDecl();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 24, classBodyDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "classBodyDeclaration"



	// $ANTLR start "memberDecl"
	// /Users/byung/workspace/antlr2/Java.g:546:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );
	public final void memberDecl() throws RecognitionException {
		int memberDecl_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:547:5: ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration )
			int alt39=4;
			switch ( input.LA(1) ) {
			case MONKEYS_AT:
				{
				int LA39_1 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case PUBLIC:
				{
				int LA39_2 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case PROTECTED:
				{
				int LA39_3 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case PRIVATE:
				{
				int LA39_4 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case STATIC:
				{
				int LA39_5 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case ABSTRACT:
				{
				int LA39_6 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case FINAL:
				{
				int LA39_7 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case NATIVE:
				{
				int LA39_8 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case SYNCHRONIZED:
				{
				int LA39_9 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case TRANSIENT:
				{
				int LA39_10 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case VOLATILE:
				{
				int LA39_11 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case STRICTFP:
				{
				int LA39_12 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}
				else if ( (synpred54_Java()) ) {
					alt39=3;
				}
				else if ( (true) ) {
					alt39=4;
				}

				}
				break;
			case IDENTIFIER:
				{
				int LA39_13 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 13, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case BOOLEAN:
				{
				int LA39_14 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 14, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case CHAR:
				{
				int LA39_15 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 15, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case BYTE:
				{
				int LA39_16 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 16, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SHORT:
				{
				int LA39_17 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 17, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case INT:
				{
				int LA39_18 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 18, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LONG:
				{
				int LA39_19 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 19, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FLOAT:
				{
				int LA39_20 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 20, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case DOUBLE:
				{
				int LA39_21 = input.LA(2);
				if ( (synpred52_Java()) ) {
					alt39=1;
				}
				else if ( (synpred53_Java()) ) {
					alt39=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 21, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LT:
			case VOID:
				{
				alt39=2;
				}
				break;
			case CLASS:
			case ENUM:
				{
				alt39=3;
				}
				break;
			case INTERFACE:
				{
				alt39=4;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 39, 0, input);
				throw nvae;
			}
			switch (alt39) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:547:10: fieldDeclaration
					{
					pushFollow(FOLLOW_fieldDeclaration_in_memberDecl1844);
					fieldDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:548:10: methodDeclaration
					{
					pushFollow(FOLLOW_methodDeclaration_in_memberDecl1855);
					methodDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:549:10: classDeclaration
					{
					pushFollow(FOLLOW_classDeclaration_in_memberDecl1866);
					classDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:550:10: interfaceDeclaration
					{
					pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl1877);
					interfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 25, memberDecl_StartIndex); }

		}
	}
	// $ANTLR end "memberDecl"


	public static class methodDeclaration_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "methodDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:554:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );
	public final JavaParser.methodDeclaration_return methodDeclaration() throws RecognitionException {
		JavaParser.methodDeclaration_return retval = new JavaParser.methodDeclaration_return();
		retval.start = input.LT(1);
		int methodDeclaration_StartIndex = input.index();

		Token IDENTIFIER8=null;
		ParserRuleReturnScope modifiers5 =null;
		ParserRuleReturnScope modifiers6 =null;
		ParserRuleReturnScope type7 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }

			// /Users/byung/workspace/antlr2/Java.g:555:5: ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) )
			int alt49=2;
			switch ( input.LA(1) ) {
			case MONKEYS_AT:
				{
				int LA49_1 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case PUBLIC:
				{
				int LA49_2 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case PROTECTED:
				{
				int LA49_3 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case PRIVATE:
				{
				int LA49_4 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case STATIC:
				{
				int LA49_5 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case ABSTRACT:
				{
				int LA49_6 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case FINAL:
				{
				int LA49_7 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case NATIVE:
				{
				int LA49_8 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case SYNCHRONIZED:
				{
				int LA49_9 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case TRANSIENT:
				{
				int LA49_10 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case VOLATILE:
				{
				int LA49_11 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case STRICTFP:
				{
				int LA49_12 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case LT:
				{
				int LA49_13 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case IDENTIFIER:
				{
				int LA49_14 = input.LA(2);
				if ( (synpred59_Java()) ) {
					alt49=1;
				}
				else if ( (true) ) {
					alt49=2;
				}

				}
				break;
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case SHORT:
			case VOID:
				{
				alt49=2;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 49, 0, input);
				throw nvae;
			}
			switch (alt49) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:558:10: modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
					{
					pushFollow(FOLLOW_modifiers_in_methodDeclaration1916);
					modifiers5=modifiers();
					state._fsp--;
					if (state.failed) return retval;
					// /Users/byung/workspace/antlr2/Java.g:559:9: ( typeParameters )?
					int alt40=2;
					int LA40_0 = input.LA(1);
					if ( (LA40_0==LT) ) {
						alt40=1;
					}
					switch (alt40) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:559:10: typeParameters
							{
							pushFollow(FOLLOW_typeParameters_in_methodDeclaration1927);
							typeParameters();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodDeclaration1948); if (state.failed) return retval;
					if ( state.backtracking==0 ) { methodStack.push(input.toString(retval.start,input.LT(-1)));}
					if ( state.backtracking==0 ) {System.out.println("Method: " + input.toString(retval.start,input.LT(-1)) + " modi: " + (modifiers5!=null?input.toString(modifiers5.start,modifiers5.stop):null));  }
					pushFollow(FOLLOW_formalParameters_in_methodDeclaration1962);
					formalParameters();
					state._fsp--;
					if (state.failed) return retval;
					// /Users/byung/workspace/antlr2/Java.g:563:9: ( 'throws' qualifiedNameList )?
					int alt41=2;
					int LA41_0 = input.LA(1);
					if ( (LA41_0==THROWS) ) {
						alt41=1;
					}
					switch (alt41) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:563:10: 'throws' qualifiedNameList
							{
							match(input,THROWS,FOLLOW_THROWS_in_methodDeclaration1973); if (state.failed) return retval;
							pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaration1975);
							qualifiedNameList();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) { kAdd("throws"); }
							}
							break;

					}

					match(input,LBRACE,FOLLOW_LBRACE_in_methodDeclaration1998); if (state.failed) return retval;
					if ( state.backtracking==0 ) { sAdd("{"); }
					// /Users/byung/workspace/antlr2/Java.g:566:9: ( explicitConstructorInvocation )?
					int alt42=2;
					alt42 = dfa42.predict(input);
					switch (alt42) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:566:10: explicitConstructorInvocation
							{
							pushFollow(FOLLOW_explicitConstructorInvocation_in_methodDeclaration2011);
							explicitConstructorInvocation();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					// /Users/byung/workspace/antlr2/Java.g:568:9: ( blockStatement )*
					loop43:
					while (true) {
						int alt43=2;
						int LA43_0 = input.LA(1);
						if ( (LA43_0==ABSTRACT||(LA43_0 >= ASSERT && LA43_0 <= BANG)||(LA43_0 >= BOOLEAN && LA43_0 <= BYTE)||(LA43_0 >= CHAR && LA43_0 <= CLASS)||LA43_0==CONTINUE||LA43_0==DO||(LA43_0 >= DOUBLE && LA43_0 <= DOUBLELITERAL)||LA43_0==ENUM||(LA43_0 >= FALSE && LA43_0 <= FINAL)||(LA43_0 >= FLOAT && LA43_0 <= FOR)||(LA43_0 >= IDENTIFIER && LA43_0 <= IF)||(LA43_0 >= INT && LA43_0 <= INTLITERAL)||LA43_0==LBRACE||(LA43_0 >= LONG && LA43_0 <= LT)||(LA43_0 >= MONKEYS_AT && LA43_0 <= NULL)||LA43_0==PLUS||(LA43_0 >= PLUSPLUS && LA43_0 <= PUBLIC)||LA43_0==RETURN||(LA43_0 >= SEMI && LA43_0 <= SHORT)||(LA43_0 >= STATIC && LA43_0 <= SUB)||(LA43_0 >= SUBSUB && LA43_0 <= SYNCHRONIZED)||(LA43_0 >= THIS && LA43_0 <= THROW)||(LA43_0 >= TILDE && LA43_0 <= WHILE)) ) {
							alt43=1;
						}

						switch (alt43) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:568:10: blockStatement
							{
							pushFollow(FOLLOW_blockStatement_in_methodDeclaration2033);
							blockStatement();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

						default :
							break loop43;
						}
					}

					match(input,RBRACE,FOLLOW_RBRACE_in_methodDeclaration2054); if (state.failed) return retval;
					if ( state.backtracking==0 ) { sAdd("}"); }
					if ( state.backtracking==0 ) { methodStack.pop();}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:571:13: modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' )
					{
					pushFollow(FOLLOW_modifiers_in_methodDeclaration2080);
					modifiers6=modifiers();
					state._fsp--;
					if (state.failed) return retval;
					// /Users/byung/workspace/antlr2/Java.g:572:9: ( typeParameters )?
					int alt44=2;
					int LA44_0 = input.LA(1);
					if ( (LA44_0==LT) ) {
						alt44=1;
					}
					switch (alt44) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:572:10: typeParameters
							{
							pushFollow(FOLLOW_typeParameters_in_methodDeclaration2093);
							typeParameters();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					// /Users/byung/workspace/antlr2/Java.g:574:9: ( type | 'void' )
					int alt45=2;
					int LA45_0 = input.LA(1);
					if ( (LA45_0==BOOLEAN||LA45_0==BYTE||LA45_0==CHAR||LA45_0==DOUBLE||LA45_0==FLOAT||LA45_0==IDENTIFIER||LA45_0==INT||LA45_0==LONG||LA45_0==SHORT) ) {
						alt45=1;
					}
					else if ( (LA45_0==VOID) ) {
						alt45=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 45, 0, input);
						throw nvae;
					}

					switch (alt45) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:574:10: type
							{
							pushFollow(FOLLOW_type_in_methodDeclaration2115);
							type7=type();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;
						case 2 :
							// /Users/byung/workspace/antlr2/Java.g:575:13: 'void'
							{
							match(input,VOID,FOLLOW_VOID_in_methodDeclaration2130); if (state.failed) return retval;
							if ( state.backtracking==0 ) { kAdd("void"); }
							}
							break;

					}

					IDENTIFIER8=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodDeclaration2154); if (state.failed) return retval;
					if ( state.backtracking==0 ) { methodStack.push(input.toString(retval.start,input.LT(-1)));}
					if ( state.backtracking==0 ) {System.out.println("Method: " + input.toString(retval.start,input.LT(-1)) + " modi: " + (modifiers6!=null?input.toString(modifiers6.start,modifiers6.stop):null)); if((type7!=null?input.toString(type7.start,type7.stop):null) != null) { if(isPrimitive((type7!=null?input.toString(type7.start,type7.stop):null))) { uAdd( (IDENTIFIER8!=null?IDENTIFIER8.getText():null));}}}
					pushFollow(FOLLOW_formalParameters_in_methodDeclaration2169);
					formalParameters();
					state._fsp--;
					if (state.failed) return retval;
					// /Users/byung/workspace/antlr2/Java.g:579:9: ( '[' ']' )*
					loop46:
					while (true) {
						int alt46=2;
						int LA46_0 = input.LA(1);
						if ( (LA46_0==LBRACKET) ) {
							alt46=1;
						}

						switch (alt46) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:579:10: '[' ']'
							{
							match(input,LBRACKET,FOLLOW_LBRACKET_in_methodDeclaration2180); if (state.failed) return retval;
							match(input,RBRACKET,FOLLOW_RBRACKET_in_methodDeclaration2182); if (state.failed) return retval;
							if ( state.backtracking==0 ) {sAdd("[");sAdd("]");}
							}
							break;

						default :
							break loop46;
						}
					}

					// /Users/byung/workspace/antlr2/Java.g:581:9: ( 'throws' qualifiedNameList )?
					int alt47=2;
					int LA47_0 = input.LA(1);
					if ( (LA47_0==THROWS) ) {
						alt47=1;
					}
					switch (alt47) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:581:10: 'throws' qualifiedNameList
							{
							match(input,THROWS,FOLLOW_THROWS_in_methodDeclaration2206); if (state.failed) return retval;
							pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaration2208);
							qualifiedNameList();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) { kAdd("throws"); }
							}
							break;

					}

					// /Users/byung/workspace/antlr2/Java.g:583:9: ( block | ';' )
					int alt48=2;
					int LA48_0 = input.LA(1);
					if ( (LA48_0==LBRACE) ) {
						alt48=1;
					}
					else if ( (LA48_0==SEMI) ) {
						alt48=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 48, 0, input);
						throw nvae;
					}

					switch (alt48) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:584:13: block
							{
							pushFollow(FOLLOW_block_in_methodDeclaration2265);
							block();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;
						case 2 :
							// /Users/byung/workspace/antlr2/Java.g:585:13: ';'
							{
							match(input,SEMI,FOLLOW_SEMI_in_methodDeclaration2279); if (state.failed) return retval;
							if ( state.backtracking==0 ) { sAdd(";");}
							}
							break;

					}

					if ( state.backtracking==0 ) { methodStack.pop();}
					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 26, methodDeclaration_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "methodDeclaration"



	// $ANTLR start "fieldDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:592:1: fieldDeclaration : modifiers type variableDeclarator ( ',' variableDeclarator )* ';' ;
	public final void fieldDeclaration() throws RecognitionException {
		int fieldDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:593:5: ( modifiers type variableDeclarator ( ',' variableDeclarator )* ';' )
			// /Users/byung/workspace/antlr2/Java.g:593:9: modifiers type variableDeclarator ( ',' variableDeclarator )* ';'
			{
			pushFollow(FOLLOW_modifiers_in_fieldDeclaration2331);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_type_in_fieldDeclaration2341);
			type();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_variableDeclarator_in_fieldDeclaration2352);
			variableDeclarator();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:596:9: ( ',' variableDeclarator )*
			loop50:
			while (true) {
				int alt50=2;
				int LA50_0 = input.LA(1);
				if ( (LA50_0==COMMA) ) {
					alt50=1;
				}

				switch (alt50) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:596:10: ',' variableDeclarator
					{
					match(input,COMMA,FOLLOW_COMMA_in_fieldDeclaration2364); if (state.failed) return;
					pushFollow(FOLLOW_variableDeclarator_in_fieldDeclaration2366);
					variableDeclarator();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(",");}
					}
					break;

				default :
					break loop50;
				}
			}

			match(input,SEMI,FOLLOW_SEMI_in_fieldDeclaration2390); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd(";"); lt = "";}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 27, fieldDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "fieldDeclaration"



	// $ANTLR start "variableDeclarator"
	// /Users/byung/workspace/antlr2/Java.g:601:1: variableDeclarator : IDENTIFIER ( '[' ']' )* ( '=' variableInitializer )? ;
	public final void variableDeclarator() throws RecognitionException {
		int variableDeclarator_StartIndex = input.index();

		Token IDENTIFIER9=null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:602:5: ( IDENTIFIER ( '[' ']' )* ( '=' variableInitializer )? )
			// /Users/byung/workspace/antlr2/Java.g:602:9: IDENTIFIER ( '[' ']' )* ( '=' variableInitializer )?
			{
			IDENTIFIER9=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variableDeclarator2411); if (state.failed) return;
			if ( state.backtracking==0 ) {System.out.println("Identifier: "+ (IDENTIFIER9!=null?IDENTIFIER9.getText():null)); if(isPrimitive(lt)) { uAdd((IDENTIFIER9!=null?IDENTIFIER9.getText():null));} }
			// /Users/byung/workspace/antlr2/Java.g:603:13: ( '[' ']' )*
			loop51:
			while (true) {
				int alt51=2;
				int LA51_0 = input.LA(1);
				if ( (LA51_0==LBRACKET) ) {
					alt51=1;
				}

				switch (alt51) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:603:14: '[' ']'
					{
					match(input,LBRACKET,FOLLOW_LBRACKET_in_variableDeclarator2428); if (state.failed) return;
					match(input,RBRACKET,FOLLOW_RBRACKET_in_variableDeclarator2430); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("[");sAdd("]");}
					}
					break;

				default :
					break loop51;
				}
			}

			// /Users/byung/workspace/antlr2/Java.g:605:9: ( '=' variableInitializer )?
			int alt52=2;
			int LA52_0 = input.LA(1);
			if ( (LA52_0==EQ) ) {
				alt52=1;
			}
			switch (alt52) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:605:10: '=' variableInitializer
					{
					match(input,EQ,FOLLOW_EQ_in_variableDeclarator2453); if (state.failed) return;
					if ( state.backtracking==0 ) {lt = "";}
					pushFollow(FOLLOW_variableInitializer_in_variableDeclarator2457);
					variableInitializer();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("=");}
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 28, variableDeclarator_StartIndex); }

		}
	}
	// $ANTLR end "variableDeclarator"



	// $ANTLR start "interfaceBodyDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:612:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );
	public final void interfaceBodyDeclaration() throws RecognitionException {
		int interfaceBodyDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:613:5: ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' )
			int alt53=5;
			switch ( input.LA(1) ) {
			case MONKEYS_AT:
				{
				int LA53_1 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case PUBLIC:
				{
				int LA53_2 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case PROTECTED:
				{
				int LA53_3 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case PRIVATE:
				{
				int LA53_4 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case STATIC:
				{
				int LA53_5 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case ABSTRACT:
				{
				int LA53_6 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FINAL:
				{
				int LA53_7 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case NATIVE:
				{
				int LA53_8 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SYNCHRONIZED:
				{
				int LA53_9 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TRANSIENT:
				{
				int LA53_10 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case VOLATILE:
				{
				int LA53_11 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case STRICTFP:
				{
				int LA53_12 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}
				else if ( (synpred70_Java()) ) {
					alt53=3;
				}
				else if ( (synpred71_Java()) ) {
					alt53=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case IDENTIFIER:
				{
				int LA53_13 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 13, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case BOOLEAN:
				{
				int LA53_14 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 14, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case CHAR:
				{
				int LA53_15 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 15, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case BYTE:
				{
				int LA53_16 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 16, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SHORT:
				{
				int LA53_17 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 17, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case INT:
				{
				int LA53_18 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 18, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LONG:
				{
				int LA53_19 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 19, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FLOAT:
				{
				int LA53_20 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 20, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case DOUBLE:
				{
				int LA53_21 = input.LA(2);
				if ( (synpred68_Java()) ) {
					alt53=1;
				}
				else if ( (synpred69_Java()) ) {
					alt53=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 21, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LT:
			case VOID:
				{
				alt53=2;
				}
				break;
			case INTERFACE:
				{
				alt53=3;
				}
				break;
			case CLASS:
			case ENUM:
				{
				alt53=4;
				}
				break;
			case SEMI:
				{
				alt53=5;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 53, 0, input);
				throw nvae;
			}
			switch (alt53) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:614:9: interfaceFieldDeclaration
					{
					pushFollow(FOLLOW_interfaceFieldDeclaration_in_interfaceBodyDeclaration2497);
					interfaceFieldDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:615:9: interfaceMethodDeclaration
					{
					pushFollow(FOLLOW_interfaceMethodDeclaration_in_interfaceBodyDeclaration2507);
					interfaceMethodDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:616:9: interfaceDeclaration
					{
					pushFollow(FOLLOW_interfaceDeclaration_in_interfaceBodyDeclaration2517);
					interfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:617:9: classDeclaration
					{
					pushFollow(FOLLOW_classDeclaration_in_interfaceBodyDeclaration2527);
					classDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:618:9: ';'
					{
					match(input,SEMI,FOLLOW_SEMI_in_interfaceBodyDeclaration2537); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 29, interfaceBodyDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "interfaceBodyDeclaration"



	// $ANTLR start "interfaceMethodDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:621:1: interfaceMethodDeclaration : modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
	public final void interfaceMethodDeclaration() throws RecognitionException {
		int interfaceMethodDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:622:5: ( modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
			// /Users/byung/workspace/antlr2/Java.g:622:9: modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
			{
			pushFollow(FOLLOW_modifiers_in_interfaceMethodDeclaration2559);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:623:9: ( typeParameters )?
			int alt54=2;
			int LA54_0 = input.LA(1);
			if ( (LA54_0==LT) ) {
				alt54=1;
			}
			switch (alt54) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:623:10: typeParameters
					{
					pushFollow(FOLLOW_typeParameters_in_interfaceMethodDeclaration2570);
					typeParameters();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:625:9: ( type | 'void' )
			int alt55=2;
			int LA55_0 = input.LA(1);
			if ( (LA55_0==BOOLEAN||LA55_0==BYTE||LA55_0==CHAR||LA55_0==DOUBLE||LA55_0==FLOAT||LA55_0==IDENTIFIER||LA55_0==INT||LA55_0==LONG||LA55_0==SHORT) ) {
				alt55=1;
			}
			else if ( (LA55_0==VOID) ) {
				alt55=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 55, 0, input);
				throw nvae;
			}

			switch (alt55) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:625:10: type
					{
					pushFollow(FOLLOW_type_in_interfaceMethodDeclaration2592);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:626:10: 'void'
					{
					match(input,VOID,FOLLOW_VOID_in_interfaceMethodDeclaration2603); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("void");}
					}
					break;

			}

			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_interfaceMethodDeclaration2625); if (state.failed) return;
			pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaration2635);
			formalParameters();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:630:9: ( '[' ']' )*
			loop56:
			while (true) {
				int alt56=2;
				int LA56_0 = input.LA(1);
				if ( (LA56_0==LBRACKET) ) {
					alt56=1;
				}

				switch (alt56) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:630:10: '[' ']'
					{
					match(input,LBRACKET,FOLLOW_LBRACKET_in_interfaceMethodDeclaration2646); if (state.failed) return;
					match(input,RBRACKET,FOLLOW_RBRACKET_in_interfaceMethodDeclaration2648); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("[");sAdd("]");}
					}
					break;

				default :
					break loop56;
				}
			}

			// /Users/byung/workspace/antlr2/Java.g:632:9: ( 'throws' qualifiedNameList )?
			int alt57=2;
			int LA57_0 = input.LA(1);
			if ( (LA57_0==THROWS) ) {
				alt57=1;
			}
			switch (alt57) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:632:10: 'throws' qualifiedNameList
					{
					match(input,THROWS,FOLLOW_THROWS_in_interfaceMethodDeclaration2672); if (state.failed) return;
					pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaration2674);
					qualifiedNameList();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("throws"); }
					}
					break;

			}

			match(input,SEMI,FOLLOW_SEMI_in_interfaceMethodDeclaration2689); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd(";");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 30, interfaceMethodDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "interfaceMethodDeclaration"



	// $ANTLR start "interfaceFieldDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:641:1: interfaceFieldDeclaration : modifiers type variableDeclarator ( ',' variableDeclarator )* ';' ;
	public final void interfaceFieldDeclaration() throws RecognitionException {
		int interfaceFieldDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:642:5: ( modifiers type variableDeclarator ( ',' variableDeclarator )* ';' )
			// /Users/byung/workspace/antlr2/Java.g:642:9: modifiers type variableDeclarator ( ',' variableDeclarator )* ';'
			{
			pushFollow(FOLLOW_modifiers_in_interfaceFieldDeclaration2713);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_type_in_interfaceFieldDeclaration2715);
			type();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2717);
			variableDeclarator();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {}
			// /Users/byung/workspace/antlr2/Java.g:643:9: ( ',' variableDeclarator )*
			loop58:
			while (true) {
				int alt58=2;
				int LA58_0 = input.LA(1);
				if ( (LA58_0==COMMA) ) {
					alt58=1;
				}

				switch (alt58) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:643:10: ',' variableDeclarator
					{
					match(input,COMMA,FOLLOW_COMMA_in_interfaceFieldDeclaration2730); if (state.failed) return;
					pushFollow(FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2732);
					variableDeclarator();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(",");}
					}
					break;

				default :
					break loop58;
				}
			}

			match(input,SEMI,FOLLOW_SEMI_in_interfaceFieldDeclaration2755); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd(";");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 31, interfaceFieldDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "interfaceFieldDeclaration"


	public static class type_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "type"
	// /Users/byung/workspace/antlr2/Java.g:649:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
	public final JavaParser.type_return type() throws RecognitionException {
		JavaParser.type_return retval = new JavaParser.type_return();
		retval.start = input.LT(1);
		int type_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return retval; }

			// /Users/byung/workspace/antlr2/Java.g:650:5: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
			int alt61=2;
			int LA61_0 = input.LA(1);
			if ( (LA61_0==IDENTIFIER) ) {
				alt61=1;
			}
			else if ( (LA61_0==BOOLEAN||LA61_0==BYTE||LA61_0==CHAR||LA61_0==DOUBLE||LA61_0==FLOAT||LA61_0==INT||LA61_0==LONG||LA61_0==SHORT) ) {
				alt61=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 61, 0, input);
				throw nvae;
			}

			switch (alt61) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:650:9: classOrInterfaceType ( '[' ']' )*
					{
					pushFollow(FOLLOW_classOrInterfaceType_in_type2778);
					classOrInterfaceType();
					state._fsp--;
					if (state.failed) return retval;
					// /Users/byung/workspace/antlr2/Java.g:651:9: ( '[' ']' )*
					loop59:
					while (true) {
						int alt59=2;
						int LA59_0 = input.LA(1);
						if ( (LA59_0==LBRACKET) ) {
							alt59=1;
						}

						switch (alt59) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:651:10: '[' ']'
							{
							match(input,LBRACKET,FOLLOW_LBRACKET_in_type2789); if (state.failed) return retval;
							match(input,RBRACKET,FOLLOW_RBRACKET_in_type2791); if (state.failed) return retval;
							if ( state.backtracking==0 ) {sAdd("[");sAdd("]");}
							}
							break;

						default :
							break loop59;
						}
					}

					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:653:9: primitiveType ( '[' ']' )*
					{
					pushFollow(FOLLOW_primitiveType_in_type2814);
					primitiveType();
					state._fsp--;
					if (state.failed) return retval;
					// /Users/byung/workspace/antlr2/Java.g:654:9: ( '[' ']' )*
					loop60:
					while (true) {
						int alt60=2;
						int LA60_0 = input.LA(1);
						if ( (LA60_0==LBRACKET) ) {
							alt60=1;
						}

						switch (alt60) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:654:10: '[' ']'
							{
							match(input,LBRACKET,FOLLOW_LBRACKET_in_type2825); if (state.failed) return retval;
							match(input,RBRACKET,FOLLOW_RBRACKET_in_type2827); if (state.failed) return retval;
							if ( state.backtracking==0 ) {sAdd("[");sAdd("]");}
							}
							break;

						default :
							break loop60;
						}
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 32, type_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "type"



	// $ANTLR start "classOrInterfaceType"
	// /Users/byung/workspace/antlr2/Java.g:659:1: classOrInterfaceType : IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* ;
	public final void classOrInterfaceType() throws RecognitionException {
		int classOrInterfaceType_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:660:5: ( IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* )
			// /Users/byung/workspace/antlr2/Java.g:660:9: IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )*
			{
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType2861); if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:661:9: ( typeArguments )?
			int alt62=2;
			int LA62_0 = input.LA(1);
			if ( (LA62_0==LT) ) {
				int LA62_1 = input.LA(2);
				if ( (LA62_1==BOOLEAN||LA62_1==BYTE||LA62_1==CHAR||LA62_1==DOUBLE||LA62_1==FLOAT||LA62_1==IDENTIFIER||LA62_1==INT||LA62_1==LONG||LA62_1==QUES||LA62_1==SHORT) ) {
					alt62=1;
				}
			}
			switch (alt62) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:661:10: typeArguments
					{
					pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2872);
					typeArguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:663:9: ( '.' IDENTIFIER ( typeArguments )? )*
			loop64:
			while (true) {
				int alt64=2;
				int LA64_0 = input.LA(1);
				if ( (LA64_0==DOT) ) {
					alt64=1;
				}

				switch (alt64) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:663:10: '.' IDENTIFIER ( typeArguments )?
					{
					match(input,DOT,FOLLOW_DOT_in_classOrInterfaceType2894); if (state.failed) return;
					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType2896); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(".");}
					// /Users/byung/workspace/antlr2/Java.g:664:13: ( typeArguments )?
					int alt63=2;
					int LA63_0 = input.LA(1);
					if ( (LA63_0==LT) ) {
						int LA63_1 = input.LA(2);
						if ( (LA63_1==BOOLEAN||LA63_1==BYTE||LA63_1==CHAR||LA63_1==DOUBLE||LA63_1==FLOAT||LA63_1==IDENTIFIER||LA63_1==INT||LA63_1==LONG||LA63_1==QUES||LA63_1==SHORT) ) {
							alt63=1;
						}
					}
					switch (alt63) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:664:14: typeArguments
							{
							pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2913);
							typeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;

				default :
					break loop64;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 33, classOrInterfaceType_StartIndex); }

		}
	}
	// $ANTLR end "classOrInterfaceType"



	// $ANTLR start "primitiveType"
	// /Users/byung/workspace/antlr2/Java.g:669:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
	public final void primitiveType() throws RecognitionException {
		int primitiveType_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:670:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
			int alt65=8;
			switch ( input.LA(1) ) {
			case BOOLEAN:
				{
				alt65=1;
				}
				break;
			case CHAR:
				{
				alt65=2;
				}
				break;
			case BYTE:
				{
				alt65=3;
				}
				break;
			case SHORT:
				{
				alt65=4;
				}
				break;
			case INT:
				{
				alt65=5;
				}
				break;
			case LONG:
				{
				alt65=6;
				}
				break;
			case FLOAT:
				{
				alt65=7;
				}
				break;
			case DOUBLE:
				{
				alt65=8;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 65, 0, input);
				throw nvae;
			}
			switch (alt65) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:670:9: 'boolean'
					{
					match(input,BOOLEAN,FOLLOW_BOOLEAN_in_primitiveType2960); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("boolean"); lt = "boolean"; }
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:671:9: 'char'
					{
					match(input,CHAR,FOLLOW_CHAR_in_primitiveType2972); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("char"); lt = "char"; }
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:672:9: 'byte'
					{
					match(input,BYTE,FOLLOW_BYTE_in_primitiveType2984); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("byte"); lt="byte"; }
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:673:9: 'short'
					{
					match(input,SHORT,FOLLOW_SHORT_in_primitiveType2996); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("short"); lt="short"; }
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:674:9: 'int'
					{
					match(input,INT,FOLLOW_INT_in_primitiveType3008); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("int"); lt="int"; }
					if ( state.backtracking==0 ) {System.out.println("Hello from int");}
					}
					break;
				case 6 :
					// /Users/byung/workspace/antlr2/Java.g:675:9: 'long'
					{
					match(input,LONG,FOLLOW_LONG_in_primitiveType3022); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("long"); lt ="long";}
					}
					break;
				case 7 :
					// /Users/byung/workspace/antlr2/Java.g:676:9: 'float'
					{
					match(input,FLOAT,FOLLOW_FLOAT_in_primitiveType3034); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("float"); lt="float"; }
					}
					break;
				case 8 :
					// /Users/byung/workspace/antlr2/Java.g:677:9: 'double'
					{
					match(input,DOUBLE,FOLLOW_DOUBLE_in_primitiveType3046); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("double"); lt="double";}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 34, primitiveType_StartIndex); }

		}
	}
	// $ANTLR end "primitiveType"



	// $ANTLR start "typeArguments"
	// /Users/byung/workspace/antlr2/Java.g:680:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
	public final void typeArguments() throws RecognitionException {
		int typeArguments_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:681:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
			// /Users/byung/workspace/antlr2/Java.g:681:9: '<' typeArgument ( ',' typeArgument )* '>'
			{
			match(input,LT,FOLLOW_LT_in_typeArguments3068); if (state.failed) return;
			pushFollow(FOLLOW_typeArgument_in_typeArguments3070);
			typeArgument();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("<");}
			// /Users/byung/workspace/antlr2/Java.g:682:9: ( ',' typeArgument )*
			loop66:
			while (true) {
				int alt66=2;
				int LA66_0 = input.LA(1);
				if ( (LA66_0==COMMA) ) {
					alt66=1;
				}

				switch (alt66) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:682:10: ',' typeArgument
					{
					match(input,COMMA,FOLLOW_COMMA_in_typeArguments3083); if (state.failed) return;
					pushFollow(FOLLOW_typeArgument_in_typeArguments3085);
					typeArgument();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(",");}
					}
					break;

				default :
					break loop66;
				}
			}

			match(input,GT,FOLLOW_GT_in_typeArguments3109); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd(">");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 35, typeArguments_StartIndex); }

		}
	}
	// $ANTLR end "typeArguments"



	// $ANTLR start "typeArgument"
	// /Users/byung/workspace/antlr2/Java.g:687:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
	public final void typeArgument() throws RecognitionException {
		int typeArgument_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:688:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
			int alt69=2;
			int LA69_0 = input.LA(1);
			if ( (LA69_0==BOOLEAN||LA69_0==BYTE||LA69_0==CHAR||LA69_0==DOUBLE||LA69_0==FLOAT||LA69_0==IDENTIFIER||LA69_0==INT||LA69_0==LONG||LA69_0==SHORT) ) {
				alt69=1;
			}
			else if ( (LA69_0==QUES) ) {
				alt69=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 69, 0, input);
				throw nvae;
			}

			switch (alt69) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:688:9: type
					{
					pushFollow(FOLLOW_type_in_typeArgument3131);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:689:9: '?' ( ( 'extends' | 'super' ) type )?
					{
					match(input,QUES,FOLLOW_QUES_in_typeArgument3141); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("?");}
					// /Users/byung/workspace/antlr2/Java.g:690:9: ( ( 'extends' | 'super' ) type )?
					int alt68=2;
					int LA68_0 = input.LA(1);
					if ( (LA68_0==EXTENDS||LA68_0==SUPER) ) {
						alt68=1;
					}
					switch (alt68) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:691:13: ( 'extends' | 'super' ) type
							{
							// /Users/byung/workspace/antlr2/Java.g:691:13: ( 'extends' | 'super' )
							int alt67=2;
							int LA67_0 = input.LA(1);
							if ( (LA67_0==EXTENDS) ) {
								alt67=1;
							}
							else if ( (LA67_0==SUPER) ) {
								alt67=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return;}
								NoViableAltException nvae =
									new NoViableAltException("", 67, 0, input);
								throw nvae;
							}

							switch (alt67) {
								case 1 :
									// /Users/byung/workspace/antlr2/Java.g:691:14: 'extends'
									{
									match(input,EXTENDS,FOLLOW_EXTENDS_in_typeArgument3168); if (state.failed) return;
									if ( state.backtracking==0 ) { kAdd("extends"); }
									}
									break;
								case 2 :
									// /Users/byung/workspace/antlr2/Java.g:692:14: 'super'
									{
									match(input,SUPER,FOLLOW_SUPER_in_typeArgument3185); if (state.failed) return;
									if ( state.backtracking==0 ) { kAdd("super"); }
									}
									break;

							}

							pushFollow(FOLLOW_type_in_typeArgument3215);
							type();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 36, typeArgument_StartIndex); }

		}
	}
	// $ANTLR end "typeArgument"



	// $ANTLR start "qualifiedNameList"
	// /Users/byung/workspace/antlr2/Java.g:698:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
	public final void qualifiedNameList() throws RecognitionException {
		int qualifiedNameList_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:699:5: ( qualifiedName ( ',' qualifiedName )* )
			// /Users/byung/workspace/antlr2/Java.g:699:9: qualifiedName ( ',' qualifiedName )*
			{
			pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3246);
			qualifiedName();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:700:9: ( ',' qualifiedName )*
			loop70:
			while (true) {
				int alt70=2;
				int LA70_0 = input.LA(1);
				if ( (LA70_0==COMMA) ) {
					alt70=1;
				}

				switch (alt70) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:700:10: ',' qualifiedName
					{
					match(input,COMMA,FOLLOW_COMMA_in_qualifiedNameList3258); if (state.failed) return;
					pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3260);
					qualifiedName();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(",");}
					}
					break;

				default :
					break loop70;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 37, qualifiedNameList_StartIndex); }

		}
	}
	// $ANTLR end "qualifiedNameList"



	// $ANTLR start "formalParameters"
	// /Users/byung/workspace/antlr2/Java.g:704:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
	public final void formalParameters() throws RecognitionException {
		int formalParameters_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:705:5: ( '(' ( formalParameterDecls )? ')' )
			// /Users/byung/workspace/antlr2/Java.g:705:9: '(' ( formalParameterDecls )? ')'
			{
			match(input,LPAREN,FOLLOW_LPAREN_in_formalParameters3294); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("(");}
			// /Users/byung/workspace/antlr2/Java.g:706:9: ( formalParameterDecls )?
			int alt71=2;
			int LA71_0 = input.LA(1);
			if ( (LA71_0==BOOLEAN||LA71_0==BYTE||LA71_0==CHAR||LA71_0==DOUBLE||LA71_0==FINAL||LA71_0==FLOAT||LA71_0==IDENTIFIER||LA71_0==INT||LA71_0==LONG||LA71_0==MONKEYS_AT||LA71_0==SHORT) ) {
				alt71=1;
			}
			switch (alt71) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:706:10: formalParameterDecls
					{
					pushFollow(FOLLOW_formalParameterDecls_in_formalParameters3307);
					formalParameterDecls();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,RPAREN,FOLLOW_RPAREN_in_formalParameters3329); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd(")"); lt = "";}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 38, formalParameters_StartIndex); }

		}
	}
	// $ANTLR end "formalParameters"



	// $ANTLR start "formalParameterDecls"
	// /Users/byung/workspace/antlr2/Java.g:711:1: formalParameterDecls : ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl );
	public final void formalParameterDecls() throws RecognitionException {
		int formalParameterDecls_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:712:5: ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl )
			int alt74=3;
			switch ( input.LA(1) ) {
			case FINAL:
				{
				int LA74_1 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case MONKEYS_AT:
				{
				int LA74_2 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case IDENTIFIER:
				{
				int LA74_3 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case BOOLEAN:
				{
				int LA74_4 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case CHAR:
				{
				int LA74_5 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case BYTE:
				{
				int LA74_6 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case SHORT:
				{
				int LA74_7 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case INT:
				{
				int LA74_8 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case LONG:
				{
				int LA74_9 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case FLOAT:
				{
				int LA74_10 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			case DOUBLE:
				{
				int LA74_11 = input.LA(2);
				if ( (synpred96_Java()) ) {
					alt74=1;
				}
				else if ( (synpred98_Java()) ) {
					alt74=2;
				}
				else if ( (true) ) {
					alt74=3;
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 74, 0, input);
				throw nvae;
			}
			switch (alt74) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:712:9: ellipsisParameterDecl
					{
					pushFollow(FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3351);
					ellipsisParameterDecl();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:713:9: normalParameterDecl ( ',' normalParameterDecl )*
					{
					pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3361);
					normalParameterDecl();
					state._fsp--;
					if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:714:9: ( ',' normalParameterDecl )*
					loop72:
					while (true) {
						int alt72=2;
						int LA72_0 = input.LA(1);
						if ( (LA72_0==COMMA) ) {
							alt72=1;
						}

						switch (alt72) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:714:10: ',' normalParameterDecl
							{
							match(input,COMMA,FOLLOW_COMMA_in_formalParameterDecls3372); if (state.failed) return;
							pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3374);
							normalParameterDecl();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd(",");}
							}
							break;

						default :
							break loop72;
						}
					}

					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:716:9: ( normalParameterDecl ',' )+ ellipsisParameterDecl
					{
					// /Users/byung/workspace/antlr2/Java.g:716:9: ( normalParameterDecl ',' )+
					int cnt73=0;
					loop73:
					while (true) {
						int alt73=2;
						switch ( input.LA(1) ) {
						case FINAL:
							{
							int LA73_1 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case MONKEYS_AT:
							{
							int LA73_2 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case IDENTIFIER:
							{
							int LA73_3 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case BOOLEAN:
							{
							int LA73_4 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case CHAR:
							{
							int LA73_5 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case BYTE:
							{
							int LA73_6 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case SHORT:
							{
							int LA73_7 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case INT:
							{
							int LA73_8 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case LONG:
							{
							int LA73_9 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case FLOAT:
							{
							int LA73_10 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						case DOUBLE:
							{
							int LA73_11 = input.LA(2);
							if ( (synpred99_Java()) ) {
								alt73=1;
							}

							}
							break;
						}
						switch (alt73) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:716:10: normalParameterDecl ','
							{
							pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3398);
							normalParameterDecl();
							state._fsp--;
							if (state.failed) return;
							match(input,COMMA,FOLLOW_COMMA_in_formalParameterDecls3408); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd(",");}
							}
							break;

						default :
							if ( cnt73 >= 1 ) break loop73;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(73, input);
							throw eee;
						}
						cnt73++;
					}

					pushFollow(FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3432);
					ellipsisParameterDecl();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 39, formalParameterDecls_StartIndex); }

		}
	}
	// $ANTLR end "formalParameterDecls"



	// $ANTLR start "normalParameterDecl"
	// /Users/byung/workspace/antlr2/Java.g:722:1: normalParameterDecl : variableModifiers type IDENTIFIER ( '[' ']' )* ;
	public final void normalParameterDecl() throws RecognitionException {
		int normalParameterDecl_StartIndex = input.index();

		Token IDENTIFIER11=null;
		ParserRuleReturnScope type10 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:723:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* )
			// /Users/byung/workspace/antlr2/Java.g:723:9: variableModifiers type IDENTIFIER ( '[' ']' )*
			{
			pushFollow(FOLLOW_variableModifiers_in_normalParameterDecl3452);
			variableModifiers();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_type_in_normalParameterDecl3454);
			type10=type();
			state._fsp--;
			if (state.failed) return;
			IDENTIFIER11=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalParameterDecl3456); if (state.failed) return;
			if ( state.backtracking==0 ) {System.out.println("TYPe: " + (type10!=null?input.toString(type10.start,type10.stop):null));if(isPrimitive((type10!=null?input.toString(type10.start,type10.stop):null))) { uAdd((IDENTIFIER11!=null?IDENTIFIER11.getText():null));} }
			// /Users/byung/workspace/antlr2/Java.g:724:9: ( '[' ']' )*
			loop75:
			while (true) {
				int alt75=2;
				int LA75_0 = input.LA(1);
				if ( (LA75_0==LBRACKET) ) {
					alt75=1;
				}

				switch (alt75) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:724:10: '[' ']'
					{
					match(input,LBRACKET,FOLLOW_LBRACKET_in_normalParameterDecl3469); if (state.failed) return;
					match(input,RBRACKET,FOLLOW_RBRACKET_in_normalParameterDecl3471); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("[");sAdd("]");}
					}
					break;

				default :
					break loop75;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 40, normalParameterDecl_StartIndex); }

		}
	}
	// $ANTLR end "normalParameterDecl"



	// $ANTLR start "ellipsisParameterDecl"
	// /Users/byung/workspace/antlr2/Java.g:728:1: ellipsisParameterDecl : variableModifiers type '...' IDENTIFIER ;
	public final void ellipsisParameterDecl() throws RecognitionException {
		int ellipsisParameterDecl_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:729:5: ( variableModifiers type '...' IDENTIFIER )
			// /Users/byung/workspace/antlr2/Java.g:729:9: variableModifiers type '...' IDENTIFIER
			{
			pushFollow(FOLLOW_variableModifiers_in_ellipsisParameterDecl3504);
			variableModifiers();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_type_in_ellipsisParameterDecl3514);
			type();
			state._fsp--;
			if (state.failed) return;
			match(input,ELLIPSIS,FOLLOW_ELLIPSIS_in_ellipsisParameterDecl3517); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("...");}
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_ellipsisParameterDecl3529); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 41, ellipsisParameterDecl_StartIndex); }

		}
	}
	// $ANTLR end "ellipsisParameterDecl"



	// $ANTLR start "explicitConstructorInvocation"
	// /Users/byung/workspace/antlr2/Java.g:735:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );
	public final void explicitConstructorInvocation() throws RecognitionException {
		int explicitConstructorInvocation_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:736:5: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' )
			int alt79=2;
			switch ( input.LA(1) ) {
			case LT:
				{
				alt79=1;
				}
				break;
			case THIS:
				{
				int LA79_2 = input.LA(2);
				if ( (synpred103_Java()) ) {
					alt79=1;
				}
				else if ( (true) ) {
					alt79=2;
				}

				}
				break;
			case SUPER:
				{
				int LA79_3 = input.LA(2);
				if ( (synpred103_Java()) ) {
					alt79=1;
				}
				else if ( (true) ) {
					alt79=2;
				}

				}
				break;
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case CHARLITERAL:
			case DOUBLE:
			case DOUBLELITERAL:
			case FALSE:
			case FLOAT:
			case FLOATLITERAL:
			case IDENTIFIER:
			case INT:
			case INTLITERAL:
			case LONG:
			case LONGLITERAL:
			case LPAREN:
			case NEW:
			case NULL:
			case SHORT:
			case STRINGLITERAL:
			case TRUE:
			case VOID:
				{
				alt79=2;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 79, 0, input);
				throw nvae;
			}
			switch (alt79) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:736:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
					{
					// /Users/byung/workspace/antlr2/Java.g:736:9: ( nonWildcardTypeArguments )?
					int alt76=2;
					int LA76_0 = input.LA(1);
					if ( (LA76_0==LT) ) {
						alt76=1;
					}
					switch (alt76) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:736:10: nonWildcardTypeArguments
							{
							pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3551);
							nonWildcardTypeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					// /Users/byung/workspace/antlr2/Java.g:738:9: ( 'this' | 'super' )
					int alt77=2;
					int LA77_0 = input.LA(1);
					if ( (LA77_0==THIS) ) {
						alt77=1;
					}
					else if ( (LA77_0==SUPER) ) {
						alt77=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						NoViableAltException nvae =
							new NoViableAltException("", 77, 0, input);
						throw nvae;
					}

					switch (alt77) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:738:10: 'this'
							{
							match(input,THIS,FOLLOW_THIS_in_explicitConstructorInvocation3578); if (state.failed) return;
							if ( state.backtracking==0 ) { kAdd("this"); }
							}
							break;
						case 2 :
							// /Users/byung/workspace/antlr2/Java.g:739:10: 'super'
							{
							match(input,SUPER,FOLLOW_SUPER_in_explicitConstructorInvocation3591); if (state.failed) return;
							if ( state.backtracking==0 ) { kAdd("super"); }
							}
							break;

					}

					pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3613);
					arguments();
					state._fsp--;
					if (state.failed) return;
					match(input,SEMI,FOLLOW_SEMI_in_explicitConstructorInvocation3615); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:743:9: primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';'
					{
					pushFollow(FOLLOW_primary_in_explicitConstructorInvocation3628);
					primary();
					state._fsp--;
					if (state.failed) return;
					match(input,DOT,FOLLOW_DOT_in_explicitConstructorInvocation3638); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(".");}
					// /Users/byung/workspace/antlr2/Java.g:745:9: ( nonWildcardTypeArguments )?
					int alt78=2;
					int LA78_0 = input.LA(1);
					if ( (LA78_0==LT) ) {
						alt78=1;
					}
					switch (alt78) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:745:10: nonWildcardTypeArguments
							{
							pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3651);
							nonWildcardTypeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,SUPER,FOLLOW_SUPER_in_explicitConstructorInvocation3672); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("super"); }
					pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3684);
					arguments();
					state._fsp--;
					if (state.failed) return;
					match(input,SEMI,FOLLOW_SEMI_in_explicitConstructorInvocation3686); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 42, explicitConstructorInvocation_StartIndex); }

		}
	}
	// $ANTLR end "explicitConstructorInvocation"


	public static class qualifiedName_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "qualifiedName"
	// /Users/byung/workspace/antlr2/Java.g:751:1: qualifiedName : IDENTIFIER ( '.' IDENTIFIER )* ;
	public final JavaParser.qualifiedName_return qualifiedName() throws RecognitionException {
		JavaParser.qualifiedName_return retval = new JavaParser.qualifiedName_return();
		retval.start = input.LT(1);
		int qualifiedName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }

			// /Users/byung/workspace/antlr2/Java.g:752:5: ( IDENTIFIER ( '.' IDENTIFIER )* )
			// /Users/byung/workspace/antlr2/Java.g:752:9: IDENTIFIER ( '.' IDENTIFIER )*
			{
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName3708); if (state.failed) return retval;
			// /Users/byung/workspace/antlr2/Java.g:753:9: ( '.' IDENTIFIER )*
			loop80:
			while (true) {
				int alt80=2;
				int LA80_0 = input.LA(1);
				if ( (LA80_0==DOT) ) {
					alt80=1;
				}

				switch (alt80) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:753:10: '.' IDENTIFIER
					{
					match(input,DOT,FOLLOW_DOT_in_qualifiedName3720); if (state.failed) return retval;
					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName3722); if (state.failed) return retval;
					if ( state.backtracking==0 ) {sAdd(".");}
					}
					break;

				default :
					break loop80;
				}
			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 43, qualifiedName_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "qualifiedName"



	// $ANTLR start "annotations"
	// /Users/byung/workspace/antlr2/Java.g:757:1: annotations : ( annotation )+ ;
	public final void annotations() throws RecognitionException {
		int annotations_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:758:5: ( ( annotation )+ )
			// /Users/byung/workspace/antlr2/Java.g:758:9: ( annotation )+
			{
			// /Users/byung/workspace/antlr2/Java.g:758:9: ( annotation )+
			int cnt81=0;
			loop81:
			while (true) {
				int alt81=2;
				int LA81_0 = input.LA(1);
				if ( (LA81_0==MONKEYS_AT) ) {
					alt81=1;
				}

				switch (alt81) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:758:10: annotation
					{
					pushFollow(FOLLOW_annotation_in_annotations3757);
					annotation();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					if ( cnt81 >= 1 ) break loop81;
					if (state.backtracking>0) {state.failed=true; return;}
					EarlyExitException eee = new EarlyExitException(81, input);
					throw eee;
				}
				cnt81++;
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 44, annotations_StartIndex); }

		}
	}
	// $ANTLR end "annotations"



	// $ANTLR start "annotation"
	// /Users/byung/workspace/antlr2/Java.g:766:1: annotation : '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
	public final void annotation() throws RecognitionException {
		int annotation_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:767:5: ( '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )? )
			// /Users/byung/workspace/antlr2/Java.g:767:9: '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )?
			{
			match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotation3790); if (state.failed) return;
			pushFollow(FOLLOW_qualifiedName_in_annotation3792);
			qualifiedName();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("@");}
			// /Users/byung/workspace/antlr2/Java.g:768:9: ( '(' ( elementValuePairs | elementValue )? ')' )?
			int alt83=2;
			int LA83_0 = input.LA(1);
			if ( (LA83_0==LPAREN) ) {
				alt83=1;
			}
			switch (alt83) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:768:13: '(' ( elementValuePairs | elementValue )? ')'
					{
					match(input,LPAREN,FOLLOW_LPAREN_in_annotation3809); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("(");}
					// /Users/byung/workspace/antlr2/Java.g:769:19: ( elementValuePairs | elementValue )?
					int alt82=3;
					int LA82_0 = input.LA(1);
					if ( (LA82_0==IDENTIFIER) ) {
						int LA82_1 = input.LA(2);
						if ( (LA82_1==EQ) ) {
							alt82=1;
						}
						else if ( ((LA82_1 >= AMP && LA82_1 <= AMPAMP)||(LA82_1 >= BANGEQ && LA82_1 <= BARBAR)||LA82_1==CARET||LA82_1==DOT||LA82_1==EQEQ||LA82_1==GT||LA82_1==INSTANCEOF||LA82_1==LBRACKET||(LA82_1 >= LPAREN && LA82_1 <= LT)||LA82_1==PERCENT||LA82_1==PLUS||LA82_1==PLUSPLUS||LA82_1==QUES||LA82_1==RPAREN||LA82_1==SLASH||LA82_1==STAR||LA82_1==SUB||LA82_1==SUBSUB) ) {
							alt82=2;
						}
					}
					else if ( (LA82_0==BANG||LA82_0==BOOLEAN||LA82_0==BYTE||(LA82_0 >= CHAR && LA82_0 <= CHARLITERAL)||(LA82_0 >= DOUBLE && LA82_0 <= DOUBLELITERAL)||LA82_0==FALSE||(LA82_0 >= FLOAT && LA82_0 <= FLOATLITERAL)||LA82_0==INT||LA82_0==INTLITERAL||LA82_0==LBRACE||(LA82_0 >= LONG && LA82_0 <= LPAREN)||LA82_0==MONKEYS_AT||(LA82_0 >= NEW && LA82_0 <= NULL)||LA82_0==PLUS||LA82_0==PLUSPLUS||LA82_0==SHORT||(LA82_0 >= STRINGLITERAL && LA82_0 <= SUB)||(LA82_0 >= SUBSUB && LA82_0 <= SUPER)||LA82_0==THIS||LA82_0==TILDE||LA82_0==TRUE||LA82_0==VOID) ) {
						alt82=2;
					}
					switch (alt82) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:769:23: elementValuePairs
							{
							pushFollow(FOLLOW_elementValuePairs_in_annotation3837);
							elementValuePairs();
							state._fsp--;
							if (state.failed) return;
							}
							break;
						case 2 :
							// /Users/byung/workspace/antlr2/Java.g:770:23: elementValue
							{
							pushFollow(FOLLOW_elementValue_in_annotation3861);
							elementValue();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,RPAREN,FOLLOW_RPAREN_in_annotation3897); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(")");}
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 45, annotation_StartIndex); }

		}
	}
	// $ANTLR end "annotation"



	// $ANTLR start "elementValuePairs"
	// /Users/byung/workspace/antlr2/Java.g:776:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
	public final void elementValuePairs() throws RecognitionException {
		int elementValuePairs_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:777:5: ( elementValuePair ( ',' elementValuePair )* )
			// /Users/byung/workspace/antlr2/Java.g:777:9: elementValuePair ( ',' elementValuePair )*
			{
			pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3930);
			elementValuePair();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:778:9: ( ',' elementValuePair )*
			loop84:
			while (true) {
				int alt84=2;
				int LA84_0 = input.LA(1);
				if ( (LA84_0==COMMA) ) {
					alt84=1;
				}

				switch (alt84) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:778:10: ',' elementValuePair
					{
					match(input,COMMA,FOLLOW_COMMA_in_elementValuePairs3941); if (state.failed) return;
					pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3943);
					elementValuePair();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(",");}
					}
					break;

				default :
					break loop84;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 46, elementValuePairs_StartIndex); }

		}
	}
	// $ANTLR end "elementValuePairs"



	// $ANTLR start "elementValuePair"
	// /Users/byung/workspace/antlr2/Java.g:782:1: elementValuePair : IDENTIFIER '=' elementValue ;
	public final void elementValuePair() throws RecognitionException {
		int elementValuePair_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:783:5: ( IDENTIFIER '=' elementValue )
			// /Users/byung/workspace/antlr2/Java.g:783:9: IDENTIFIER '=' elementValue
			{
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_elementValuePair3976); if (state.failed) return;
			match(input,EQ,FOLLOW_EQ_in_elementValuePair3978); if (state.failed) return;
			pushFollow(FOLLOW_elementValue_in_elementValuePair3980);
			elementValue();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("=");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 47, elementValuePair_StartIndex); }

		}
	}
	// $ANTLR end "elementValuePair"



	// $ANTLR start "elementValue"
	// /Users/byung/workspace/antlr2/Java.g:786:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
	public final void elementValue() throws RecognitionException {
		int elementValue_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:787:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
			int alt85=3;
			switch ( input.LA(1) ) {
			case BANG:
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case CHARLITERAL:
			case DOUBLE:
			case DOUBLELITERAL:
			case FALSE:
			case FLOAT:
			case FLOATLITERAL:
			case IDENTIFIER:
			case INT:
			case INTLITERAL:
			case LONG:
			case LONGLITERAL:
			case LPAREN:
			case NEW:
			case NULL:
			case PLUS:
			case PLUSPLUS:
			case SHORT:
			case STRINGLITERAL:
			case SUB:
			case SUBSUB:
			case SUPER:
			case THIS:
			case TILDE:
			case TRUE:
			case VOID:
				{
				alt85=1;
				}
				break;
			case MONKEYS_AT:
				{
				alt85=2;
				}
				break;
			case LBRACE:
				{
				alt85=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 85, 0, input);
				throw nvae;
			}
			switch (alt85) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:787:9: conditionalExpression
					{
					pushFollow(FOLLOW_conditionalExpression_in_elementValue4002);
					conditionalExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:788:9: annotation
					{
					pushFollow(FOLLOW_annotation_in_elementValue4012);
					annotation();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:789:9: elementValueArrayInitializer
					{
					pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue4022);
					elementValueArrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 48, elementValue_StartIndex); }

		}
	}
	// $ANTLR end "elementValue"



	// $ANTLR start "elementValueArrayInitializer"
	// /Users/byung/workspace/antlr2/Java.g:792:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' ;
	public final void elementValueArrayInitializer() throws RecognitionException {
		int elementValueArrayInitializer_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:793:5: ( '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' )
			// /Users/byung/workspace/antlr2/Java.g:793:9: '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}'
			{
			match(input,LBRACE,FOLLOW_LBRACE_in_elementValueArrayInitializer4042); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("{");}
			// /Users/byung/workspace/antlr2/Java.g:794:9: ( elementValue ( ',' elementValue )* )?
			int alt87=2;
			int LA87_0 = input.LA(1);
			if ( (LA87_0==BANG||LA87_0==BOOLEAN||LA87_0==BYTE||(LA87_0 >= CHAR && LA87_0 <= CHARLITERAL)||(LA87_0 >= DOUBLE && LA87_0 <= DOUBLELITERAL)||LA87_0==FALSE||(LA87_0 >= FLOAT && LA87_0 <= FLOATLITERAL)||LA87_0==IDENTIFIER||LA87_0==INT||LA87_0==INTLITERAL||LA87_0==LBRACE||(LA87_0 >= LONG && LA87_0 <= LPAREN)||LA87_0==MONKEYS_AT||(LA87_0 >= NEW && LA87_0 <= NULL)||LA87_0==PLUS||LA87_0==PLUSPLUS||LA87_0==SHORT||(LA87_0 >= STRINGLITERAL && LA87_0 <= SUB)||(LA87_0 >= SUBSUB && LA87_0 <= SUPER)||LA87_0==THIS||LA87_0==TILDE||LA87_0==TRUE||LA87_0==VOID) ) {
				alt87=1;
			}
			switch (alt87) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:794:10: elementValue ( ',' elementValue )*
					{
					pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer4055);
					elementValue();
					state._fsp--;
					if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:795:13: ( ',' elementValue )*
					loop86:
					while (true) {
						int alt86=2;
						int LA86_0 = input.LA(1);
						if ( (LA86_0==COMMA) ) {
							int LA86_1 = input.LA(2);
							if ( (LA86_1==BANG||LA86_1==BOOLEAN||LA86_1==BYTE||(LA86_1 >= CHAR && LA86_1 <= CHARLITERAL)||(LA86_1 >= DOUBLE && LA86_1 <= DOUBLELITERAL)||LA86_1==FALSE||(LA86_1 >= FLOAT && LA86_1 <= FLOATLITERAL)||LA86_1==IDENTIFIER||LA86_1==INT||LA86_1==INTLITERAL||LA86_1==LBRACE||(LA86_1 >= LONG && LA86_1 <= LPAREN)||LA86_1==MONKEYS_AT||(LA86_1 >= NEW && LA86_1 <= NULL)||LA86_1==PLUS||LA86_1==PLUSPLUS||LA86_1==SHORT||(LA86_1 >= STRINGLITERAL && LA86_1 <= SUB)||(LA86_1 >= SUBSUB && LA86_1 <= SUPER)||LA86_1==THIS||LA86_1==TILDE||LA86_1==TRUE||LA86_1==VOID) ) {
								alt86=1;
							}

						}

						switch (alt86) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:795:14: ',' elementValue
							{
							match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer4070); if (state.failed) return;
							pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer4072);
							elementValue();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd(",");}
							}
							break;

						default :
							break loop86;
						}
					}

					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:797:12: ( ',' )?
			int alt88=2;
			int LA88_0 = input.LA(1);
			if ( (LA88_0==COMMA) ) {
				alt88=1;
			}
			switch (alt88) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:797:13: ','
					{
					match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer4103); if (state.failed) return;
					}
					break;

			}

			match(input,RBRACE,FOLLOW_RBRACE_in_elementValueArrayInitializer4107); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd(","); sAdd("}");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 49, elementValueArrayInitializer_StartIndex); }

		}
	}
	// $ANTLR end "elementValueArrayInitializer"



	// $ANTLR start "annotationTypeDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:804:1: annotationTypeDeclaration : modifiers '@' 'interface' IDENTIFIER annotationTypeBody ;
	public final void annotationTypeDeclaration() throws RecognitionException {
		int annotationTypeDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:805:5: ( modifiers '@' 'interface' IDENTIFIER annotationTypeBody )
			// /Users/byung/workspace/antlr2/Java.g:805:9: modifiers '@' 'interface' IDENTIFIER annotationTypeBody
			{
			pushFollow(FOLLOW_modifiers_in_annotationTypeDeclaration4132);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotationTypeDeclaration4134); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("@");}
			match(input,INTERFACE,FOLLOW_INTERFACE_in_annotationTypeDeclaration4145); if (state.failed) return;
			if ( state.backtracking==0 ) { kAdd("interface"); }
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationTypeDeclaration4157); if (state.failed) return;
			pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration4167);
			annotationTypeBody();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 50, annotationTypeDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "annotationTypeDeclaration"



	// $ANTLR start "annotationTypeBody"
	// /Users/byung/workspace/antlr2/Java.g:812:1: annotationTypeBody : '{' ( annotationTypeElementDeclaration )* '}' ;
	public final void annotationTypeBody() throws RecognitionException {
		int annotationTypeBody_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:813:5: ( '{' ( annotationTypeElementDeclaration )* '}' )
			// /Users/byung/workspace/antlr2/Java.g:813:9: '{' ( annotationTypeElementDeclaration )* '}'
			{
			match(input,LBRACE,FOLLOW_LBRACE_in_annotationTypeBody4188); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("{");}
			// /Users/byung/workspace/antlr2/Java.g:814:9: ( annotationTypeElementDeclaration )*
			loop89:
			while (true) {
				int alt89=2;
				int LA89_0 = input.LA(1);
				if ( (LA89_0==ABSTRACT||LA89_0==BOOLEAN||LA89_0==BYTE||LA89_0==CHAR||LA89_0==CLASS||LA89_0==DOUBLE||LA89_0==ENUM||LA89_0==FINAL||LA89_0==FLOAT||LA89_0==IDENTIFIER||(LA89_0 >= INT && LA89_0 <= INTERFACE)||LA89_0==LONG||LA89_0==LT||(LA89_0 >= MONKEYS_AT && LA89_0 <= NATIVE)||(LA89_0 >= PRIVATE && LA89_0 <= PUBLIC)||(LA89_0 >= SEMI && LA89_0 <= SHORT)||(LA89_0 >= STATIC && LA89_0 <= STRICTFP)||LA89_0==SYNCHRONIZED||LA89_0==TRANSIENT||(LA89_0 >= VOID && LA89_0 <= VOLATILE)) ) {
					alt89=1;
				}

				switch (alt89) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:814:10: annotationTypeElementDeclaration
					{
					pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody4201);
					annotationTypeElementDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop89;
				}
			}

			match(input,RBRACE,FOLLOW_RBRACE_in_annotationTypeBody4223); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("}");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 51, annotationTypeBody_StartIndex); }

		}
	}
	// $ANTLR end "annotationTypeBody"



	// $ANTLR start "annotationTypeElementDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:822:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );
	public final void annotationTypeElementDeclaration() throws RecognitionException {
		int annotationTypeElementDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:823:5: ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' )
			int alt90=7;
			switch ( input.LA(1) ) {
			case MONKEYS_AT:
				{
				int LA90_1 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case PUBLIC:
				{
				int LA90_2 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case PROTECTED:
				{
				int LA90_3 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case PRIVATE:
				{
				int LA90_4 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case STATIC:
				{
				int LA90_5 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case ABSTRACT:
				{
				int LA90_6 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FINAL:
				{
				int LA90_7 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case NATIVE:
				{
				int LA90_8 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SYNCHRONIZED:
				{
				int LA90_9 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TRANSIENT:
				{
				int LA90_10 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case VOLATILE:
				{
				int LA90_11 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case STRICTFP:
				{
				int LA90_12 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}
				else if ( (synpred119_Java()) ) {
					alt90=3;
				}
				else if ( (synpred120_Java()) ) {
					alt90=4;
				}
				else if ( (synpred121_Java()) ) {
					alt90=5;
				}
				else if ( (synpred122_Java()) ) {
					alt90=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case IDENTIFIER:
				{
				int LA90_13 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 13, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case BOOLEAN:
				{
				int LA90_14 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 14, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case CHAR:
				{
				int LA90_15 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 15, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case BYTE:
				{
				int LA90_16 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 16, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SHORT:
				{
				int LA90_17 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 17, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case INT:
				{
				int LA90_18 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 18, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LONG:
				{
				int LA90_19 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 19, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FLOAT:
				{
				int LA90_20 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 20, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case DOUBLE:
				{
				int LA90_21 = input.LA(2);
				if ( (synpred117_Java()) ) {
					alt90=1;
				}
				else if ( (synpred118_Java()) ) {
					alt90=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 21, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case CLASS:
				{
				alt90=3;
				}
				break;
			case INTERFACE:
				{
				alt90=4;
				}
				break;
			case ENUM:
				{
				alt90=5;
				}
				break;
			case SEMI:
				{
				alt90=7;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 90, 0, input);
				throw nvae;
			}
			switch (alt90) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:823:9: annotationMethodDeclaration
					{
					pushFollow(FOLLOW_annotationMethodDeclaration_in_annotationTypeElementDeclaration4247);
					annotationMethodDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:824:9: interfaceFieldDeclaration
					{
					pushFollow(FOLLOW_interfaceFieldDeclaration_in_annotationTypeElementDeclaration4257);
					interfaceFieldDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:825:9: normalClassDeclaration
					{
					pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementDeclaration4267);
					normalClassDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:826:9: normalInterfaceDeclaration
					{
					pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementDeclaration4277);
					normalInterfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:827:9: enumDeclaration
					{
					pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementDeclaration4287);
					enumDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 6 :
					// /Users/byung/workspace/antlr2/Java.g:828:9: annotationTypeDeclaration
					{
					pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementDeclaration4297);
					annotationTypeDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 7 :
					// /Users/byung/workspace/antlr2/Java.g:829:9: ';'
					{
					match(input,SEMI,FOLLOW_SEMI_in_annotationTypeElementDeclaration4307); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 52, annotationTypeElementDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "annotationTypeElementDeclaration"



	// $ANTLR start "annotationMethodDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:832:1: annotationMethodDeclaration : modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';' ;
	public final void annotationMethodDeclaration() throws RecognitionException {
		int annotationMethodDeclaration_StartIndex = input.index();

		Token IDENTIFIER13=null;
		ParserRuleReturnScope type12 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:833:5: ( modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';' )
			// /Users/byung/workspace/antlr2/Java.g:833:9: modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';'
			{
			pushFollow(FOLLOW_modifiers_in_annotationMethodDeclaration4330);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_type_in_annotationMethodDeclaration4332);
			type12=type();
			state._fsp--;
			if (state.failed) return;
			IDENTIFIER13=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationMethodDeclaration4334); if (state.failed) return;
			if ( state.backtracking==0 ) {System.out.println("TYPe: " + (type12!=null?input.toString(type12.start,type12.stop):null));if(isPrimitive((type12!=null?input.toString(type12.start,type12.stop):null))) { uAdd((IDENTIFIER13!=null?IDENTIFIER13.getText():null));} }
			match(input,LPAREN,FOLLOW_LPAREN_in_annotationMethodDeclaration4346); if (state.failed) return;
			match(input,RPAREN,FOLLOW_RPAREN_in_annotationMethodDeclaration4348); if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:834:17: ( 'default' elementValue )?
			int alt91=2;
			int LA91_0 = input.LA(1);
			if ( (LA91_0==DEFAULT) ) {
				alt91=1;
			}
			switch (alt91) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:834:18: 'default' elementValue
					{
					match(input,DEFAULT,FOLLOW_DEFAULT_in_annotationMethodDeclaration4351); if (state.failed) return;
					pushFollow(FOLLOW_elementValue_in_annotationMethodDeclaration4353);
					elementValue();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("(");sAdd(")");kAdd("default");}
					}
					break;

			}

			match(input,SEMI,FOLLOW_SEMI_in_annotationMethodDeclaration4384); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd(";");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 53, annotationMethodDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "annotationMethodDeclaration"



	// $ANTLR start "block"
	// /Users/byung/workspace/antlr2/Java.g:839:1: block : '{' ( blockStatement )* '}' ;
	public final void block() throws RecognitionException {
		int block_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:840:5: ( '{' ( blockStatement )* '}' )
			// /Users/byung/workspace/antlr2/Java.g:840:9: '{' ( blockStatement )* '}'
			{
			match(input,LBRACE,FOLLOW_LBRACE_in_block4411); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("{");}
			// /Users/byung/workspace/antlr2/Java.g:841:9: ( blockStatement )*
			loop92:
			while (true) {
				int alt92=2;
				int LA92_0 = input.LA(1);
				if ( (LA92_0==ABSTRACT||(LA92_0 >= ASSERT && LA92_0 <= BANG)||(LA92_0 >= BOOLEAN && LA92_0 <= BYTE)||(LA92_0 >= CHAR && LA92_0 <= CLASS)||LA92_0==CONTINUE||LA92_0==DO||(LA92_0 >= DOUBLE && LA92_0 <= DOUBLELITERAL)||LA92_0==ENUM||(LA92_0 >= FALSE && LA92_0 <= FINAL)||(LA92_0 >= FLOAT && LA92_0 <= FOR)||(LA92_0 >= IDENTIFIER && LA92_0 <= IF)||(LA92_0 >= INT && LA92_0 <= INTLITERAL)||LA92_0==LBRACE||(LA92_0 >= LONG && LA92_0 <= LT)||(LA92_0 >= MONKEYS_AT && LA92_0 <= NULL)||LA92_0==PLUS||(LA92_0 >= PLUSPLUS && LA92_0 <= PUBLIC)||LA92_0==RETURN||(LA92_0 >= SEMI && LA92_0 <= SHORT)||(LA92_0 >= STATIC && LA92_0 <= SUB)||(LA92_0 >= SUBSUB && LA92_0 <= SYNCHRONIZED)||(LA92_0 >= THIS && LA92_0 <= THROW)||(LA92_0 >= TILDE && LA92_0 <= WHILE)) ) {
					alt92=1;
				}

				switch (alt92) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:841:10: blockStatement
					{
					pushFollow(FOLLOW_blockStatement_in_block4425);
					blockStatement();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop92;
				}
			}

			match(input,RBRACE,FOLLOW_RBRACE_in_block4446); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd("}");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 54, block_StartIndex); }

		}
	}
	// $ANTLR end "block"



	// $ANTLR start "blockStatement"
	// /Users/byung/workspace/antlr2/Java.g:870:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
	public final void blockStatement() throws RecognitionException {
		int blockStatement_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:871:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
			int alt93=3;
			switch ( input.LA(1) ) {
			case FINAL:
				{
				int LA93_1 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (synpred126_Java()) ) {
					alt93=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 93, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case MONKEYS_AT:
				{
				int LA93_2 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (synpred126_Java()) ) {
					alt93=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 93, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case IDENTIFIER:
				{
				int LA93_3 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case BOOLEAN:
				{
				int LA93_4 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case CHAR:
				{
				int LA93_5 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case BYTE:
				{
				int LA93_6 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case SHORT:
				{
				int LA93_7 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case INT:
				{
				int LA93_8 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case LONG:
				{
				int LA93_9 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case FLOAT:
				{
				int LA93_10 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case DOUBLE:
				{
				int LA93_11 = input.LA(2);
				if ( (synpred125_Java()) ) {
					alt93=1;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case ABSTRACT:
			case CLASS:
			case ENUM:
			case INTERFACE:
			case NATIVE:
			case PRIVATE:
			case PROTECTED:
			case PUBLIC:
			case STATIC:
			case STRICTFP:
			case TRANSIENT:
			case VOLATILE:
				{
				alt93=2;
				}
				break;
			case SYNCHRONIZED:
				{
				int LA93_18 = input.LA(2);
				if ( (synpred126_Java()) ) {
					alt93=2;
				}
				else if ( (true) ) {
					alt93=3;
				}

				}
				break;
			case ASSERT:
			case BANG:
			case BREAK:
			case CHARLITERAL:
			case CONTINUE:
			case DO:
			case DOUBLELITERAL:
			case FALSE:
			case FLOATLITERAL:
			case FOR:
			case IF:
			case INTLITERAL:
			case LBRACE:
			case LONGLITERAL:
			case LPAREN:
			case NEW:
			case NULL:
			case PLUS:
			case PLUSPLUS:
			case RETURN:
			case SEMI:
			case STRINGLITERAL:
			case SUB:
			case SUBSUB:
			case SUPER:
			case SWITCH:
			case THIS:
			case THROW:
			case TILDE:
			case TRUE:
			case TRY:
			case VOID:
			case WHILE:
				{
				alt93=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 93, 0, input);
				throw nvae;
			}
			switch (alt93) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:871:9: localVariableDeclarationStatement
					{
					pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatement4471);
					localVariableDeclarationStatement();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:872:9: classOrInterfaceDeclaration
					{
					pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement4481);
					classOrInterfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:873:9: statement
					{
					pushFollow(FOLLOW_statement_in_blockStatement4491);
					statement();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 55, blockStatement_StartIndex); }

		}
	}
	// $ANTLR end "blockStatement"



	// $ANTLR start "localVariableDeclarationStatement"
	// /Users/byung/workspace/antlr2/Java.g:877:1: localVariableDeclarationStatement : localVariableDeclaration ';' ;
	public final void localVariableDeclarationStatement() throws RecognitionException {
		int localVariableDeclarationStatement_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:878:5: ( localVariableDeclaration ';' )
			// /Users/byung/workspace/antlr2/Java.g:878:9: localVariableDeclaration ';'
			{
			pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4512);
			localVariableDeclaration();
			state._fsp--;
			if (state.failed) return;
			match(input,SEMI,FOLLOW_SEMI_in_localVariableDeclarationStatement4522); if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd(";");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 56, localVariableDeclarationStatement_StartIndex); }

		}
	}
	// $ANTLR end "localVariableDeclarationStatement"



	// $ANTLR start "localVariableDeclaration"
	// /Users/byung/workspace/antlr2/Java.g:882:1: localVariableDeclaration : variableModifiers type variableDeclarator ( ',' variableDeclarator )* ;
	public final void localVariableDeclaration() throws RecognitionException {
		int localVariableDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:883:5: ( variableModifiers type variableDeclarator ( ',' variableDeclarator )* )
			// /Users/byung/workspace/antlr2/Java.g:883:9: variableModifiers type variableDeclarator ( ',' variableDeclarator )*
			{
			pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration4545);
			variableModifiers();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_type_in_localVariableDeclaration4547);
			type();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_variableDeclarator_in_localVariableDeclaration4557);
			variableDeclarator();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:885:9: ( ',' variableDeclarator )*
			loop94:
			while (true) {
				int alt94=2;
				int LA94_0 = input.LA(1);
				if ( (LA94_0==COMMA) ) {
					alt94=1;
				}

				switch (alt94) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:885:10: ',' variableDeclarator
					{
					match(input,COMMA,FOLLOW_COMMA_in_localVariableDeclaration4568); if (state.failed) return;
					pushFollow(FOLLOW_variableDeclarator_in_localVariableDeclaration4570);
					variableDeclarator();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(",");}
					}
					break;

				default :
					break loop94;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 57, localVariableDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "localVariableDeclaration"



	// $ANTLR start "statement"
	// /Users/byung/workspace/antlr2/Java.g:889:1: statement : ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' );
	public final void statement() throws RecognitionException {
		int statement_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:890:5: ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' )
			int alt101=17;
			switch ( input.LA(1) ) {
			case LBRACE:
				{
				alt101=1;
				}
				break;
			case ASSERT:
				{
				int LA101_2 = input.LA(2);
				if ( (synpred130_Java()) ) {
					alt101=2;
				}
				else if ( (synpred132_Java()) ) {
					alt101=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 101, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case IF:
				{
				alt101=4;
				}
				break;
			case FOR:
				{
				alt101=5;
				}
				break;
			case WHILE:
				{
				alt101=6;
				}
				break;
			case DO:
				{
				alt101=7;
				}
				break;
			case TRY:
				{
				alt101=8;
				}
				break;
			case SWITCH:
				{
				alt101=9;
				}
				break;
			case SYNCHRONIZED:
				{
				alt101=10;
				}
				break;
			case RETURN:
				{
				alt101=11;
				}
				break;
			case THROW:
				{
				alt101=12;
				}
				break;
			case BREAK:
				{
				alt101=13;
				}
				break;
			case CONTINUE:
				{
				alt101=14;
				}
				break;
			case BANG:
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case CHARLITERAL:
			case DOUBLE:
			case DOUBLELITERAL:
			case FALSE:
			case FLOAT:
			case FLOATLITERAL:
			case INT:
			case INTLITERAL:
			case LONG:
			case LONGLITERAL:
			case LPAREN:
			case NEW:
			case NULL:
			case PLUS:
			case PLUSPLUS:
			case SHORT:
			case STRINGLITERAL:
			case SUB:
			case SUBSUB:
			case SUPER:
			case THIS:
			case TILDE:
			case TRUE:
			case VOID:
				{
				alt101=15;
				}
				break;
			case IDENTIFIER:
				{
				int LA101_22 = input.LA(2);
				if ( (synpred148_Java()) ) {
					alt101=15;
				}
				else if ( (synpred149_Java()) ) {
					alt101=16;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 101, 22, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SEMI:
				{
				alt101=17;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 101, 0, input);
				throw nvae;
			}
			switch (alt101) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:890:9: block
					{
					pushFollow(FOLLOW_block_in_statement4604);
					block();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:892:9: ( 'assert' ) expression ( ':' expression )? ';'
					{
					// /Users/byung/workspace/antlr2/Java.g:892:9: ( 'assert' )
					// /Users/byung/workspace/antlr2/Java.g:892:10: 'assert'
					{
					match(input,ASSERT,FOLLOW_ASSERT_in_statement4628); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("assert");}
					}

					pushFollow(FOLLOW_expression_in_statement4650);
					expression();
					state._fsp--;
					if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:894:20: ( ':' expression )?
					int alt95=2;
					int LA95_0 = input.LA(1);
					if ( (LA95_0==COLON) ) {
						alt95=1;
					}
					switch (alt95) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:894:21: ':' expression
							{
							match(input,COLON,FOLLOW_COLON_in_statement4653); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_statement4655);
							expression();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd(":");}
							}
							break;

					}

					match(input,SEMI,FOLLOW_SEMI_in_statement4661); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:895:9: 'assert' expression ( ':' expression )? ';'
					{
					match(input,ASSERT,FOLLOW_ASSERT_in_statement4673); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_statement4676);
					expression();
					state._fsp--;
					if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:895:30: ( ':' expression )?
					int alt96=2;
					int LA96_0 = input.LA(1);
					if ( (LA96_0==COLON) ) {
						alt96=1;
					}
					switch (alt96) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:895:31: ':' expression
							{
							match(input,COLON,FOLLOW_COLON_in_statement4679); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_statement4681);
							expression();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd(":");}
							}
							break;

					}

					match(input,SEMI,FOLLOW_SEMI_in_statement4687); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("assert");sAdd(";");}
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:896:9: 'if' parExpression statement ( 'else' statement )?
					{
					match(input,IF,FOLLOW_IF_in_statement4711); if (state.failed) return;
					pushFollow(FOLLOW_parExpression_in_statement4713);
					parExpression();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_statement_in_statement4715);
					statement();
					state._fsp--;
					if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:896:38: ( 'else' statement )?
					int alt97=2;
					int LA97_0 = input.LA(1);
					if ( (LA97_0==ELSE) ) {
						int LA97_1 = input.LA(2);
						if ( (synpred133_Java()) ) {
							alt97=1;
						}
					}
					switch (alt97) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:896:39: 'else' statement
							{
							match(input,ELSE,FOLLOW_ELSE_in_statement4718); if (state.failed) return;
							pushFollow(FOLLOW_statement_in_statement4720);
							statement();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) {kAdd("else");}
							}
							break;

					}

					if ( state.backtracking==0 ) {kAdd("if");}
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:897:9: forstatement
					{
					pushFollow(FOLLOW_forstatement_in_statement4745);
					forstatement();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 6 :
					// /Users/byung/workspace/antlr2/Java.g:898:9: 'while' parExpression statement
					{
					match(input,WHILE,FOLLOW_WHILE_in_statement4755); if (state.failed) return;
					pushFollow(FOLLOW_parExpression_in_statement4757);
					parExpression();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_statement_in_statement4759);
					statement();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("while");}
					}
					break;
				case 7 :
					// /Users/byung/workspace/antlr2/Java.g:899:9: 'do' statement 'while' parExpression ';'
					{
					match(input,DO,FOLLOW_DO_in_statement4771); if (state.failed) return;
					pushFollow(FOLLOW_statement_in_statement4773);
					statement();
					state._fsp--;
					if (state.failed) return;
					match(input,WHILE,FOLLOW_WHILE_in_statement4775); if (state.failed) return;
					pushFollow(FOLLOW_parExpression_in_statement4777);
					parExpression();
					state._fsp--;
					if (state.failed) return;
					match(input,SEMI,FOLLOW_SEMI_in_statement4779); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("do");kAdd("while");sAdd(";");}
					}
					break;
				case 8 :
					// /Users/byung/workspace/antlr2/Java.g:900:9: trystatement
					{
					pushFollow(FOLLOW_trystatement_in_statement4791);
					trystatement();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 9 :
					// /Users/byung/workspace/antlr2/Java.g:901:9: 'switch' parExpression '{' switchBlockStatementGroups '}'
					{
					match(input,SWITCH,FOLLOW_SWITCH_in_statement4801); if (state.failed) return;
					pushFollow(FOLLOW_parExpression_in_statement4803);
					parExpression();
					state._fsp--;
					if (state.failed) return;
					match(input,LBRACE,FOLLOW_LBRACE_in_statement4805); if (state.failed) return;
					pushFollow(FOLLOW_switchBlockStatementGroups_in_statement4807);
					switchBlockStatementGroups();
					state._fsp--;
					if (state.failed) return;
					match(input,RBRACE,FOLLOW_RBRACE_in_statement4809); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("switch");sAdd("{");sAdd("}");}
					}
					break;
				case 10 :
					// /Users/byung/workspace/antlr2/Java.g:902:9: 'synchronized' parExpression block
					{
					match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_statement4821); if (state.failed) return;
					pushFollow(FOLLOW_parExpression_in_statement4823);
					parExpression();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_block_in_statement4825);
					block();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("synchronized");}
					}
					break;
				case 11 :
					// /Users/byung/workspace/antlr2/Java.g:903:9: 'return' ( expression )? ';'
					{
					match(input,RETURN,FOLLOW_RETURN_in_statement4837); if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:903:18: ( expression )?
					int alt98=2;
					int LA98_0 = input.LA(1);
					if ( (LA98_0==BANG||LA98_0==BOOLEAN||LA98_0==BYTE||(LA98_0 >= CHAR && LA98_0 <= CHARLITERAL)||(LA98_0 >= DOUBLE && LA98_0 <= DOUBLELITERAL)||LA98_0==FALSE||(LA98_0 >= FLOAT && LA98_0 <= FLOATLITERAL)||LA98_0==IDENTIFIER||LA98_0==INT||LA98_0==INTLITERAL||(LA98_0 >= LONG && LA98_0 <= LPAREN)||(LA98_0 >= NEW && LA98_0 <= NULL)||LA98_0==PLUS||LA98_0==PLUSPLUS||LA98_0==SHORT||(LA98_0 >= STRINGLITERAL && LA98_0 <= SUB)||(LA98_0 >= SUBSUB && LA98_0 <= SUPER)||LA98_0==THIS||LA98_0==TILDE||LA98_0==TRUE||LA98_0==VOID) ) {
						alt98=1;
					}
					switch (alt98) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:903:19: expression
							{
							pushFollow(FOLLOW_expression_in_statement4840);
							expression();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,SEMI,FOLLOW_SEMI_in_statement4845); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("return");sAdd(";");}
					}
					break;
				case 12 :
					// /Users/byung/workspace/antlr2/Java.g:904:9: 'throw' expression ';'
					{
					match(input,THROW,FOLLOW_THROW_in_statement4857); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_statement4859);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,SEMI,FOLLOW_SEMI_in_statement4861); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("throw");sAdd(";");}
					}
					break;
				case 13 :
					// /Users/byung/workspace/antlr2/Java.g:905:9: 'break' ( IDENTIFIER )? ';'
					{
					match(input,BREAK,FOLLOW_BREAK_in_statement4873); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("break");}
					// /Users/byung/workspace/antlr2/Java.g:906:13: ( IDENTIFIER )?
					int alt99=2;
					int LA99_0 = input.LA(1);
					if ( (LA99_0==IDENTIFIER) ) {
						alt99=1;
					}
					switch (alt99) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:906:14: IDENTIFIER
							{
							match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4890); if (state.failed) return;
							}
							break;

					}

					match(input,SEMI,FOLLOW_SEMI_in_statement4907); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					}
					break;
				case 14 :
					// /Users/byung/workspace/antlr2/Java.g:908:9: 'continue' ( IDENTIFIER )? ';'
					{
					match(input,CONTINUE,FOLLOW_CONTINUE_in_statement4919); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("continue");}
					// /Users/byung/workspace/antlr2/Java.g:909:13: ( IDENTIFIER )?
					int alt100=2;
					int LA100_0 = input.LA(1);
					if ( (LA100_0==IDENTIFIER) ) {
						alt100=1;
					}
					switch (alt100) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:909:14: IDENTIFIER
							{
							match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4936); if (state.failed) return;
							}
							break;

					}

					match(input,SEMI,FOLLOW_SEMI_in_statement4953); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					}
					break;
				case 15 :
					// /Users/byung/workspace/antlr2/Java.g:911:9: expression ';'
					{
					pushFollow(FOLLOW_expression_in_statement4965);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,SEMI,FOLLOW_SEMI_in_statement4968); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					}
					break;
				case 16 :
					// /Users/byung/workspace/antlr2/Java.g:912:9: IDENTIFIER ':' statement
					{
					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4984); if (state.failed) return;
					match(input,COLON,FOLLOW_COLON_in_statement4986); if (state.failed) return;
					pushFollow(FOLLOW_statement_in_statement4988);
					statement();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {System.out.println(":1 ");sAdd(":");}
					}
					break;
				case 17 :
					// /Users/byung/workspace/antlr2/Java.g:913:9: ';'
					{
					match(input,SEMI,FOLLOW_SEMI_in_statement5000); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 58, statement_StartIndex); }

		}
	}
	// $ANTLR end "statement"



	// $ANTLR start "switchBlockStatementGroups"
	// /Users/byung/workspace/antlr2/Java.g:917:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
	public final void switchBlockStatementGroups() throws RecognitionException {
		int switchBlockStatementGroups_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:918:5: ( ( switchBlockStatementGroup )* )
			// /Users/byung/workspace/antlr2/Java.g:918:9: ( switchBlockStatementGroup )*
			{
			// /Users/byung/workspace/antlr2/Java.g:918:9: ( switchBlockStatementGroup )*
			loop102:
			while (true) {
				int alt102=2;
				int LA102_0 = input.LA(1);
				if ( (LA102_0==CASE||LA102_0==DEFAULT) ) {
					alt102=1;
				}

				switch (alt102) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:918:10: switchBlockStatementGroup
					{
					pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups5024);
					switchBlockStatementGroup();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop102;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 59, switchBlockStatementGroups_StartIndex); }

		}
	}
	// $ANTLR end "switchBlockStatementGroups"



	// $ANTLR start "switchBlockStatementGroup"
	// /Users/byung/workspace/antlr2/Java.g:921:1: switchBlockStatementGroup : switchLabel ( blockStatement )* ;
	public final void switchBlockStatementGroup() throws RecognitionException {
		int switchBlockStatementGroup_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:922:5: ( switchLabel ( blockStatement )* )
			// /Users/byung/workspace/antlr2/Java.g:923:9: switchLabel ( blockStatement )*
			{
			pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup5053);
			switchLabel();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:924:9: ( blockStatement )*
			loop103:
			while (true) {
				int alt103=2;
				int LA103_0 = input.LA(1);
				if ( (LA103_0==ABSTRACT||(LA103_0 >= ASSERT && LA103_0 <= BANG)||(LA103_0 >= BOOLEAN && LA103_0 <= BYTE)||(LA103_0 >= CHAR && LA103_0 <= CLASS)||LA103_0==CONTINUE||LA103_0==DO||(LA103_0 >= DOUBLE && LA103_0 <= DOUBLELITERAL)||LA103_0==ENUM||(LA103_0 >= FALSE && LA103_0 <= FINAL)||(LA103_0 >= FLOAT && LA103_0 <= FOR)||(LA103_0 >= IDENTIFIER && LA103_0 <= IF)||(LA103_0 >= INT && LA103_0 <= INTLITERAL)||LA103_0==LBRACE||(LA103_0 >= LONG && LA103_0 <= LT)||(LA103_0 >= MONKEYS_AT && LA103_0 <= NULL)||LA103_0==PLUS||(LA103_0 >= PLUSPLUS && LA103_0 <= PUBLIC)||LA103_0==RETURN||(LA103_0 >= SEMI && LA103_0 <= SHORT)||(LA103_0 >= STATIC && LA103_0 <= SUB)||(LA103_0 >= SUBSUB && LA103_0 <= SYNCHRONIZED)||(LA103_0 >= THIS && LA103_0 <= THROW)||(LA103_0 >= TILDE && LA103_0 <= WHILE)) ) {
					alt103=1;
				}

				switch (alt103) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:924:10: blockStatement
					{
					pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup5064);
					blockStatement();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop103;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 60, switchBlockStatementGroup_StartIndex); }

		}
	}
	// $ANTLR end "switchBlockStatementGroup"



	// $ANTLR start "switchLabel"
	// /Users/byung/workspace/antlr2/Java.g:928:1: switchLabel : ( 'case' expression ':' | 'default' ':' );
	public final void switchLabel() throws RecognitionException {
		int switchLabel_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:929:5: ( 'case' expression ':' | 'default' ':' )
			int alt104=2;
			int LA104_0 = input.LA(1);
			if ( (LA104_0==CASE) ) {
				alt104=1;
			}
			else if ( (LA104_0==DEFAULT) ) {
				alt104=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 104, 0, input);
				throw nvae;
			}

			switch (alt104) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:929:9: 'case' expression ':'
					{
					match(input,CASE,FOLLOW_CASE_in_switchLabel5095); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_switchLabel5097);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,COLON,FOLLOW_COLON_in_switchLabel5099); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("case");sAdd(":");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:930:9: 'default' ':'
					{
					match(input,DEFAULT,FOLLOW_DEFAULT_in_switchLabel5110); if (state.failed) return;
					match(input,COLON,FOLLOW_COLON_in_switchLabel5112); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("default");sAdd(":");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 61, switchLabel_StartIndex); }

		}
	}
	// $ANTLR end "switchLabel"



	// $ANTLR start "trystatement"
	// /Users/byung/workspace/antlr2/Java.g:934:1: trystatement : 'try' block ( catches 'finally' block | catches | 'finally' block ) ;
	public final void trystatement() throws RecognitionException {
		int trystatement_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:935:5: ( 'try' block ( catches 'finally' block | catches | 'finally' block ) )
			// /Users/byung/workspace/antlr2/Java.g:935:9: 'try' block ( catches 'finally' block | catches | 'finally' block )
			{
			match(input,TRY,FOLLOW_TRY_in_trystatement5135); if (state.failed) return;
			pushFollow(FOLLOW_block_in_trystatement5137);
			block();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {kAdd("try");}
			// /Users/byung/workspace/antlr2/Java.g:936:9: ( catches 'finally' block | catches | 'finally' block )
			int alt105=3;
			int LA105_0 = input.LA(1);
			if ( (LA105_0==CATCH) ) {
				int LA105_1 = input.LA(2);
				if ( (synpred153_Java()) ) {
					alt105=1;
				}
				else if ( (synpred154_Java()) ) {
					alt105=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 105, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA105_0==FINALLY) ) {
				alt105=3;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 105, 0, input);
				throw nvae;
			}

			switch (alt105) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:936:13: catches 'finally' block
					{
					pushFollow(FOLLOW_catches_in_trystatement5153);
					catches();
					state._fsp--;
					if (state.failed) return;
					match(input,FINALLY,FOLLOW_FINALLY_in_trystatement5155); if (state.failed) return;
					pushFollow(FOLLOW_block_in_trystatement5157);
					block();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("finally");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:937:13: catches
					{
					pushFollow(FOLLOW_catches_in_trystatement5173);
					catches();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:938:13: 'finally' block
					{
					match(input,FINALLY,FOLLOW_FINALLY_in_trystatement5187); if (state.failed) return;
					pushFollow(FOLLOW_block_in_trystatement5189);
					block();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("finally");}
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 62, trystatement_StartIndex); }

		}
	}
	// $ANTLR end "trystatement"



	// $ANTLR start "catches"
	// /Users/byung/workspace/antlr2/Java.g:942:1: catches : catchClause ( catchClause )* ;
	public final void catches() throws RecognitionException {
		int catches_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:943:5: ( catchClause ( catchClause )* )
			// /Users/byung/workspace/antlr2/Java.g:943:9: catchClause ( catchClause )*
			{
			pushFollow(FOLLOW_catchClause_in_catches5222);
			catchClause();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:944:9: ( catchClause )*
			loop106:
			while (true) {
				int alt106=2;
				int LA106_0 = input.LA(1);
				if ( (LA106_0==CATCH) ) {
					alt106=1;
				}

				switch (alt106) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:944:10: catchClause
					{
					pushFollow(FOLLOW_catchClause_in_catches5233);
					catchClause();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop106;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 63, catches_StartIndex); }

		}
	}
	// $ANTLR end "catches"



	// $ANTLR start "catchClause"
	// /Users/byung/workspace/antlr2/Java.g:948:1: catchClause : 'catch' '(' formalParameter ')' block ;
	public final void catchClause() throws RecognitionException {
		int catchClause_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:949:5: ( 'catch' '(' formalParameter ')' block )
			// /Users/byung/workspace/antlr2/Java.g:949:9: 'catch' '(' formalParameter ')' block
			{
			match(input,CATCH,FOLLOW_CATCH_in_catchClause5264); if (state.failed) return;
			match(input,LPAREN,FOLLOW_LPAREN_in_catchClause5266); if (state.failed) return;
			pushFollow(FOLLOW_formalParameter_in_catchClause5268);
			formalParameter();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {kAdd("catch"); sAdd("(");}
			match(input,RPAREN,FOLLOW_RPAREN_in_catchClause5280); if (state.failed) return;
			pushFollow(FOLLOW_block_in_catchClause5282);
			block();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {sAdd(")");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 64, catchClause_StartIndex); }

		}
	}
	// $ANTLR end "catchClause"



	// $ANTLR start "formalParameter"
	// /Users/byung/workspace/antlr2/Java.g:953:1: formalParameter : variableModifiers type IDENTIFIER ( '[' ']' )* ;
	public final void formalParameter() throws RecognitionException {
		int formalParameter_StartIndex = input.index();

		Token IDENTIFIER15=null;
		ParserRuleReturnScope type14 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:954:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* )
			// /Users/byung/workspace/antlr2/Java.g:954:9: variableModifiers type IDENTIFIER ( '[' ']' )*
			{
			pushFollow(FOLLOW_variableModifiers_in_formalParameter5304);
			variableModifiers();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_type_in_formalParameter5306);
			type14=type();
			state._fsp--;
			if (state.failed) return;
			IDENTIFIER15=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_formalParameter5308); if (state.failed) return;
			if ( state.backtracking==0 ) {System.out.println("TYPe: " + (type14!=null?input.toString(type14.start,type14.stop):null) + " ");if(isPrimitive((type14!=null?input.toString(type14.start,type14.stop):null))) { uAdd((IDENTIFIER15!=null?IDENTIFIER15.getText():null));} }
			// /Users/byung/workspace/antlr2/Java.g:955:9: ( '[' ']' )*
			loop107:
			while (true) {
				int alt107=2;
				int LA107_0 = input.LA(1);
				if ( (LA107_0==LBRACKET) ) {
					alt107=1;
				}

				switch (alt107) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:955:10: '[' ']'
					{
					match(input,LBRACKET,FOLLOW_LBRACKET_in_formalParameter5321); if (state.failed) return;
					match(input,RBRACKET,FOLLOW_RBRACKET_in_formalParameter5323); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("[");sAdd("]");}
					}
					break;

				default :
					break loop107;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 65, formalParameter_StartIndex); }

		}
	}
	// $ANTLR end "formalParameter"



	// $ANTLR start "forstatement"
	// /Users/byung/workspace/antlr2/Java.g:959:1: forstatement : ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement );
	public final void forstatement() throws RecognitionException {
		int forstatement_StartIndex = input.index();

		Token IDENTIFIER17=null;
		ParserRuleReturnScope type16 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:960:5: ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement )
			int alt111=2;
			int LA111_0 = input.LA(1);
			if ( (LA111_0==FOR) ) {
				int LA111_1 = input.LA(2);
				if ( (synpred157_Java()) ) {
					alt111=1;
				}
				else if ( (true) ) {
					alt111=2;
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 111, 0, input);
				throw nvae;
			}

			switch (alt111) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:962:9: 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement
					{
					match(input,FOR,FOLLOW_FOR_in_forstatement5373); if (state.failed) return;
					match(input,LPAREN,FOLLOW_LPAREN_in_forstatement5375); if (state.failed) return;
					pushFollow(FOLLOW_variableModifiers_in_forstatement5377);
					variableModifiers();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_type_in_forstatement5379);
					type16=type();
					state._fsp--;
					if (state.failed) return;
					IDENTIFIER17=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_forstatement5381); if (state.failed) return;
					match(input,COLON,FOLLOW_COLON_in_forstatement5383); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("for"); sAdd("(");sAdd(":");}
					if ( state.backtracking==0 ) {System.out.println("TYPe: " + (type16!=null?input.toString(type16.start,type16.stop):null));if(isPrimitive((type16!=null?input.toString(type16.start,type16.stop):null))) { uAdd((IDENTIFIER17!=null?IDENTIFIER17.getText():null));} }
					pushFollow(FOLLOW_expression_in_forstatement5398);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,RPAREN,FOLLOW_RPAREN_in_forstatement5400); if (state.failed) return;
					pushFollow(FOLLOW_statement_in_forstatement5402);
					statement();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(")");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:966:9: 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement
					{
					match(input,FOR,FOLLOW_FOR_in_forstatement5436); if (state.failed) return;
					match(input,LPAREN,FOLLOW_LPAREN_in_forstatement5438); if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("for"); sAdd("(");}
					// /Users/byung/workspace/antlr2/Java.g:967:17: ( forInit )?
					int alt108=2;
					int LA108_0 = input.LA(1);
					if ( (LA108_0==BANG||LA108_0==BOOLEAN||LA108_0==BYTE||(LA108_0 >= CHAR && LA108_0 <= CHARLITERAL)||(LA108_0 >= DOUBLE && LA108_0 <= DOUBLELITERAL)||(LA108_0 >= FALSE && LA108_0 <= FINAL)||(LA108_0 >= FLOAT && LA108_0 <= FLOATLITERAL)||LA108_0==IDENTIFIER||LA108_0==INT||LA108_0==INTLITERAL||(LA108_0 >= LONG && LA108_0 <= LPAREN)||LA108_0==MONKEYS_AT||(LA108_0 >= NEW && LA108_0 <= NULL)||LA108_0==PLUS||LA108_0==PLUSPLUS||LA108_0==SHORT||(LA108_0 >= STRINGLITERAL && LA108_0 <= SUB)||(LA108_0 >= SUBSUB && LA108_0 <= SUPER)||LA108_0==THIS||LA108_0==TILDE||LA108_0==TRUE||LA108_0==VOID) ) {
						alt108=1;
					}
					switch (alt108) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:967:18: forInit
							{
							pushFollow(FOLLOW_forInit_in_forstatement5460);
							forInit();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,SEMI,FOLLOW_SEMI_in_forstatement5481); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					// /Users/byung/workspace/antlr2/Java.g:969:17: ( expression )?
					int alt109=2;
					int LA109_0 = input.LA(1);
					if ( (LA109_0==BANG||LA109_0==BOOLEAN||LA109_0==BYTE||(LA109_0 >= CHAR && LA109_0 <= CHARLITERAL)||(LA109_0 >= DOUBLE && LA109_0 <= DOUBLELITERAL)||LA109_0==FALSE||(LA109_0 >= FLOAT && LA109_0 <= FLOATLITERAL)||LA109_0==IDENTIFIER||LA109_0==INT||LA109_0==INTLITERAL||(LA109_0 >= LONG && LA109_0 <= LPAREN)||(LA109_0 >= NEW && LA109_0 <= NULL)||LA109_0==PLUS||LA109_0==PLUSPLUS||LA109_0==SHORT||(LA109_0 >= STRINGLITERAL && LA109_0 <= SUB)||(LA109_0 >= SUBSUB && LA109_0 <= SUPER)||LA109_0==THIS||LA109_0==TILDE||LA109_0==TRUE||LA109_0==VOID) ) {
						alt109=1;
					}
					switch (alt109) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:969:18: expression
							{
							pushFollow(FOLLOW_expression_in_forstatement5502);
							expression();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,SEMI,FOLLOW_SEMI_in_forstatement5523); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(";");}
					// /Users/byung/workspace/antlr2/Java.g:971:17: ( expressionList )?
					int alt110=2;
					int LA110_0 = input.LA(1);
					if ( (LA110_0==BANG||LA110_0==BOOLEAN||LA110_0==BYTE||(LA110_0 >= CHAR && LA110_0 <= CHARLITERAL)||(LA110_0 >= DOUBLE && LA110_0 <= DOUBLELITERAL)||LA110_0==FALSE||(LA110_0 >= FLOAT && LA110_0 <= FLOATLITERAL)||LA110_0==IDENTIFIER||LA110_0==INT||LA110_0==INTLITERAL||(LA110_0 >= LONG && LA110_0 <= LPAREN)||(LA110_0 >= NEW && LA110_0 <= NULL)||LA110_0==PLUS||LA110_0==PLUSPLUS||LA110_0==SHORT||(LA110_0 >= STRINGLITERAL && LA110_0 <= SUB)||(LA110_0 >= SUBSUB && LA110_0 <= SUPER)||LA110_0==THIS||LA110_0==TILDE||LA110_0==TRUE||LA110_0==VOID) ) {
						alt110=1;
					}
					switch (alt110) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:971:18: expressionList
							{
							pushFollow(FOLLOW_expressionList_in_forstatement5544);
							expressionList();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,RPAREN,FOLLOW_RPAREN_in_forstatement5565); if (state.failed) return;
					pushFollow(FOLLOW_statement_in_forstatement5567);
					statement();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(")");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 66, forstatement_StartIndex); }

		}
	}
	// $ANTLR end "forstatement"



	// $ANTLR start "forInit"
	// /Users/byung/workspace/antlr2/Java.g:975:1: forInit : ( localVariableDeclaration | expressionList );
	public final void forInit() throws RecognitionException {
		int forInit_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:976:5: ( localVariableDeclaration | expressionList )
			int alt112=2;
			switch ( input.LA(1) ) {
			case FINAL:
			case MONKEYS_AT:
				{
				alt112=1;
				}
				break;
			case IDENTIFIER:
				{
				int LA112_3 = input.LA(2);
				if ( (synpred161_Java()) ) {
					alt112=1;
				}
				else if ( (true) ) {
					alt112=2;
				}

				}
				break;
			case BOOLEAN:
				{
				int LA112_4 = input.LA(2);
				if ( (synpred161_Java()) ) {
					alt112=1;
				}
				else if ( (true) ) {
					alt112=2;
				}

				}
				break;
			case CHAR:
				{
				int LA112_5 = input.LA(2);
				if ( (synpred161_Java()) ) {
					alt112=1;
				}
				else if ( (true) ) {
					alt112=2;
				}

				}
				break;
			case BYTE:
				{
				int LA112_6 = input.LA(2);
				if ( (synpred161_Java()) ) {
					alt112=1;
				}
				else if ( (true) ) {
					alt112=2;
				}

				}
				break;
			case SHORT:
				{
				int LA112_7 = input.LA(2);
				if ( (synpred161_Java()) ) {
					alt112=1;
				}
				else if ( (true) ) {
					alt112=2;
				}

				}
				break;
			case INT:
				{
				int LA112_8 = input.LA(2);
				if ( (synpred161_Java()) ) {
					alt112=1;
				}
				else if ( (true) ) {
					alt112=2;
				}

				}
				break;
			case LONG:
				{
				int LA112_9 = input.LA(2);
				if ( (synpred161_Java()) ) {
					alt112=1;
				}
				else if ( (true) ) {
					alt112=2;
				}

				}
				break;
			case FLOAT:
				{
				int LA112_10 = input.LA(2);
				if ( (synpred161_Java()) ) {
					alt112=1;
				}
				else if ( (true) ) {
					alt112=2;
				}

				}
				break;
			case DOUBLE:
				{
				int LA112_11 = input.LA(2);
				if ( (synpred161_Java()) ) {
					alt112=1;
				}
				else if ( (true) ) {
					alt112=2;
				}

				}
				break;
			case BANG:
			case CHARLITERAL:
			case DOUBLELITERAL:
			case FALSE:
			case FLOATLITERAL:
			case INTLITERAL:
			case LONGLITERAL:
			case LPAREN:
			case NEW:
			case NULL:
			case PLUS:
			case PLUSPLUS:
			case STRINGLITERAL:
			case SUB:
			case SUBSUB:
			case SUPER:
			case THIS:
			case TILDE:
			case TRUE:
			case VOID:
				{
				alt112=2;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 112, 0, input);
				throw nvae;
			}
			switch (alt112) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:976:9: localVariableDeclaration
					{
					pushFollow(FOLLOW_localVariableDeclaration_in_forInit5589);
					localVariableDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:977:9: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_forInit5599);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 67, forInit_StartIndex); }

		}
	}
	// $ANTLR end "forInit"


	public static class parExpression_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "parExpression"
	// /Users/byung/workspace/antlr2/Java.g:980:1: parExpression : '(' expression ')' ;
	public final JavaParser.parExpression_return parExpression() throws RecognitionException {
		JavaParser.parExpression_return retval = new JavaParser.parExpression_return();
		retval.start = input.LT(1);
		int parExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }

			// /Users/byung/workspace/antlr2/Java.g:981:5: ( '(' expression ')' )
			// /Users/byung/workspace/antlr2/Java.g:981:9: '(' expression ')'
			{
			match(input,LPAREN,FOLLOW_LPAREN_in_parExpression5619); if (state.failed) return retval;
			pushFollow(FOLLOW_expression_in_parExpression5621);
			expression();
			state._fsp--;
			if (state.failed) return retval;
			match(input,RPAREN,FOLLOW_RPAREN_in_parExpression5623); if (state.failed) return retval;
			if ( state.backtracking==0 ) {sAdd("(");sAdd(")");}
			if ( state.backtracking==0 ) { System.out.println("parEx: " + input.toString(retval.start,input.LT(-1)));}
			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 68, parExpression_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "parExpression"



	// $ANTLR start "expressionList"
	// /Users/byung/workspace/antlr2/Java.g:984:1: expressionList : expression ( ',' expression )* ;
	public final void expressionList() throws RecognitionException {
		int expressionList_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:985:5: ( expression ( ',' expression )* )
			// /Users/byung/workspace/antlr2/Java.g:985:9: expression ( ',' expression )*
			{
			pushFollow(FOLLOW_expression_in_expressionList5647);
			expression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:986:9: ( ',' expression )*
			loop113:
			while (true) {
				int alt113=2;
				int LA113_0 = input.LA(1);
				if ( (LA113_0==COMMA) ) {
					alt113=1;
				}

				switch (alt113) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:986:10: ',' expression
					{
					match(input,COMMA,FOLLOW_COMMA_in_expressionList5659); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_expressionList5661);
					expression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(",");}
					}
					break;

				default :
					break loop113;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 69, expressionList_StartIndex); }

		}
	}
	// $ANTLR end "expressionList"



	// $ANTLR start "expression"
	// /Users/byung/workspace/antlr2/Java.g:991:1: expression : conditionalExpression ( assignmentOperator expression )? ;
	public final void expression() throws RecognitionException {
		int expression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:992:5: ( conditionalExpression ( assignmentOperator expression )? )
			// /Users/byung/workspace/antlr2/Java.g:992:9: conditionalExpression ( assignmentOperator expression )?
			{
			pushFollow(FOLLOW_conditionalExpression_in_expression5695);
			conditionalExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:994:9: ( assignmentOperator expression )?
			int alt114=2;
			int LA114_0 = input.LA(1);
			if ( (LA114_0==AMPEQ||LA114_0==BAREQ||LA114_0==CARETEQ||LA114_0==EQ||LA114_0==GT||LA114_0==LT||LA114_0==PERCENTEQ||LA114_0==PLUSEQ||LA114_0==SLASHEQ||LA114_0==STAREQ||LA114_0==SUBEQ) ) {
				alt114=1;
			}
			switch (alt114) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:994:10: assignmentOperator expression
					{
					pushFollow(FOLLOW_assignmentOperator_in_expression5709);
					assignmentOperator();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_expression_in_expression5711);
					expression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 70, expression_StartIndex); }

		}
	}
	// $ANTLR end "expression"



	// $ANTLR start "assignmentOperator"
	// /Users/byung/workspace/antlr2/Java.g:999:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' );
	public final void assignmentOperator() throws RecognitionException {
		int assignmentOperator_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1000:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' )
			int alt115=12;
			switch ( input.LA(1) ) {
			case EQ:
				{
				alt115=1;
				}
				break;
			case PLUSEQ:
				{
				alt115=2;
				}
				break;
			case SUBEQ:
				{
				alt115=3;
				}
				break;
			case STAREQ:
				{
				alt115=4;
				}
				break;
			case SLASHEQ:
				{
				alt115=5;
				}
				break;
			case AMPEQ:
				{
				alt115=6;
				}
				break;
			case BAREQ:
				{
				alt115=7;
				}
				break;
			case CARETEQ:
				{
				alt115=8;
				}
				break;
			case PERCENTEQ:
				{
				alt115=9;
				}
				break;
			case LT:
				{
				alt115=10;
				}
				break;
			case GT:
				{
				int LA115_11 = input.LA(2);
				if ( (LA115_11==GT) ) {
					int LA115_12 = input.LA(3);
					if ( (LA115_12==GT) ) {
						alt115=11;
					}
					else if ( (LA115_12==EQ) ) {
						alt115=12;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 115, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 115, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 115, 0, input);
				throw nvae;
			}
			switch (alt115) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1000:9: '='
					{
					match(input,EQ,FOLLOW_EQ_in_assignmentOperator5744); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("=");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1001:9: '+='
					{
					match(input,PLUSEQ,FOLLOW_PLUSEQ_in_assignmentOperator5756); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("+");sAdd("=");}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1002:9: '-='
					{
					match(input,SUBEQ,FOLLOW_SUBEQ_in_assignmentOperator5768); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("-");sAdd("=");}
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:1003:9: '*='
					{
					match(input,STAREQ,FOLLOW_STAREQ_in_assignmentOperator5780); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("*");sAdd("=");}
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:1004:9: '/='
					{
					match(input,SLASHEQ,FOLLOW_SLASHEQ_in_assignmentOperator5792); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("/");sAdd("=");}
					}
					break;
				case 6 :
					// /Users/byung/workspace/antlr2/Java.g:1005:9: '&='
					{
					match(input,AMPEQ,FOLLOW_AMPEQ_in_assignmentOperator5804); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("&");sAdd("=");}
					}
					break;
				case 7 :
					// /Users/byung/workspace/antlr2/Java.g:1006:9: '|='
					{
					match(input,BAREQ,FOLLOW_BAREQ_in_assignmentOperator5816); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("|");sAdd("=");}
					}
					break;
				case 8 :
					// /Users/byung/workspace/antlr2/Java.g:1007:9: '^='
					{
					match(input,CARETEQ,FOLLOW_CARETEQ_in_assignmentOperator5829); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("^");sAdd("=");}
					}
					break;
				case 9 :
					// /Users/byung/workspace/antlr2/Java.g:1008:9: '%='
					{
					match(input,PERCENTEQ,FOLLOW_PERCENTEQ_in_assignmentOperator5841); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("%");sAdd("=");}
					}
					break;
				case 10 :
					// /Users/byung/workspace/antlr2/Java.g:1009:10: '<' '<' '='
					{
					match(input,LT,FOLLOW_LT_in_assignmentOperator5854); if (state.failed) return;
					match(input,LT,FOLLOW_LT_in_assignmentOperator5856); if (state.failed) return;
					match(input,EQ,FOLLOW_EQ_in_assignmentOperator5858); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("<");sAdd("<");sAdd("=");}
					}
					break;
				case 11 :
					// /Users/byung/workspace/antlr2/Java.g:1010:10: '>' '>' '>' '='
					{
					match(input,GT,FOLLOW_GT_in_assignmentOperator5871); if (state.failed) return;
					match(input,GT,FOLLOW_GT_in_assignmentOperator5873); if (state.failed) return;
					match(input,GT,FOLLOW_GT_in_assignmentOperator5875); if (state.failed) return;
					match(input,EQ,FOLLOW_EQ_in_assignmentOperator5877); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(">");sAdd(">");sAdd(">");sAdd("=");}
					}
					break;
				case 12 :
					// /Users/byung/workspace/antlr2/Java.g:1011:10: '>' '>' '='
					{
					match(input,GT,FOLLOW_GT_in_assignmentOperator5890); if (state.failed) return;
					match(input,GT,FOLLOW_GT_in_assignmentOperator5892); if (state.failed) return;
					match(input,EQ,FOLLOW_EQ_in_assignmentOperator5894); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(">");sAdd(">");sAdd("=");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 71, assignmentOperator_StartIndex); }

		}
	}
	// $ANTLR end "assignmentOperator"



	// $ANTLR start "conditionalExpression"
	// /Users/byung/workspace/antlr2/Java.g:1015:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
	public final void conditionalExpression() throws RecognitionException {
		int conditionalExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1016:5: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
			// /Users/byung/workspace/antlr2/Java.g:1016:9: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
			{
			pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression5917);
			conditionalOrExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1017:9: ( '?' expression ':' conditionalExpression )?
			int alt116=2;
			int LA116_0 = input.LA(1);
			if ( (LA116_0==QUES) ) {
				alt116=1;
			}
			switch (alt116) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1017:10: '?' expression ':' conditionalExpression
					{
					match(input,QUES,FOLLOW_QUES_in_conditionalExpression5929); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_conditionalExpression5931);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,COLON,FOLLOW_COLON_in_conditionalExpression5933); if (state.failed) return;
					pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression5935);
					conditionalExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("?");sAdd(":");}
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 72, conditionalExpression_StartIndex); }

		}
	}
	// $ANTLR end "conditionalExpression"



	// $ANTLR start "conditionalOrExpression"
	// /Users/byung/workspace/antlr2/Java.g:1021:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
	public final void conditionalOrExpression() throws RecognitionException {
		int conditionalOrExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1022:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1022:9: conditionalAndExpression ( '||' conditionalAndExpression )*
			{
			pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5969);
			conditionalAndExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1023:9: ( '||' conditionalAndExpression )*
			loop117:
			while (true) {
				int alt117=2;
				int LA117_0 = input.LA(1);
				if ( (LA117_0==BARBAR) ) {
					alt117=1;
				}

				switch (alt117) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1023:10: '||' conditionalAndExpression
					{
					match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression5980); if (state.failed) return;
					pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5982);
					conditionalAndExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("||");}
					}
					break;

				default :
					break loop117;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 73, conditionalOrExpression_StartIndex); }

		}
	}
	// $ANTLR end "conditionalOrExpression"



	// $ANTLR start "conditionalAndExpression"
	// /Users/byung/workspace/antlr2/Java.g:1027:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
	public final void conditionalAndExpression() throws RecognitionException {
		int conditionalAndExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1028:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1028:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
			{
			pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression6015);
			inclusiveOrExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1029:9: ( '&&' inclusiveOrExpression )*
			loop118:
			while (true) {
				int alt118=2;
				int LA118_0 = input.LA(1);
				if ( (LA118_0==AMPAMP) ) {
					alt118=1;
				}

				switch (alt118) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1029:10: '&&' inclusiveOrExpression
					{
					match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression6026); if (state.failed) return;
					pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression6028);
					inclusiveOrExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("&&");}
					}
					break;

				default :
					break loop118;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 74, conditionalAndExpression_StartIndex); }

		}
	}
	// $ANTLR end "conditionalAndExpression"



	// $ANTLR start "inclusiveOrExpression"
	// /Users/byung/workspace/antlr2/Java.g:1033:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
	public final void inclusiveOrExpression() throws RecognitionException {
		int inclusiveOrExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1034:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1034:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
			{
			pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression6061);
			exclusiveOrExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1035:9: ( '|' exclusiveOrExpression )*
			loop119:
			while (true) {
				int alt119=2;
				int LA119_0 = input.LA(1);
				if ( (LA119_0==BAR) ) {
					alt119=1;
				}

				switch (alt119) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1035:10: '|' exclusiveOrExpression
					{
					match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression6072); if (state.failed) return;
					pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression6074);
					exclusiveOrExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("|");}
					}
					break;

				default :
					break loop119;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 75, inclusiveOrExpression_StartIndex); }

		}
	}
	// $ANTLR end "inclusiveOrExpression"



	// $ANTLR start "exclusiveOrExpression"
	// /Users/byung/workspace/antlr2/Java.g:1039:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
	public final void exclusiveOrExpression() throws RecognitionException {
		int exclusiveOrExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1040:5: ( andExpression ( '^' andExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1040:9: andExpression ( '^' andExpression )*
			{
			pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression6107);
			andExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1041:9: ( '^' andExpression )*
			loop120:
			while (true) {
				int alt120=2;
				int LA120_0 = input.LA(1);
				if ( (LA120_0==CARET) ) {
					alt120=1;
				}

				switch (alt120) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1041:10: '^' andExpression
					{
					match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression6118); if (state.failed) return;
					pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression6120);
					andExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("^");}
					}
					break;

				default :
					break loop120;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 76, exclusiveOrExpression_StartIndex); }

		}
	}
	// $ANTLR end "exclusiveOrExpression"



	// $ANTLR start "andExpression"
	// /Users/byung/workspace/antlr2/Java.g:1045:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
	public final void andExpression() throws RecognitionException {
		int andExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1046:5: ( equalityExpression ( '&' equalityExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1046:9: equalityExpression ( '&' equalityExpression )*
			{
			pushFollow(FOLLOW_equalityExpression_in_andExpression6153);
			equalityExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1047:9: ( '&' equalityExpression )*
			loop121:
			while (true) {
				int alt121=2;
				int LA121_0 = input.LA(1);
				if ( (LA121_0==AMP) ) {
					alt121=1;
				}

				switch (alt121) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1047:10: '&' equalityExpression
					{
					match(input,AMP,FOLLOW_AMP_in_andExpression6164); if (state.failed) return;
					pushFollow(FOLLOW_equalityExpression_in_andExpression6166);
					equalityExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("&");}
					}
					break;

				default :
					break loop121;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 77, andExpression_StartIndex); }

		}
	}
	// $ANTLR end "andExpression"



	// $ANTLR start "equalityExpression"
	// /Users/byung/workspace/antlr2/Java.g:1051:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
	public final void equalityExpression() throws RecognitionException {
		int equalityExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1052:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1052:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
			{
			pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression6199);
			instanceOfExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1053:9: ( ( '==' | '!=' ) instanceOfExpression )*
			loop123:
			while (true) {
				int alt123=2;
				int LA123_0 = input.LA(1);
				if ( (LA123_0==BANGEQ||LA123_0==EQEQ) ) {
					alt123=1;
				}

				switch (alt123) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1054:13: ( '==' | '!=' ) instanceOfExpression
					{
					// /Users/byung/workspace/antlr2/Java.g:1054:13: ( '==' | '!=' )
					int alt122=2;
					int LA122_0 = input.LA(1);
					if ( (LA122_0==EQEQ) ) {
						alt122=1;
					}
					else if ( (LA122_0==BANGEQ) ) {
						alt122=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						NoViableAltException nvae =
							new NoViableAltException("", 122, 0, input);
						throw nvae;
					}

					switch (alt122) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1054:17: '=='
							{
							match(input,EQEQ,FOLLOW_EQEQ_in_equalityExpression6231); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("=");sAdd("=");}
							}
							break;
						case 2 :
							// /Users/byung/workspace/antlr2/Java.g:1055:17: '!='
							{
							match(input,BANGEQ,FOLLOW_BANGEQ_in_equalityExpression6251); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("!=");sAdd("=");}
							}
							break;

					}

					pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression6281);
					instanceOfExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop123;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 78, equalityExpression_StartIndex); }

		}
	}
	// $ANTLR end "equalityExpression"



	// $ANTLR start "instanceOfExpression"
	// /Users/byung/workspace/antlr2/Java.g:1061:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
	public final void instanceOfExpression() throws RecognitionException {
		int instanceOfExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1062:5: ( relationalExpression ( 'instanceof' type )? )
			// /Users/byung/workspace/antlr2/Java.g:1062:9: relationalExpression ( 'instanceof' type )?
			{
			pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression6312);
			relationalExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1063:9: ( 'instanceof' type )?
			int alt124=2;
			int LA124_0 = input.LA(1);
			if ( (LA124_0==INSTANCEOF) ) {
				alt124=1;
			}
			switch (alt124) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1063:10: 'instanceof' type
					{
					match(input,INSTANCEOF,FOLLOW_INSTANCEOF_in_instanceOfExpression6323); if (state.failed) return;
					pushFollow(FOLLOW_type_in_instanceOfExpression6325);
					type();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("instanceof");}
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 79, instanceOfExpression_StartIndex); }

		}
	}
	// $ANTLR end "instanceOfExpression"



	// $ANTLR start "relationalExpression"
	// /Users/byung/workspace/antlr2/Java.g:1067:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
	public final void relationalExpression() throws RecognitionException {
		int relationalExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1068:5: ( shiftExpression ( relationalOp shiftExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1068:9: shiftExpression ( relationalOp shiftExpression )*
			{
			pushFollow(FOLLOW_shiftExpression_in_relationalExpression6358);
			shiftExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1069:9: ( relationalOp shiftExpression )*
			loop125:
			while (true) {
				int alt125=2;
				int LA125_0 = input.LA(1);
				if ( (LA125_0==LT) ) {
					int LA125_2 = input.LA(2);
					if ( (LA125_2==BANG||LA125_2==BOOLEAN||LA125_2==BYTE||(LA125_2 >= CHAR && LA125_2 <= CHARLITERAL)||(LA125_2 >= DOUBLE && LA125_2 <= DOUBLELITERAL)||LA125_2==EQ||LA125_2==FALSE||(LA125_2 >= FLOAT && LA125_2 <= FLOATLITERAL)||LA125_2==IDENTIFIER||LA125_2==INT||LA125_2==INTLITERAL||(LA125_2 >= LONG && LA125_2 <= LPAREN)||(LA125_2 >= NEW && LA125_2 <= NULL)||LA125_2==PLUS||LA125_2==PLUSPLUS||LA125_2==SHORT||(LA125_2 >= STRINGLITERAL && LA125_2 <= SUB)||(LA125_2 >= SUBSUB && LA125_2 <= SUPER)||LA125_2==THIS||LA125_2==TILDE||LA125_2==TRUE||LA125_2==VOID) ) {
						alt125=1;
					}

				}
				else if ( (LA125_0==GT) ) {
					int LA125_3 = input.LA(2);
					if ( (LA125_3==BANG||LA125_3==BOOLEAN||LA125_3==BYTE||(LA125_3 >= CHAR && LA125_3 <= CHARLITERAL)||(LA125_3 >= DOUBLE && LA125_3 <= DOUBLELITERAL)||LA125_3==EQ||LA125_3==FALSE||(LA125_3 >= FLOAT && LA125_3 <= FLOATLITERAL)||LA125_3==IDENTIFIER||LA125_3==INT||LA125_3==INTLITERAL||(LA125_3 >= LONG && LA125_3 <= LPAREN)||(LA125_3 >= NEW && LA125_3 <= NULL)||LA125_3==PLUS||LA125_3==PLUSPLUS||LA125_3==SHORT||(LA125_3 >= STRINGLITERAL && LA125_3 <= SUB)||(LA125_3 >= SUBSUB && LA125_3 <= SUPER)||LA125_3==THIS||LA125_3==TILDE||LA125_3==TRUE||LA125_3==VOID) ) {
						alt125=1;
					}

				}

				switch (alt125) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1069:10: relationalOp shiftExpression
					{
					pushFollow(FOLLOW_relationalOp_in_relationalExpression6370);
					relationalOp();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_shiftExpression_in_relationalExpression6372);
					shiftExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop125;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 80, relationalExpression_StartIndex); }

		}
	}
	// $ANTLR end "relationalExpression"



	// $ANTLR start "relationalOp"
	// /Users/byung/workspace/antlr2/Java.g:1073:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );
	public final void relationalOp() throws RecognitionException {
		int relationalOp_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1074:5: ( '<' '=' | '>' '=' | '<' | '>' )
			int alt126=4;
			int LA126_0 = input.LA(1);
			if ( (LA126_0==LT) ) {
				int LA126_1 = input.LA(2);
				if ( (LA126_1==EQ) ) {
					alt126=1;
				}
				else if ( (LA126_1==BANG||LA126_1==BOOLEAN||LA126_1==BYTE||(LA126_1 >= CHAR && LA126_1 <= CHARLITERAL)||(LA126_1 >= DOUBLE && LA126_1 <= DOUBLELITERAL)||LA126_1==FALSE||(LA126_1 >= FLOAT && LA126_1 <= FLOATLITERAL)||LA126_1==IDENTIFIER||LA126_1==INT||LA126_1==INTLITERAL||(LA126_1 >= LONG && LA126_1 <= LPAREN)||(LA126_1 >= NEW && LA126_1 <= NULL)||LA126_1==PLUS||LA126_1==PLUSPLUS||LA126_1==SHORT||(LA126_1 >= STRINGLITERAL && LA126_1 <= SUB)||(LA126_1 >= SUBSUB && LA126_1 <= SUPER)||LA126_1==THIS||LA126_1==TILDE||LA126_1==TRUE||LA126_1==VOID) ) {
					alt126=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 126, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA126_0==GT) ) {
				int LA126_2 = input.LA(2);
				if ( (LA126_2==EQ) ) {
					alt126=2;
				}
				else if ( (LA126_2==BANG||LA126_2==BOOLEAN||LA126_2==BYTE||(LA126_2 >= CHAR && LA126_2 <= CHARLITERAL)||(LA126_2 >= DOUBLE && LA126_2 <= DOUBLELITERAL)||LA126_2==FALSE||(LA126_2 >= FLOAT && LA126_2 <= FLOATLITERAL)||LA126_2==IDENTIFIER||LA126_2==INT||LA126_2==INTLITERAL||(LA126_2 >= LONG && LA126_2 <= LPAREN)||(LA126_2 >= NEW && LA126_2 <= NULL)||LA126_2==PLUS||LA126_2==PLUSPLUS||LA126_2==SHORT||(LA126_2 >= STRINGLITERAL && LA126_2 <= SUB)||(LA126_2 >= SUBSUB && LA126_2 <= SUPER)||LA126_2==THIS||LA126_2==TILDE||LA126_2==TRUE||LA126_2==VOID) ) {
					alt126=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 126, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 126, 0, input);
				throw nvae;
			}

			switch (alt126) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1074:10: '<' '='
					{
					match(input,LT,FOLLOW_LT_in_relationalOp6404); if (state.failed) return;
					match(input,EQ,FOLLOW_EQ_in_relationalOp6406); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("<");{sAdd("=");}}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1075:10: '>' '='
					{
					match(input,GT,FOLLOW_GT_in_relationalOp6419); if (state.failed) return;
					match(input,EQ,FOLLOW_EQ_in_relationalOp6421); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(">");{sAdd("=");}}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1076:9: '<'
					{
					match(input,LT,FOLLOW_LT_in_relationalOp6433); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("<");}
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:1077:9: '>'
					{
					match(input,GT,FOLLOW_GT_in_relationalOp6445); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(">");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 81, relationalOp_StartIndex); }

		}
	}
	// $ANTLR end "relationalOp"



	// $ANTLR start "shiftExpression"
	// /Users/byung/workspace/antlr2/Java.g:1080:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
	public final void shiftExpression() throws RecognitionException {
		int shiftExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1081:5: ( additiveExpression ( shiftOp additiveExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1081:9: additiveExpression ( shiftOp additiveExpression )*
			{
			pushFollow(FOLLOW_additiveExpression_in_shiftExpression6467);
			additiveExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1082:9: ( shiftOp additiveExpression )*
			loop127:
			while (true) {
				int alt127=2;
				int LA127_0 = input.LA(1);
				if ( (LA127_0==LT) ) {
					int LA127_1 = input.LA(2);
					if ( (LA127_1==LT) ) {
						int LA127_4 = input.LA(3);
						if ( (LA127_4==BANG||LA127_4==BOOLEAN||LA127_4==BYTE||(LA127_4 >= CHAR && LA127_4 <= CHARLITERAL)||(LA127_4 >= DOUBLE && LA127_4 <= DOUBLELITERAL)||LA127_4==FALSE||(LA127_4 >= FLOAT && LA127_4 <= FLOATLITERAL)||LA127_4==IDENTIFIER||LA127_4==INT||LA127_4==INTLITERAL||(LA127_4 >= LONG && LA127_4 <= LPAREN)||(LA127_4 >= NEW && LA127_4 <= NULL)||LA127_4==PLUS||LA127_4==PLUSPLUS||LA127_4==SHORT||(LA127_4 >= STRINGLITERAL && LA127_4 <= SUB)||(LA127_4 >= SUBSUB && LA127_4 <= SUPER)||LA127_4==THIS||LA127_4==TILDE||LA127_4==TRUE||LA127_4==VOID) ) {
							alt127=1;
						}

					}

				}
				else if ( (LA127_0==GT) ) {
					int LA127_2 = input.LA(2);
					if ( (LA127_2==GT) ) {
						int LA127_5 = input.LA(3);
						if ( (LA127_5==GT) ) {
							int LA127_7 = input.LA(4);
							if ( (LA127_7==BANG||LA127_7==BOOLEAN||LA127_7==BYTE||(LA127_7 >= CHAR && LA127_7 <= CHARLITERAL)||(LA127_7 >= DOUBLE && LA127_7 <= DOUBLELITERAL)||LA127_7==FALSE||(LA127_7 >= FLOAT && LA127_7 <= FLOATLITERAL)||LA127_7==IDENTIFIER||LA127_7==INT||LA127_7==INTLITERAL||(LA127_7 >= LONG && LA127_7 <= LPAREN)||(LA127_7 >= NEW && LA127_7 <= NULL)||LA127_7==PLUS||LA127_7==PLUSPLUS||LA127_7==SHORT||(LA127_7 >= STRINGLITERAL && LA127_7 <= SUB)||(LA127_7 >= SUBSUB && LA127_7 <= SUPER)||LA127_7==THIS||LA127_7==TILDE||LA127_7==TRUE||LA127_7==VOID) ) {
								alt127=1;
							}

						}
						else if ( (LA127_5==BANG||LA127_5==BOOLEAN||LA127_5==BYTE||(LA127_5 >= CHAR && LA127_5 <= CHARLITERAL)||(LA127_5 >= DOUBLE && LA127_5 <= DOUBLELITERAL)||LA127_5==FALSE||(LA127_5 >= FLOAT && LA127_5 <= FLOATLITERAL)||LA127_5==IDENTIFIER||LA127_5==INT||LA127_5==INTLITERAL||(LA127_5 >= LONG && LA127_5 <= LPAREN)||(LA127_5 >= NEW && LA127_5 <= NULL)||LA127_5==PLUS||LA127_5==PLUSPLUS||LA127_5==SHORT||(LA127_5 >= STRINGLITERAL && LA127_5 <= SUB)||(LA127_5 >= SUBSUB && LA127_5 <= SUPER)||LA127_5==THIS||LA127_5==TILDE||LA127_5==TRUE||LA127_5==VOID) ) {
							alt127=1;
						}

					}

				}

				switch (alt127) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1082:10: shiftOp additiveExpression
					{
					pushFollow(FOLLOW_shiftOp_in_shiftExpression6478);
					shiftOp();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_additiveExpression_in_shiftExpression6480);
					additiveExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop127;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 82, shiftExpression_StartIndex); }

		}
	}
	// $ANTLR end "shiftExpression"



	// $ANTLR start "shiftOp"
	// /Users/byung/workspace/antlr2/Java.g:1087:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' );
	public final void shiftOp() throws RecognitionException {
		int shiftOp_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1088:5: ( '<' '<' | '>' '>' '>' | '>' '>' )
			int alt128=3;
			int LA128_0 = input.LA(1);
			if ( (LA128_0==LT) ) {
				alt128=1;
			}
			else if ( (LA128_0==GT) ) {
				int LA128_2 = input.LA(2);
				if ( (LA128_2==GT) ) {
					int LA128_3 = input.LA(3);
					if ( (LA128_3==GT) ) {
						alt128=2;
					}
					else if ( (LA128_3==BANG||LA128_3==BOOLEAN||LA128_3==BYTE||(LA128_3 >= CHAR && LA128_3 <= CHARLITERAL)||(LA128_3 >= DOUBLE && LA128_3 <= DOUBLELITERAL)||LA128_3==FALSE||(LA128_3 >= FLOAT && LA128_3 <= FLOATLITERAL)||LA128_3==IDENTIFIER||LA128_3==INT||LA128_3==INTLITERAL||(LA128_3 >= LONG && LA128_3 <= LPAREN)||(LA128_3 >= NEW && LA128_3 <= NULL)||LA128_3==PLUS||LA128_3==PLUSPLUS||LA128_3==SHORT||(LA128_3 >= STRINGLITERAL && LA128_3 <= SUB)||(LA128_3 >= SUBSUB && LA128_3 <= SUPER)||LA128_3==THIS||LA128_3==TILDE||LA128_3==TRUE||LA128_3==VOID) ) {
						alt128=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 128, 3, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 128, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 128, 0, input);
				throw nvae;
			}

			switch (alt128) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1088:10: '<' '<'
					{
					match(input,LT,FOLLOW_LT_in_shiftOp6513); if (state.failed) return;
					match(input,LT,FOLLOW_LT_in_shiftOp6515); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("<");}
					if ( state.backtracking==0 ) {sAdd("<");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1089:10: '>' '>' '>'
					{
					match(input,GT,FOLLOW_GT_in_shiftOp6531); if (state.failed) return;
					match(input,GT,FOLLOW_GT_in_shiftOp6533); if (state.failed) return;
					match(input,GT,FOLLOW_GT_in_shiftOp6535); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(">");}
					if ( state.backtracking==0 ) {sAdd(">");}
					if ( state.backtracking==0 ) {sAdd(">");}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1090:10: '>' '>'
					{
					match(input,GT,FOLLOW_GT_in_shiftOp6550); if (state.failed) return;
					match(input,GT,FOLLOW_GT_in_shiftOp6552); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd(">");}
					if ( state.backtracking==0 ) {sAdd(">");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 83, shiftOp_StartIndex); }

		}
	}
	// $ANTLR end "shiftOp"



	// $ANTLR start "additiveExpression"
	// /Users/byung/workspace/antlr2/Java.g:1094:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
	public final void additiveExpression() throws RecognitionException {
		int additiveExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1095:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1095:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
			{
			pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6576);
			multiplicativeExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1096:9: ( ( '+' | '-' ) multiplicativeExpression )*
			loop130:
			while (true) {
				int alt130=2;
				int LA130_0 = input.LA(1);
				if ( (LA130_0==PLUS||LA130_0==SUB) ) {
					alt130=1;
				}

				switch (alt130) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1097:13: ( '+' | '-' ) multiplicativeExpression
					{
					// /Users/byung/workspace/antlr2/Java.g:1097:13: ( '+' | '-' )
					int alt129=2;
					int LA129_0 = input.LA(1);
					if ( (LA129_0==PLUS) ) {
						alt129=1;
					}
					else if ( (LA129_0==SUB) ) {
						alt129=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						NoViableAltException nvae =
							new NoViableAltException("", 129, 0, input);
						throw nvae;
					}

					switch (alt129) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1097:17: '+'
							{
							match(input,PLUS,FOLLOW_PLUS_in_additiveExpression6607); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("+");}
							}
							break;
						case 2 :
							// /Users/byung/workspace/antlr2/Java.g:1098:17: '-'
							{
							match(input,SUB,FOLLOW_SUB_in_additiveExpression6627); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("-");}
							}
							break;

					}

					pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6657);
					multiplicativeExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop130;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 84, additiveExpression_StartIndex); }

		}
	}
	// $ANTLR end "additiveExpression"



	// $ANTLR start "multiplicativeExpression"
	// /Users/byung/workspace/antlr2/Java.g:1104:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
	public final void multiplicativeExpression() throws RecognitionException {
		int multiplicativeExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1105:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
			// /Users/byung/workspace/antlr2/Java.g:1106:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
			{
			pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6695);
			unaryExpression();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1107:9: ( ( '*' | '/' | '%' ) unaryExpression )*
			loop132:
			while (true) {
				int alt132=2;
				int LA132_0 = input.LA(1);
				if ( (LA132_0==PERCENT||LA132_0==SLASH||LA132_0==STAR) ) {
					alt132=1;
				}

				switch (alt132) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1108:13: ( '*' | '/' | '%' ) unaryExpression
					{
					// /Users/byung/workspace/antlr2/Java.g:1108:13: ( '*' | '/' | '%' )
					int alt131=3;
					switch ( input.LA(1) ) {
					case STAR:
						{
						alt131=1;
						}
						break;
					case SLASH:
						{
						alt131=2;
						}
						break;
					case PERCENT:
						{
						alt131=3;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return;}
						NoViableAltException nvae =
							new NoViableAltException("", 131, 0, input);
						throw nvae;
					}
					switch (alt131) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1108:17: '*'
							{
							match(input,STAR,FOLLOW_STAR_in_multiplicativeExpression6726); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("*");}
							}
							break;
						case 2 :
							// /Users/byung/workspace/antlr2/Java.g:1109:17: '/'
							{
							match(input,SLASH,FOLLOW_SLASH_in_multiplicativeExpression6746); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("/");}
							}
							break;
						case 3 :
							// /Users/byung/workspace/antlr2/Java.g:1110:17: '%'
							{
							match(input,PERCENT,FOLLOW_PERCENT_in_multiplicativeExpression6766); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("%");}
							}
							break;

					}

					pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6796);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop132;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 85, multiplicativeExpression_StartIndex); }

		}
	}
	// $ANTLR end "multiplicativeExpression"



	// $ANTLR start "unaryExpression"
	// /Users/byung/workspace/antlr2/Java.g:1120:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
	public final void unaryExpression() throws RecognitionException {
		int unaryExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1121:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
			int alt133=5;
			switch ( input.LA(1) ) {
			case PLUS:
				{
				alt133=1;
				}
				break;
			case SUB:
				{
				alt133=2;
				}
				break;
			case PLUSPLUS:
				{
				alt133=3;
				}
				break;
			case SUBSUB:
				{
				alt133=4;
				}
				break;
			case BANG:
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case CHARLITERAL:
			case DOUBLE:
			case DOUBLELITERAL:
			case FALSE:
			case FLOAT:
			case FLOATLITERAL:
			case IDENTIFIER:
			case INT:
			case INTLITERAL:
			case LONG:
			case LONGLITERAL:
			case LPAREN:
			case NEW:
			case NULL:
			case SHORT:
			case STRINGLITERAL:
			case SUPER:
			case THIS:
			case TILDE:
			case TRUE:
			case VOID:
				{
				alt133=5;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 133, 0, input);
				throw nvae;
			}
			switch (alt133) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1121:9: '+' unaryExpression
					{
					match(input,PLUS,FOLLOW_PLUS_in_unaryExpression6829); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression6832);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("+"); }
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1122:9: '-' unaryExpression
					{
					match(input,SUB,FOLLOW_SUB_in_unaryExpression6845); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression6847);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("-");}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1123:9: '++' unaryExpression
					{
					match(input,PLUSPLUS,FOLLOW_PLUSPLUS_in_unaryExpression6859); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression6861);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("++");}
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:1124:9: '--' unaryExpression
					{
					match(input,SUBSUB,FOLLOW_SUBSUB_in_unaryExpression6873); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression6875);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("--");}
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:1125:9: unaryExpressionNotPlusMinus
					{
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6887);
					unaryExpressionNotPlusMinus();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 86, unaryExpression_StartIndex); }

		}
	}
	// $ANTLR end "unaryExpression"



	// $ANTLR start "unaryExpressionNotPlusMinus"
	// /Users/byung/workspace/antlr2/Java.g:1128:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
	public final void unaryExpressionNotPlusMinus() throws RecognitionException {
		int unaryExpressionNotPlusMinus_StartIndex = input.index();

		ParserRuleReturnScope primary18 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1129:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
			int alt136=4;
			switch ( input.LA(1) ) {
			case TILDE:
				{
				alt136=1;
				}
				break;
			case BANG:
				{
				alt136=2;
				}
				break;
			case LPAREN:
				{
				int LA136_3 = input.LA(2);
				if ( (synpred202_Java()) ) {
					alt136=3;
				}
				else if ( (true) ) {
					alt136=4;
				}

				}
				break;
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case CHARLITERAL:
			case DOUBLE:
			case DOUBLELITERAL:
			case FALSE:
			case FLOAT:
			case FLOATLITERAL:
			case IDENTIFIER:
			case INT:
			case INTLITERAL:
			case LONG:
			case LONGLITERAL:
			case NEW:
			case NULL:
			case SHORT:
			case STRINGLITERAL:
			case SUPER:
			case THIS:
			case TRUE:
			case VOID:
				{
				alt136=4;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 136, 0, input);
				throw nvae;
			}
			switch (alt136) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1129:9: '~' unaryExpression
					{
					match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6907); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6909);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("~");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1130:9: '!' unaryExpression
					{
					match(input,BANG,FOLLOW_BANG_in_unaryExpressionNotPlusMinus6921); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6923);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("!");}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1131:9: castExpression
					{
					pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6935);
					castExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:1132:9: primary ( selector )* ( '++' | '--' )?
					{
					pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus6945);
					primary18=primary();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {System.out.println("Primary : " + (primary18!=null?input.toString(primary18.start,primary18.stop):null));if(containsIdentifier((primary18!=null?input.toString(primary18.start,primary18.stop):null))){ System.out.println("Adding primary");uAdd((primary18!=null?input.toString(primary18.start,primary18.stop):null)); }}
					// /Users/byung/workspace/antlr2/Java.g:1133:9: ( selector )*
					loop134:
					while (true) {
						int alt134=2;
						int LA134_0 = input.LA(1);
						if ( (LA134_0==DOT||LA134_0==LBRACKET) ) {
							alt134=1;
						}

						switch (alt134) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1133:10: selector
							{
							pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus6958);
							selector();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop134;
						}
					}

					// /Users/byung/workspace/antlr2/Java.g:1135:9: ( '++' | '--' )?
					int alt135=3;
					int LA135_0 = input.LA(1);
					if ( (LA135_0==PLUSPLUS) ) {
						alt135=1;
					}
					else if ( (LA135_0==SUBSUB) ) {
						alt135=2;
					}
					switch (alt135) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1135:13: '++'
							{
							match(input,PLUSPLUS,FOLLOW_PLUSPLUS_in_unaryExpressionNotPlusMinus6983); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("++");}
							}
							break;
						case 2 :
							// /Users/byung/workspace/antlr2/Java.g:1136:13: '--'
							{
							match(input,SUBSUB,FOLLOW_SUBSUB_in_unaryExpressionNotPlusMinus6999); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("--");}
							}
							break;

					}

					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 87, unaryExpressionNotPlusMinus_StartIndex); }

		}
	}
	// $ANTLR end "unaryExpressionNotPlusMinus"



	// $ANTLR start "castExpression"
	// /Users/byung/workspace/antlr2/Java.g:1140:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus );
	public final void castExpression() throws RecognitionException {
		int castExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1141:5: ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus )
			int alt137=2;
			int LA137_0 = input.LA(1);
			if ( (LA137_0==LPAREN) ) {
				int LA137_1 = input.LA(2);
				if ( (synpred206_Java()) ) {
					alt137=1;
				}
				else if ( (true) ) {
					alt137=2;
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 137, 0, input);
				throw nvae;
			}

			switch (alt137) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1141:9: '(' primitiveType ')' unaryExpression
					{
					match(input,LPAREN,FOLLOW_LPAREN_in_castExpression7032); if (state.failed) return;
					pushFollow(FOLLOW_primitiveType_in_castExpression7034);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					match(input,RPAREN,FOLLOW_RPAREN_in_castExpression7036); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_castExpression7038);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("(");sAdd(")");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1142:9: '(' type ')' unaryExpressionNotPlusMinus
					{
					match(input,LPAREN,FOLLOW_LPAREN_in_castExpression7050); if (state.failed) return;
					pushFollow(FOLLOW_type_in_castExpression7052);
					type();
					state._fsp--;
					if (state.failed) return;
					match(input,RPAREN,FOLLOW_RPAREN_in_castExpression7054); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression7056);
					unaryExpressionNotPlusMinus();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("(");sAdd(")");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 88, castExpression_StartIndex); }

		}
	}
	// $ANTLR end "castExpression"


	public static class primary_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "primary"
	// /Users/byung/workspace/antlr2/Java.g:1148:1: primary : ( parExpression | 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
	public final JavaParser.primary_return primary() throws RecognitionException {
		JavaParser.primary_return retval = new JavaParser.primary_return();
		retval.start = input.LT(1);
		int primary_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return retval; }

			// /Users/byung/workspace/antlr2/Java.g:1149:5: ( parExpression | 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
			int alt143=8;
			switch ( input.LA(1) ) {
			case LPAREN:
				{
				alt143=1;
				}
				break;
			case THIS:
				{
				alt143=2;
				}
				break;
			case IDENTIFIER:
				{
				alt143=3;
				}
				break;
			case SUPER:
				{
				alt143=4;
				}
				break;
			case CHARLITERAL:
			case DOUBLELITERAL:
			case FALSE:
			case FLOATLITERAL:
			case INTLITERAL:
			case LONGLITERAL:
			case NULL:
			case STRINGLITERAL:
			case TRUE:
				{
				alt143=5;
				}
				break;
			case NEW:
				{
				alt143=6;
				}
				break;
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case SHORT:
				{
				alt143=7;
				}
				break;
			case VOID:
				{
				alt143=8;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 143, 0, input);
				throw nvae;
			}
			switch (alt143) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1149:9: parExpression
					{
					pushFollow(FOLLOW_parExpression_in_primary7080);
					parExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1150:9: 'this' ( '.' IDENTIFIER )* ( identifierSuffix )?
					{
					match(input,THIS,FOLLOW_THIS_in_primary7102); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("this");}
					// /Users/byung/workspace/antlr2/Java.g:1151:9: ( '.' IDENTIFIER )*
					loop138:
					while (true) {
						int alt138=2;
						int LA138_0 = input.LA(1);
						if ( (LA138_0==DOT) ) {
							int LA138_2 = input.LA(2);
							if ( (LA138_2==IDENTIFIER) ) {
								int LA138_3 = input.LA(3);
								if ( (synpred208_Java()) ) {
									alt138=1;
								}

							}

						}

						switch (alt138) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1151:10: '.' IDENTIFIER
							{
							match(input,DOT,FOLLOW_DOT_in_primary7115); if (state.failed) return retval;
							match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary7117); if (state.failed) return retval;
							if ( state.backtracking==0 ) {sAdd(".");}
							}
							break;

						default :
							break loop138;
						}
					}

					// /Users/byung/workspace/antlr2/Java.g:1153:9: ( identifierSuffix )?
					int alt139=2;
					switch ( input.LA(1) ) {
						case LBRACKET:
							{
							int LA139_1 = input.LA(2);
							if ( (synpred209_Java()) ) {
								alt139=1;
							}
							}
							break;
						case LPAREN:
							{
							alt139=1;
							}
							break;
						case DOT:
							{
							int LA139_3 = input.LA(2);
							if ( (synpred209_Java()) ) {
								alt139=1;
							}
							}
							break;
					}
					switch (alt139) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1153:10: identifierSuffix
							{
							pushFollow(FOLLOW_identifierSuffix_in_primary7141);
							identifierSuffix();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1155:9: IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )?
					{
					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary7162); if (state.failed) return retval;
					if ( state.backtracking==0 ) {System.out.println("bkId1");}
					// /Users/byung/workspace/antlr2/Java.g:1156:9: ( '.' IDENTIFIER )*
					loop140:
					while (true) {
						int alt140=2;
						int LA140_0 = input.LA(1);
						if ( (LA140_0==DOT) ) {
							int LA140_2 = input.LA(2);
							if ( (LA140_2==IDENTIFIER) ) {
								int LA140_3 = input.LA(3);
								if ( (synpred211_Java()) ) {
									alt140=1;
								}

							}

						}

						switch (alt140) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1156:10: '.' IDENTIFIER
							{
							match(input,DOT,FOLLOW_DOT_in_primary7175); if (state.failed) return retval;
							match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary7177); if (state.failed) return retval;
							if ( state.backtracking==0 ) {sAdd(".");}
							}
							break;

						default :
							break loop140;
						}
					}

					// /Users/byung/workspace/antlr2/Java.g:1158:9: ( identifierSuffix )?
					int alt141=2;
					switch ( input.LA(1) ) {
						case LBRACKET:
							{
							int LA141_1 = input.LA(2);
							if ( (synpred212_Java()) ) {
								alt141=1;
							}
							}
							break;
						case LPAREN:
							{
							alt141=1;
							}
							break;
						case DOT:
							{
							int LA141_3 = input.LA(2);
							if ( (synpred212_Java()) ) {
								alt141=1;
							}
							}
							break;
					}
					switch (alt141) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1158:10: identifierSuffix
							{
							pushFollow(FOLLOW_identifierSuffix_in_primary7202);
							identifierSuffix();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:1160:9: 'super' superSuffix
					{
					match(input,SUPER,FOLLOW_SUPER_in_primary7223); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("super");}
					pushFollow(FOLLOW_superSuffix_in_primary7235);
					superSuffix();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:1162:9: literal
					{
					pushFollow(FOLLOW_literal_in_primary7245);
					literal();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 6 :
					// /Users/byung/workspace/antlr2/Java.g:1163:9: creator
					{
					pushFollow(FOLLOW_creator_in_primary7263);
					creator();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 7 :
					// /Users/byung/workspace/antlr2/Java.g:1164:9: primitiveType ( '[' ']' )* '.' 'class'
					{
					pushFollow(FOLLOW_primitiveType_in_primary7273);
					primitiveType();
					state._fsp--;
					if (state.failed) return retval;
					// /Users/byung/workspace/antlr2/Java.g:1165:9: ( '[' ']' )*
					loop142:
					while (true) {
						int alt142=2;
						int LA142_0 = input.LA(1);
						if ( (LA142_0==LBRACKET) ) {
							alt142=1;
						}

						switch (alt142) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1165:10: '[' ']'
							{
							match(input,LBRACKET,FOLLOW_LBRACKET_in_primary7285); if (state.failed) return retval;
							match(input,RBRACKET,FOLLOW_RBRACKET_in_primary7287); if (state.failed) return retval;
							if ( state.backtracking==0 ) {sAdd("["); sAdd("]");}
							}
							break;

						default :
							break loop142;
						}
					}

					match(input,DOT,FOLLOW_DOT_in_primary7310); if (state.failed) return retval;
					match(input,CLASS,FOLLOW_CLASS_in_primary7312); if (state.failed) return retval;
					if ( state.backtracking==0 ) {sAdd(".");kAdd("class");}
					}
					break;
				case 8 :
					// /Users/byung/workspace/antlr2/Java.g:1168:9: 'void' '.' 'class'
					{
					match(input,VOID,FOLLOW_VOID_in_primary7324); if (state.failed) return retval;
					match(input,DOT,FOLLOW_DOT_in_primary7326); if (state.failed) return retval;
					match(input,CLASS,FOLLOW_CLASS_in_primary7328); if (state.failed) return retval;
					if ( state.backtracking==0 ) {kAdd("void");sAdd(".");kAdd("class");}
					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 89, primary_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "primary"



	// $ANTLR start "superSuffix"
	// /Users/byung/workspace/antlr2/Java.g:1172:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );
	public final void superSuffix() throws RecognitionException {
		int superSuffix_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1173:5: ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? )
			int alt146=2;
			int LA146_0 = input.LA(1);
			if ( (LA146_0==LPAREN) ) {
				alt146=1;
			}
			else if ( (LA146_0==DOT) ) {
				alt146=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 146, 0, input);
				throw nvae;
			}

			switch (alt146) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1173:9: arguments
					{
					pushFollow(FOLLOW_arguments_in_superSuffix7358);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1174:9: '.' ( typeArguments )? IDENTIFIER ( arguments )?
					{
					match(input,DOT,FOLLOW_DOT_in_superSuffix7368); if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:1174:13: ( typeArguments )?
					int alt144=2;
					int LA144_0 = input.LA(1);
					if ( (LA144_0==LT) ) {
						alt144=1;
					}
					switch (alt144) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1174:14: typeArguments
							{
							pushFollow(FOLLOW_typeArguments_in_superSuffix7371);
							typeArguments();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd(".");}
							}
							break;

					}

					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_superSuffix7394); if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:1177:9: ( arguments )?
					int alt145=2;
					int LA145_0 = input.LA(1);
					if ( (LA145_0==LPAREN) ) {
						alt145=1;
					}
					switch (alt145) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1177:10: arguments
							{
							pushFollow(FOLLOW_arguments_in_superSuffix7406);
							arguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 90, superSuffix_StartIndex); }

		}
	}
	// $ANTLR end "superSuffix"



	// $ANTLR start "identifierSuffix"
	// /Users/byung/workspace/antlr2/Java.g:1182:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator );
	public final void identifierSuffix() throws RecognitionException {
		int identifierSuffix_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1183:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator )
			int alt149=8;
			switch ( input.LA(1) ) {
			case LBRACKET:
				{
				int LA149_1 = input.LA(2);
				if ( (LA149_1==RBRACKET) ) {
					alt149=1;
				}
				else if ( (LA149_1==BANG||LA149_1==BOOLEAN||LA149_1==BYTE||(LA149_1 >= CHAR && LA149_1 <= CHARLITERAL)||(LA149_1 >= DOUBLE && LA149_1 <= DOUBLELITERAL)||LA149_1==FALSE||(LA149_1 >= FLOAT && LA149_1 <= FLOATLITERAL)||LA149_1==IDENTIFIER||LA149_1==INT||LA149_1==INTLITERAL||(LA149_1 >= LONG && LA149_1 <= LPAREN)||(LA149_1 >= NEW && LA149_1 <= NULL)||LA149_1==PLUS||LA149_1==PLUSPLUS||LA149_1==SHORT||(LA149_1 >= STRINGLITERAL && LA149_1 <= SUB)||(LA149_1 >= SUBSUB && LA149_1 <= SUPER)||LA149_1==THIS||LA149_1==TILDE||LA149_1==TRUE||LA149_1==VOID) ) {
					alt149=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 149, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LPAREN:
				{
				alt149=3;
				}
				break;
			case DOT:
				{
				switch ( input.LA(2) ) {
				case CLASS:
					{
					alt149=4;
					}
					break;
				case THIS:
					{
					alt149=6;
					}
					break;
				case SUPER:
					{
					alt149=7;
					}
					break;
				case NEW:
					{
					alt149=8;
					}
					break;
				case LT:
					{
					alt149=5;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 149, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 149, 0, input);
				throw nvae;
			}
			switch (alt149) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1183:9: ( '[' ']' )+ '.' 'class'
					{
					// /Users/byung/workspace/antlr2/Java.g:1183:9: ( '[' ']' )+
					int cnt147=0;
					loop147:
					while (true) {
						int alt147=2;
						int LA147_0 = input.LA(1);
						if ( (LA147_0==LBRACKET) ) {
							alt147=1;
						}

						switch (alt147) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1183:10: '[' ']'
							{
							match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix7439); if (state.failed) return;
							match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix7441); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("["); sAdd("]");}
							}
							break;

						default :
							if ( cnt147 >= 1 ) break loop147;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(147, input);
							throw eee;
						}
						cnt147++;
					}

					match(input,DOT,FOLLOW_DOT_in_identifierSuffix7464); if (state.failed) return;
					match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix7466); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("."); kAdd("class");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1186:9: ( '[' expression ']' )+
					{
					// /Users/byung/workspace/antlr2/Java.g:1186:9: ( '[' expression ']' )+
					int cnt148=0;
					loop148:
					while (true) {
						int alt148=2;
						int LA148_0 = input.LA(1);
						if ( (LA148_0==LBRACKET) ) {
							int LA148_2 = input.LA(2);
							if ( (synpred224_Java()) ) {
								alt148=1;
							}

						}

						switch (alt148) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1186:10: '[' expression ']'
							{
							match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix7479); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_identifierSuffix7481);
							expression();
							state._fsp--;
							if (state.failed) return;
							match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix7483); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("["); sAdd("]");}
							}
							break;

						default :
							if ( cnt148 >= 1 ) break loop148;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(148, input);
							throw eee;
						}
						cnt148++;
					}

					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1188:9: arguments
					{
					pushFollow(FOLLOW_arguments_in_identifierSuffix7507);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:1189:9: '.' 'class'
					{
					match(input,DOT,FOLLOW_DOT_in_identifierSuffix7517); if (state.failed) return;
					match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix7519); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("."); kAdd("class");}
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:1190:9: '.' nonWildcardTypeArguments IDENTIFIER arguments
					{
					match(input,DOT,FOLLOW_DOT_in_identifierSuffix7532); if (state.failed) return;
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_identifierSuffix7534);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return;
					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_identifierSuffix7536); if (state.failed) return;
					pushFollow(FOLLOW_arguments_in_identifierSuffix7538);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 6 :
					// /Users/byung/workspace/antlr2/Java.g:1191:9: '.' 'this'
					{
					match(input,DOT,FOLLOW_DOT_in_identifierSuffix7548); if (state.failed) return;
					match(input,THIS,FOLLOW_THIS_in_identifierSuffix7550); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("."); kAdd("this");}
					}
					break;
				case 7 :
					// /Users/byung/workspace/antlr2/Java.g:1192:9: '.' 'super' arguments
					{
					match(input,DOT,FOLLOW_DOT_in_identifierSuffix7562); if (state.failed) return;
					match(input,SUPER,FOLLOW_SUPER_in_identifierSuffix7564); if (state.failed) return;
					pushFollow(FOLLOW_arguments_in_identifierSuffix7566);
					arguments();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("."); kAdd("super");}
					}
					break;
				case 8 :
					// /Users/byung/workspace/antlr2/Java.g:1193:9: innerCreator
					{
					pushFollow(FOLLOW_innerCreator_in_identifierSuffix7578);
					innerCreator();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 91, identifierSuffix_StartIndex); }

		}
	}
	// $ANTLR end "identifierSuffix"



	// $ANTLR start "selector"
	// /Users/byung/workspace/antlr2/Java.g:1197:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' );
	public final void selector() throws RecognitionException {
		int selector_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1198:5: ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' )
			int alt151=5;
			int LA151_0 = input.LA(1);
			if ( (LA151_0==DOT) ) {
				switch ( input.LA(2) ) {
				case IDENTIFIER:
					{
					alt151=1;
					}
					break;
				case THIS:
					{
					alt151=2;
					}
					break;
				case SUPER:
					{
					alt151=3;
					}
					break;
				case NEW:
					{
					alt151=4;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 151, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}
			else if ( (LA151_0==LBRACKET) ) {
				alt151=5;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 151, 0, input);
				throw nvae;
			}

			switch (alt151) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1198:9: '.' IDENTIFIER ( arguments )?
					{
					match(input,DOT,FOLLOW_DOT_in_selector7600); if (state.failed) return;
					match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_selector7602); if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:1199:9: ( arguments )?
					int alt150=2;
					int LA150_0 = input.LA(1);
					if ( (LA150_0==LPAREN) ) {
						alt150=1;
					}
					switch (alt150) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1199:10: arguments
							{
							pushFollow(FOLLOW_arguments_in_selector7613);
							arguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1201:9: '.' 'this'
					{
					match(input,DOT,FOLLOW_DOT_in_selector7634); if (state.failed) return;
					match(input,THIS,FOLLOW_THIS_in_selector7636); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("."); kAdd("this");}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1202:9: '.' 'super' superSuffix
					{
					match(input,DOT,FOLLOW_DOT_in_selector7648); if (state.failed) return;
					match(input,SUPER,FOLLOW_SUPER_in_selector7650); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("."); kAdd("super");}
					pushFollow(FOLLOW_superSuffix_in_selector7662);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:1204:9: innerCreator
					{
					pushFollow(FOLLOW_innerCreator_in_selector7672);
					innerCreator();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:1205:9: '[' expression ']'
					{
					match(input,LBRACKET,FOLLOW_LBRACKET_in_selector7682); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_selector7684);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,RBRACKET,FOLLOW_RBRACKET_in_selector7686); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("["); sAdd("]");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 92, selector_StartIndex); }

		}
	}
	// $ANTLR end "selector"



	// $ANTLR start "creator"
	// /Users/byung/workspace/antlr2/Java.g:1208:1: creator : ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest | 'new' classOrInterfaceType classCreatorRest | arrayCreator );
	public final void creator() throws RecognitionException {
		int creator_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1209:5: ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest | 'new' classOrInterfaceType classCreatorRest | arrayCreator )
			int alt152=3;
			int LA152_0 = input.LA(1);
			if ( (LA152_0==NEW) ) {
				int LA152_1 = input.LA(2);
				if ( (synpred236_Java()) ) {
					alt152=1;
				}
				else if ( (synpred237_Java()) ) {
					alt152=2;
				}
				else if ( (true) ) {
					alt152=3;
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 152, 0, input);
				throw nvae;
			}

			switch (alt152) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1209:9: 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest
					{
					match(input,NEW,FOLLOW_NEW_in_creator7709); if (state.failed) return;
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator7711);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_classOrInterfaceType_in_creator7713);
					classOrInterfaceType();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_classCreatorRest_in_creator7715);
					classCreatorRest();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("new");}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1210:9: 'new' classOrInterfaceType classCreatorRest
					{
					match(input,NEW,FOLLOW_NEW_in_creator7727); if (state.failed) return;
					pushFollow(FOLLOW_classOrInterfaceType_in_creator7729);
					classOrInterfaceType();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_classCreatorRest_in_creator7731);
					classCreatorRest();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("new");}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1211:9: arrayCreator
					{
					pushFollow(FOLLOW_arrayCreator_in_creator7743);
					arrayCreator();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 93, creator_StartIndex); }

		}
	}
	// $ANTLR end "creator"



	// $ANTLR start "arrayCreator"
	// /Users/byung/workspace/antlr2/Java.g:1214:1: arrayCreator : ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer | 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )* );
	public final void arrayCreator() throws RecognitionException {
		int arrayCreator_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1215:5: ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer | 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )* )
			int alt156=2;
			int LA156_0 = input.LA(1);
			if ( (LA156_0==NEW) ) {
				int LA156_1 = input.LA(2);
				if ( (synpred239_Java()) ) {
					alt156=1;
				}
				else if ( (true) ) {
					alt156=2;
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 156, 0, input);
				throw nvae;
			}

			switch (alt156) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1215:9: 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer
					{
					match(input,NEW,FOLLOW_NEW_in_arrayCreator7763); if (state.failed) return;
					pushFollow(FOLLOW_createdName_in_arrayCreator7765);
					createdName();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("new");}
					match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7777); if (state.failed) return;
					match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7779); if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("["); sAdd("]");}
					// /Users/byung/workspace/antlr2/Java.g:1217:9: ( '[' ']' )*
					loop153:
					while (true) {
						int alt153=2;
						int LA153_0 = input.LA(1);
						if ( (LA153_0==LBRACKET) ) {
							alt153=1;
						}

						switch (alt153) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1217:10: '[' ']'
							{
							match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7792); if (state.failed) return;
							match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7794); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("["); sAdd("]");}
							}
							break;

						default :
							break loop153;
						}
					}

					pushFollow(FOLLOW_arrayInitializer_in_arrayCreator7817);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1221:9: 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )*
					{
					match(input,NEW,FOLLOW_NEW_in_arrayCreator7828); if (state.failed) return;
					pushFollow(FOLLOW_createdName_in_arrayCreator7830);
					createdName();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {kAdd("new");}
					match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7842); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_arrayCreator7844);
					expression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {sAdd("[");}
					match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7856); if (state.failed) return;
					if ( state.backtracking==0 ) { sAdd("]");}
					// /Users/byung/workspace/antlr2/Java.g:1224:9: ( '[' expression ']' )*
					loop154:
					while (true) {
						int alt154=2;
						int LA154_0 = input.LA(1);
						if ( (LA154_0==LBRACKET) ) {
							int LA154_1 = input.LA(2);
							if ( (synpred240_Java()) ) {
								alt154=1;
							}

						}

						switch (alt154) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1224:13: '[' expression ']'
							{
							match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7872); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_arrayCreator7874);
							expression();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("[");}
							match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7890); if (state.failed) return;
							if ( state.backtracking==0 ) { sAdd("]");}
							}
							break;

						default :
							break loop154;
						}
					}

					// /Users/byung/workspace/antlr2/Java.g:1227:9: ( '[' ']' )*
					loop155:
					while (true) {
						int alt155=2;
						int LA155_0 = input.LA(1);
						if ( (LA155_0==LBRACKET) ) {
							int LA155_2 = input.LA(2);
							if ( (LA155_2==RBRACKET) ) {
								alt155=1;
							}

						}

						switch (alt155) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1227:10: '[' ']'
							{
							match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7914); if (state.failed) return;
							match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7916); if (state.failed) return;
							if ( state.backtracking==0 ) {sAdd("["); sAdd("]");}
							}
							break;

						default :
							break loop155;
						}
					}

					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 94, arrayCreator_StartIndex); }

		}
	}
	// $ANTLR end "arrayCreator"



	// $ANTLR start "variableInitializer"
	// /Users/byung/workspace/antlr2/Java.g:1231:1: variableInitializer : ( arrayInitializer | expression );
	public final void variableInitializer() throws RecognitionException {
		int variableInitializer_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1232:5: ( arrayInitializer | expression )
			int alt157=2;
			int LA157_0 = input.LA(1);
			if ( (LA157_0==LBRACE) ) {
				alt157=1;
			}
			else if ( (LA157_0==BANG||LA157_0==BOOLEAN||LA157_0==BYTE||(LA157_0 >= CHAR && LA157_0 <= CHARLITERAL)||(LA157_0 >= DOUBLE && LA157_0 <= DOUBLELITERAL)||LA157_0==FALSE||(LA157_0 >= FLOAT && LA157_0 <= FLOATLITERAL)||LA157_0==IDENTIFIER||LA157_0==INT||LA157_0==INTLITERAL||(LA157_0 >= LONG && LA157_0 <= LPAREN)||(LA157_0 >= NEW && LA157_0 <= NULL)||LA157_0==PLUS||LA157_0==PLUSPLUS||LA157_0==SHORT||(LA157_0 >= STRINGLITERAL && LA157_0 <= SUB)||(LA157_0 >= SUBSUB && LA157_0 <= SUPER)||LA157_0==THIS||LA157_0==TILDE||LA157_0==TRUE||LA157_0==VOID) ) {
				alt157=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 157, 0, input);
				throw nvae;
			}

			switch (alt157) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1232:9: arrayInitializer
					{
					pushFollow(FOLLOW_arrayInitializer_in_variableInitializer7949);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1233:9: expression
					{
					pushFollow(FOLLOW_expression_in_variableInitializer7959);
					expression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 95, variableInitializer_StartIndex); }

		}
	}
	// $ANTLR end "variableInitializer"



	// $ANTLR start "arrayInitializer"
	// /Users/byung/workspace/antlr2/Java.g:1236:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}' ;
	public final void arrayInitializer() throws RecognitionException {
		int arrayInitializer_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1237:5: ( '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}' )
			// /Users/byung/workspace/antlr2/Java.g:1237:9: '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}'
			{
			match(input,LBRACE,FOLLOW_LBRACE_in_arrayInitializer7979); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("{");}
			// /Users/byung/workspace/antlr2/Java.g:1238:13: ( variableInitializer ( ',' variableInitializer )* )?
			int alt159=2;
			int LA159_0 = input.LA(1);
			if ( (LA159_0==BANG||LA159_0==BOOLEAN||LA159_0==BYTE||(LA159_0 >= CHAR && LA159_0 <= CHARLITERAL)||(LA159_0 >= DOUBLE && LA159_0 <= DOUBLELITERAL)||LA159_0==FALSE||(LA159_0 >= FLOAT && LA159_0 <= FLOATLITERAL)||LA159_0==IDENTIFIER||LA159_0==INT||LA159_0==INTLITERAL||LA159_0==LBRACE||(LA159_0 >= LONG && LA159_0 <= LPAREN)||(LA159_0 >= NEW && LA159_0 <= NULL)||LA159_0==PLUS||LA159_0==PLUSPLUS||LA159_0==SHORT||(LA159_0 >= STRINGLITERAL && LA159_0 <= SUB)||(LA159_0 >= SUBSUB && LA159_0 <= SUPER)||LA159_0==THIS||LA159_0==TILDE||LA159_0==TRUE||LA159_0==VOID) ) {
				alt159=1;
			}
			switch (alt159) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1238:14: variableInitializer ( ',' variableInitializer )*
					{
					pushFollow(FOLLOW_variableInitializer_in_arrayInitializer7996);
					variableInitializer();
					state._fsp--;
					if (state.failed) return;
					// /Users/byung/workspace/antlr2/Java.g:1239:17: ( ',' variableInitializer )*
					loop158:
					while (true) {
						int alt158=2;
						int LA158_0 = input.LA(1);
						if ( (LA158_0==COMMA) ) {
							int LA158_1 = input.LA(2);
							if ( (LA158_1==BANG||LA158_1==BOOLEAN||LA158_1==BYTE||(LA158_1 >= CHAR && LA158_1 <= CHARLITERAL)||(LA158_1 >= DOUBLE && LA158_1 <= DOUBLELITERAL)||LA158_1==FALSE||(LA158_1 >= FLOAT && LA158_1 <= FLOATLITERAL)||LA158_1==IDENTIFIER||LA158_1==INT||LA158_1==INTLITERAL||LA158_1==LBRACE||(LA158_1 >= LONG && LA158_1 <= LPAREN)||(LA158_1 >= NEW && LA158_1 <= NULL)||LA158_1==PLUS||LA158_1==PLUSPLUS||LA158_1==SHORT||(LA158_1 >= STRINGLITERAL && LA158_1 <= SUB)||(LA158_1 >= SUBSUB && LA158_1 <= SUPER)||LA158_1==THIS||LA158_1==TILDE||LA158_1==TRUE||LA158_1==VOID) ) {
								alt158=1;
							}

						}

						switch (alt158) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1239:18: ',' variableInitializer
							{
							match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer8015); if (state.failed) return;
							pushFollow(FOLLOW_variableInitializer_in_arrayInitializer8017);
							variableInitializer();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) { sAdd(",");}
							}
							break;

						default :
							break loop158;
						}
					}

					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:1242:13: ( ',' )?
			int alt160=2;
			int LA160_0 = input.LA(1);
			if ( (LA160_0==COMMA) ) {
				alt160=1;
			}
			switch (alt160) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1242:14: ','
					{
					match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer8069); if (state.failed) return;
					}
					break;

			}

			if ( state.backtracking==0 ) { sAdd("{");}
			match(input,RBRACE,FOLLOW_RBRACE_in_arrayInitializer8084); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("}");}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 96, arrayInitializer_StartIndex); }

		}
	}
	// $ANTLR end "arrayInitializer"



	// $ANTLR start "createdName"
	// /Users/byung/workspace/antlr2/Java.g:1247:1: createdName : ( classOrInterfaceType | primitiveType );
	public final void createdName() throws RecognitionException {
		int createdName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1248:5: ( classOrInterfaceType | primitiveType )
			int alt161=2;
			int LA161_0 = input.LA(1);
			if ( (LA161_0==IDENTIFIER) ) {
				alt161=1;
			}
			else if ( (LA161_0==BOOLEAN||LA161_0==BYTE||LA161_0==CHAR||LA161_0==DOUBLE||LA161_0==FLOAT||LA161_0==INT||LA161_0==LONG||LA161_0==SHORT) ) {
				alt161=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 161, 0, input);
				throw nvae;
			}

			switch (alt161) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1248:9: classOrInterfaceType
					{
					pushFollow(FOLLOW_classOrInterfaceType_in_createdName8119);
					classOrInterfaceType();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1249:9: primitiveType
					{
					pushFollow(FOLLOW_primitiveType_in_createdName8129);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 97, createdName_StartIndex); }

		}
	}
	// $ANTLR end "createdName"



	// $ANTLR start "innerCreator"
	// /Users/byung/workspace/antlr2/Java.g:1252:1: innerCreator : '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest ;
	public final void innerCreator() throws RecognitionException {
		int innerCreator_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1253:5: ( '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest )
			// /Users/byung/workspace/antlr2/Java.g:1253:9: '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest
			{
			match(input,DOT,FOLLOW_DOT_in_innerCreator8150); if (state.failed) return;
			match(input,NEW,FOLLOW_NEW_in_innerCreator8152); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("."); kAdd("new");}
			// /Users/byung/workspace/antlr2/Java.g:1254:9: ( nonWildcardTypeArguments )?
			int alt162=2;
			int LA162_0 = input.LA(1);
			if ( (LA162_0==LT) ) {
				alt162=1;
			}
			switch (alt162) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1254:10: nonWildcardTypeArguments
					{
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator8165);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_innerCreator8186); if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1257:9: ( typeArguments )?
			int alt163=2;
			int LA163_0 = input.LA(1);
			if ( (LA163_0==LT) ) {
				alt163=1;
			}
			switch (alt163) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1257:10: typeArguments
					{
					pushFollow(FOLLOW_typeArguments_in_innerCreator8197);
					typeArguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_classCreatorRest_in_innerCreator8218);
			classCreatorRest();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 98, innerCreator_StartIndex); }

		}
	}
	// $ANTLR end "innerCreator"



	// $ANTLR start "classCreatorRest"
	// /Users/byung/workspace/antlr2/Java.g:1263:1: classCreatorRest : arguments ( classBody )? ;
	public final void classCreatorRest() throws RecognitionException {
		int classCreatorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1264:5: ( arguments ( classBody )? )
			// /Users/byung/workspace/antlr2/Java.g:1264:9: arguments ( classBody )?
			{
			pushFollow(FOLLOW_arguments_in_classCreatorRest8239);
			arguments();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1265:9: ( classBody )?
			int alt164=2;
			int LA164_0 = input.LA(1);
			if ( (LA164_0==LBRACE) ) {
				alt164=1;
			}
			switch (alt164) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1265:10: classBody
					{
					pushFollow(FOLLOW_classBody_in_classCreatorRest8250);
					classBody();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 99, classCreatorRest_StartIndex); }

		}
	}
	// $ANTLR end "classCreatorRest"



	// $ANTLR start "nonWildcardTypeArguments"
	// /Users/byung/workspace/antlr2/Java.g:1270:1: nonWildcardTypeArguments : '<' typeList '>' ;
	public final void nonWildcardTypeArguments() throws RecognitionException {
		int nonWildcardTypeArguments_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1271:5: ( '<' typeList '>' )
			// /Users/byung/workspace/antlr2/Java.g:1271:9: '<' typeList '>'
			{
			match(input,LT,FOLLOW_LT_in_nonWildcardTypeArguments8282); if (state.failed) return;
			pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments8284);
			typeList();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("<"); }
			match(input,GT,FOLLOW_GT_in_nonWildcardTypeArguments8296); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd(">"); }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 100, nonWildcardTypeArguments_StartIndex); }

		}
	}
	// $ANTLR end "nonWildcardTypeArguments"



	// $ANTLR start "arguments"
	// /Users/byung/workspace/antlr2/Java.g:1275:1: arguments : '(' ( expressionList )? ')' ;
	public final void arguments() throws RecognitionException {
		int arguments_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1276:5: ( '(' ( expressionList )? ')' )
			// /Users/byung/workspace/antlr2/Java.g:1276:9: '(' ( expressionList )? ')'
			{
			match(input,LPAREN,FOLLOW_LPAREN_in_arguments8318); if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1276:13: ( expressionList )?
			int alt165=2;
			int LA165_0 = input.LA(1);
			if ( (LA165_0==BANG||LA165_0==BOOLEAN||LA165_0==BYTE||(LA165_0 >= CHAR && LA165_0 <= CHARLITERAL)||(LA165_0 >= DOUBLE && LA165_0 <= DOUBLELITERAL)||LA165_0==FALSE||(LA165_0 >= FLOAT && LA165_0 <= FLOATLITERAL)||LA165_0==IDENTIFIER||LA165_0==INT||LA165_0==INTLITERAL||(LA165_0 >= LONG && LA165_0 <= LPAREN)||(LA165_0 >= NEW && LA165_0 <= NULL)||LA165_0==PLUS||LA165_0==PLUSPLUS||LA165_0==SHORT||(LA165_0 >= STRINGLITERAL && LA165_0 <= SUB)||(LA165_0 >= SUBSUB && LA165_0 <= SUPER)||LA165_0==THIS||LA165_0==TILDE||LA165_0==TRUE||LA165_0==VOID) ) {
				alt165=1;
			}
			switch (alt165) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1276:14: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_arguments8321);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { sAdd("("); }
					}
					break;

			}

			match(input,RPAREN,FOLLOW_RPAREN_in_arguments8336); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd(")"); }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 101, arguments_StartIndex); }

		}
	}
	// $ANTLR end "arguments"



	// $ANTLR start "literal"
	// /Users/byung/workspace/antlr2/Java.g:1280:1: literal : ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL );
	public final void literal() throws RecognitionException {
		int literal_StartIndex = input.index();

		Token INTLITERAL19=null;
		Token LONGLITERAL20=null;
		Token FLOATLITERAL21=null;
		Token DOUBLELITERAL22=null;
		Token CHARLITERAL23=null;
		Token STRINGLITERAL24=null;
		Token TRUE25=null;
		Token FALSE26=null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1281:5: ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL )
			int alt166=9;
			switch ( input.LA(1) ) {
			case INTLITERAL:
				{
				alt166=1;
				}
				break;
			case LONGLITERAL:
				{
				alt166=2;
				}
				break;
			case FLOATLITERAL:
				{
				alt166=3;
				}
				break;
			case DOUBLELITERAL:
				{
				alt166=4;
				}
				break;
			case CHARLITERAL:
				{
				alt166=5;
				}
				break;
			case STRINGLITERAL:
				{
				alt166=6;
				}
				break;
			case TRUE:
				{
				alt166=7;
				}
				break;
			case FALSE:
				{
				alt166=8;
				}
				break;
			case NULL:
				{
				alt166=9;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 166, 0, input);
				throw nvae;
			}
			switch (alt166) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1281:9: INTLITERAL
					{
					INTLITERAL19=(Token)match(input,INTLITERAL,FOLLOW_INTLITERAL_in_literal8358); if (state.failed) return;
					if ( state.backtracking==0 ) { cAdd((INTLITERAL19!=null?INTLITERAL19.getText():null));}
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1282:9: LONGLITERAL
					{
					LONGLITERAL20=(Token)match(input,LONGLITERAL,FOLLOW_LONGLITERAL_in_literal8370); if (state.failed) return;
					if ( state.backtracking==0 ) { cAdd((LONGLITERAL20!=null?LONGLITERAL20.getText():null));}
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1283:9: FLOATLITERAL
					{
					FLOATLITERAL21=(Token)match(input,FLOATLITERAL,FOLLOW_FLOATLITERAL_in_literal8382); if (state.failed) return;
					if ( state.backtracking==0 ) { cAdd((FLOATLITERAL21!=null?FLOATLITERAL21.getText():null));}
					}
					break;
				case 4 :
					// /Users/byung/workspace/antlr2/Java.g:1284:9: DOUBLELITERAL
					{
					DOUBLELITERAL22=(Token)match(input,DOUBLELITERAL,FOLLOW_DOUBLELITERAL_in_literal8394); if (state.failed) return;
					if ( state.backtracking==0 ) { cAdd((DOUBLELITERAL22!=null?DOUBLELITERAL22.getText():null));}
					}
					break;
				case 5 :
					// /Users/byung/workspace/antlr2/Java.g:1285:9: CHARLITERAL
					{
					CHARLITERAL23=(Token)match(input,CHARLITERAL,FOLLOW_CHARLITERAL_in_literal8406); if (state.failed) return;
					if ( state.backtracking==0 ) {  System.out.println("Adding Char"); cAdd((CHARLITERAL23!=null?CHARLITERAL23.getText():null));}
					}
					break;
				case 6 :
					// /Users/byung/workspace/antlr2/Java.g:1286:9: STRINGLITERAL
					{
					STRINGLITERAL24=(Token)match(input,STRINGLITERAL,FOLLOW_STRINGLITERAL_in_literal8418); if (state.failed) return;
					if ( state.backtracking==0 ) { System.out.println("Adding String"); cAdd((STRINGLITERAL24!=null?STRINGLITERAL24.getText():null));}
					}
					break;
				case 7 :
					// /Users/byung/workspace/antlr2/Java.g:1287:9: TRUE
					{
					TRUE25=(Token)match(input,TRUE,FOLLOW_TRUE_in_literal8430); if (state.failed) return;
					if ( state.backtracking==0 ) { cAdd((TRUE25!=null?TRUE25.getText():null));}
					}
					break;
				case 8 :
					// /Users/byung/workspace/antlr2/Java.g:1288:9: FALSE
					{
					FALSE26=(Token)match(input,FALSE,FOLLOW_FALSE_in_literal8442); if (state.failed) return;
					if ( state.backtracking==0 ) { cAdd((FALSE26!=null?FALSE26.getText():null));}
					}
					break;
				case 9 :
					// /Users/byung/workspace/antlr2/Java.g:1289:9: NULL
					{
					match(input,NULL,FOLLOW_NULL_in_literal8454); if (state.failed) return;
					if ( state.backtracking==0 ) { kAdd("null");}
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 102, literal_StartIndex); }

		}
	}
	// $ANTLR end "literal"



	// $ANTLR start "classHeader"
	// /Users/byung/workspace/antlr2/Java.g:1296:1: classHeader : modifiers 'class' IDENTIFIER ;
	public final void classHeader() throws RecognitionException {
		int classHeader_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1297:5: ( modifiers 'class' IDENTIFIER )
			// /Users/byung/workspace/antlr2/Java.g:1297:9: modifiers 'class' IDENTIFIER
			{
			pushFollow(FOLLOW_modifiers_in_classHeader8480);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			match(input,CLASS,FOLLOW_CLASS_in_classHeader8482); if (state.failed) return;
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classHeader8484); if (state.failed) return;
			if ( state.backtracking==0 ) { kAdd("class"); }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 103, classHeader_StartIndex); }

		}
	}
	// $ANTLR end "classHeader"



	// $ANTLR start "enumHeader"
	// /Users/byung/workspace/antlr2/Java.g:1300:1: enumHeader : modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER ;
	public final void enumHeader() throws RecognitionException {
		int enumHeader_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1301:5: ( modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER )
			// /Users/byung/workspace/antlr2/Java.g:1301:9: modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER
			{
			pushFollow(FOLLOW_modifiers_in_enumHeader8506);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			if ( input.LA(1)==ENUM||input.LA(1)==IDENTIFIER ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumHeader8514); if (state.failed) return;
			if ( state.backtracking==0 ) { kAdd("enum"); }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 104, enumHeader_StartIndex); }

		}
	}
	// $ANTLR end "enumHeader"



	// $ANTLR start "interfaceHeader"
	// /Users/byung/workspace/antlr2/Java.g:1304:1: interfaceHeader : modifiers 'interface' IDENTIFIER ;
	public final void interfaceHeader() throws RecognitionException {
		int interfaceHeader_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1305:5: ( modifiers 'interface' IDENTIFIER )
			// /Users/byung/workspace/antlr2/Java.g:1305:9: modifiers 'interface' IDENTIFIER
			{
			pushFollow(FOLLOW_modifiers_in_interfaceHeader8536);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			match(input,INTERFACE,FOLLOW_INTERFACE_in_interfaceHeader8538); if (state.failed) return;
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_interfaceHeader8540); if (state.failed) return;
			if ( state.backtracking==0 ) { kAdd("interface"); }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 105, interfaceHeader_StartIndex); }

		}
	}
	// $ANTLR end "interfaceHeader"



	// $ANTLR start "annotationHeader"
	// /Users/byung/workspace/antlr2/Java.g:1308:1: annotationHeader : modifiers '@' 'interface' IDENTIFIER ;
	public final void annotationHeader() throws RecognitionException {
		int annotationHeader_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1309:5: ( modifiers '@' 'interface' IDENTIFIER )
			// /Users/byung/workspace/antlr2/Java.g:1309:9: modifiers '@' 'interface' IDENTIFIER
			{
			pushFollow(FOLLOW_modifiers_in_annotationHeader8562);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotationHeader8564); if (state.failed) return;
			match(input,INTERFACE,FOLLOW_INTERFACE_in_annotationHeader8566); if (state.failed) return;
			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationHeader8568); if (state.failed) return;
			if ( state.backtracking==0 ) { sAdd("@");kAdd("class"); }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 106, annotationHeader_StartIndex); }

		}
	}
	// $ANTLR end "annotationHeader"



	// $ANTLR start "typeHeader"
	// /Users/byung/workspace/antlr2/Java.g:1312:1: typeHeader : modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER ;
	public final void typeHeader() throws RecognitionException {
		int typeHeader_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1313:5: ( modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER )
			// /Users/byung/workspace/antlr2/Java.g:1313:9: modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER
			{
			pushFollow(FOLLOW_modifiers_in_typeHeader8590);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1313:19: ( 'class' | 'enum' | ( ( '@' )? 'interface' ) )
			int alt168=3;
			switch ( input.LA(1) ) {
			case CLASS:
				{
				alt168=1;
				}
				break;
			case ENUM:
				{
				alt168=2;
				}
				break;
			case INTERFACE:
			case MONKEYS_AT:
				{
				alt168=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 168, 0, input);
				throw nvae;
			}
			switch (alt168) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1313:20: 'class'
					{
					match(input,CLASS,FOLLOW_CLASS_in_typeHeader8593); if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1313:28: 'enum'
					{
					match(input,ENUM,FOLLOW_ENUM_in_typeHeader8595); if (state.failed) return;
					}
					break;
				case 3 :
					// /Users/byung/workspace/antlr2/Java.g:1313:35: ( ( '@' )? 'interface' )
					{
					// /Users/byung/workspace/antlr2/Java.g:1313:35: ( ( '@' )? 'interface' )
					// /Users/byung/workspace/antlr2/Java.g:1313:36: ( '@' )? 'interface'
					{
					// /Users/byung/workspace/antlr2/Java.g:1313:36: ( '@' )?
					int alt167=2;
					int LA167_0 = input.LA(1);
					if ( (LA167_0==MONKEYS_AT) ) {
						alt167=1;
					}
					switch (alt167) {
						case 1 :
							// /Users/byung/workspace/antlr2/Java.g:1313:36: '@'
							{
							match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_typeHeader8598); if (state.failed) return;
							}
							break;

					}

					match(input,INTERFACE,FOLLOW_INTERFACE_in_typeHeader8602); if (state.failed) return;
					}

					}
					break;

			}

			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_typeHeader8606); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 107, typeHeader_StartIndex); }

		}
	}
	// $ANTLR end "typeHeader"



	// $ANTLR start "methodHeader"
	// /Users/byung/workspace/antlr2/Java.g:1316:1: methodHeader : modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '(' ;
	public final void methodHeader() throws RecognitionException {
		int methodHeader_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1317:5: ( modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '(' )
			// /Users/byung/workspace/antlr2/Java.g:1317:9: modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '('
			{
			pushFollow(FOLLOW_modifiers_in_methodHeader8626);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1317:19: ( typeParameters )?
			int alt169=2;
			int LA169_0 = input.LA(1);
			if ( (LA169_0==LT) ) {
				alt169=1;
			}
			switch (alt169) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1317:19: typeParameters
					{
					pushFollow(FOLLOW_typeParameters_in_methodHeader8628);
					typeParameters();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// /Users/byung/workspace/antlr2/Java.g:1317:35: ( type | 'void' )?
			int alt170=3;
			switch ( input.LA(1) ) {
				case IDENTIFIER:
					{
					int LA170_1 = input.LA(2);
					if ( (LA170_1==DOT||LA170_1==IDENTIFIER||LA170_1==LBRACKET||LA170_1==LT) ) {
						alt170=1;
					}
					}
					break;
				case BOOLEAN:
				case BYTE:
				case CHAR:
				case DOUBLE:
				case FLOAT:
				case INT:
				case LONG:
				case SHORT:
					{
					alt170=1;
					}
					break;
				case VOID:
					{
					alt170=2;
					}
					break;
			}
			switch (alt170) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1317:36: type
					{
					pushFollow(FOLLOW_type_in_methodHeader8632);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/byung/workspace/antlr2/Java.g:1317:41: 'void'
					{
					match(input,VOID,FOLLOW_VOID_in_methodHeader8634); if (state.failed) return;
					}
					break;

			}

			match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodHeader8638); if (state.failed) return;
			match(input,LPAREN,FOLLOW_LPAREN_in_methodHeader8640); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 108, methodHeader_StartIndex); }

		}
	}
	// $ANTLR end "methodHeader"



	// $ANTLR start "fieldHeader"
	// /Users/byung/workspace/antlr2/Java.g:1320:1: fieldHeader : modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) ;
	public final void fieldHeader() throws RecognitionException {
		int fieldHeader_StartIndex = input.index();

		Token IDENTIFIER28=null;
		ParserRuleReturnScope type27 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1321:5: ( modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) )
			// /Users/byung/workspace/antlr2/Java.g:1321:9: modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' )
			{
			pushFollow(FOLLOW_modifiers_in_fieldHeader8660);
			modifiers();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_type_in_fieldHeader8662);
			type27=type();
			state._fsp--;
			if (state.failed) return;
			IDENTIFIER28=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldHeader8664); if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1321:35: ( '[' ']' )*
			loop171:
			while (true) {
				int alt171=2;
				int LA171_0 = input.LA(1);
				if ( (LA171_0==LBRACKET) ) {
					alt171=1;
				}

				switch (alt171) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1321:36: '[' ']'
					{
					match(input,LBRACKET,FOLLOW_LBRACKET_in_fieldHeader8667); if (state.failed) return;
					match(input,RBRACKET,FOLLOW_RBRACKET_in_fieldHeader8668); if (state.failed) return;
					}
					break;

				default :
					break loop171;
				}
			}

			if ( input.LA(1)==COMMA||input.LA(1)==EQ||input.LA(1)==SEMI ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			if ( state.backtracking==0 ) {System.out.println("TYPe: " + (type27!=null?input.toString(type27.start,type27.stop):null) + (IDENTIFIER28!=null?IDENTIFIER28.getText():null));if(isPrimitive((type27!=null?input.toString(type27.start,type27.stop):null))) { uAdd((IDENTIFIER28!=null?IDENTIFIER28.getText():null));} }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 109, fieldHeader_StartIndex); }

		}
	}
	// $ANTLR end "fieldHeader"



	// $ANTLR start "localVariableHeader"
	// /Users/byung/workspace/antlr2/Java.g:1324:1: localVariableHeader : variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) ;
	public final void localVariableHeader() throws RecognitionException {
		int localVariableHeader_StartIndex = input.index();

		Token IDENTIFIER30=null;
		ParserRuleReturnScope type29 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return; }

			// /Users/byung/workspace/antlr2/Java.g:1325:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) )
			// /Users/byung/workspace/antlr2/Java.g:1325:9: variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' )
			{
			pushFollow(FOLLOW_variableModifiers_in_localVariableHeader8700);
			variableModifiers();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_type_in_localVariableHeader8702);
			type29=type();
			state._fsp--;
			if (state.failed) return;
			IDENTIFIER30=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_localVariableHeader8704); if (state.failed) return;
			// /Users/byung/workspace/antlr2/Java.g:1325:43: ( '[' ']' )*
			loop172:
			while (true) {
				int alt172=2;
				int LA172_0 = input.LA(1);
				if ( (LA172_0==LBRACKET) ) {
					alt172=1;
				}

				switch (alt172) {
				case 1 :
					// /Users/byung/workspace/antlr2/Java.g:1325:44: '[' ']'
					{
					match(input,LBRACKET,FOLLOW_LBRACKET_in_localVariableHeader8707); if (state.failed) return;
					match(input,RBRACKET,FOLLOW_RBRACKET_in_localVariableHeader8708); if (state.failed) return;
					}
					break;

				default :
					break loop172;
				}
			}

			if ( input.LA(1)==COMMA||input.LA(1)==EQ||input.LA(1)==SEMI ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			if ( state.backtracking==0 ) {System.out.println("TYPe: " + (type29!=null?input.toString(type29.start,type29.stop):null) + (IDENTIFIER30!=null?IDENTIFIER30.getText():null));if(isPrimitive((type29!=null?input.toString(type29.start,type29.stop):null))) { uAdd((IDENTIFIER30!=null?IDENTIFIER30.getText():null));} }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 110, localVariableHeader_StartIndex); }

		}
	}
	// $ANTLR end "localVariableHeader"

	// $ANTLR start synpred2_Java
	public final void synpred2_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:335:13: ( ( annotations )? packageDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:335:13: ( annotations )? packageDeclaration
		{
		// /Users/byung/workspace/antlr2/Java.g:335:13: ( annotations )?
		int alt173=2;
		int LA173_0 = input.LA(1);
		if ( (LA173_0==MONKEYS_AT) ) {
			alt173=1;
		}
		switch (alt173) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:335:14: annotations
				{
				pushFollow(FOLLOW_annotations_in_synpred2_Java91);
				annotations();
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		pushFollow(FOLLOW_packageDeclaration_in_synpred2_Java120);
		packageDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred2_Java

	// $ANTLR start synpred12_Java
	public final void synpred12_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:379:10: ( classDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:379:10: classDeclaration
		{
		pushFollow(FOLLOW_classDeclaration_in_synpred12_Java501);
		classDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred12_Java

	// $ANTLR start synpred27_Java
	public final void synpred27_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:410:9: ( normalClassDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:410:9: normalClassDeclaration
		{
		pushFollow(FOLLOW_normalClassDeclaration_in_synpred27_Java755);
		normalClassDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred27_Java

	// $ANTLR start synpred43_Java
	public final void synpred43_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:502:9: ( normalInterfaceDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:502:9: normalInterfaceDeclaration
		{
		pushFollow(FOLLOW_normalInterfaceDeclaration_in_synpred43_Java1486);
		normalInterfaceDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred43_Java

	// $ANTLR start synpred52_Java
	public final void synpred52_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:547:10: ( fieldDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:547:10: fieldDeclaration
		{
		pushFollow(FOLLOW_fieldDeclaration_in_synpred52_Java1844);
		fieldDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred52_Java

	// $ANTLR start synpred53_Java
	public final void synpred53_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:548:10: ( methodDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:548:10: methodDeclaration
		{
		pushFollow(FOLLOW_methodDeclaration_in_synpred53_Java1855);
		methodDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred53_Java

	// $ANTLR start synpred54_Java
	public final void synpred54_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:549:10: ( classDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:549:10: classDeclaration
		{
		pushFollow(FOLLOW_classDeclaration_in_synpred54_Java1866);
		classDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred54_Java

	// $ANTLR start synpred57_Java
	public final void synpred57_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:566:10: ( explicitConstructorInvocation )
		// /Users/byung/workspace/antlr2/Java.g:566:10: explicitConstructorInvocation
		{
		pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred57_Java2011);
		explicitConstructorInvocation();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred57_Java

	// $ANTLR start synpred59_Java
	public final void synpred59_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:558:10: ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' )
		// /Users/byung/workspace/antlr2/Java.g:558:10: modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
		{
		pushFollow(FOLLOW_modifiers_in_synpred59_Java1916);
		modifiers();
		state._fsp--;
		if (state.failed) return;
		// /Users/byung/workspace/antlr2/Java.g:559:9: ( typeParameters )?
		int alt176=2;
		int LA176_0 = input.LA(1);
		if ( (LA176_0==LT) ) {
			alt176=1;
		}
		switch (alt176) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:559:10: typeParameters
				{
				pushFollow(FOLLOW_typeParameters_in_synpred59_Java1927);
				typeParameters();
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred59_Java1948); if (state.failed) return;
		pushFollow(FOLLOW_formalParameters_in_synpred59_Java1962);
		formalParameters();
		state._fsp--;
		if (state.failed) return;
		// /Users/byung/workspace/antlr2/Java.g:563:9: ( 'throws' qualifiedNameList )?
		int alt177=2;
		int LA177_0 = input.LA(1);
		if ( (LA177_0==THROWS) ) {
			alt177=1;
		}
		switch (alt177) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:563:10: 'throws' qualifiedNameList
				{
				match(input,THROWS,FOLLOW_THROWS_in_synpred59_Java1973); if (state.failed) return;
				pushFollow(FOLLOW_qualifiedNameList_in_synpred59_Java1975);
				qualifiedNameList();
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		match(input,LBRACE,FOLLOW_LBRACE_in_synpred59_Java1998); if (state.failed) return;
		// /Users/byung/workspace/antlr2/Java.g:566:9: ( explicitConstructorInvocation )?
		int alt178=2;
		alt178 = dfa178.predict(input);
		switch (alt178) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:566:10: explicitConstructorInvocation
				{
				pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred59_Java2011);
				explicitConstructorInvocation();
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		// /Users/byung/workspace/antlr2/Java.g:568:9: ( blockStatement )*
		loop179:
		while (true) {
			int alt179=2;
			int LA179_0 = input.LA(1);
			if ( (LA179_0==ABSTRACT||(LA179_0 >= ASSERT && LA179_0 <= BANG)||(LA179_0 >= BOOLEAN && LA179_0 <= BYTE)||(LA179_0 >= CHAR && LA179_0 <= CLASS)||LA179_0==CONTINUE||LA179_0==DO||(LA179_0 >= DOUBLE && LA179_0 <= DOUBLELITERAL)||LA179_0==ENUM||(LA179_0 >= FALSE && LA179_0 <= FINAL)||(LA179_0 >= FLOAT && LA179_0 <= FOR)||(LA179_0 >= IDENTIFIER && LA179_0 <= IF)||(LA179_0 >= INT && LA179_0 <= INTLITERAL)||LA179_0==LBRACE||(LA179_0 >= LONG && LA179_0 <= LT)||(LA179_0 >= MONKEYS_AT && LA179_0 <= NULL)||LA179_0==PLUS||(LA179_0 >= PLUSPLUS && LA179_0 <= PUBLIC)||LA179_0==RETURN||(LA179_0 >= SEMI && LA179_0 <= SHORT)||(LA179_0 >= STATIC && LA179_0 <= SUB)||(LA179_0 >= SUBSUB && LA179_0 <= SYNCHRONIZED)||(LA179_0 >= THIS && LA179_0 <= THROW)||(LA179_0 >= TILDE && LA179_0 <= WHILE)) ) {
				alt179=1;
			}

			switch (alt179) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:568:10: blockStatement
				{
				pushFollow(FOLLOW_blockStatement_in_synpred59_Java2033);
				blockStatement();
				state._fsp--;
				if (state.failed) return;
				}
				break;

			default :
				break loop179;
			}
		}

		match(input,RBRACE,FOLLOW_RBRACE_in_synpred59_Java2054); if (state.failed) return;
		}

	}
	// $ANTLR end synpred59_Java

	// $ANTLR start synpred68_Java
	public final void synpred68_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:614:9: ( interfaceFieldDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:614:9: interfaceFieldDeclaration
		{
		pushFollow(FOLLOW_interfaceFieldDeclaration_in_synpred68_Java2497);
		interfaceFieldDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred68_Java

	// $ANTLR start synpred69_Java
	public final void synpred69_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:615:9: ( interfaceMethodDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:615:9: interfaceMethodDeclaration
		{
		pushFollow(FOLLOW_interfaceMethodDeclaration_in_synpred69_Java2507);
		interfaceMethodDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred69_Java

	// $ANTLR start synpred70_Java
	public final void synpred70_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:616:9: ( interfaceDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:616:9: interfaceDeclaration
		{
		pushFollow(FOLLOW_interfaceDeclaration_in_synpred70_Java2517);
		interfaceDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred70_Java

	// $ANTLR start synpred71_Java
	public final void synpred71_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:617:9: ( classDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:617:9: classDeclaration
		{
		pushFollow(FOLLOW_classDeclaration_in_synpred71_Java2527);
		classDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred71_Java

	// $ANTLR start synpred96_Java
	public final void synpred96_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:712:9: ( ellipsisParameterDecl )
		// /Users/byung/workspace/antlr2/Java.g:712:9: ellipsisParameterDecl
		{
		pushFollow(FOLLOW_ellipsisParameterDecl_in_synpred96_Java3351);
		ellipsisParameterDecl();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred96_Java

	// $ANTLR start synpred98_Java
	public final void synpred98_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:713:9: ( normalParameterDecl ( ',' normalParameterDecl )* )
		// /Users/byung/workspace/antlr2/Java.g:713:9: normalParameterDecl ( ',' normalParameterDecl )*
		{
		pushFollow(FOLLOW_normalParameterDecl_in_synpred98_Java3361);
		normalParameterDecl();
		state._fsp--;
		if (state.failed) return;
		// /Users/byung/workspace/antlr2/Java.g:714:9: ( ',' normalParameterDecl )*
		loop182:
		while (true) {
			int alt182=2;
			int LA182_0 = input.LA(1);
			if ( (LA182_0==COMMA) ) {
				alt182=1;
			}

			switch (alt182) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:714:10: ',' normalParameterDecl
				{
				match(input,COMMA,FOLLOW_COMMA_in_synpred98_Java3372); if (state.failed) return;
				pushFollow(FOLLOW_normalParameterDecl_in_synpred98_Java3374);
				normalParameterDecl();
				state._fsp--;
				if (state.failed) return;
				}
				break;

			default :
				break loop182;
			}
		}

		}

	}
	// $ANTLR end synpred98_Java

	// $ANTLR start synpred99_Java
	public final void synpred99_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:716:10: ( normalParameterDecl ',' )
		// /Users/byung/workspace/antlr2/Java.g:716:10: normalParameterDecl ','
		{
		pushFollow(FOLLOW_normalParameterDecl_in_synpred99_Java3398);
		normalParameterDecl();
		state._fsp--;
		if (state.failed) return;
		match(input,COMMA,FOLLOW_COMMA_in_synpred99_Java3408); if (state.failed) return;
		}

	}
	// $ANTLR end synpred99_Java

	// $ANTLR start synpred103_Java
	public final void synpred103_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:736:9: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' )
		// /Users/byung/workspace/antlr2/Java.g:736:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
		{
		// /Users/byung/workspace/antlr2/Java.g:736:9: ( nonWildcardTypeArguments )?
		int alt183=2;
		int LA183_0 = input.LA(1);
		if ( (LA183_0==LT) ) {
			alt183=1;
		}
		switch (alt183) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:736:10: nonWildcardTypeArguments
				{
				pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred103_Java3551);
				nonWildcardTypeArguments();
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		if ( input.LA(1)==SUPER||input.LA(1)==THIS ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_arguments_in_synpred103_Java3613);
		arguments();
		state._fsp--;
		if (state.failed) return;
		match(input,SEMI,FOLLOW_SEMI_in_synpred103_Java3615); if (state.failed) return;
		}

	}
	// $ANTLR end synpred103_Java

	// $ANTLR start synpred117_Java
	public final void synpred117_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:823:9: ( annotationMethodDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:823:9: annotationMethodDeclaration
		{
		pushFollow(FOLLOW_annotationMethodDeclaration_in_synpred117_Java4247);
		annotationMethodDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred117_Java

	// $ANTLR start synpred118_Java
	public final void synpred118_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:824:9: ( interfaceFieldDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:824:9: interfaceFieldDeclaration
		{
		pushFollow(FOLLOW_interfaceFieldDeclaration_in_synpred118_Java4257);
		interfaceFieldDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred118_Java

	// $ANTLR start synpred119_Java
	public final void synpred119_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:825:9: ( normalClassDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:825:9: normalClassDeclaration
		{
		pushFollow(FOLLOW_normalClassDeclaration_in_synpred119_Java4267);
		normalClassDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred119_Java

	// $ANTLR start synpred120_Java
	public final void synpred120_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:826:9: ( normalInterfaceDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:826:9: normalInterfaceDeclaration
		{
		pushFollow(FOLLOW_normalInterfaceDeclaration_in_synpred120_Java4277);
		normalInterfaceDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred120_Java

	// $ANTLR start synpred121_Java
	public final void synpred121_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:827:9: ( enumDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:827:9: enumDeclaration
		{
		pushFollow(FOLLOW_enumDeclaration_in_synpred121_Java4287);
		enumDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred121_Java

	// $ANTLR start synpred122_Java
	public final void synpred122_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:828:9: ( annotationTypeDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:828:9: annotationTypeDeclaration
		{
		pushFollow(FOLLOW_annotationTypeDeclaration_in_synpred122_Java4297);
		annotationTypeDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred122_Java

	// $ANTLR start synpred125_Java
	public final void synpred125_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:871:9: ( localVariableDeclarationStatement )
		// /Users/byung/workspace/antlr2/Java.g:871:9: localVariableDeclarationStatement
		{
		pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred125_Java4471);
		localVariableDeclarationStatement();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred125_Java

	// $ANTLR start synpred126_Java
	public final void synpred126_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:872:9: ( classOrInterfaceDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:872:9: classOrInterfaceDeclaration
		{
		pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred126_Java4481);
		classOrInterfaceDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred126_Java

	// $ANTLR start synpred130_Java
	public final void synpred130_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:892:9: ( ( 'assert' ) expression ( ':' expression )? ';' )
		// /Users/byung/workspace/antlr2/Java.g:892:9: ( 'assert' ) expression ( ':' expression )? ';'
		{
		// /Users/byung/workspace/antlr2/Java.g:892:9: ( 'assert' )
		// /Users/byung/workspace/antlr2/Java.g:892:10: 'assert'
		{
		match(input,ASSERT,FOLLOW_ASSERT_in_synpred130_Java4628); if (state.failed) return;
		}

		pushFollow(FOLLOW_expression_in_synpred130_Java4650);
		expression();
		state._fsp--;
		if (state.failed) return;
		// /Users/byung/workspace/antlr2/Java.g:894:20: ( ':' expression )?
		int alt186=2;
		int LA186_0 = input.LA(1);
		if ( (LA186_0==COLON) ) {
			alt186=1;
		}
		switch (alt186) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:894:21: ':' expression
				{
				match(input,COLON,FOLLOW_COLON_in_synpred130_Java4653); if (state.failed) return;
				pushFollow(FOLLOW_expression_in_synpred130_Java4655);
				expression();
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		match(input,SEMI,FOLLOW_SEMI_in_synpred130_Java4661); if (state.failed) return;
		}

	}
	// $ANTLR end synpred130_Java

	// $ANTLR start synpred132_Java
	public final void synpred132_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:895:9: ( 'assert' expression ( ':' expression )? ';' )
		// /Users/byung/workspace/antlr2/Java.g:895:9: 'assert' expression ( ':' expression )? ';'
		{
		match(input,ASSERT,FOLLOW_ASSERT_in_synpred132_Java4673); if (state.failed) return;
		pushFollow(FOLLOW_expression_in_synpred132_Java4676);
		expression();
		state._fsp--;
		if (state.failed) return;
		// /Users/byung/workspace/antlr2/Java.g:895:30: ( ':' expression )?
		int alt187=2;
		int LA187_0 = input.LA(1);
		if ( (LA187_0==COLON) ) {
			alt187=1;
		}
		switch (alt187) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:895:31: ':' expression
				{
				match(input,COLON,FOLLOW_COLON_in_synpred132_Java4679); if (state.failed) return;
				pushFollow(FOLLOW_expression_in_synpred132_Java4681);
				expression();
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		match(input,SEMI,FOLLOW_SEMI_in_synpred132_Java4687); if (state.failed) return;
		}

	}
	// $ANTLR end synpred132_Java

	// $ANTLR start synpred133_Java
	public final void synpred133_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:896:39: ( 'else' statement )
		// /Users/byung/workspace/antlr2/Java.g:896:39: 'else' statement
		{
		match(input,ELSE,FOLLOW_ELSE_in_synpred133_Java4718); if (state.failed) return;
		pushFollow(FOLLOW_statement_in_synpred133_Java4720);
		statement();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred133_Java

	// $ANTLR start synpred148_Java
	public final void synpred148_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:911:9: ( expression ';' )
		// /Users/byung/workspace/antlr2/Java.g:911:9: expression ';'
		{
		pushFollow(FOLLOW_expression_in_synpred148_Java4965);
		expression();
		state._fsp--;
		if (state.failed) return;
		match(input,SEMI,FOLLOW_SEMI_in_synpred148_Java4968); if (state.failed) return;
		}

	}
	// $ANTLR end synpred148_Java

	// $ANTLR start synpred149_Java
	public final void synpred149_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:912:9: ( IDENTIFIER ':' statement )
		// /Users/byung/workspace/antlr2/Java.g:912:9: IDENTIFIER ':' statement
		{
		match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred149_Java4984); if (state.failed) return;
		match(input,COLON,FOLLOW_COLON_in_synpred149_Java4986); if (state.failed) return;
		pushFollow(FOLLOW_statement_in_synpred149_Java4988);
		statement();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred149_Java

	// $ANTLR start synpred153_Java
	public final void synpred153_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:936:13: ( catches 'finally' block )
		// /Users/byung/workspace/antlr2/Java.g:936:13: catches 'finally' block
		{
		pushFollow(FOLLOW_catches_in_synpred153_Java5153);
		catches();
		state._fsp--;
		if (state.failed) return;
		match(input,FINALLY,FOLLOW_FINALLY_in_synpred153_Java5155); if (state.failed) return;
		pushFollow(FOLLOW_block_in_synpred153_Java5157);
		block();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred153_Java

	// $ANTLR start synpred154_Java
	public final void synpred154_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:937:13: ( catches )
		// /Users/byung/workspace/antlr2/Java.g:937:13: catches
		{
		pushFollow(FOLLOW_catches_in_synpred154_Java5173);
		catches();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred154_Java

	// $ANTLR start synpred157_Java
	public final void synpred157_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:962:9: ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement )
		// /Users/byung/workspace/antlr2/Java.g:962:9: 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement
		{
		match(input,FOR,FOLLOW_FOR_in_synpred157_Java5373); if (state.failed) return;
		match(input,LPAREN,FOLLOW_LPAREN_in_synpred157_Java5375); if (state.failed) return;
		pushFollow(FOLLOW_variableModifiers_in_synpred157_Java5377);
		variableModifiers();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_type_in_synpred157_Java5379);
		type();
		state._fsp--;
		if (state.failed) return;
		match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred157_Java5381); if (state.failed) return;
		match(input,COLON,FOLLOW_COLON_in_synpred157_Java5383); if (state.failed) return;
		pushFollow(FOLLOW_expression_in_synpred157_Java5398);
		expression();
		state._fsp--;
		if (state.failed) return;
		match(input,RPAREN,FOLLOW_RPAREN_in_synpred157_Java5400); if (state.failed) return;
		pushFollow(FOLLOW_statement_in_synpred157_Java5402);
		statement();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred157_Java

	// $ANTLR start synpred161_Java
	public final void synpred161_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:976:9: ( localVariableDeclaration )
		// /Users/byung/workspace/antlr2/Java.g:976:9: localVariableDeclaration
		{
		pushFollow(FOLLOW_localVariableDeclaration_in_synpred161_Java5589);
		localVariableDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred161_Java

	// $ANTLR start synpred202_Java
	public final void synpred202_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1131:9: ( castExpression )
		// /Users/byung/workspace/antlr2/Java.g:1131:9: castExpression
		{
		pushFollow(FOLLOW_castExpression_in_synpred202_Java6935);
		castExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred202_Java

	// $ANTLR start synpred206_Java
	public final void synpred206_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1141:9: ( '(' primitiveType ')' unaryExpression )
		// /Users/byung/workspace/antlr2/Java.g:1141:9: '(' primitiveType ')' unaryExpression
		{
		match(input,LPAREN,FOLLOW_LPAREN_in_synpred206_Java7032); if (state.failed) return;
		pushFollow(FOLLOW_primitiveType_in_synpred206_Java7034);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		match(input,RPAREN,FOLLOW_RPAREN_in_synpred206_Java7036); if (state.failed) return;
		pushFollow(FOLLOW_unaryExpression_in_synpred206_Java7038);
		unaryExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred206_Java

	// $ANTLR start synpred208_Java
	public final void synpred208_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1151:10: ( '.' IDENTIFIER )
		// /Users/byung/workspace/antlr2/Java.g:1151:10: '.' IDENTIFIER
		{
		match(input,DOT,FOLLOW_DOT_in_synpred208_Java7115); if (state.failed) return;
		match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred208_Java7117); if (state.failed) return;
		}

	}
	// $ANTLR end synpred208_Java

	// $ANTLR start synpred209_Java
	public final void synpred209_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1153:10: ( identifierSuffix )
		// /Users/byung/workspace/antlr2/Java.g:1153:10: identifierSuffix
		{
		pushFollow(FOLLOW_identifierSuffix_in_synpred209_Java7141);
		identifierSuffix();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred209_Java

	// $ANTLR start synpred211_Java
	public final void synpred211_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1156:10: ( '.' IDENTIFIER )
		// /Users/byung/workspace/antlr2/Java.g:1156:10: '.' IDENTIFIER
		{
		match(input,DOT,FOLLOW_DOT_in_synpred211_Java7175); if (state.failed) return;
		match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred211_Java7177); if (state.failed) return;
		}

	}
	// $ANTLR end synpred211_Java

	// $ANTLR start synpred212_Java
	public final void synpred212_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1158:10: ( identifierSuffix )
		// /Users/byung/workspace/antlr2/Java.g:1158:10: identifierSuffix
		{
		pushFollow(FOLLOW_identifierSuffix_in_synpred212_Java7202);
		identifierSuffix();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred212_Java

	// $ANTLR start synpred224_Java
	public final void synpred224_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1186:10: ( '[' expression ']' )
		// /Users/byung/workspace/antlr2/Java.g:1186:10: '[' expression ']'
		{
		match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred224_Java7479); if (state.failed) return;
		pushFollow(FOLLOW_expression_in_synpred224_Java7481);
		expression();
		state._fsp--;
		if (state.failed) return;
		match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred224_Java7483); if (state.failed) return;
		}

	}
	// $ANTLR end synpred224_Java

	// $ANTLR start synpred236_Java
	public final void synpred236_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1209:9: ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest )
		// /Users/byung/workspace/antlr2/Java.g:1209:9: 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest
		{
		match(input,NEW,FOLLOW_NEW_in_synpred236_Java7709); if (state.failed) return;
		pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred236_Java7711);
		nonWildcardTypeArguments();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_classOrInterfaceType_in_synpred236_Java7713);
		classOrInterfaceType();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_classCreatorRest_in_synpred236_Java7715);
		classCreatorRest();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred236_Java

	// $ANTLR start synpred237_Java
	public final void synpred237_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1210:9: ( 'new' classOrInterfaceType classCreatorRest )
		// /Users/byung/workspace/antlr2/Java.g:1210:9: 'new' classOrInterfaceType classCreatorRest
		{
		match(input,NEW,FOLLOW_NEW_in_synpred237_Java7727); if (state.failed) return;
		pushFollow(FOLLOW_classOrInterfaceType_in_synpred237_Java7729);
		classOrInterfaceType();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_classCreatorRest_in_synpred237_Java7731);
		classCreatorRest();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred237_Java

	// $ANTLR start synpred239_Java
	public final void synpred239_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1215:9: ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer )
		// /Users/byung/workspace/antlr2/Java.g:1215:9: 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer
		{
		match(input,NEW,FOLLOW_NEW_in_synpred239_Java7763); if (state.failed) return;
		pushFollow(FOLLOW_createdName_in_synpred239_Java7765);
		createdName();
		state._fsp--;
		if (state.failed) return;
		match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred239_Java7777); if (state.failed) return;
		match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred239_Java7779); if (state.failed) return;
		// /Users/byung/workspace/antlr2/Java.g:1217:9: ( '[' ']' )*
		loop200:
		while (true) {
			int alt200=2;
			int LA200_0 = input.LA(1);
			if ( (LA200_0==LBRACKET) ) {
				alt200=1;
			}

			switch (alt200) {
			case 1 :
				// /Users/byung/workspace/antlr2/Java.g:1217:10: '[' ']'
				{
				match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred239_Java7792); if (state.failed) return;
				match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred239_Java7794); if (state.failed) return;
				}
				break;

			default :
				break loop200;
			}
		}

		pushFollow(FOLLOW_arrayInitializer_in_synpred239_Java7817);
		arrayInitializer();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred239_Java

	// $ANTLR start synpred240_Java
	public final void synpred240_Java_fragment() throws RecognitionException {
		// /Users/byung/workspace/antlr2/Java.g:1224:13: ( '[' expression ']' )
		// /Users/byung/workspace/antlr2/Java.g:1224:13: '[' expression ']'
		{
		match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred240_Java7872); if (state.failed) return;
		pushFollow(FOLLOW_expression_in_synpred240_Java7874);
		expression();
		state._fsp--;
		if (state.failed) return;
		match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred240_Java7890); if (state.failed) return;
		}

	}
	// $ANTLR end synpred240_Java

	// Delegated rules

	public final boolean synpred125_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred125_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred122_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred122_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred161_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred161_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred239_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred239_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred153_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred153_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred70_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred70_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred211_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred211_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred236_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred236_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred130_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred130_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred12_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred12_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred57_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred57_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred117_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred117_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred133_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred133_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred68_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred68_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred53_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred53_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred209_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred209_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred119_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred119_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred98_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred98_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred224_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred224_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred121_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred121_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred208_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred208_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred202_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred202_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred59_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred59_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred240_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred240_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred149_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred149_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred132_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred132_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred157_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred157_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred212_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred212_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred52_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred52_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred154_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred154_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred2_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred2_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred71_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred71_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred206_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred206_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred237_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred237_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred148_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred148_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred120_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred120_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred103_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred103_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred96_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred96_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred54_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred54_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred99_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred99_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred69_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred69_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred43_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred43_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred118_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred118_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred126_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred126_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred27_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred27_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}


	protected DFA42 dfa42 = new DFA42(this);
	protected DFA178 dfa178 = new DFA178(this);
	static final String DFA42_eotS =
		"\74\uffff";
	static final String DFA42_eofS =
		"\74\uffff";
	static final String DFA42_minS =
		"\1\4\1\uffff\27\0\43\uffff";
	static final String DFA42_maxS =
		"\1\165\1\uffff\27\0\43\uffff";
	static final String DFA42_acceptS =
		"\1\uffff\1\1\27\uffff\1\2\42\uffff";
	static final String DFA42_specialS =
		"\2\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
		"\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\43\uffff}>";
	static final String[] DFA42_transitionS = {
			"\1\31\3\uffff\2\31\4\uffff\1\20\1\31\1\22\4\uffff\1\21\1\12\1\31\4\uffff"+
			"\1\31\1\uffff\1\31\1\uffff\1\27\1\11\3\uffff\1\31\5\uffff\1\15\1\31\1"+
			"\uffff\1\26\1\10\1\31\5\uffff\1\5\1\31\3\uffff\1\24\1\31\1\6\3\uffff"+
			"\1\31\2\uffff\1\25\1\7\1\4\1\1\1\uffff\2\31\1\17\1\16\4\uffff\1\31\1"+
			"\uffff\4\31\1\uffff\1\31\1\uffff\1\31\1\uffff\1\31\1\23\4\uffff\2\31"+
			"\1\13\1\31\1\uffff\1\31\1\3\2\31\1\uffff\1\2\1\31\1\uffff\2\31\1\14\1"+
			"\31\1\30\2\31",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
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

	static final short[] DFA42_eot = DFA.unpackEncodedString(DFA42_eotS);
	static final short[] DFA42_eof = DFA.unpackEncodedString(DFA42_eofS);
	static final char[] DFA42_min = DFA.unpackEncodedStringToUnsignedChars(DFA42_minS);
	static final char[] DFA42_max = DFA.unpackEncodedStringToUnsignedChars(DFA42_maxS);
	static final short[] DFA42_accept = DFA.unpackEncodedString(DFA42_acceptS);
	static final short[] DFA42_special = DFA.unpackEncodedString(DFA42_specialS);
	static final short[][] DFA42_transition;

	static {
		int numStates = DFA42_transitionS.length;
		DFA42_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA42_transition[i] = DFA.unpackEncodedString(DFA42_transitionS[i]);
		}
	}

	protected class DFA42 extends DFA {

		public DFA42(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 42;
			this.eot = DFA42_eot;
			this.eof = DFA42_eof;
			this.min = DFA42_min;
			this.max = DFA42_max;
			this.accept = DFA42_accept;
			this.special = DFA42_special;
			this.transition = DFA42_transition;
		}
		@Override
		public String getDescription() {
			return "566:9: ( explicitConstructorInvocation )?";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA42_2 = input.LA(1);
						 
						int index42_2 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_2);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA42_3 = input.LA(1);
						 
						int index42_3 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_3);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA42_4 = input.LA(1);
						 
						int index42_4 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_4);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA42_5 = input.LA(1);
						 
						int index42_5 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_5);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA42_6 = input.LA(1);
						 
						int index42_6 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_6);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA42_7 = input.LA(1);
						 
						int index42_7 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_7);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA42_8 = input.LA(1);
						 
						int index42_8 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_8);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA42_9 = input.LA(1);
						 
						int index42_9 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_9);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA42_10 = input.LA(1);
						 
						int index42_10 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_10);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA42_11 = input.LA(1);
						 
						int index42_11 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_11);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA42_12 = input.LA(1);
						 
						int index42_12 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_12);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA42_13 = input.LA(1);
						 
						int index42_13 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_13);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA42_14 = input.LA(1);
						 
						int index42_14 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_14);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA42_15 = input.LA(1);
						 
						int index42_15 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_15);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA42_16 = input.LA(1);
						 
						int index42_16 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_16);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA42_17 = input.LA(1);
						 
						int index42_17 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_17);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA42_18 = input.LA(1);
						 
						int index42_18 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_18);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA42_19 = input.LA(1);
						 
						int index42_19 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_19);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA42_20 = input.LA(1);
						 
						int index42_20 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_20);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA42_21 = input.LA(1);
						 
						int index42_21 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_21);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA42_22 = input.LA(1);
						 
						int index42_22 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_22);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA42_23 = input.LA(1);
						 
						int index42_23 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_23);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA42_24 = input.LA(1);
						 
						int index42_24 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index42_24);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 42, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA178_eotS =
		"\74\uffff";
	static final String DFA178_eofS =
		"\74\uffff";
	static final String DFA178_minS =
		"\1\4\1\uffff\27\0\43\uffff";
	static final String DFA178_maxS =
		"\1\165\1\uffff\27\0\43\uffff";
	static final String DFA178_acceptS =
		"\1\uffff\1\1\27\uffff\1\2\42\uffff";
	static final String DFA178_specialS =
		"\2\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
		"\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\43\uffff}>";
	static final String[] DFA178_transitionS = {
			"\1\31\3\uffff\2\31\4\uffff\1\20\1\31\1\22\4\uffff\1\21\1\12\1\31\4\uffff"+
			"\1\31\1\uffff\1\31\1\uffff\1\27\1\11\3\uffff\1\31\5\uffff\1\15\1\31\1"+
			"\uffff\1\26\1\10\1\31\5\uffff\1\5\1\31\3\uffff\1\24\1\31\1\6\3\uffff"+
			"\1\31\2\uffff\1\25\1\7\1\4\1\1\1\uffff\2\31\1\17\1\16\4\uffff\1\31\1"+
			"\uffff\4\31\1\uffff\1\31\1\uffff\1\31\1\uffff\1\31\1\23\4\uffff\2\31"+
			"\1\13\1\31\1\uffff\1\31\1\3\2\31\1\uffff\1\2\1\31\1\uffff\2\31\1\14\1"+
			"\31\1\30\2\31",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
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

	static final short[] DFA178_eot = DFA.unpackEncodedString(DFA178_eotS);
	static final short[] DFA178_eof = DFA.unpackEncodedString(DFA178_eofS);
	static final char[] DFA178_min = DFA.unpackEncodedStringToUnsignedChars(DFA178_minS);
	static final char[] DFA178_max = DFA.unpackEncodedStringToUnsignedChars(DFA178_maxS);
	static final short[] DFA178_accept = DFA.unpackEncodedString(DFA178_acceptS);
	static final short[] DFA178_special = DFA.unpackEncodedString(DFA178_specialS);
	static final short[][] DFA178_transition;

	static {
		int numStates = DFA178_transitionS.length;
		DFA178_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA178_transition[i] = DFA.unpackEncodedString(DFA178_transitionS[i]);
		}
	}

	protected class DFA178 extends DFA {

		public DFA178(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 178;
			this.eot = DFA178_eot;
			this.eof = DFA178_eof;
			this.min = DFA178_min;
			this.max = DFA178_max;
			this.accept = DFA178_accept;
			this.special = DFA178_special;
			this.transition = DFA178_transition;
		}
		@Override
		public String getDescription() {
			return "566:9: ( explicitConstructorInvocation )?";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA178_2 = input.LA(1);
						 
						int index178_2 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_2);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA178_3 = input.LA(1);
						 
						int index178_3 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_3);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA178_4 = input.LA(1);
						 
						int index178_4 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_4);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA178_5 = input.LA(1);
						 
						int index178_5 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_5);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA178_6 = input.LA(1);
						 
						int index178_6 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_6);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA178_7 = input.LA(1);
						 
						int index178_7 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_7);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA178_8 = input.LA(1);
						 
						int index178_8 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_8);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA178_9 = input.LA(1);
						 
						int index178_9 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_9);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA178_10 = input.LA(1);
						 
						int index178_10 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_10);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA178_11 = input.LA(1);
						 
						int index178_11 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_11);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA178_12 = input.LA(1);
						 
						int index178_12 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_12);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA178_13 = input.LA(1);
						 
						int index178_13 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_13);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA178_14 = input.LA(1);
						 
						int index178_14 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_14);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA178_15 = input.LA(1);
						 
						int index178_15 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_15);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA178_16 = input.LA(1);
						 
						int index178_16 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_16);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA178_17 = input.LA(1);
						 
						int index178_17 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_17);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA178_18 = input.LA(1);
						 
						int index178_18 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_18);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA178_19 = input.LA(1);
						 
						int index178_19 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_19);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA178_20 = input.LA(1);
						 
						int index178_20 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_20);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA178_21 = input.LA(1);
						 
						int index178_21 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_21);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA178_22 = input.LA(1);
						 
						int index178_22 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_22);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA178_23 = input.LA(1);
						 
						int index178_23 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_23);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA178_24 = input.LA(1);
						 
						int index178_24 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred57_Java()) ) {s = 1;}
						else if ( (true) ) {s = 25;}
						 
						input.seek(index178_24);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 178, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	public static final BitSet FOLLOW_annotations_in_compilationUnit91 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
	public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit120 = new BitSet(new long[]{0x1200102000800012L,0x0011040C10700600L});
	public static final BitSet FOLLOW_importDeclaration_in_compilationUnit142 = new BitSet(new long[]{0x1200102000800012L,0x0011040C10700600L});
	public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit164 = new BitSet(new long[]{0x1000102000800012L,0x0011040C10700600L});
	public static final BitSet FOLLOW_PACKAGE_in_packageDeclaration195 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration197 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_packageDeclaration209 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IMPORT_in_importDeclaration232 = new BitSet(new long[]{0x0040000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_STATIC_in_importDeclaration245 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_importDeclaration268 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_DOT_in_importDeclaration270 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
	public static final BitSet FOLLOW_STAR_in_importDeclaration272 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_importDeclaration285 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IMPORT_in_importDeclaration302 = new BitSet(new long[]{0x0040000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_STATIC_in_importDeclaration315 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_importDeclaration339 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_DOT_in_importDeclaration350 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_importDeclaration352 = new BitSet(new long[]{0x0000000080000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_DOT_in_importDeclaration376 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
	public static final BitSet FOLLOW_STAR_in_importDeclaration378 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_importDeclaration402 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedImportName425 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_DOT_in_qualifiedImportName437 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedImportName439 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration470 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMI_in_typeDeclaration480 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration501 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration511 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotation_in_modifiers546 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_PUBLIC_in_modifiers556 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_PROTECTED_in_modifiers568 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_PRIVATE_in_modifiers580 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_STATIC_in_modifiers591 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_ABSTRACT_in_modifiers605 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_FINAL_in_modifiers616 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_NATIVE_in_modifiers627 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_SYNCHRONIZED_in_modifiers638 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_TRANSIENT_in_modifiers649 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_VOLATILE_in_modifiers660 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_STRICTFP_in_modifiers671 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
	public static final BitSet FOLLOW_FINAL_in_variableModifiers704 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
	public static final BitSet FOLLOW_annotation_in_variableModifiers719 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
	public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration755 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration765 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_normalClassDeclaration785 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_CLASS_in_normalClassDeclaration788 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_normalClassDeclaration790 = new BitSet(new long[]{0x0100010000000000L,0x0000000000000082L});
	public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration803 = new BitSet(new long[]{0x0100010000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_EXTENDS_in_normalClassDeclaration825 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_normalClassDeclaration827 = new BitSet(new long[]{0x0100000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_IMPLEMENTS_in_normalClassDeclaration851 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_typeList_in_normalClassDeclaration853 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_classBody_in_normalClassDeclaration888 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LT_in_typeParameters919 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_typeParameter_in_typeParameters935 = new BitSet(new long[]{0x0008000002000000L});
	public static final BitSet FOLLOW_COMMA_in_typeParameters950 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_typeParameter_in_typeParameters952 = new BitSet(new long[]{0x0008000002000000L});
	public static final BitSet FOLLOW_GT_in_typeParameters979 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_typeParameter1001 = new BitSet(new long[]{0x0000010000000002L});
	public static final BitSet FOLLOW_EXTENDS_in_typeParameter1013 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_typeBound_in_typeParameter1015 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_typeBound1050 = new BitSet(new long[]{0x0000000000000022L});
	public static final BitSet FOLLOW_AMP_in_typeBound1061 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_typeBound1063 = new BitSet(new long[]{0x0000000000000022L});
	public static final BitSet FOLLOW_modifiers_in_enumDeclaration1098 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_ENUM_in_enumDeclaration1110 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_enumDeclaration1133 = new BitSet(new long[]{0x0100000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_IMPLEMENTS_in_enumDeclaration1146 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_typeList_in_enumDeclaration1148 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_enumBody_in_enumDeclaration1171 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACE_in_enumBody1206 = new BitSet(new long[]{0x0040000002000000L,0x0000000011000200L});
	public static final BitSet FOLLOW_enumConstants_in_enumBody1219 = new BitSet(new long[]{0x0000000002000000L,0x0000000011000000L});
	public static final BitSet FOLLOW_COMMA_in_enumBody1241 = new BitSet(new long[]{0x0000000000000000L,0x0000000011000000L});
	public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody1255 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
	public static final BitSet FOLLOW_RBRACE_in_enumBody1277 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_enumConstant_in_enumConstants1298 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_COMMA_in_enumConstants1309 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_enumConstant_in_enumConstants1311 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_annotations_in_enumConstant1347 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_enumConstant1368 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000042L});
	public static final BitSet FOLLOW_arguments_in_enumConstant1379 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
	public static final BitSet FOLLOW_classBody_in_enumConstant1401 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMI_in_enumBodyDeclarations1442 = new BitSet(new long[]{0x1840502100A14012L,0x0019040C30700692L});
	public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1455 = new BitSet(new long[]{0x1840502100A14012L,0x0019040C30700692L});
	public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1486 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1496 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_normalInterfaceDeclaration1520 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_INTERFACE_in_normalInterfaceDeclaration1522 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_normalInterfaceDeclaration1524 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000082L});
	public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration1538 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_EXTENDS_in_normalInterfaceDeclaration1560 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration1562 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration1585 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_typeList1615 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_COMMA_in_typeList1628 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_typeList1630 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_LBRACE_in_classBody1665 = new BitSet(new long[]{0x1840502100A14010L,0x0019040C31700692L});
	public static final BitSet FOLLOW_classBodyDeclaration_in_classBody1678 = new BitSet(new long[]{0x1840502100A14010L,0x0019040C31700692L});
	public static final BitSet FOLLOW_RBRACE_in_classBody1700 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACE_in_interfaceBody1721 = new BitSet(new long[]{0x1840502100A14010L,0x0019040C31700690L});
	public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody1734 = new BitSet(new long[]{0x1840502100A14010L,0x0019040C31700690L});
	public static final BitSet FOLLOW_RBRACE_in_interfaceBody1756 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMI_in_classBodyDeclaration1777 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STATIC_in_classBodyDeclaration1790 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_classBodyDeclaration1813 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration1823 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldDeclaration_in_memberDecl1844 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_methodDeclaration_in_memberDecl1855 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_memberDecl1866 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl1877 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_methodDeclaration1916 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000080L});
	public static final BitSet FOLLOW_typeParameters_in_methodDeclaration1927 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_methodDeclaration1948 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_formalParameters_in_methodDeclaration1962 = new BitSet(new long[]{0x0000000000000000L,0x0000400000000002L});
	public static final BitSet FOLLOW_THROWS_in_methodDeclaration1973 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaration1975 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACE_in_methodDeclaration1998 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1EF2L});
	public static final BitSet FOLLOW_explicitConstructorInvocation_in_methodDeclaration2011 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
	public static final BitSet FOLLOW_blockStatement_in_methodDeclaration2033 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
	public static final BitSet FOLLOW_RBRACE_in_methodDeclaration2054 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_methodDeclaration2080 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000090L});
	public static final BitSet FOLLOW_typeParameters_in_methodDeclaration2093 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000010L});
	public static final BitSet FOLLOW_type_in_methodDeclaration2115 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_VOID_in_methodDeclaration2130 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_methodDeclaration2154 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_formalParameters_in_methodDeclaration2169 = new BitSet(new long[]{0x0000000000000000L,0x0000400010000006L});
	public static final BitSet FOLLOW_LBRACKET_in_methodDeclaration2180 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_methodDeclaration2182 = new BitSet(new long[]{0x0000000000000000L,0x0000400010000006L});
	public static final BitSet FOLLOW_THROWS_in_methodDeclaration2206 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaration2208 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000002L});
	public static final BitSet FOLLOW_block_in_methodDeclaration2265 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMI_in_methodDeclaration2279 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_fieldDeclaration2331 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_fieldDeclaration2341 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_variableDeclarator_in_fieldDeclaration2352 = new BitSet(new long[]{0x0000000002000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_COMMA_in_fieldDeclaration2364 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_variableDeclarator_in_fieldDeclaration2366 = new BitSet(new long[]{0x0000000002000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_fieldDeclaration2390 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_variableDeclarator2411 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_variableDeclarator2428 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_variableDeclarator2430 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_EQ_in_variableDeclarator2453 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1872L});
	public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator2457 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceFieldDeclaration_in_interfaceBodyDeclaration2497 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceMethodDeclaration_in_interfaceBodyDeclaration2507 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceBodyDeclaration2517 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_interfaceBodyDeclaration2527 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMI_in_interfaceBodyDeclaration2537 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_interfaceMethodDeclaration2559 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000090L});
	public static final BitSet FOLLOW_typeParameters_in_interfaceMethodDeclaration2570 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000010L});
	public static final BitSet FOLLOW_type_in_interfaceMethodDeclaration2592 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_VOID_in_interfaceMethodDeclaration2603 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_interfaceMethodDeclaration2625 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaration2635 = new BitSet(new long[]{0x0000000000000000L,0x0000400010000004L});
	public static final BitSet FOLLOW_LBRACKET_in_interfaceMethodDeclaration2646 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_interfaceMethodDeclaration2648 = new BitSet(new long[]{0x0000000000000000L,0x0000400010000004L});
	public static final BitSet FOLLOW_THROWS_in_interfaceMethodDeclaration2672 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaration2674 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_interfaceMethodDeclaration2689 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_interfaceFieldDeclaration2713 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_interfaceFieldDeclaration2715 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2717 = new BitSet(new long[]{0x0000000002000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_COMMA_in_interfaceFieldDeclaration2730 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2732 = new BitSet(new long[]{0x0000000002000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_interfaceFieldDeclaration2755 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classOrInterfaceType_in_type2778 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_type2789 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_type2791 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_primitiveType_in_type2814 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_type2825 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_type2827 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType2861 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000080L});
	public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2872 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_DOT_in_classOrInterfaceType2894 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType2896 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000080L});
	public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2913 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_BOOLEAN_in_primitiveType2960 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CHAR_in_primitiveType2972 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BYTE_in_primitiveType2984 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SHORT_in_primitiveType2996 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_primitiveType3008 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LONG_in_primitiveType3022 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOAT_in_primitiveType3034 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOUBLE_in_primitiveType3046 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LT_in_typeArguments3068 = new BitSet(new long[]{0x0840400100214000L,0x0000000020800010L});
	public static final BitSet FOLLOW_typeArgument_in_typeArguments3070 = new BitSet(new long[]{0x0008000002000000L});
	public static final BitSet FOLLOW_COMMA_in_typeArguments3083 = new BitSet(new long[]{0x0840400100214000L,0x0000000020800010L});
	public static final BitSet FOLLOW_typeArgument_in_typeArguments3085 = new BitSet(new long[]{0x0008000002000000L});
	public static final BitSet FOLLOW_GT_in_typeArguments3109 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_typeArgument3131 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_QUES_in_typeArgument3141 = new BitSet(new long[]{0x0000010000000002L,0x0000010000000000L});
	public static final BitSet FOLLOW_EXTENDS_in_typeArgument3168 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_SUPER_in_typeArgument3185 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_typeArgument3215 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3246 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_COMMA_in_qualifiedNameList3258 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3260 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_LPAREN_in_formalParameters3294 = new BitSet(new long[]{0x0840500100214000L,0x0000000028000210L});
	public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters3307 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_formalParameters3329 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3351 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3361 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_COMMA_in_formalParameterDecls3372 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
	public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3374 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3398 = new BitSet(new long[]{0x0000000002000000L});
	public static final BitSet FOLLOW_COMMA_in_formalParameterDecls3408 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
	public static final BitSet FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3432 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifiers_in_normalParameterDecl3452 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_normalParameterDecl3454 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_normalParameterDecl3456 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_normalParameterDecl3469 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_normalParameterDecl3471 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_variableModifiers_in_ellipsisParameterDecl3504 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_ellipsisParameterDecl3514 = new BitSet(new long[]{0x0000000800000000L});
	public static final BitSet FOLLOW_ELLIPSIS_in_ellipsisParameterDecl3517 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_ellipsisParameterDecl3529 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3551 = new BitSet(new long[]{0x0000000000000000L,0x0000110000000000L});
	public static final BitSet FOLLOW_THIS_in_explicitConstructorInvocation3578 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_SUPER_in_explicitConstructorInvocation3591 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3613 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation3615 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation3628 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_DOT_in_explicitConstructorInvocation3638 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000080L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3651 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
	public static final BitSet FOLLOW_SUPER_in_explicitConstructorInvocation3672 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3684 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation3686 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName3708 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_DOT_in_qualifiedName3720 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName3722 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_annotation_in_annotations3757 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
	public static final BitSet FOLLOW_MONKEYS_AT_in_annotation3790 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_qualifiedName_in_annotation3792 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
	public static final BitSet FOLLOW_LPAREN_in_annotation3809 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0280A1A72L});
	public static final BitSet FOLLOW_elementValuePairs_in_annotation3837 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_elementValue_in_annotation3861 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_annotation3897 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3930 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_COMMA_in_elementValuePairs3941 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3943 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_elementValuePair3976 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_EQ_in_elementValuePair3978 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1A72L});
	public static final BitSet FOLLOW_elementValue_in_elementValuePair3980 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalExpression_in_elementValue4002 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotation_in_elementValue4012 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue4022 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACE_in_elementValueArrayInitializer4042 = new BitSet(new long[]{0x2840C80302614200L,0x000A91B0210A1A72L});
	public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer4055 = new BitSet(new long[]{0x0000000002000000L,0x0000000001000000L});
	public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer4070 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1A72L});
	public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer4072 = new BitSet(new long[]{0x0000000002000000L,0x0000000001000000L});
	public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer4103 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
	public static final BitSet FOLLOW_RBRACE_in_elementValueArrayInitializer4107 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_annotationTypeDeclaration4132 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_MONKEYS_AT_in_annotationTypeDeclaration4134 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_INTERFACE_in_annotationTypeDeclaration4145 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_annotationTypeDeclaration4157 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration4167 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACE_in_annotationTypeBody4188 = new BitSet(new long[]{0x1840502100A14010L,0x0011040C31700610L});
	public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody4201 = new BitSet(new long[]{0x1840502100A14010L,0x0011040C31700610L});
	public static final BitSet FOLLOW_RBRACE_in_annotationTypeBody4223 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationMethodDeclaration_in_annotationTypeElementDeclaration4247 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceFieldDeclaration_in_annotationTypeElementDeclaration4257 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementDeclaration4267 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementDeclaration4277 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementDeclaration4287 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementDeclaration4297 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMI_in_annotationTypeElementDeclaration4307 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_annotationMethodDeclaration4330 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_annotationMethodDeclaration4332 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_annotationMethodDeclaration4334 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_LPAREN_in_annotationMethodDeclaration4346 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_annotationMethodDeclaration4348 = new BitSet(new long[]{0x0000000020000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_DEFAULT_in_annotationMethodDeclaration4351 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1A72L});
	public static final BitSet FOLLOW_elementValue_in_annotationMethodDeclaration4353 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_annotationMethodDeclaration4384 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACE_in_block4411 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
	public static final BitSet FOLLOW_blockStatement_in_block4425 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
	public static final BitSet FOLLOW_RBRACE_in_block4446 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatement4471 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement4481 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_statement_in_blockStatement4491 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4512 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_localVariableDeclarationStatement4522 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration4545 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_localVariableDeclaration4547 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_variableDeclarator_in_localVariableDeclaration4557 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_COMMA_in_localVariableDeclaration4568 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_variableDeclarator_in_localVariableDeclaration4570 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_block_in_statement4604 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASSERT_in_statement4628 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_statement4650 = new BitSet(new long[]{0x0000000001000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_COLON_in_statement4653 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_statement4655 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_statement4661 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASSERT_in_statement4673 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_statement4676 = new BitSet(new long[]{0x0000000001000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_COLON_in_statement4679 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_statement4681 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_statement4687 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IF_in_statement4711 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_parExpression_in_statement4713 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_statement4715 = new BitSet(new long[]{0x0000001000000002L});
	public static final BitSet FOLLOW_ELSE_in_statement4718 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_statement4720 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_forstatement_in_statement4745 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_WHILE_in_statement4755 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_parExpression_in_statement4757 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_statement4759 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DO_in_statement4771 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_statement4773 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
	public static final BitSet FOLLOW_WHILE_in_statement4775 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_parExpression_in_statement4777 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_statement4779 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_trystatement_in_statement4791 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SWITCH_in_statement4801 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_parExpression_in_statement4803 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACE_in_statement4805 = new BitSet(new long[]{0x0000000020080000L,0x0000000001000000L});
	public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement4807 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
	public static final BitSet FOLLOW_RBRACE_in_statement4809 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SYNCHRONIZED_in_statement4821 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_parExpression_in_statement4823 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_statement4825 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RETURN_in_statement4837 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0300A1870L});
	public static final BitSet FOLLOW_expression_in_statement4840 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_statement4845 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_THROW_in_statement4857 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_statement4859 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_statement4861 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BREAK_in_statement4873 = new BitSet(new long[]{0x0040000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_statement4890 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_statement4907 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CONTINUE_in_statement4919 = new BitSet(new long[]{0x0040000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_statement4936 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_statement4953 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_statement4965 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_statement4968 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_statement4984 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_COLON_in_statement4986 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_statement4988 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMI_in_statement5000 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups5024 = new BitSet(new long[]{0x0000000020080002L});
	public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup5053 = new BitSet(new long[]{0x38C1D82350E1C312L,0x003FB7BC347A1E72L});
	public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup5064 = new BitSet(new long[]{0x38C1D82350E1C312L,0x003FB7BC347A1E72L});
	public static final BitSet FOLLOW_CASE_in_switchLabel5095 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_switchLabel5097 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_COLON_in_switchLabel5099 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DEFAULT_in_switchLabel5110 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_COLON_in_switchLabel5112 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TRY_in_trystatement5135 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_trystatement5137 = new BitSet(new long[]{0x0000200000100000L});
	public static final BitSet FOLLOW_catches_in_trystatement5153 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_FINALLY_in_trystatement5155 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_trystatement5157 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_catches_in_trystatement5173 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FINALLY_in_trystatement5187 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_trystatement5189 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_catchClause_in_catches5222 = new BitSet(new long[]{0x0000000000100002L});
	public static final BitSet FOLLOW_catchClause_in_catches5233 = new BitSet(new long[]{0x0000000000100002L});
	public static final BitSet FOLLOW_CATCH_in_catchClause5264 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_LPAREN_in_catchClause5266 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
	public static final BitSet FOLLOW_formalParameter_in_catchClause5268 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_catchClause5280 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_catchClause5282 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifiers_in_formalParameter5304 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_formalParameter5306 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_formalParameter5308 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_formalParameter5321 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_formalParameter5323 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_FOR_in_forstatement5373 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_LPAREN_in_forstatement5375 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
	public static final BitSet FOLLOW_variableModifiers_in_forstatement5377 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_forstatement5379 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_forstatement5381 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_COLON_in_forstatement5383 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_forstatement5398 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_forstatement5400 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_forstatement5402 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FOR_in_forstatement5436 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_LPAREN_in_forstatement5438 = new BitSet(new long[]{0x2840D80300614200L,0x000A91B0300A1A70L});
	public static final BitSet FOLLOW_forInit_in_forstatement5460 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_forstatement5481 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0300A1870L});
	public static final BitSet FOLLOW_expression_in_forstatement5502 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_forstatement5523 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0280A1870L});
	public static final BitSet FOLLOW_expressionList_in_forstatement5544 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_forstatement5565 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_forstatement5567 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_localVariableDeclaration_in_forInit5589 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expressionList_in_forInit5599 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_parExpression5619 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_parExpression5621 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_parExpression5623 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_expressionList5647 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_COMMA_in_expressionList5659 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_expressionList5661 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_conditionalExpression_in_expression5695 = new BitSet(new long[]{0x0008004000042082L,0x0000004280050080L});
	public static final BitSet FOLLOW_assignmentOperator_in_expression5709 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_expression5711 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EQ_in_assignmentOperator5744 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUSEQ_in_assignmentOperator5756 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SUBEQ_in_assignmentOperator5768 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STAREQ_in_assignmentOperator5780 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SLASHEQ_in_assignmentOperator5792 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AMPEQ_in_assignmentOperator5804 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BAREQ_in_assignmentOperator5816 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CARETEQ_in_assignmentOperator5829 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PERCENTEQ_in_assignmentOperator5841 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LT_in_assignmentOperator5854 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
	public static final BitSet FOLLOW_LT_in_assignmentOperator5856 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_EQ_in_assignmentOperator5858 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GT_in_assignmentOperator5871 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_GT_in_assignmentOperator5873 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_GT_in_assignmentOperator5875 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_EQ_in_assignmentOperator5877 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GT_in_assignmentOperator5890 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_GT_in_assignmentOperator5892 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_EQ_in_assignmentOperator5894 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression5917 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
	public static final BitSet FOLLOW_QUES_in_conditionalExpression5929 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_conditionalExpression5931 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_COLON_in_conditionalExpression5933 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression5935 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5969 = new BitSet(new long[]{0x0000000000001002L});
	public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression5980 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5982 = new BitSet(new long[]{0x0000000000001002L});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression6015 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression6026 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression6028 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression6061 = new BitSet(new long[]{0x0000000000000802L});
	public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression6072 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression6074 = new BitSet(new long[]{0x0000000000000802L});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression6107 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression6118 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression6120 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression6153 = new BitSet(new long[]{0x0000000000000022L});
	public static final BitSet FOLLOW_AMP_in_andExpression6164 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression6166 = new BitSet(new long[]{0x0000000000000022L});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression6199 = new BitSet(new long[]{0x0000008000000402L});
	public static final BitSet FOLLOW_EQEQ_in_equalityExpression6231 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_BANGEQ_in_equalityExpression6251 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression6281 = new BitSet(new long[]{0x0000008000000402L});
	public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression6312 = new BitSet(new long[]{0x0400000000000002L});
	public static final BitSet FOLLOW_INSTANCEOF_in_instanceOfExpression6323 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_instanceOfExpression6325 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftExpression_in_relationalExpression6358 = new BitSet(new long[]{0x0008000000000002L,0x0000000000000080L});
	public static final BitSet FOLLOW_relationalOp_in_relationalExpression6370 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_shiftExpression_in_relationalExpression6372 = new BitSet(new long[]{0x0008000000000002L,0x0000000000000080L});
	public static final BitSet FOLLOW_LT_in_relationalOp6404 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_EQ_in_relationalOp6406 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GT_in_relationalOp6419 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_EQ_in_relationalOp6421 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LT_in_relationalOp6433 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GT_in_relationalOp6445 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6467 = new BitSet(new long[]{0x0008000000000002L,0x0000000000000080L});
	public static final BitSet FOLLOW_shiftOp_in_shiftExpression6478 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6480 = new BitSet(new long[]{0x0008000000000002L,0x0000000000000080L});
	public static final BitSet FOLLOW_LT_in_shiftOp6513 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
	public static final BitSet FOLLOW_LT_in_shiftOp6515 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GT_in_shiftOp6531 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_GT_in_shiftOp6533 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_GT_in_shiftOp6535 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GT_in_shiftOp6550 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_GT_in_shiftOp6552 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6576 = new BitSet(new long[]{0x0000000000000002L,0x0000002000020000L});
	public static final BitSet FOLLOW_PLUS_in_additiveExpression6607 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_SUB_in_additiveExpression6627 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6657 = new BitSet(new long[]{0x0000000000000002L,0x0000002000020000L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6695 = new BitSet(new long[]{0x0000000000000002L,0x0000000140008000L});
	public static final BitSet FOLLOW_STAR_in_multiplicativeExpression6726 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_SLASH_in_multiplicativeExpression6746 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_PERCENT_in_multiplicativeExpression6766 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6796 = new BitSet(new long[]{0x0000000000000002L,0x0000000140008000L});
	public static final BitSet FOLLOW_PLUS_in_unaryExpression6829 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6832 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SUB_in_unaryExpression6845 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6847 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUSPLUS_in_unaryExpression6859 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6861 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SUBSUB_in_unaryExpression6873 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6875 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6887 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6907 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6909 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BANG_in_unaryExpressionNotPlusMinus6921 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6923 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6935 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus6945 = new BitSet(new long[]{0x0000000080000002L,0x0000008000080004L});
	public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus6958 = new BitSet(new long[]{0x0000000080000002L,0x0000008000080004L});
	public static final BitSet FOLLOW_PLUSPLUS_in_unaryExpressionNotPlusMinus6983 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SUBSUB_in_unaryExpressionNotPlusMinus6999 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_castExpression7032 = new BitSet(new long[]{0x0800400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_primitiveType_in_castExpression7034 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_castExpression7036 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_unaryExpression_in_castExpression7038 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_castExpression7050 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_castExpression7052 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_castExpression7054 = new BitSet(new long[]{0x2840C80300614200L,0x000A911020001870L});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression7056 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_parExpression_in_primary7080 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_THIS_in_primary7102 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000044L});
	public static final BitSet FOLLOW_DOT_in_primary7115 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_primary7117 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000044L});
	public static final BitSet FOLLOW_identifierSuffix_in_primary7141 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_primary7162 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000044L});
	public static final BitSet FOLLOW_DOT_in_primary7175 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_primary7177 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000044L});
	public static final BitSet FOLLOW_identifierSuffix_in_primary7202 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SUPER_in_primary7223 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_superSuffix_in_primary7235 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_primary7245 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_creator_in_primary7263 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_primary7273 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_primary7285 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_primary7287 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_DOT_in_primary7310 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_CLASS_in_primary7312 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_VOID_in_primary7324 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_DOT_in_primary7326 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_CLASS_in_primary7328 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arguments_in_superSuffix7358 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_superSuffix7368 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000080L});
	public static final BitSet FOLLOW_typeArguments_in_superSuffix7371 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_superSuffix7394 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
	public static final BitSet FOLLOW_arguments_in_superSuffix7406 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix7439 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix7441 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_DOT_in_identifierSuffix7464 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_CLASS_in_identifierSuffix7466 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix7479 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_identifierSuffix7481 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix7483 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_arguments_in_identifierSuffix7507 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_identifierSuffix7517 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_CLASS_in_identifierSuffix7519 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_identifierSuffix7532 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_identifierSuffix7534 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_identifierSuffix7536 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_arguments_in_identifierSuffix7538 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_identifierSuffix7548 = new BitSet(new long[]{0x0000000000000000L,0x0000100000000000L});
	public static final BitSet FOLLOW_THIS_in_identifierSuffix7550 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_identifierSuffix7562 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
	public static final BitSet FOLLOW_SUPER_in_identifierSuffix7564 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_arguments_in_identifierSuffix7566 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_innerCreator_in_identifierSuffix7578 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector7600 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_selector7602 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
	public static final BitSet FOLLOW_arguments_in_selector7613 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector7634 = new BitSet(new long[]{0x0000000000000000L,0x0000100000000000L});
	public static final BitSet FOLLOW_THIS_in_selector7636 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector7648 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
	public static final BitSet FOLLOW_SUPER_in_selector7650 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_superSuffix_in_selector7662 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_innerCreator_in_selector7672 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACKET_in_selector7682 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_selector7684 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_selector7686 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEW_in_creator7709 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator7711 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_classOrInterfaceType_in_creator7713 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_classCreatorRest_in_creator7715 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEW_in_creator7727 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_classOrInterfaceType_in_creator7729 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_classCreatorRest_in_creator7731 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arrayCreator_in_creator7743 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEW_in_arrayCreator7763 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_createdName_in_arrayCreator7765 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7777 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7779 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000006L});
	public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7792 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7794 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000006L});
	public static final BitSet FOLLOW_arrayInitializer_in_arrayCreator7817 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEW_in_arrayCreator7828 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_createdName_in_arrayCreator7830 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7842 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_arrayCreator7844 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7856 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7872 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_arrayCreator7874 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7890 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7914 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7916 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer7949 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_variableInitializer7959 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACE_in_arrayInitializer7979 = new BitSet(new long[]{0x2840C80302614200L,0x000A91B0210A1872L});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer7996 = new BitSet(new long[]{0x0000000002000000L,0x0000000001000000L});
	public static final BitSet FOLLOW_COMMA_in_arrayInitializer8015 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1872L});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer8017 = new BitSet(new long[]{0x0000000002000000L,0x0000000001000000L});
	public static final BitSet FOLLOW_COMMA_in_arrayInitializer8069 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
	public static final BitSet FOLLOW_RBRACE_in_arrayInitializer8084 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classOrInterfaceType_in_createdName8119 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_createdName8129 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_innerCreator8150 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_NEW_in_innerCreator8152 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000080L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator8165 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_innerCreator8186 = new BitSet(new long[]{0x0000000000000000L,0x00000000000000C0L});
	public static final BitSet FOLLOW_typeArguments_in_innerCreator8197 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_classCreatorRest_in_innerCreator8218 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arguments_in_classCreatorRest8239 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
	public static final BitSet FOLLOW_classBody_in_classCreatorRest8250 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LT_in_nonWildcardTypeArguments8282 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments8284 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_GT_in_nonWildcardTypeArguments8296 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_arguments8318 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0280A1870L});
	public static final BitSet FOLLOW_expressionList_in_arguments8321 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_arguments8336 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INTLITERAL_in_literal8358 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LONGLITERAL_in_literal8370 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOATLITERAL_in_literal8382 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOUBLELITERAL_in_literal8394 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CHARLITERAL_in_literal8406 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRINGLITERAL_in_literal8418 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TRUE_in_literal8430 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FALSE_in_literal8442 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_in_literal8454 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_classHeader8480 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_CLASS_in_classHeader8482 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_classHeader8484 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_enumHeader8506 = new BitSet(new long[]{0x0040002000000000L});
	public static final BitSet FOLLOW_set_in_enumHeader8508 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_enumHeader8514 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_interfaceHeader8536 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_INTERFACE_in_interfaceHeader8538 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_interfaceHeader8540 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_annotationHeader8562 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_MONKEYS_AT_in_annotationHeader8564 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_INTERFACE_in_annotationHeader8566 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_annotationHeader8568 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_typeHeader8590 = new BitSet(new long[]{0x1000002000800000L,0x0000000000000200L});
	public static final BitSet FOLLOW_CLASS_in_typeHeader8593 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_ENUM_in_typeHeader8595 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_MONKEYS_AT_in_typeHeader8598 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_INTERFACE_in_typeHeader8602 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_typeHeader8606 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_methodHeader8626 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000090L});
	public static final BitSet FOLLOW_typeParameters_in_methodHeader8628 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000010L});
	public static final BitSet FOLLOW_type_in_methodHeader8632 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_VOID_in_methodHeader8634 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_methodHeader8638 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_LPAREN_in_methodHeader8640 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_fieldHeader8660 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_fieldHeader8662 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_fieldHeader8664 = new BitSet(new long[]{0x0000004002000000L,0x0000000010000004L});
	public static final BitSet FOLLOW_LBRACKET_in_fieldHeader8667 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_fieldHeader8668 = new BitSet(new long[]{0x0000004002000000L,0x0000000010000004L});
	public static final BitSet FOLLOW_set_in_fieldHeader8672 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifiers_in_localVariableHeader8700 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_localVariableHeader8702 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_localVariableHeader8704 = new BitSet(new long[]{0x0000004002000000L,0x0000000010000004L});
	public static final BitSet FOLLOW_LBRACKET_in_localVariableHeader8707 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_localVariableHeader8708 = new BitSet(new long[]{0x0000004002000000L,0x0000000010000004L});
	public static final BitSet FOLLOW_set_in_localVariableHeader8712 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotations_in_synpred2_Java91 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
	public static final BitSet FOLLOW_packageDeclaration_in_synpred2_Java120 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_synpred12_Java501 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_normalClassDeclaration_in_synpred27_Java755 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_normalInterfaceDeclaration_in_synpred43_Java1486 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldDeclaration_in_synpred52_Java1844 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_methodDeclaration_in_synpred53_Java1855 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_synpred54_Java1866 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred57_Java2011 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifiers_in_synpred59_Java1916 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000080L});
	public static final BitSet FOLLOW_typeParameters_in_synpred59_Java1927 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_synpred59_Java1948 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_formalParameters_in_synpred59_Java1962 = new BitSet(new long[]{0x0000000000000000L,0x0000400000000002L});
	public static final BitSet FOLLOW_THROWS_in_synpred59_Java1973 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_qualifiedNameList_in_synpred59_Java1975 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACE_in_synpred59_Java1998 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1EF2L});
	public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred59_Java2011 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
	public static final BitSet FOLLOW_blockStatement_in_synpred59_Java2033 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
	public static final BitSet FOLLOW_RBRACE_in_synpred59_Java2054 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceFieldDeclaration_in_synpred68_Java2497 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceMethodDeclaration_in_synpred69_Java2507 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceDeclaration_in_synpred70_Java2517 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_synpred71_Java2527 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ellipsisParameterDecl_in_synpred96_Java3351 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_normalParameterDecl_in_synpred98_Java3361 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_COMMA_in_synpred98_Java3372 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
	public static final BitSet FOLLOW_normalParameterDecl_in_synpred98_Java3374 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_normalParameterDecl_in_synpred99_Java3398 = new BitSet(new long[]{0x0000000002000000L});
	public static final BitSet FOLLOW_COMMA_in_synpred99_Java3408 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred103_Java3551 = new BitSet(new long[]{0x0000000000000000L,0x0000110000000000L});
	public static final BitSet FOLLOW_set_in_synpred103_Java3577 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_arguments_in_synpred103_Java3613 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_synpred103_Java3615 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationMethodDeclaration_in_synpred117_Java4247 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceFieldDeclaration_in_synpred118_Java4257 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_normalClassDeclaration_in_synpred119_Java4267 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_normalInterfaceDeclaration_in_synpred120_Java4277 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_enumDeclaration_in_synpred121_Java4287 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationTypeDeclaration_in_synpred122_Java4297 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred125_Java4471 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred126_Java4481 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASSERT_in_synpred130_Java4628 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_synpred130_Java4650 = new BitSet(new long[]{0x0000000001000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_COLON_in_synpred130_Java4653 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_synpred130_Java4655 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_synpred130_Java4661 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASSERT_in_synpred132_Java4673 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_synpred132_Java4676 = new BitSet(new long[]{0x0000000001000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_COLON_in_synpred132_Java4679 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_synpred132_Java4681 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_synpred132_Java4687 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ELSE_in_synpred133_Java4718 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_synpred133_Java4720 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_synpred148_Java4965 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_SEMI_in_synpred148_Java4968 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_synpred149_Java4984 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_COLON_in_synpred149_Java4986 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_synpred149_Java4988 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_catches_in_synpred153_Java5153 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_FINALLY_in_synpred153_Java5155 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_synpred153_Java5157 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_catches_in_synpred154_Java5173 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FOR_in_synpred157_Java5373 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_LPAREN_in_synpred157_Java5375 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
	public static final BitSet FOLLOW_variableModifiers_in_synpred157_Java5377 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_type_in_synpred157_Java5379 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_synpred157_Java5381 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_COLON_in_synpred157_Java5383 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_synpred157_Java5398 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_synpred157_Java5400 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
	public static final BitSet FOLLOW_statement_in_synpred157_Java5402 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_localVariableDeclaration_in_synpred161_Java5589 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_synpred202_Java6935 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_synpred206_Java7032 = new BitSet(new long[]{0x0800400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_primitiveType_in_synpred206_Java7034 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_synpred206_Java7036 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_unaryExpression_in_synpred206_Java7038 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred208_Java7115 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_synpred208_Java7117 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_synpred209_Java7141 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred211_Java7175 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_IDENTIFIER_in_synpred211_Java7177 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_synpred212_Java7202 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACKET_in_synpred224_Java7479 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_synpred224_Java7481 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_synpred224_Java7483 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEW_in_synpred236_Java7709 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred236_Java7711 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_classOrInterfaceType_in_synpred236_Java7713 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_classCreatorRest_in_synpred236_Java7715 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEW_in_synpred237_Java7727 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_classOrInterfaceType_in_synpred237_Java7729 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_classCreatorRest_in_synpred237_Java7731 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEW_in_synpred239_Java7763 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
	public static final BitSet FOLLOW_createdName_in_synpred239_Java7765 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_LBRACKET_in_synpred239_Java7777 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_synpred239_Java7779 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000006L});
	public static final BitSet FOLLOW_LBRACKET_in_synpred239_Java7792 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_synpred239_Java7794 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000006L});
	public static final BitSet FOLLOW_arrayInitializer_in_synpred239_Java7817 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LBRACKET_in_synpred240_Java7872 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
	public static final BitSet FOLLOW_expression_in_synpred240_Java7874 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RBRACKET_in_synpred240_Java7890 = new BitSet(new long[]{0x0000000000000002L});
}
