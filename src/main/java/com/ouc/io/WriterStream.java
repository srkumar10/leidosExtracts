package com.ouc.io;

import org.beanio.BeanIOConfigurationException;
import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class WriterStream {

    BeanWriter createWriter(String directory, String fileName, String mappingFile, String streamName) {

        // Initialize
        StreamFactory factory;
        BufferedWriter dataFileWriter;
        BeanWriter writer = null;
        InputStream mappingFileStream;

        try {

            Path outputFile = Paths.get(directory + "/" + fileName);

            // Create a StreamFactory
            factory = StreamFactory.newInstance();

            // Load the mapping file
            mappingFileStream = Files.newInputStream(Paths.get(mappingFile));
            factory.load(mappingFileStream);

            // Create output data stream
            dataFileWriter = Files.newBufferedWriter(outputFile);

            // Create bean writer to output a file and specify the mapping output stream defined in the mapping file
            writer = factory.createWriter(streamName, dataFileWriter);

        } catch (BeanIOConfigurationException | IOException e) {
            System.exit(1);
        }

        return writer;
    }
}
