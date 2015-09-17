import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;


/**
 * 
 * @author byung
 * Note 
 *  /*
 *  this should be copied and pasted to JavaLexer.java at the end of constructors.
  * public boolean extra;
	public JavaLexer(CharStream input, boolean b) {
		this(input);
		extra = b;
	}
  *
 *
 */
public class Main {
	
	MainController mainController;
	
	public Main() {
		mainController = new MainController();
	}
	
	public static void main(String[] args) throws Exception {
//		MainController main = new MainController();
//		//main.parse("TestClass.java");
//		MainView view = new MainView(main);
//		view.setVisible(true);
	//	@SuppressWarnings("unused")
		Main main = new Main();
//		parse("");
	}
	
	public static void parse(String path) {
		JavaParser parser = null;
		File initialFile = new File("TestClass.java");
		try {
			InputStream targetStream = new FileInputStream(initialFile);
			ANTLRInputStream input = new ANTLRInputStream(targetStream);
			new ANTLRInputStream();
			JavaLexer lexer = new JavaLexer(input,true);
			

			CommonTokenStream tokens = new CommonTokenStream(lexer);


			parser = new JavaParser(tokens);

			parser.compilationUnit();
//			System.out.println("***** PACKAGE *******");
//			System.out.println(parser.ci.packageName);
//			System.out.println("***** CLASS NAME ****");
//			System.out.println(parser.ci.className);
//			System.out.println("********** TOTAL ************");
//			System.out.println(parser.ci.keywords.toString() + " " + parser.ci.keywords.size());
//			System.out.println(parser.ci.udis.toString() + " " + parser.ci.udis.size());
//			System.out.println(parser.ci.constants.toString() + " " + parser.ci.constants.size());
//			System.out.println(parser.ci.specialChars.toString() + " " + parser.ci.specialChars.size());
//			System.out.println("********** Unique ************");
//			System.out.println(parser.ci.keywordsSet.toString() + " " + parser.ci.keywordsSet.size());
//			System.out.println(parser.ci.udisSet.toString() + " " + parser.ci.udisSet.size());
//			System.out.println(parser.ci.constantsSet.toString() + " " + parser.ci.constantsSet.size());
//			System.out.println(parser.ci.specialCharsSet.toString() + " " + parser.ci.specialCharsSet.size());
		}	catch (Exception e) {
			e.printStackTrace();
			//return null;
		}
	//	return parser.ci;
	}
	
	public static void parseFilelength(String path) {
		JavaParser parser = null;
		File initialFile = new File("TestClass.java");
		try {
			InputStream targetStream = new FileInputStream(initialFile);
			ANTLRInputStream input = new ANTLRInputStream(targetStream);
			new ANTLRInputStream();
			JavaLexer lexer = new JavaLexer(input,false);
			

			CommonTokenStream tokens = new CommonTokenStream(lexer);
//			System.out.println(tokens.getTokens().toString());
//			System.out.println(tokens.LT(1).getText().length());
//			//System.out.println(tokens.LA(1));
//			//System.out.println(tokens.LA(2));
//			tokens.consume();
//			System.out.println(tokens.LT(1).getText().length());
//			System.out.println(tokens.LT(1).getText());
//			
//			tokens.consume();
//			System.out.println((char)tokens.LA(1));
			int sumForTotal = 0;
			int commentCounter = 0;
			int whiteSpaceCounter = 0;
			
			tokens.reset();
			
			while(tokens.LT(1).getType() != JavaLexer.EOF) {
				System.out.println(tokens.LT(1).getText().length());
				if(tokens.LT(1).getType() == JavaLexer.WS) {
					whiteSpaceCounter += tokens.LT(1).getText().length();
				} else if(tokens.LT(1).getType() == JavaLexer.COMMENT || 
						tokens.LT(1).getType() == JavaLexer.LINE_COMMENT) {
					commentCounter+= tokens.LT(1).getText().length();
				}
				
				 
//				sum += tokens.LT(1).getText().length();
				System.out.println(tokens.LT(1).getText());
				sumForTotal += tokens.LT(1).getText().length();
				tokens.consume();
			}
			
			if(tokens.LT(1).getType() == JavaLexer.EOF) {
				System.out.println("EOF");
			}
			sumForTotal += tokens.LT(1).getText().length();
			
			System.out.println(tokens.size());
			System.out.println("Total chars in a file: " + sumForTotal);
			System.out.println("Total ws in a file: " + whiteSpaceCounter);
			System.out.println("Total comments in a file: " + commentCounter);
			System.out.println("Overall Percentage of comment: " + ((float) commentCounter / (float) sumForTotal) * 100f);
			System.out.println("Overall Percentage of whitespace: " + ((float) whiteSpaceCounter / (float) sumForTotal) * 100f);

		}	catch (Exception e) {
			e.printStackTrace();
			
		}
	//	return parser.ci;
	}

	
}