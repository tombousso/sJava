package sjava.gradle

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.file.*

class SjavaCompile extends DefaultTask {
	@InputFiles
	File getSjavaJar() {
		return new File(project.ext.sjavaHome.toString() + "/sjava.jar")
	}

	@InputFiles
	FileCollection getCompileClasspath() {
		return project.sourceSets.main.compileClasspath
	}

	@InputFiles
	FileTree getSourceFiles() {
		return project.fileTree("src/main/sjava")
	}

	@OutputDirectory
	File getDestinationDir() {
		return project.file("build/classes/main")
	}

	@TaskAction
	public void compile() {
		project.javaexec {
			classpath = project.files(getSjavaJar()) + getCompileClasspath()
			main = "sjava.compiler.Main"
			args = ["build"] + getSourceFiles() + ["-d", getDestinationDir()]
		}
	}
}
