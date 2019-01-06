package com.guxingke.simpleNioServer.core;

import java.util.ArrayList;
import java.util.List;

public class Options {

	private static List<String> widgetPackageNames = new ArrayList<String>();
	private static String templatePath = "templates";
	private static String resourcesPath = "resources";

	public static void setTemplatePath(String templatePath) {
		Options.templatePath = templatePath;
	}

	public static void setResourcesPath(String resourcesPath) {
		Options.resourcesPath = resourcesPath;
	}

	public static String getTemplatePath() {
		return templatePath;
	}

	public static String getResourcesPath() {
		return resourcesPath;
	}

	public static String getResourcesFlag() {
		int index = resourcesPath.lastIndexOf("/");
		return resourcesPath.substring(index + 1);
	}

	public static void addPackagePath(String path) {
		widgetPackageNames.add(path);
	}

	public static List<String> getPackagePathList() {
		return widgetPackageNames;
	}
}
