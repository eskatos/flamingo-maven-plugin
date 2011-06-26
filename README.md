# flamingo-maven-plugin

flamingo-maven-plugin transcode SVG files into Java2D classes that can implement ResizableIcon from the Flamingo/Peacock project

The project is hosted in maven central.
[here](http://search.maven.org/#search%7Cga%7C1%7Cflamingo-maven-plugin) you'll find a quick copy/paste for the dependency.

## Transcode SVG files

Use the following goal: transcode

### Configuration inside the POM

Here is a quick example:

    <plugin>
	<groupId>org.codeartisans</groupId>
	<artifactId>flamingo-maven-plugin</artifactId>
	<executions>
	    <execution>
		<id>actions</id>
		<phase>generate-sources</phase>
		<goals>
		    <goal>transcode</goal>
		</goals>
		<configuration>
		    <sourceDirectory>${project.basedir}/src/main/svg</sourceDirectory>
		    <outputPackage>com.example.icons</outputPackage>
		    <implementsResizableIcon>true</implementsResizableIcon><!-- Defaults to false -->
		    <stopOnFailure>false</stopOnFailure> <!-- Defaults to true -->
		</configuration>
	    </execution>
	</executions>
    </plugin>
