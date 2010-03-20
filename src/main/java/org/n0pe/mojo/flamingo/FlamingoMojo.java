package org.n0pe.mojo.flamingo;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal transcode
 */
public class FlamingoMojo
        extends AbstractMojo
{

    /**
     * @parameter
     * @required
     */
    private File svgDirectory;
    /**
     * @parameter
     * @required
     */
    private String java2dPackage;
    /**
     * @parameter default-value="${project.build.directory}/generated-sources/java"
     * @required
     */
    private File java2dDirectory;

    @Override
    public final void execute()
            throws MojoExecutionException, MojoFailureException
    {
        if ( getLog().isDebugEnabled() ) {
            getLog().debug( "Will process *.svg in: " + svgDirectory );
            getLog().debug( "ResizableIcons will have package: " + java2dPackage );
            getLog().debug( "Class files will be but in: " + java2dDirectory + File.separator
                    + java2dPackage.replaceAll( "\\.", File.separator ) );
        }
        try {
            SvgTranscoderCli.main( new String[]{ svgDirectory.getAbsolutePath(),
                                                 java2dDirectory.getAbsolutePath(),
                                                 java2dPackage } );
        } catch ( IOException ex ) {
            throw new MojoFailureException( ex.getMessage(), ex );
        }
    }

}
