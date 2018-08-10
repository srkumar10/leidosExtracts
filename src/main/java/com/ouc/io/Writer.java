package com.ouc.io;

import com.ouc.utils.ConfigProperties;
import org.beanio.BeanWriter;

public class Writer {

    private String mapFile;
    private String fileName;
    private String streamName;
    private String outputDir;


    public Writer(String outputDirectory, String fileName, String mappingFile, String streamName, ConfigProperties props) {
        this.mapFile = props.readProperty(mappingFile);
        this.fileName = fileName;
        this.streamName = streamName;
        outputDir = props.readProperty(outputDirectory);
    }


    public BeanWriter getWriter() {
        return new WriterStream().createWriter(outputDir, fileName, mapFile, streamName);
    }
}
