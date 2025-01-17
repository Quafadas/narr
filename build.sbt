import laika.config.SyntaxHighlighting
import laika.format.Markdown
import laika.helium.Helium
import laika.helium.config.{HeliumIcon, IconLink}

val appVersion:String = "1.0"
val globalScalaVersion = "3.3.4"

ThisBuild / tlSitePublishBranch := Some("main")
ThisBuild / tlBaseVersion := "0.0"
ThisBuild / organization := "io.github.quafadas"
ThisBuild / organizationName := "quafadas"
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / startYear := Some(2023)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("quafadas", "Simon Parten"),
  tlGitHubDev("dragonfly-ai", "dragonfly.ai" )
)
ThisBuild / scalaVersion := globalScalaVersion
ThisBuild / tlBaseVersion := appVersion
ThisBuild / tlCiReleaseBranches := Seq("main")

ThisBuild / tlSonatypeUseLegacyHost := false
//ThisBuild / sonatypeCredentialHost := xerial.sbt.Sonatype.sonatypeLegacy

lazy val narr = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .settings(
    description := "Native Typed Arrays with Scala Semantics for Scala JVM, ScalaJS, and Scala Native!",
  )
  .jvmSettings()
  .jsSettings()

lazy val root = tlCrossRootProject.aggregate(narr, tests).settings(name := "narr")

lazy val docs = project
  .in(file("site"))
  .dependsOn(narr.jvm)
  .settings(
    laikaExtensions := Seq(Markdown.GitHubFlavor, SyntaxHighlighting),
    laikaConfig ~= { _.withRawContent },
    tlSiteHelium := {
      Helium.defaults.site.metadata(
          title = Some("S"),
          language = Some("en"),
          description = Some("S"),
          authors = Seq("one"),
        )
        .site
        .topNavigationBar(
          homeLink = IconLink.internal(laika.ast.Path(List("index.md")), HeliumIcon.home),
          navLinks = Seq(IconLink.external("https://github.com/dragonfly-ai/narr", HeliumIcon.github))
        )
    }
  )
  .enablePlugins(TypelevelSitePlugin)
  .enablePlugins(NoPublishPlugin)

lazy val unidocs = project
  .in(file("unidocs"))
  .enablePlugins(TypelevelUnidocPlugin) // also enables the ScalaUnidocPlugin
  .settings(
    name := "narr-docs",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(narr.jvm, narr.js, narr.native)
  )

lazy val tests = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .in(file("tests"))
  .enablePlugins(NoPublishPlugin)
  .dependsOn(narr)
  .settings(
    name := "narr-tests",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.4" % Test
  )