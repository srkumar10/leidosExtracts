package com.ouc.main;


public class App {
    public static void main(String[] args) {
        try {
            //new CompareFiles().execute();
            //new LoadMetersToMYSQLDB().execute();
            //new LoadMetersToSQLDB().execute();
            //new LoadInstallsToSQLDB().execute();
            //new FindDuplicates().execute();
            //new RemoveDuplicates().execute();
            new LoadCustomersToSQLDB().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
