val githubRepo = githubHosting("logicovercode", "app-core", "techLeadAtLogicOverCode", "techlead@logicovercode.com")

val sBuild = SBuild("com.logicovercode", "app-core", "0.0.003")
  .sourceDirectories("core")
  .testSourceDirectories("core-spec")
  .dependencies( better_files(), cats_core() )
  .testDependencies( scalatest() )
  .scalaVersions(scala_2_13_MaxVersion, Seq(scala_2_13_MaxVersion, scala_3_3_1))
  .javaCompatibility("1.8", "1.8")
  .publish(githubRepo.developer, MIT_License, githubRepo, Opts.resolver.sonatypeStaging)

val appCoreProject = (project in file("."))
  .settings(sBuild.settings)
