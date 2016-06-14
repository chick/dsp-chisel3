organization := "edu.berkeley.cs"
version := "1.0"
name := "chisel.dsp"

scalaVersion := "2.11.7"

// Provide a managed dependency on X if -DXVersion="" is supplied on the command line.
// The following are the default development versions, not the "release" versions.
val defaultVersions = Map(
  "chisel-iotesters" -> "1.0",
  "chisel3" -> "3.0",
  "firrtl" -> "0.1-SNAPSHOT",
  "firrtl-interpreter" -> "0.1"
)

libraryDependencies ++= (
  Seq(
    "chisel-iotesters", "chisel3","firrtl","firrtl-interpreter"
  ).map { dep: String =>
    "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep))
  })

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)