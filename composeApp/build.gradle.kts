import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            // Для создания cross-platform fatJar нам нужны все нативные библиотеки
            // implementation(compose.desktop.currentOs)
            implementation(compose.desktop.windows_x64)
            implementation(compose.desktop.linux_x64)
            implementation(compose.desktop.macos_x64)
            implementation(compose.desktop.macos_arm64)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.AppImage,
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.Msi
            )

            modules = arrayListOf(
                "jdk.security.auth",
                "java.base",
                "java.desktop",
                "java.sql",
                "jdk.unsupported"
            )
            packageName = "SplitKitCat"
            packageVersion = (project.findProperty("versionName") as String?) ?: "1.0.0"
        }
        buildTypes.release.proguard {
            isEnabled = false
//            obfuscate = true
//            optimize = true
//            joinOutputJars = true
//            configurationFiles.from(
//                file("proguard-rules.pro")
//            )
        }
    }
}

tasks.register<Jar>("fatJar") {
    manifest {
        attributes["Main-Class"] = "org.example.project.MainKt"
    }
//    archiveBaseName.set("SplitKitCat-fat")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(kotlin.targets.getByName("jvm").compilations.getByName("main").output)
    from({
        configurations.getByName("jvmRuntimeClasspath")
            .map { if (it.isDirectory) it else zipTree(it) }
    })
}
