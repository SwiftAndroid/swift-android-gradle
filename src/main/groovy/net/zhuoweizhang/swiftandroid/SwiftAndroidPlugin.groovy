package net.zhuoweizhang.swiftandroid

import org.gradle.api.*
import org.gradle.api.tasks.*

public class SwiftAndroidPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		Task writeNdkConfigTask = createWriteNdkConfigTask(project, "writeNdkConfigSwift")
		Task compileSwiftTask = createCompileSwiftTask(project, "compileSwift")
		compileSwiftTask.dependsOn("writeNdkConfigSwift")
		Task copySwiftStdlibTask = createCopyStdlibTask(project, "copySwiftStdlib")
		Task copySwiftTask = createCopyTask(project, "copySwift")
		copySwiftTask.dependsOn("compileSwift", "copySwiftStdlib")
		Task cleanSwiftTask = project.task(
			type: Delete, "cleanSwift") {
			// I don't trust Swift Package Manager's --clean
			delete "src/main/swift/.build"
		}
		project.afterEvaluate {
			// according to Protobuf gradle plugin, the Android variants are only available here
			// TODO: read those variants; we only support debug right now
			Task compileNdkTask = project.tasks.getByName("compileDebugNdk")
			compileNdkTask.dependsOn("copySwift")
			Task cleanTask = project.tasks.getByName("clean")
			cleanTask.dependsOn("cleanSwift")
		}
	}
	public static File getNdkRoot() {
		return new File(System.getenv("ANDROID_NDK_HOME"))
	}

	public static File getSwiftRoot() {
		return new File("which swift".execute().text).
			parentFile.parentFile
	}

	public static Task createCopyStdlibTask(Project project, String name) {
		return project.task(type: Copy, name) {
			from({
				new File(SwiftAndroidPlugin.swiftRoot, "lib/swift/android")
			})
			include("*.so")
			from({
				new File(SwiftAndroidPlugin.ndkRoot,
					"/sources/cxx-stl/llvm-libc++/libs/armeabi-v7a/libc++_shared.so")
			})
			into("src/main/jniLibs/armeabi-v7a")
		}
	}
	public static Task createCopyTask(Project project, String name) {
		return project.task(type: Copy, name) {
			from("src/main/swift/.build/debug")
			include("*.so")
			into("src/main/jniLibs/armeabi-v7a")
		}
	}
	public static Task createWriteNdkConfigTask(Project project, String name) {
		return project.task(type: Exec, name) {
			commandLine("bash", "-c", 'echo \"export ANDROID_NDK_HOME=\\\"$ANDROID_NDK_HOME\\\"\" >~/.swift-android-ndk-home')
		}
	}
	public static Task createCompileSwiftTask(Project project, String name) {
		return project.task(type: Exec, name) {
			commandLine("bash", "-c", "SWIFTC=\"" +
				SwiftAndroidPlugin.swiftRoot.absolutePath +
				"/bin/swiftc-pm-android\" swift build")
			workingDir("src/main/swift")
		}
	}
}
