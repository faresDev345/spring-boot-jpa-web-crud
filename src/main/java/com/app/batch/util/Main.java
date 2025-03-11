package com.app.batch.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		
		String str = "123.45";
		try {
		    int num = Integer.parseInt(str); // Lève NumberFormatException
		} catch (NumberFormatException e) {
		    System.err.println(" <<<<<<< >>>>>>>>>>>>> Erreur de format numérique: " + e.getMessage());
		}

		try {
		    double num = Double.parseDouble(str); // Conversion correcte
		    System.out.println("Numéro: " + num);
		} catch (NumberFormatException e) {
		    System.err.println("Erreur de format numérique: " + e.getMessage());
		}
		  //Printing Info
        System.out.println("Choose the DataSource that you would like to connect to:");
        System.out.println("1. Postgres");
        System.out.println("2. Mysql");

        //Reading choice from user
        Scanner keyboard = new Scanner(System.in);
        int choice = 2;
        try
        {
           // choice = Integer.parseInt(keyboard.nextLine());

            //Calling appropriate methods based on selection
            switch(choice){
                case 1:
                    PostgresDB postgresDB = new PostgresDB();
                    postgresDB.initiateConnection();
                    initiateOperations(postgresDB);
                    break;
                case 2:
                    MysqlDB myDB = new MysqlDB();
                    myDB.initiateConnection();
                    initiateOperations(myDB);
                    break;
            }
        }
        catch (Exception ex)
        {

            System.out.println("Error:\n " + ex.getMessage());
        	 System.out.println("Invalid Input. Terminating the program");
             System.exit(-1);
        }



    }

    public static void initiateOperations(Object object) throws Exception 
    {
        JDBCUtil jdbcUtil = null;

        if(object instanceof MysqlDB)
        {
            jdbcUtil = ((MysqlDB) object).jdbcUtil;
        }else
        {
            jdbcUtil = ((PostgresDB)object).jdbcUtil;
        }


        System.out.println("What would you like to do?");
        System.out.println("A. Execute Simple query \n");
        System.out.println("B. List all tables\n");
        System.out.println("C. Show metadata of a table \n");
        Scanner input = new Scanner(System.in);
        String choice =  "O";
        try
        {
            choice =  input.nextLine();
        }
        catch (Exception   ex)
        {
            System.out.println("Invalid Input. Terminating the program");
            System.out.println("Error:\n " + ex.getMessage());
            System.exit(-1);
        }


        switch (choice){

            case "A":
                jdbcUtil.executeQuery();
                break;
            case "B":
                jdbcUtil.listAllTables();
                break;
            case "C":
                ArrayList<String> tableList = jdbcUtil.getTablesList();
                Map<String,String> mapTable=  new HashMap<>();
                for(int i = 0; i < tableList.size(); i++)
                {
                    System.out.println("D"+i +" >>>>>>>>>>> Table : " + tableList.get(i));
                    mapTable.put("D"+i, tableList.get(i));
                }

                System.out.println("Choose the table you would like to see the metadata for: ");
                Scanner scanner = new Scanner(System.in);
                String number = "-1";

                try
                { 
						number =  (scanner.nextLine());
						if(!number.contains("D") || !number.startsWith("D"))
						{
						    System.out.println("Invalid choice.");
						    System.exit(-1);
						}  
							jdbcUtil.showTableMetaData(mapTable.get(number));
						 
		                break;
					 
                }  catch(Exception ex)
                {
                    System.out.println("Exception: Terminating the program.." + ex.getMessage());
                }  

				

        }
    }



}
