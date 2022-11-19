# WindowLicker を使えるようにする

- Check out https://github.com/petercoulton/windowlicker
  windowlicker-core-DEV.jar
  windowlicker-swing-DEV.jar
```diff
diff --git a/build.xml b/build.xml
index e74a2a7..e62ac7a 100644
--- a/build.xml
+++ b/build.xml
@@ -54,7 +54,7 @@
 		<sequential>
 			<mkdir dir="@{classdir}" />
 
-			<javac srcdir="@{srcdir}" classpathref="@{classpathref}" destdir="@{classdir}" debug="true" source="1.5" target="1.5" fork="true" />
+			<javac srcdir="@{srcdir}" classpathref="@{classpathref}" destdir="@{classdir}" debug="true" source="1.7" target="1.7" fork="true" />
 
 			<copy todir="@{classdir}">
 				<fileset dir="@{srcdir}" excludes="**/*.java" />

```