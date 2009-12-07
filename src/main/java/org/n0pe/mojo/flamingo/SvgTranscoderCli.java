package org.n0pe.mojo.flamingo;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import org.codehaus.plexus.util.FileUtils;
import org.jvnet.flamingo.svg.SvgTranscoder;
import org.jvnet.flamingo.svg.TranscoderListener;


public class SvgTranscoderCli {


    public static void outputUsage() {
        System.out.println("");
        System.out.println("Usage: svg2java2d svgDirectory java2dDirectory java2dPackage");
        System.out.println("");
    }


    public static void main(String[] args)
            throws IOException {

        if (args.length != 3) {
            outputUsage();
            System.exit(1);
        }

        final File svgDirectory = new File(args[0]);
        final String java2dPackage = args[2];
        final File java2dDirectory = new File(args[1]);

        if (!svgDirectory.exists()) {
            outputUsage();
            System.exit(1);
        }
        if (!java2dDirectory.exists()) {
            FileUtils.forceMkdir(java2dDirectory);
        }

        // System.out.println("Will process *.svg in: " + svgDirectory);
        // System.out.println("ResizableIcons will have package: " + java2dPackage);
        // System.out.println("Class files will be but in: " + java2dDirectory + File.separator+java2dPackage.replaceAll("\\.", File.separator));


        for (File eachSvg : svgDirectory.listFiles(new FilenameFilter() {


            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".svg");
            }


        })) {
            final String svgClassName = classNameFromFileName(
                    eachSvg.getName().substring(0, eachSvg.getName().length() - 4)) + "Icon";


            final File java2dClassFileDirectory = new File(java2dDirectory + File.separator +
                    java2dPackage.replaceAll("\\.", File.separator));

            FileUtils.forceMkdir(java2dClassFileDirectory);

            final String javaClassFilename = java2dClassFileDirectory + File.separator + svgClassName + ".java";

            // System.out.println("Processing " + eachSvg.getName() + " => " + javaClassFilename);
            // System.out.println(java2dPackage + "." + svgClassName);

            try {
                final CountDownLatch latch = new CountDownLatch(1);
                final PrintWriter pw = new PrintWriter(javaClassFilename);

                final SvgTranscoder transcoder = new SvgTranscoder(eachSvg.toURI().toURL().toString(), svgClassName);
                transcoder.setJavaToImplementResizableIconInterface(true);
                transcoder.setJavaPackageName(java2dPackage);
                transcoder.setListener(new TranscoderListener() {


                    @Override
                    public Writer getWriter() {
                        return pw;
                    }


                    @Override
                    public void finished() {
                        latch.countDown();
                    }


                });
                transcoder.transcode();
                latch.await();
            } catch (Exception e) {
                // e.printStackTrace();
                System.err.println("Unable to transcode: "+eachSvg.getAbsolutePath());
            }
        }
    }


    private static String classNameFromFileName(final String filename) {
        String className = filename.toUpperCase(Locale.ENGLISH).toLowerCase();
        className = className.replace('-', ' ');
        className = className.replace('_', ' ');
        className = upperCaseFirstLetterOfWords(className);
        className = className.replace(" ", "");
        return className;
    }


    private static String upperCaseFirstLetterOfWords(final String input) {
        if (input == null ||
                input.length() < 1) {
            return input;
        }
        char ch;
        char prevCh = '.';
        int i;
        final StringBuffer sb = new StringBuffer(input.length());
        for (i = 0; i < input.length(); i++) {
            ch = input.charAt(i);
            if (Character.isLetter(ch) && !Character.isLetter(prevCh)) {
                sb.append(Character.toUpperCase(ch));
            } else if (Character.isLetter(ch)) {
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
            prevCh = ch;
        }
        return sb.toString();
    }


}
