package net.zhuoweizhang.swiftandroid

import org.gradle.api.*
import org.gradle.api.tasks.*

public class SwiftAndroidPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		//Task writeNdkConfigTask = createWriteNdkConfigTask(project, "writeNdkConfigSwift")
		Task compileSwiftTask = createCompileSwiftTask(project, "compileSwift")
		//compileSwiftTask.dependsOn("writeNdkConfigSwift")
		Task copySwiftStdlibTask = createCopyStdlibTask(project, "copySwiftStdlib")
		Task copySwiftTask = createCopyTask(project, "copySwift")
		copySwiftTask.dependsOn("compileSwift", "copySwiftStdlib")
		project.afterEvaluate {
			// according to Protobuf gradle plugin, the Android variants are only available here
			// TODO: read those variants; we only support debug right now
			Task compileNdkTask = project.tasks.getByName("compileDebugNdk")
			compileNdkTask.dependsOn("copySwift")
		}
	}
	public static File getNdkRoot() {
		return new File("/home/zhuowei/ndk")
	}

	public static File getSwiftRoot() {
		return new File("/home/zhuowei/build/Ninja-ReleaseAssert+stdlib-DebugAssert/swift-linux-x86_64")
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
			commandLine("bash", "-c", 'echo ANDROID_NDK_HOME=$ANDROID_NDK_HOME >~/.swift-android-ndk-home')
		}
	}
	public static Task createCompileSwiftTask(Project project, String name) {
		return project.task(type: Exec, name) {
			commandLine("bash", "-c", "SWIFTC=" + SwiftAndroidPlugin.swiftRoot.absolutePath + 
				"/bin/swiftc-pm-android swift build")
			workingDir("src/main/swift")
		}
	}
}
