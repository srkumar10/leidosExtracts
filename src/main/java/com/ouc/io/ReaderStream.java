package com.ouc.io;


import org.beanio.BeanIOConfigurationException;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class ReaderStream {

    BeanReader createReader(String directory, String fileName, String mappingFile, String streamName) {

        // Initialize
        StreamFactory factory = null;
        InputStream mappingFileStream = null;
        InputStream dataFileStream = null;
        BeanReader reader = null;

        try {
            Path inputFile = Paths.get(directory + "/" + fileName);

            // Create a StreamFactory
            factory = StreamFactory.newInstance();

            // Load the mapping file
            mappingFileStream = Files.newInputStream(Paths.get(mappingFile));
            factory.load(mappingFileStream);

            // Load the data file
            dataFileStream = Files.newInputStream(inputFile);

            // Create bean reader to read from a data file
            reader = factory.createReader(streamName, new InputStreamReader(dataFileStream));

        } catch (BeanIOConfigurationException | IOException e) {
            System.out.println(e.getLocalizedMessage());
            //System.exit(1);
        }

        return reader;
    }
}
