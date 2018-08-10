package com.ouc.main;

import com.ouc.batch.processInstalls;
import com.ouc.io.Reader;
import com.ouc.model.Install;
import com.ouc.utils.ConfigProperties;
import com.ouc.utils.SQLDatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class LoadInstallsToSQLDB {

    private ConfigProperties props;
    private List<Install> installList;
    private List<String> filesList;

    public void execute() {
        props = new ConfigProperties();
        String inputDir = props.readProperty("input.dir");
        String mappingFile = props.readProperty("in.install.mapping");
        Path inDirPath = Paths.get(inputDir);
        filesList = readFiles(inDirPath);

        if ((filesList != null) && (!filesList.isEmpty())) {
            for (String fileName : filesList) {
                if (fileName.equalsIgnoreCase("Leidos_AccountSetup_Install.csv")) {
                    installList = new processInstalls().execute(new Reader(inputDir, fileName, mappingFile, "install").mapObjects(), props);
                    loadData(installList);
                }
            }
        } else {
            System.out.println("No files to process");
        }
    }

    private void loadData(List<Install> installList) {
        try {
            insertDB(installList);
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

    public java.sql.Date convertDateTime(java.util.Date dateTime) {

        return new java.sql.Date(dateTime.getTime());

    }


    public void insertDB(List<Install> installList) throws SQLException {

        Connection dbConnection = null;
        PreparedStatement ps = null;
        final int batchSize = 1000;
        int batchCount = 0;
        int count = 0;

        String query = " insert into OUConsumption.INSTALLS "
                + "(RECORD_NBR, TRANS_TYPE, UTILITY_ID, ACCT_NBR, BILL_CYC, SP_ID, SP_DESC, ADDR1, ADDR2, CITY, STATE, ZIP, RATE_CD, MTR_BADGE, LAT, LON, INSTALL_DT, TOU, CI, MTR_TYPE, NET_MTR, SITE_ID, MULTIPLIER, PIN, COMMODITY)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?)";


        try {
            dbConnection = new SQLDatabase().getDBConnection();

            if (dbConnection != null) {
                ps = dbConnection.prepareStatement(query);
                dbConnection.setAutoCommit(false);
                System.out.println("Started processing the records");


                for (Install install : installList) {
                    ps.setInt(1, install.getRecordNumber());
                    ps.setString(2, install.getTransactionType());
                    ps.setString(3, install.getUtilityID());
                    ps.setString(4, install.getAccountNumber());
                    ps.setString(5, install.getBillingCycle());
                    ps.setString(6, install.getServicePointID());
                    ps.setString(7, install.getDescription());
                    ps.setString(8, install.getAddress1());
                    ps.setString(9, install.getAddress2());
                    ps.setString(10, install.getCity());
                    ps.setString(11, install.getState());
                    ps.setString(12, install.getZip());
                    ps.setString(13, install.getRateCode());
                    ps.setString(14, install.getMeterBadge());
                    ps.setString(15, install.getLat());
                    ps.setString(16, install.getLon());
                    ps.setDate(17, convertDateTime(install.getInstallDate()));
                    ps.setString(18, install.getTou());
                    ps.setString(19, install.getCi());
                    ps.setString(20, install.getMeterType());
                    ps.setString(21, install.getNetMeter());
                    ps.setString(22, install.getSiteID());
                    ps.setString(23, install.getMultiplier());
                    ps.setString(24, install.getPin());
                    ps.setString(25, install.getCommodity());

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
            }


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
}
