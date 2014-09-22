import java.util.regex.*;
import java.io.*;
import java.util.*;

public class Lexer {

	private ArrayList<String> names;
	private ArrayList<Pattern> patterns;

	public Lexer (File f) throws FileNotFoundException{
		Scanner sc = new Scanner (f);
		names = new ArrayList<String>();
		patterns = new ArrayList<Pattern>();
		while (sc.hasNextLine()) {
			Scanner ls = new Scanner (sc.nextLine());
			names.add (ls.next());
			patterns.add (Pattern.compile (ls.next(), Pattern.MULTILINE));
		}
	}

	public ArrayList<Token> tokenize (String s) throws ParserException {
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
					res.add (new Token (names.get(i), s.substring (pos, end), i+1));
					pos = end;
					break;
				} else if (i == patterns.size() - 1) {
					throw new ParserException();
				}
			}
		}
		res.add (new Token ("$", "", 0));
		return res;
	}

	public ArrayList<Token> tokenize (File f) throws IOException, ParserException {
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

	public static void main (String[] args) throws IOException, ParserException {
		File f = new File (args[0]);
		File inp = new File (args[1]);
		Lexer lex = new Lexer (f);
		for (Token t : lex.tokenize (inp)) {
			System.out.println (t);
		}
	}
}
