import java.io.*;
public abstract class Writer {

	/* Class for writing out the parser generator. Will take a Parsegen object
	 * and produce the basic parser loop and templates. Each target language
	 * will have a subclass of Writer. */

	public abstract void write (Parsegen pg, File dir) throws IOException, FileNotFoundException;

	public final void copy (File f, File dir) throws IOException {
		File target = new File (dir.getCanonicalPath() + "/" + f.getName());
		if (target.getCanonicalPath().equals(f.getCanonicalPath())) {
			System.out.println ("Copy of " + f.getName() + " to itself detected, aborting copy.");
			return;
		}
		BufferedReader br = new BufferedReader (new FileReader (f));
		BufferedWriter bw = new BufferedWriter (new FileWriter (target));
		int c = br.read();
		while (c != -1) {
			bw.write (c);
			c = br.read();
		}
		br.close();
		bw.close();
	}

}
