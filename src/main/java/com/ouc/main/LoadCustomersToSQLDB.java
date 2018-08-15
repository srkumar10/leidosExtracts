package com.ouc.main;

import com.ouc.batch.processCustomers;
import com.ouc.io.Reader;
import com.ouc.model.Customer;
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
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LoadCustomersToSQLDB {

    private ConfigProperties props;
    private List<Customer> customerList;
    private List<String> filesList;

    void execute() {
        props = new ConfigProperties();
        String inputDir = props.readProperty("input.dir");
        String mappingFile = props.readProperty("in.customer.mapping");
        Path inDirPath = Paths.get(inputDir);
        filesList = readFiles(inDirPath);

        if ((filesList != null) && (!filesList.isEmpty())) {
            for (String fileName : filesList) {
                if (fileName.startsWith("OUC_Customers_BIQ")) {
                    customerList = new processCustomers().execute(new Reader(inputDir, fileName, mappingFile, "customers").mapObjects(), props);
                    processCustomers(customerList);
                }
            }
        } else {
            System.out.println("No files to process");
        }
    }

    private void processCustomers(List<Customer> customerList) {
        try {
            insertDB(customerList);
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


    public void insertDB(List<Customer> customerList) throws SQLException {

        Connection dbConnection = null;
        PreparedStatement ps = null;
        final int batchSize = 1000;
        int batchCount = 0;
        int count = 0;

        String query = " insert into OUConsumption.CUSTOMERS"
                + "(ACCT_NBR, SP_ID, MTR_ID, MTR_BADGE, MTR_CONFIG)"
                + " values (?, ?, ?, ?, ?)";


        try {
            dbConnection = new SQLDatabase().getDBConnection();
            ps = dbConnection.prepareStatement(query);

            dbConnection.setAutoCommit(false);
            System.out.println("Started processing the records");

            for (Customer customer : customerList) {
                ps.setString(1, customer.getAccountID());
                ps.setString(2, customer.getServicePointID());
                ps.setString(3, customer.getMeterID());
                ps.setString(4, customer.getMeterBadge());
                ps.setString(5, customer.getMeterConfigID());
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
