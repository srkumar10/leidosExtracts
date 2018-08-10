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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class CompareFiles {

    ConfigProperties props;
    private List<String> filesList;
    private List<Meter> meters1;
    private List<Meter> meters2;

    public void execute() {
        props = new ConfigProperties();

        String inputDir = props.readProperty("input.dir");
        String mappingFile = props.readProperty("in.meter.mapping");
        String path = props.readProperty("input.dir");
        Path inDirPath = Paths.get(path);
        filesList = readFiles(inDirPath);

        if ((filesList != null) && (!filesList.isEmpty())) {
            for (String fileName : filesList) {
                if (fileName.equalsIgnoreCase("Leidos_Meter_Inventory.csv")) {
                    meters1 = new processMeters().execute(new Reader(inputDir, fileName, mappingFile, "meters").mapObjects(), props);
                } else if (fileName.equalsIgnoreCase("OUC_MeterInventory_Final_174573.csv")) {
                    meters2 = new processMeters().execute(new Reader(inputDir, fileName, mappingFile, "meters").mapObjects(), props);
                }
            }
            compareMetersList(removeDuplicates(meters1), removeDuplicates(meters2));

            //compareMetersList(meters1, meters2);
            //findDuplicates(meters1);
            //List<Meter> uniqueMeters1 = removeDuplicates(meters1);

            //OUC_MeterInventory_Final
            //OUC_MeterInventory-07-26-2018

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


    public void compareMetersList(List<Meter> meters1, List<Meter> meters2) {

        if ((meters1 != null) && (!meters1.isEmpty())) {
            if ((meters2 != null) && (!meters2.isEmpty())) {

                // Create meters1 map with key as meterBadge
                Map<String, Meter> meters1Map = meters1.stream().collect(Collectors.toMap(Meter::getMeterBadge, Function.identity()));

                // Create meters2 map with key as meterBadge
                Map<String, Meter> meters2Map = meters2.stream().collect(Collectors.toMap(Meter::getMeterBadge, Function.identity()));

                BeanWriter writer = new Writer("output.dir", "meterDifference.csv", "out.meter.mapping", "meters", props).getWriter();

                for (String m2Key : meters2Map.keySet()) {
                    if (!meters1Map.containsKey(m2Key)) {
                        Meter m2 = meters2Map.get(m2Key);
                        writer.write(m2);
                    }
                }

                writer.flush();
                writer.close();
            }
        }
    }


    public List<Meter> removeDuplicates(List<Meter> meterList) {
        Set<String> itemsList = new HashSet<String>();
        Set<Meter> uniqueList = new HashSet<Meter>();
        for (Meter m : meterList) {
            if (!itemsList.contains(m.getMeterBadge())) {
                itemsList.add(m.getMeterBadge());
                uniqueList.add(m);
            }
        }

        return new ArrayList<>(uniqueList);
    }
}
