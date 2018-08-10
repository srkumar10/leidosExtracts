package com.ouc.main;

import com.ouc.batch.processMeters;
import com.ouc.io.Reader;
import com.ouc.model.Meter;
import com.ouc.utils.ConfigProperties;
import com.ouc.utils.SQLDatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LoadMetersToSQLDB {

    private ConfigProperties props;
    private List<Meter> meterList;
    private List<String> filesList;

    void execute() {
        props = new ConfigProperties();
        String inputDir = props.readProperty("input.dir");
        String mappingFile = props.readProperty("in.meter.mapping");
        Path inDirPath = Paths.get(inputDir);
        filesList = readFiles(inDirPath);

        if ((filesList != null) && (!filesList.isEmpty())) {
            for (String fileName : filesList) {
                if (fileName.startsWith("OUC_MeterInventory_Final_174573")) {
                    meterList = new processMeters().execute(new Reader(inputDir, fileName, mappingFile, "meters").mapObjects(), props);
                    processMeters(meterList);
                }
            }
        } else {
            System.out.println("No files to process");
        }
    }

    private void processMeters(List<Meter> meterList) {
        try {
            insertDB(meterList);
        } catch (SQLException e) {
            e.printStackTrace();
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


    public void insertDB(List<Meter> meterList) throws SQLException {

        Connection dbConnection = null;
        PreparedStatement ps = null;
        final int batchSize = 1000;
        int batchCount = 0;
        int count = 0;

        String query = " insert into OUConsumption.METER_INVENTORY"
                + "(MTR_BADGE_NBR, MFG_CD, MODEL_CD, MTR_TYPE_CD, SP_ID, SP_TYPE_CD, PREM_ID, PREM_TYPE_CD, LOAD_DATE)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try {
            dbConnection = new SQLDatabase().getDBConnection();
            ps = dbConnection.prepareStatement(query);

            dbConnection.setAutoCommit(false);
            System.out.println("Started processing the records");

            for (Meter meter : meterList) {
                ps.setString(1, meter.getMeterBadge());
                ps.setString(2, meter.getManufactureCode());
                ps.setString(3, meter.getModelCode());
                ps.setString(4, meter.getMeterType());
                ps.setString(5, meter.getServicePointID());
                ps.setString(6, meter.getServicePointType());
                ps.setString(7, meter.getPremiseID());
                ps.setString(8, meter.getPremiseType());
                ps.setDate(9, getDateTime());
                ps.addBatch();


                if (++count % batchSize == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                    count = 0; //reset count
                    dbConnection.commit();
                    System.out.println(batchCount++ + " " + "Batch inserted into the table");
                }
            }

            if (count > 0) {
                ps.executeBatch(); // insert remaining records
                dbConnection.commit();
            }

            System.out.println("All records inserted into the table");


        } catch (SQLException e) {
            dbConnection.rollback();
            dbConnection.setAutoCommit(false);
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    public java.sql.Date getDateTime() {

        String dateString = "07-27-2018";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(dateString, formatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        //java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());

        return sqlDate;

    }

}
