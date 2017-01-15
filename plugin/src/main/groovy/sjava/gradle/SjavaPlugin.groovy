package sjava.gradle

import org.gradle.api.*
import org.gradle.api.plugins.JavaPlugin

class SjavaPlugin implements Plugin<Project> {
	public void apply(Project project) {
		project.plugins.apply(JavaPlugin)
		project.extensions.create("sjava", SjavaPluginExtension)
		def task = project.task(type: SjavaCompile, "compileSjava")
		project.getTasksByName("classes", false)[0].dependsOn(task)
	}
}

class SjavaPluginExtension {
	File home
}
