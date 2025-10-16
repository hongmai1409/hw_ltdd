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
        google()
        mavenCentral()
    }
}

<<<<<<< HEAD
rootProject.name = "JetpackCompose"
include(":app")
=======
<<<<<<< HEAD
rootProject.name = "MyApp"
include(":app")
=======
rootProject.name = "HW2_bt3"
include(":app")
include(":app")
 
>>>>>>> 13268ee (hw2)
>>>>>>> 50aa58d3afa097313323db823eb306d8e4335611
