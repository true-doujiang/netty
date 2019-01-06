package com.guxingke.simpleNioServer.template;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.guxingke.simpleNioServer.core.HttpRequest;
import com.guxingke.simpleNioServer.core.Options;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemakerUtil {

	private static Configuration cfg;

	public static void initTemplate(String templatesPath) throws IOException {
		cfg = new Configuration();
		cfg.setDirectoryForTemplateLoading(new File(templatesPath));
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}

	public static void initTemplate() throws IOException {
		cfg = new Configuration();
		cfg.setDirectoryForTemplateLoading(new File(Options.getTemplatePath()));
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}

	private FreemakerUtil() {}


	public static String renderTemplate(HttpRequest request, String tempalteName)
                                            throws IOException, TemplateException {
		Template temp = cfg.getTemplate(tempalteName);
		Writer out = new StringWriter();
		temp.process(request.getAttrs(), out);
		return out.toString();
	}

	// TODO
	public static void main(String[] args) throws IOException, TemplateException {
		FreemakerUtil.initTemplate("templates");

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("test", new Point(1, 2));

		String result = FreemakerUtil.renderTemplate(root, "test.ftl");

		System.out.println(result);
	}

	public static String renderTemplate(Map<String, Object> root, String tempalteName)
                                                    throws IOException, TemplateException {
		Template temp = cfg.getTemplate(tempalteName);
		Writer out = new StringWriter();
		temp.process(root, out);
		return out.toString();
	}
}
