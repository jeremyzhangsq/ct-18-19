package gen;

import java.io.PrintWriter;

public class TextVisitor extends BaseGenVisitor<Register> {
	private PrintWriter writer;
	public TextVisitor(PrintWriter writer){
		this.writer = writer;
	}
}
