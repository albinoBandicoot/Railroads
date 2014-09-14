public abstract class Writer {

	/* Class for writing out the parser generator. Will take a Parsegen object
	 * and produce the basic parser loop and templates. Each target language
	 * will have a subclass of Writer. */

	public abstract void write (Parsegen pg);

}
