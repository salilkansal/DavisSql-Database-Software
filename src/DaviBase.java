import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A lite version of MySQL.
 * @author Salil Kansal
 * @version 1.0
 * @since 2016-04-09
 */
public class DaviBase {

    private static String prompt = "davisql> ";
    private static String version = "Version 0.1";
    private static String currDatabase = "INFORMATION_SCHEMA";
    private static SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    public static void main(String[] args) {

        String userCommand;
        if (FirstRun.firstRun())
            FirstRun.createDefaultTables();

        splashScreen();


        Scanner scanner = new Scanner(System.in).useDelimiter(";");
        do {
            System.out.print(prompt);
            userCommand = scanner.next().trim();

            parse(userCommand);


        } while (!userCommand.equalsIgnoreCase("exit"));
        System.out.println("Exiting...");

    }


    private static void parse(String userCommand) {
        Scanner s1 = new Scanner(userCommand.toUpperCase());
        switch (s1.next()) {
            case "EXIT":
                break;
            case "SHOW":
                switch (userCommand.toUpperCase()) {
                    case "SHOW SCHEMAS":
                        String temp = currDatabase;
                        currDatabase = "INFORMATION_SCHEMA";
                        parseSelectString("SELECT * FROM SCHEMATA");
                        currDatabase = temp;
                        break;
                    case "SHOW TABLES":
                        //select from tables where informationSchema = currDB
                }
                break;
            case "USE":
                currDatabase = s1.next().toUpperCase();
                System.out.printf("Database switched to %s\n", currDatabase);
                break;
            case "CREATE":
                switch (getWord(userCommand, 2).toUpperCase()) {
                    case "TABLE":
                        parseCreateTableString(userCommand.toUpperCase());
                        break;
                    case "SCHEMA":
                        parseCreateSchemaString(userCommand.toUpperCase());
                        break;
                }
                break;
            case "INSERT":
                parseInsertString(userCommand);
                break;
            case "SELECT":
                parseSelectString(userCommand);
                break;
            case "HELP":
                System.out.println("SHOW SCHEMAS – Displays all schemas defined in your database.");
                System.out.println("USE – Chooses a schema.");
                System.out.println("SHOW TABLES – Displays all tables in the currently chosen schema.");
                System.out.println("CREATE SCHEMA – Creates a new schema to hold tables.");
                System.out.println("CREATE TABLE – Creates a new table schema, i.e. a new empty table.");
                System.out.println("INSERT INTO TABLE – Inserts a row/record into a table.");
                System.out.println("SELECT-FROM-WHERE” -style query");
                System.out.println("EXIT – Cleanly exits the program and saves all table and index information in non-volatile files.");
                break;
            default:
                System.out.println("Unknown command");
                break;
        }
    }


    private static void parseCreateSchemaString(String userCommand) {

        List<String> data = new LinkedList<>();
        data.add(getWord(userCommand, 3));
        insertTable("INFORMATION_SCHEMA", "SCHEMATA", data);
    }

    private static void parseInsertString(String userCommand) {
        String tableName = getWord(userCommand, 3);
        String columnList = userCommand.substring(userCommand.indexOf("(") + 1, userCommand.length() - 1);
        String[] cols = columnList.trim().split(",");
        List<String> data = new LinkedList<>();
        for (String val : cols) {
            String t = val.trim();
            if (t.charAt(0) == '\'')
                t = t.substring(1, t.length() - 1);
            data.add(t);
        }
        insertTable(currDatabase, tableName, data);

    }


    private static void parseCreateTableString(String userCommand) {


        String tableName = getWord(userCommand, 3);

        String columnList = userCommand.substring(userCommand.indexOf("(") + 1);
        columnList = columnList.substring(0, columnList.length() - 1);
        List<List<String>> colums = new LinkedList<>();
        String[] cols = columnList.split(",");
        int i = 0;
        while (i < cols.length) {
            List<String> temp = new LinkedList<>();
            Scanner s1 = new Scanner(cols[i]);
            while (s1.hasNext()) {
                temp.add(s1.next());
            }
            colums.add(temp);
            i++;

        }

        createTable(tableName, colums);
    }

