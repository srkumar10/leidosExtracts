package com.ouc.main;

import com.ouc.batch.processMeters;
import com.ouc.io.Reader;
import com.ouc.io.Writer;
import com.ouc.model.Meter;
import com.ouc.utils.ConfigProperties;
import org.beanio.BeanWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FindDuplicates {

    ConfigProperties props;
    private List<String> filesList;
    private List<Meter> meterList;
    Set<String> duplicates;

    public void execute() {
        props = new ConfigProperties();

        String inputDir = props.readProperty("input.dir");
        String mappingFile = props.readProperty("in.meter.mapping");
        String path = props.readProperty("input.dir");
        Path inDirPath = Paths.get(path);
        filesList = readFiles(inDirPath);

        if ((filesList != null) && (!filesList.isEmpty())) {
            for (String fileName : filesList) {
                if (fileName.startsWith("Leidos_MeterInventory_Final_174573")) {
                    meterList = new processMeters().execute(new Reader(inputDir, fileName, mappingFile, "meters").mapObjects(), props);
                }
            }

            List<Meter> duplicates = findDuplicates(meterList);


            if (!duplicates.isEmpty()) {
                BeanWriter writer = new Writer("output.dir", "duplicates.csv", "out.meter.mapping", "meters", props).getWriter();
                writeMeters(duplicates, writer);
                System.out.println("Duplicate file generated !");
            } else {
                System.out.println("The file doesn't have any duplicates");
            }


        } else {
            System.out.println("No files to process");
        }
    }

    public List<String> readFiles(Path dirPath) {

        try {
            filesList = Files.walk(dirPath)
                    .filter(p -> p.toString().endsWith(".csv"))
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesList;
    }

    public List<Meter> findDuplicates(List<Meter> meterList) {

        Set<String> itemsList = new HashSet<String>();
        Set<Meter> duplicates = new HashSet<Meter>();


        for (Meter m : meterList) {
            if (itemsList.contains(m.getMeterBadge())) {
                duplicates.add(m);
            } else {
                itemsList.add(m.getMeterBadge());
                //System.out.println(m.getMeterBadge());
            }
        }
        return new ArrayList<>(duplicates);
    }

    private void writeMeters(List<Meter> meters, BeanWriter writer) {
        meters.forEach(writer::write);
        writer.flush();
        writer.close();
    }
}
