// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
}

tasks.register("copyGitHooks", Copy::class.java) {
    description = "Copies the git hooks from /git-hooks to the .git folder."
    group = "git hooks"
    from("$rootDir/scripts/pre-commit")
    into("$rootDir/.git/hooks/")
}
tasks.register("installGitHooks") {
    description = "Installs the pre-commit git hooks in a cross-platform way"
    group = "git hooks"

    dependsOn("copyGitHooks")

    doLast {
        val os = org.gradle.internal.os.OperatingSystem.current()

        if (!os.isWindows) {
            providers.exec {
                workingDir = rootDir
                commandLine("chmod", "-R", "+x", ".git/hooks/")
            }
            logger.lifecycle("Git hook installed successfully (Unix).")
        } else {
            logger.lifecycle("Windows detected – skipping chmod (not required).")
        }
    }
}

afterEvaluate {
    tasks.getByPath(":app:preBuild").dependsOn(":installGitHooks")
}