    private static void parseSelectString(String userCommand) {

        String[] data = userCommand.split(" ");
        String tableName = data[3];
        LinkedHashMap<String, List<String>> tableStruct = getTableStructure(currDatabase, tableName.toUpperCase());
        if (tableStruct == null) return;
        SelectedTable table = new SelectedTable();
        for (String colName : tableStruct.keySet()) {
            table.headerRow.row.add(colName);
            table.colStructure.add(tableStruct.get(colName).get(0));
        }

        try {
            RandomAccessFile tableFile = new RandomAccessFile(getDatName(currDatabase, tableName), "rwd");
            int index = 0;
            while (index < tableFile.length()) {

                Row row = new Row();

                for (String colType : table.colStructure) {
                    if (colType.matches("VARCHAR\\(\\d+\\)") || colType.matches("VARCHAR\\(\\d+\\)")) {
                        String t = readVarChar(tableFile);
                        if (t.equals("0")) {
                            row.row.add("");
                        } else {
                            row.row.add(t);
                        }
                        index += t.length() + 1;

                    } else if (colType.matches("char\\(\\d+\\)") || colType.matches("CHAR\\(\\d+\\)")) {
                        int length = Integer.parseInt(colType.substring(5, colType.indexOf(")")));
                        String t = readChar(tableFile, length);
                        if (t.equals("0")) {
                            row.row.add("");
                        } else {
                            row.row.add(t);
                        }
                        index += t.length();

                    } else if (colType.equalsIgnoreCase("byte")) {
                        String t = String.valueOf(tableFile.readByte());
                        if (t.equals("0")) {
                            row.row.add("");
                        } else {
                            row.row.add(t);
                        }
                        index += 1;


                    } else if (colType.equalsIgnoreCase("short int") || colType.equalsIgnoreCase("short")) {
                        String t = String.valueOf(tableFile.readShort());
                        if (t.equals("0")) {
                            row.row.add("");
                        } else {
                            row.row.add(t);
                        }
                        index += 2;

                    } else if (colType.equalsIgnoreCase("int")) {
                        String t = String.valueOf(tableFile.readInt());
                        if (t.equals("0")) {
                            row.row.add("");
                        } else {
                            row.row.add(t);
                        }
                        index += 4;
                    } else if (colType.equalsIgnoreCase("LONG INT") || colType.equalsIgnoreCase("long")) {
                        String t = String.valueOf(tableFile.readLong());
                        if (t.equals("0")) {
                            row.row.add("");
                        } else {
                            row.row.add(t);
                        }
                        index += 8;

                    } else if (colType.equalsIgnoreCase("float")) {
                        String t = String.valueOf(tableFile.readFloat());
                        if (t.equals("0.0")) {
                            row.row.add("");
                        } else {
                            row.row.add(t);
                        }
                        index += 4;

                    } else if (colType.equalsIgnoreCase("double")) {
                        String t = String.valueOf(tableFile.readDouble());
                        if (t.equals("0.0")) {
                            row.row.add("");
                        } else {
                            row.row.add(t);
                        }
                        index += 8;


                    } else if (colType.equalsIgnoreCase("datetime")) {
                        Long t = readLong(tableFile);
                        if (t == 0) {
                            row.row.add("");
                        } else {
                            Date date = new Date(t);
                            row.row.add(dt.format(date));
                        }
                        index += 8;

                    } else if (colType.equalsIgnoreCase("date")) {
                        Long t = readLong(tableFile);
                        if (t == 0) {
                            row.row.add("");
                        } else {
                            Date date = new Date(t);
                            row.row.add(d.format(date));
                        }
                        index += 8;
                    }
                }
                if (data.length > 4) {
                    String[] t = data[5].split("(?!<>=)\\b",3);
                    String conditionCol = t[0];
                    String conditionVal = t[2];
                    if(conditionVal.charAt(conditionVal.length()-1)=='\''){
                        conditionVal = conditionVal.substring(0,conditionVal.length()-1);
                    }
                    String operator = t[1];
                    if(operator.charAt(operator.length()-1)=='\''){
                        operator = operator.substring(0,operator.length()-1);
                    }
                    Set<String> cols = tableStruct.keySet();
                    List<String> temp = new LinkedList<>();
                    temp.addAll(cols);
                    int position = temp.indexOf(conditionCol.toUpperCase());
                    String addedValue = row.row.get(position);
                    switch (operator) {
                        case ">":
                            String colType = tableStruct.get(conditionCol.toUpperCase()).get(0);

                            if (colType.matches("varchar\\(\\d+\\)") || colType.matches("VARCHAR\\(\\d+\\)")) {
                                if (addedValue.compareToIgnoreCase(conditionVal) > 0) {
                                    table.data.add(row);
                                }

                            } else if (colType.matches("char\\(\\d+\\)") || colType.matches("CHAR\\(\\d+\\)")) {
                                if (addedValue.compareToIgnoreCase(conditionVal) > 0) {
                                    table.data.add(row);
                                }

                            } else if (colType.equalsIgnoreCase("byte")) {
                                if (Byte.parseByte(addedValue) > Byte.parseByte(conditionVal)) {
                                    table.data.add(row);
                                }


                            } else if (colType.equalsIgnoreCase("short int") || colType.equalsIgnoreCase("short")) {
                                if (Short.parseShort(addedValue) > Short.parseShort(conditionVal)) {
                                    table.data.add(row);
                                }

                            } else if (colType.equalsIgnoreCase("int")) {
                                if (Integer.parseInt(addedValue) > Integer.parseInt(conditionVal)) {
                                    table.data.add(row);
                                }
                            } else if (colType.equalsIgnoreCase("LONG INT") || colType.equalsIgnoreCase("long")) {
                                if (Long.parseLong(addedValue) > Long.parseLong(conditionVal)) {
                                    table.data.add(row);
                                }

                            } else if (colType.equalsIgnoreCase("float")) {
                                if (Float.parseFloat(addedValue) > Float.parseFloat(conditionVal)) {
                                    table.data.add(row);
                                }

                            } else if (colType.equalsIgnoreCase("double")) {
                                if (Double.parseDouble(addedValue) > Double.parseDouble(conditionVal)) {
                                    table.data.add(row);
                                }


                            } else if (colType.equalsIgnoreCase("datetime")) {

                                try {
                                    if (dt.parse(addedValue).compareTo(dt.parse(conditionVal)) > 0) {
                                        table.data.add(row);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            } else if (colType.equalsIgnoreCase("date")) {
                                try {
                                    if (d.parse(addedValue).compareTo(d.parse(conditionVal)) > 0) {
                                        table.data.add(row);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }


                            break;
                        case "<":
                            colType = tableStruct.get(conditionCol.toUpperCase()).get(0);

                            if (colType.matches("VARCHAR\\(\\d+\\)") || colType.matches("VARCHAR\\(\\d+\\)")) {
                                if (addedValue.compareToIgnoreCase(conditionVal) < 0) {
                                    table.data.add(row);
                                }

                            } else if (colType.matches("char\\(\\d+\\)") || colType.matches("CHAR\\(\\d+\\)")) {
                                if (addedValue.compareToIgnoreCase(conditionVal) < 0) {
                                    table.data.add(row);
                                }

                            } else if (colType.equalsIgnoreCase("byte")) {
                                if (Byte.parseByte(addedValue) < Byte.parseByte(conditionVal)) {
                                    table.data.add(row);
                                }


                            } else if (colType.equalsIgnoreCase("short int") || colType.equalsIgnoreCase("short")) {
                                if (Short.parseShort(addedValue) < Short.parseShort(conditionVal)) {
                                    table.data.add(row);
                                }

                            } else if (colType.equalsIgnoreCase("int")) {
                                if (Integer.parseInt(addedValue) < Integer.parseInt(conditionVal)) {
                                    table.data.add(row);
                                }
                            } else if (colType.equalsIgnoreCase("LONG INT") || colType.equalsIgnoreCase("long")) {
                                if (Long.parseLong(addedValue) < Long.parseLong(conditionVal)) {
                                    table.data.add(row);
                                }

                            } else if (colType.equalsIgnoreCase("float")) {
                                if (Float.parseFloat(addedValue) < Float.parseFloat(conditionVal)) {
                                    table.data.add(row);
                                }

                            } else if (colType.equalsIgnoreCase("double")) {
                                if (Double.parseDouble(addedValue) < Double.parseDouble(conditionVal)) {
                                    table.data.add(row);
                                }


                            } else if (colType.equalsIgnoreCase("datetime")) {

                                try {
                                    if (dt.parse(addedValue).compareTo(dt.parse(conditionVal)) < 0) {
                                        table.data.add(row);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            } else if (colType.equalsIgnoreCase("date")) {
                                try {
                                    if (d.parse(addedValue).compareTo(d.parse(conditionVal)) < 0) {
                                        table.data.add(row);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case "=":
                            if (addedValue.equalsIgnoreCase(conditionVal))
                                table.data.add(row);
                            break;
                    }
                } else {
                    table.data.add(row);
                }


            }

            table.printTable();

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    private static void insertTable(String database, String tableName, List<String> cols) {
        try {
            RandomAccessFile tables_tableName = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "TABLES", "TABLE_NAME"), "rwd");
            TreeMap<String, List<Integer>> tables_tableNameHash = new TreeMap<>();
            HashingFunctions.getHashfromFile(tables_tableName, tables_tableNameHash);
            if (!tables_tableNameHash.containsKey(tableName.toUpperCase())) {
                System.out.println("Please create a table first using create <table name> ( column1 datatype key, column2 datatype key...)");
                return;
            }

            RandomAccessFile tableFile = new RandomAccessFile(getDatName(database, tableName), "rwd");
            tableFile.seek(tableFile.length());

            int numOfValues = cols.size();

            //get complete table structure of this table
            LinkedHashMap<String, List<String>> columnStructure = getTableStructure(database, tableName.toUpperCase());
            if (columnStructure == null) {
                System.out.println("Unknown Table Name");
                return;
            }
            if (numOfValues > columnStructure.size()) {
                System.out.printf("The table %s contains only %d columns. Please enter values less than equal to %d", tableName, columnStructure.size(), columnStructure.size());
            } else if (numOfValues == columnStructure.size()) {

                Iterator<String> inputIterator = cols.iterator();
                Iterator<String> targetIterator = columnStructure.keySet().iterator();

                while (inputIterator.hasNext() && targetIterator.hasNext()) {
                    String input = inputIterator.next();  //get input data
                    String targetColName = targetIterator.next(); //get output column name
                    List<String> targetColProp = columnStructure.get(targetColName); //get output column properties
                    String targetDataType = targetColProp.get(0); //get target column data type

                    // this method will write the input data to target column depending upon the data type
                    writeColDataToCol(database, tableName, tableFile, input, targetColName, targetDataType);


                }

            } else if (numOfValues < columnStructure.size()) {

                Iterator<String> inputIterator = cols.iterator();
                Iterator<String> targetIterator = columnStructure.keySet().iterator();
                while (inputIterator.hasNext()) {
                    inputIterator.next();
                    targetIterator.next();
                }
                while (targetIterator.hasNext()) {
                    String constraint = columnStructure.get(targetIterator.next()).get(1);
                    if (constraint.equalsIgnoreCase("PRIMARY KEY") || constraint.equalsIgnoreCase("NOT NULL")) {
                        System.out.println("Primary key or Not null value on specified");
                        return;
                    }
                }

                inputIterator = cols.iterator();
                targetIterator = columnStructure.keySet().iterator();

                while (inputIterator.hasNext()) {
                    String input = inputIterator.next();  //get input data
                    String targetColName = targetIterator.next(); //get output column name
                    List<String> targetColProp = columnStructure.get(targetColName); //get output column properties
                    String targetDataType = targetColProp.get(0); //get target column data type
                    writeColDataToCol(database, tableName, tableFile, input, targetColName, targetDataType);
                }

                while (targetIterator.hasNext()) {
                    String targetColName = targetIterator.next(); //get output column name
                    List<String> targetColProp = columnStructure.get(targetColName); //get output column properties
                    String targetDataType = targetColProp.get(0); //get target column data type
                    writeColDataToCol(database, tableName, tableFile, String.valueOf(0x00), targetColName, targetDataType);
                }

            }


            //increment table_rows in tables

            RandomAccessFile tables_file = new RandomAccessFile(getDatName("INFORMATION_SCHEMA", "TABLES"), "rwd");
            tables_file.seek(0);
            int index = 0;

            while (index < tables_file.length()) {
                String dname = readVarChar(tables_file);
                String tname = readVarChar(tables_file);
                index += dname.length() + 1 + tname.length() + 1;
                if (dname.equalsIgnoreCase(database) && tname.equalsIgnoreCase(tableName)) {
                    Long rows = tables_file.readLong();
                    rows++;
                    tables_file.seek(index);
                    tables_file.writeLong(rows);
                    RandomAccessFile numRows = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "TABLES", "TABLE_ROWS"), "rwd");
                    TreeMap<Long, List<Integer>> numRowsHash = new TreeMap<>();
                    HashingFunctions.getLongHashFromFile(numRows, numRowsHash);
                    numRowsHash.get(rows - 1).remove(new Integer(index));
                    if (numRowsHash.containsKey(rows)) {
                        numRowsHash.get(rows).add(index);
                    } else {
                        List<Integer> l1 = new LinkedList<>();
                        l1.add(index);
                        numRowsHash.put(rows, l1);
                    }
                    HashingFunctions.saveLongHashtoFile(numRows, numRowsHash);
                    break;
                }
                tables_file.readLong();
                index += 8;
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void writeColDataToCol(String database, String tableName, RandomAccessFile tableFile, String input, String targetColName, String targetDataType) throws IOException {
        RandomAccessFile currColIndex = new RandomAccessFile(getNdxName(database, tableName, targetColName), "rwd");

        if (targetDataType.matches("VARCHAR\\(\\d+\\)") || targetDataType.matches("VARCHAR\\(\\d+\\)")) {
            int address = (int) tableFile.getFilePointer();
            writeVarchar(tableFile, input);
            TreeMap<String, List<Integer>> temp = new TreeMap<>();
            HashingFunctions.getHashfromFile(currColIndex, temp);
            if (temp.containsKey(input))
                temp.get(input).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(input, l1);
            }
            HashingFunctions.saveHashtoFile(currColIndex, temp);
        } else if (targetDataType.matches("char\\(\\d+\\)") || targetDataType.matches("CHAR\\(\\d+\\)")) {
            int length = Integer.parseInt(targetDataType.substring(5, targetDataType.indexOf(")")));
            int address = (int) tableFile.getFilePointer();
            writeChar(tableFile, input, length);
            TreeMap<String, List<Integer>> temp = new TreeMap<>();

            HashingFunctions.readCharHashFromFile(currColIndex, temp, length);
            if (temp.containsKey(input))
                temp.get(input).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(input, l1);
            }
            HashingFunctions.saveCharHashtoFile(currColIndex, temp, length);

        } else if (targetDataType.equalsIgnoreCase("byte")) {
            int address = (int) tableFile.getFilePointer();
            tableFile.writeByte(Byte.parseByte(input));
            TreeMap<Byte, List<Integer>> temp = new TreeMap<>();
            HashingFunctions.getByteHashFromFile(currColIndex, temp);
            if (temp.containsKey(Byte.parseByte(input)))
                temp.get(Byte.parseByte(input)).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(Byte.parseByte(input), l1);
            }
            HashingFunctions.saveByteHashtoFile(currColIndex, temp);


        } else if (targetDataType.equalsIgnoreCase("short int") || targetDataType.equalsIgnoreCase("short")) {
            int address = (int) tableFile.getFilePointer();
            tableFile.writeShort(Short.parseShort(input));

            TreeMap<Short, List<Integer>> temp = new TreeMap<>();

            HashingFunctions.getShortHashFromFile(currColIndex, temp);

            if (temp.containsKey(Short.parseShort(input)))
                temp.get(Short.parseShort(input)).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(Short.parseShort(input), l1);
            }

            HashingFunctions.saveShortHashtoFile(currColIndex, temp);

        } else if (targetDataType.equalsIgnoreCase("int")) {
            int address = (int) tableFile.getFilePointer();
            tableFile.writeInt(Integer.parseInt(input));
            TreeMap<Integer, List<Integer>> temp = new TreeMap<>();

            HashingFunctions.getIntHashFromFile(currColIndex, temp);
            if (temp.containsKey(Integer.parseInt(input)))
                temp.get(Integer.parseInt(input)).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(Integer.parseInt(input), l1);
            }
            HashingFunctions.saveIntHashtoFile(currColIndex, temp);
        } else if (targetDataType.equalsIgnoreCase("LONG INT") || targetDataType.equalsIgnoreCase("long")) {
            int address = (int) tableFile.getFilePointer();
            tableFile.writeLong(Long.parseLong(input));
            TreeMap<Long, List<Integer>> temp = new TreeMap<>();
            HashingFunctions.getLongHashFromFile(currColIndex, temp);
            if (temp.containsKey(Long.parseLong(input)))
                temp.get(Long.parseLong(input)).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(Long.parseLong(input), l1);
            }
            HashingFunctions.saveLongHashtoFile(currColIndex, temp);

        } else if (targetDataType.equalsIgnoreCase("float")) {
            int address = (int) tableFile.getFilePointer();
            tableFile.writeFloat(Float.parseFloat(input));

            TreeMap<Float, List<Integer>> temp = new TreeMap<>();

            HashingFunctions.getFloatHashfromFile(currColIndex, temp);

            if (temp.containsKey(Float.parseFloat(input)))
                temp.get(Float.parseFloat(input)).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(Float.parseFloat(input), l1);
            }
            HashingFunctions.saveFloatHashFromFile(currColIndex, temp);

        } else if (targetDataType.equalsIgnoreCase("double")) {
            int address = (int) tableFile.getFilePointer();
            tableFile.writeDouble(Double.parseDouble(input));

            TreeMap<Double, List<Integer>> temp = new TreeMap<>();
            HashingFunctions.getDoubleHashfromFile(currColIndex, temp);
            if (temp.containsKey(Double.parseDouble(input)))
                temp.get(Double.parseDouble(input)).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(Double.parseDouble(input), l1);
            }
            HashingFunctions.saveDoubleHashtoFile(currColIndex, temp);


        } else if (targetDataType.equalsIgnoreCase("datetime")) {
            int address = (int) tableFile.getFilePointer();
            Date d = null;
            Long key;
            if (input.equalsIgnoreCase("0")) {
                key = (long) 0;
                tableFile.writeLong(0);
            } else {
                try {
                    d = dt.parse(input);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (d == null) {
                    System.out.println("Incorrect Datetime format. use: yyyy-MM-dd_HH:mm:ss");
                    return;
                }

                key = d.getTime();
                tableFile.writeLong(d.getTime());
            }
            TreeMap<Long, List<Integer>> temp = new TreeMap<>();
            HashingFunctions.getLongHashFromFile(currColIndex, temp);
            if (temp.containsKey(key))
                temp.get(key).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(key, l1);
            }
            HashingFunctions.saveLongHashtoFile(currColIndex, temp);


        } else if (targetDataType.equalsIgnoreCase("date")) {
            int address = (int) tableFile.getFilePointer();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date d = null;
            Long key;
            if (input.equalsIgnoreCase("0")) {
                key = (long) 0;
                tableFile.writeLong(0);
            } else {
                try {
                    d = dateFormat.parse(input);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (d == null) {
                    System.out.println("Incorrect Date format. use: yyyy-MM-dd");
                    return;
                }
                key = d.getTime();
                tableFile.writeLong(d.getTime());
            }
            TreeMap<Long, List<Integer>> temp = new TreeMap<>();
            HashingFunctions.getLongHashFromFile(currColIndex, temp);
            if (temp.containsKey(key))
                temp.get(key).add(address);
            else {
                List<Integer> l1 = new LinkedList<>();
                l1.add(address);
                temp.put(key, l1);
            }
            HashingFunctions.saveLongHashtoFile(currColIndex, temp);
        }
    }


    private static LinkedHashMap<String, List<String>> getTableStructure(String database, String tableName) {
        try {
            RandomAccessFile columnsFile = new RandomAccessFile(getDatName("INFORMATION_SCHEMA", "COLUMNS"), "rwd");
            RandomAccessFile columns_table_schemaFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "COLUMNS", "TABLE_SCHEMA"), "rwd");
            TreeMap<String, List<Integer>> schemaNameHash = new TreeMap<>();
            HashingFunctions.getHashfromFile(columns_table_schemaFile, schemaNameHash);

            int startingAddress = schemaNameHash.get(database).get(0);

            columnsFile.seek(startingAddress);
            boolean dataFound = false;

            while (!dataFound) {
                String databaseName = readVarChar(columnsFile);
                String tablename = readVarChar(columnsFile);
                dataFound = tablename.equals(tableName);
                if (!dataFound) {
                    startingAddress += databaseName.length() + 1 + tablename.length() + 1;
                    startingAddress += readVarChar(columnsFile).length() + 1;
                    readInt(columnsFile);
                    startingAddress += 4;
                    startingAddress += readVarChar(columnsFile).length() + 1;
                    startingAddress += readVarChar(columnsFile).length() + 1;
                    startingAddress += readVarChar(columnsFile).length() + 1;
                }
            }
            columnsFile.seek(startingAddress);
            //iterate and check if table name is same if different break
            LinkedHashMap<String, List<String>> columnStructure = new LinkedHashMap<>();
            while (startingAddress < columnsFile.length()) {
                startingAddress += readVarChar(columnsFile).length() + 1; //read database name
                String tName = readVarChar(columnsFile); //read table name
                if (!tName.equals(tableName)) break; //if table name different then break loop, all columns read
                startingAddress += tName.length() + 1;

                String colName = readVarChar(columnsFile);  //read column name, this is key of LinkedHashMap

                startingAddress += colName.length() + 1;


                readInt(columnsFile); //ignoring ordinal position, it will always be in the correct order
                startingAddress += 4; //incrementing pointer by 4
                String datatype = readVarChar(columnsFile); //reading isNullable
                startingAddress += datatype.length() + 1;

                String isNullable = readVarChar(columnsFile); //reading dataType
                startingAddress += isNullable.length() + 1;

                String ColumnKey = readVarChar(columnsFile); //reading columnKey
                startingAddress += ColumnKey.length() + 1;

                String constraint = "";

                if (ColumnKey.equals("PRI"))             //if key is PRI then set constraint as primary key
                    constraint = "PRIMARY KEY";
                else if (isNullable.equals("NO"))      //if key is not there and constraint is not null then it is not null
                    constraint = "NOT NULL";

                LinkedList<String> l1 = new LinkedList<>();
                l1.add(datatype);
                l1.add(constraint);
                columnStructure.put(colName, l1);

            }
            return columnStructure;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private static void createTable(String tableName, List<List<String>> colums) {
        if (currDatabase == null) {
            System.out.println("Please choose a schema first");
            return;
        }
        try {

            RandomAccessFile newTableFile = new RandomAccessFile(getDatName(currDatabase, tableName), "rwd");
            List<String> l1 = new LinkedList<>();
            l1.add(currDatabase);
            l1.add(tableName);
            l1.add("0");
            insertTable("INFORMATION_SCHEMA", "TABLES", l1);
            int index = 1;
            for (List<String> list : colums) {
                RandomAccessFile temp = new RandomAccessFile(getNdxName(currDatabase, tableName, list.get(0)), "rwd");
                List<String> colData = new LinkedList<>();
                colData.add(currDatabase);
                colData.add(tableName);
                colData.add(list.get(0));
                colData.add(String.valueOf(index++));
                colData.add(list.get(1));
                String isNullable = "YES";
                String columnKey = "";
                if (list.size() > 2) {
                    if (list.get(2).equals("NOT") && list.get(3).equals("NULL")) {
                        isNullable = "NO";
                    } else if (list.get(2).equals("PRIMARY") && list.get(3).equals("KEY")) {
                        columnKey = "PRI";
                        isNullable = "NO";
                    }
                }
                colData.add(isNullable);
                colData.add(columnKey);
                insertTable("INFORMATION_SCHEMA", "COLUMNS", colData);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Display the welcome splash screen
     */
    private static void splashScreen() {
        System.out.println(line("*", 80));
        System.out.print("Welcome to DavisBase "); // Display the string.
        version();
        System.out.println("Type \"HELP;\" to display supported commands.");
        System.out.println(line("*", 80));
    }

    private static void version() {
        System.out.println(version);
    }


    static private void writeVarchar(RandomAccessFile raf, String data) throws IOException {
        raf.writeByte(data.length());
        raf.writeBytes(data);
    }

    static private void writeChar(RandomAccessFile raf, String data, int n) throws IOException {
        raf.writeBytes(data);
        for (int i = 0; i < n - data.length(); i++) {
            raf.writeByte(0x00);
        }

    }

    static private Long readLong(RandomAccessFile raf) throws IOException {
        return raf.readLong();
    }

    static private Integer readInt(RandomAccessFile raf) throws IOException {
        return raf.readInt();
    }

    static private String readChar(RandomAccessFile raf, int n) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            byte b = raf.readByte();
            if (b == 0x00) break;
            sb.append((char) b);
        }
        return sb.toString();
    }

    static private String readVarChar(RandomAccessFile raf) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte length = raf.readByte();
        for (int i = 0; i < length; i++) {
            sb.append((char) raf.readByte());
        }
        return sb.toString();
    }


    private static File getNdxName(String databaseName, String table, String columnName) {
        File file = new File("IndexFiles/" + databaseName + "/" + table + "/" + databaseName + "." + table + "." + columnName + ".ndx");
        new File(String.format("IndexFiles/%s/%s", databaseName, table)).mkdirs();

        return file;
    }

    private static File getDatName(String databaseName, String table) {

        File file = new File("TableFiles/" + databaseName + "/" + databaseName + "." + table + ".dat");
        new File(String.format("TableFiles/%s", databaseName)).mkdirs();

        return file;

    }

    /**
     * @param s   The String to be repeated
     * @param num The number of time to repeat String s.
     * @return String A String object, which is the String s appended to itself num times.
     */
    private static String line(String s, int num) {
        StringBuilder a = new StringBuilder();
        for (int i = 0; i < num; i++) {
            a.append(s);
        }
        return a.toString();
    }


    private static String getWord(String temp, int n) {

        Pattern pattern = Pattern.compile("\\s([A-Za-z]+)");
        Matcher matcher = pattern.matcher(temp);
        for (int i = 0; i < n - 1; i++) {
            matcher.find();
        }
        return matcher.group(1);
    }
}



