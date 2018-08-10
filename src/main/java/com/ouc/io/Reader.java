package com.ouc.io;

import org.beanio.BeanReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Reader {

    private String mapFile;
    private String inputDir;
    private String fileName;
    private String streamName;

    public Reader(String inputDirectory, String fileName, String mappingFile, String streamName) {
        inputDir = inputDirectory;
        mapFile = mappingFile;
        this.fileName = fileName;
        this.streamName = streamName;

    }

    public List<Object> mapObjects() {
        Object record = null;
        List<Object> objects = new ArrayList<Object>();

        BeanReader reader = new ReaderStream().createReader(inputDir, fileName, mapFile, streamName);

        while ((record = reader.read()) != null) {
            if ("header".equals(reader.getRecordName())) {
                Map<String, Object> header = (Map<String, Object>) record;
            } else if ("detail".equals(reader.getRecordName())) {
                objects.add((Object) record);
            } else if ("trailer".equals(reader.getRecordName())) {
                // Integer recordCount = (Integer) record;
            }
        }

        return objects;
    }
}
