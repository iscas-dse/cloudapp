package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

public class ASTVariable extends ASTBase{
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
