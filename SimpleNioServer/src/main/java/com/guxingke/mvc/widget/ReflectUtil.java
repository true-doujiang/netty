package com.guxingke.mvc.widget;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 反射工具类
 * 
 * @author guxingke
 *
 */
public class ReflectUtil {

	/**
	 * 获取某个包下的所有class
	 * 
	 * @param packageName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	public static List<Class> getClassesByPackageName(String packageName)
			throws IOException, ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory,
			String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				// 递归查找文件夹【即对应的包】下面的所有文件
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName
						+ '.' + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class
						.forName(packageName
								+ "."
								+ file.getName()
										.substring(0,
												file.getName()
														.length() - 6)));
			}
		}
		return classes;
	}
}
