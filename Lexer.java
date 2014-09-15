import java.util.regex.*;
import java.io.*;
import java.util.*;

public class Lexer {

	private ArrayList<Terminal> terms;
	private ArrayList<Pattern> patterns;
	
	public Lexer (ArrayList<Terminal> terms) {
		this.terms = terms;
		patterns = new ArrayList<Pattern>();
		compile();
	}

	public Lexer (File f) throws FileNotFoundException{
		Scanner sc = new Scanner (f);
		terms = new ArrayList<Terminal>();
		int i = 1;
		while (sc.hasNextLine()) {
			Scanner ls = new Scanner (sc.nextLine());
			Terminal t = new Terminal (ls.next(), ls.next());
			t.id = i++;
			terms.add (t);
			System.out.println ("Found terminal with name = " + t.name + "; regex = [" + t.regex + "]");
		}
		patterns = new ArrayList<Pattern>();
		compile();
	}

	private void compile () {
		for (Terminal t : terms) {
			patterns.add (Pattern.compile (t.regex, Pattern.MULTILINE));
		}
	}


	public ArrayList<Token> tokenize (String s) throws LexerException {
		int pos = 0;
		ArrayList<Token> res = new ArrayList<Token>();
		while (pos != s.length()) {
			String sub = s.substring (pos);
			System.out.println ("Looking at position " + pos);

			for (int i = 0; i < patterns.size(); i++) {
				System.out.println ("Trying rule " + i);
				Matcher m = patterns.get(i).matcher (sub);
				if (m.lookingAt()) {
					int end = m.end() + pos;
					res.add (new Token (terms.get(i), s.substring (pos, end)));
					pos = end;
					break;
				} else if (i == patterns.size() - 1) {
					throw new LexerException();
				}
			}
		}
		res.add (new Token (Terminal.EOF, ""));
		return res;
	}

	public ArrayList<Token> tokenize (File f) throws IOException, LexerException {
		BufferedReader br = new BufferedReader (new FileReader(f));
		StringBuilder sb = new StringBuilder();
		String s = br.readLine();
		while (s != null) {
			sb.append (s + "\n");
			s = br.readLine();
		}
		br.close();
		return tokenize(sb.toString());
	}

	public static void main (String[] args) throws IOException, LexerException {
		File f = new File (args[0]);
		File inp = new File (args[1]);
		Lexer lex = new Lexer (f);
		for (Token t : lex.tokenize (inp)) {
			System.out.println (t);
		}
	}
}
