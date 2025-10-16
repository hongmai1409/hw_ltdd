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
rootProject.name = "BaiTapTuan3"
include(":app")
 
=======
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
>>>>>>> 5cbef83aa5a495c8655b787d4555da75026e9944
