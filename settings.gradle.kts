dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven {
            setUrl("https://maven.pkg.github.com/mParticle/crossplatform-sdk-tests")
        }
    }
}
include(":core-sdk-samples:higgs-shop-sample-app:app")