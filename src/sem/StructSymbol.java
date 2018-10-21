package sem;

import ast.FunDecl;
import ast.StructTypeDecl;

public class StructSymbol extends Symbol {
	public StructTypeDecl std;
	public StructSymbol(StructTypeDecl std) {
		super(std.stype.structName);
		this.std = std;
	}
}
