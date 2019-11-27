rootProject.name = "RxCommon"
rootProject.buildFileName = "build.gradle.kts"

include(
  ":rxcommon-core",
  ":rxcommon-binding",
  ":rxcommon-binding-livedata"
)

enableFeaturePreview("GRADLE_METADATA")
