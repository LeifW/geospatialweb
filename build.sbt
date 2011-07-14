organization := "org.geospatialweb"

name := "geospatialweb"

version := "0.1"

libraryDependencies := Seq(
  "com.hp.hpl.jena" % "arq" % "2.8.8",
  "com.novocode" % "junit-interface" % "0.7" % "test"
)

compileOrder := CompileOrder.JavaThenScala
