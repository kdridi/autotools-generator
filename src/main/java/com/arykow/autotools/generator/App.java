package com.arykow.autotools.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class App {
	private String templateName;
	private String projectName;
	private String projectDirectory;
	private boolean projectForced;

	private void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	private void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	private void setProjectDirectory(String projectDirectory) {
		this.projectDirectory = projectDirectory;
	}

	private void setProjectForced(boolean projectForced) {
		this.projectForced = projectForced;
	}

	private void generate() throws Exception {
		File directory = new File(projectDirectory);
		if (!directory.isDirectory()) {
			throw new RuntimeException();
		}
		File output = new File(directory, projectName);
		if (output.exists()) {
			if (projectForced) {
				if (!output.isDirectory()) {
					throw new RuntimeException();
				}
			} else {
				throw new RuntimeException();
			}
		} else if (!output.mkdir()) {
			throw new RuntimeException();
		}

		CodeSource src = getClass().getProtectionDomain().getCodeSource();
		if (src != null) {
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			while (true) {
				ZipEntry e = zip.getNextEntry();
				if (e == null)
					break;
				if (!e.isDirectory() && e.getName().startsWith(String.format("%s", templateName))) {
					// generate(output, e);

					StringWriter writer = new StringWriter();
					IOUtils.copy(zip, writer);
					String content = writer.toString();

					Map<String, String> expressions = new HashMap<String, String>();
					expressions.put("author", "Karim DRIDI");
					expressions.put("name_undescore", projectName);
					expressions.put("license", "<Place your desired license here.>");
					expressions.put("name", projectName);
					expressions.put("version", "1.0");
					expressions.put("copyright", "Your copyright notice");
					expressions.put("description", "Hello World in C++,");

					for (Map.Entry<String, String> entry : expressions.entrySet()) {
						content = content.replaceAll(String.format("<project\\.%s>", entry.getKey()), entry.getValue());
					}

					String name = e.getName().substring(templateName.length() + 1);
					if ("gitignore".equals(name)) {
						name = ".gitignore";
					}
					File file = new File(output, name);
					File parent = file.getParentFile();
					if (parent.exists()) {
						if (!parent.isDirectory()) {
							throw new RuntimeException();
						}
					} else {
						if (!parent.mkdirs()) {
							throw new RuntimeException();
						}
					}
					OutputStream stream = new FileOutputStream(file);
					IOUtils.copy(new StringReader(content), stream);
					IOUtils.closeQuietly(stream);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 2) {
			App app = new App();
			app.setTemplateName("simple");
			app.setProjectName(args[1]);
			app.setProjectDirectory(args[0]);
			app.setProjectForced(true);
			app.generate();
		} else {
			System.out.println("Please run: generator [dir] [name]");
		}
	}

}
