package org.codeartisans.mojo.flamingo;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.jvnet.flamingo.svg.SvgTranscoder;
import org.jvnet.flamingo.svg.TranscoderListener;

/**
 * @goal transcode
 */
public class SvgTranscoderMojo
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
    private static FilenameFilter svgFilter = new FilenameFilter()
    {

        @Override
        public boolean accept( File dir, String name )
        {
            return name.endsWith( ".svg" );
        }

    };

    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException
    {
        if ( getLog().isDebugEnabled() ) {
            getLog().debug( "Will process *.svg in: " + svgDirectory );
            getLog().debug( "ResizableIcons will have package: " + java2dPackage );
            getLog().debug( "Class files will be but in: " + java2dDirectory + File.separator + java2dPackage.replaceAll( "\\.", File.separator ) );
        }
        try {

            FileUtils.forceMkdir( java2dDirectory );

            for ( File eachSvg : svgDirectory.listFiles( svgFilter ) ) {

                final String svgClassName = classNameFromFileName( eachSvg.getName().substring( 0, eachSvg.getName().length() - 4 ) ) + "Icon";
                final File java2dClassFileDirectory = new File( java2dDirectory + File.separator + java2dPackage.replaceAll( "\\.", File.separator ) );
                final String javaClassFilename = java2dClassFileDirectory + File.separator + svgClassName + ".java";

                FileUtils.forceMkdir( java2dClassFileDirectory );

                try {
                    final CountDownLatch latch = new CountDownLatch( 1 );
                    final PrintWriter pw = new PrintWriter( javaClassFilename );

                    final SvgTranscoder transcoder = new SvgTranscoder( eachSvg.toURI().toURL().toString(), svgClassName );
                    transcoder.setJavaToImplementResizableIconInterface( true );
                    transcoder.setJavaPackageName( java2dPackage );
                    transcoder.setListener( new TranscoderListener()
                    {

                        @Override
                        public Writer getWriter()
                        {
                            return pw;
                        }

                        @Override
                        public void finished()
                        {
                            latch.countDown();
                        }

                    } );
                    transcoder.transcode();
                    latch.await();
                } catch ( Exception ex ) {
                    if ( getLog().isDebugEnabled() ) {
                        getLog().debug( ex.getMessage(), ex );
                    }
                    System.err.println( "Unable to transcode: " + eachSvg.getAbsolutePath() );
                }
            }
        } catch ( IOException ex ) {
            throw new MojoFailureException( ex.getMessage(), ex );
        }
    }

    private static String classNameFromFileName( final String filename )
    {
        String className = filename.toUpperCase( Locale.ENGLISH ).toLowerCase();
        className = className.replace( '-', ' ' );
        className = className.replace( '_', ' ' );
        className = upperCaseFirstLetterOfWords( className );
        className = className.replace( " ", "" );
        return className;
    }

    private static String upperCaseFirstLetterOfWords( final String input )
    {
        if ( input == null || input.length() < 1 ) {
            return input;
        }
        char ch;
        char prevCh = '.';
        int i;
        final StringBuffer sb = new StringBuffer( input.length() );
        for ( i = 0; i < input.length(); i++ ) {
            ch = input.charAt( i );
            if ( Character.isLetter( ch ) && !Character.isLetter( prevCh ) ) {
                sb.append( Character.toUpperCase( ch ) );
            } else if ( Character.isLetter( ch ) ) {
                sb.append( Character.toLowerCase( ch ) );
            } else {
                sb.append( ch );
            }
            prevCh = ch;
        }
        return sb.toString();
    }

}
