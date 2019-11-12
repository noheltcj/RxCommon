rootProject.name = "RxCommon"
rootProject.buildFileName = "build.gradle.kts"

include(
  ":rxcommon-core",
  ":rxcommon-binding"
)

enableFeaturePreview("GRADLE_METADATA")
