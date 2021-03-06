package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class PreProcessUtil {
	public static String preProcess(File file) {
		String content=null;
		try {
			content = Files.toString(file, Charsets.UTF_8);
			content=content.replaceAll("!|！", "#");
			content=content.replaceAll("#ruby/object:Puppet::Resource::Type","!!cn.ac.iscas.cloudeploy.v2.puppet.transform.ResouceType");
			content=content.replaceAll("#ruby/sym ", "");
			content=content.replaceAll("#ruby/object:Proxy::Puppet::PuppetClass","!!cn.ac.iscas.cloudeploy.v2.puppet.transform.PuppetClass");
			content=content.replaceAll("else: #ruby/object:Puppet::Parser::AST::Else", "ifStatementElse: #ruby/object:Puppet::Parser::AST::Else");
			content=content.replaceAll("#ruby/object:Puppet::Parser::AST::", "!!cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.AST");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
