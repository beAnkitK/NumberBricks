pluginManagement {
  repositories {
    google {
        content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
        }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  
  repositories {
    google {
        content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
        }
    }
    mavenCentral()
  }
  
  versionCatalogs {
    create("libs") {
        from(files("gradle/version-catalogs/libs.versions.toml"))
    }
    create("plugin") {
        from(files("gradle/version-catalogs/plugins.versions.toml"))
    }
    create("build") {
        from(files("gradle/version-catalogs/project.versions.toml"))
    }
  }
}

rootProject.name = "NumberBricks"
include(":app")
include(":numberbricks")