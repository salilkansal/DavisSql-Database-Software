import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DaviBase {

    private static String prompt = "davisql> ";
    private static String version = "Version 0.1";
    private static String currDatabase = null;

    public static void main(String[] args) throws IOException {

        String userCommand;
        if (firstRun())
            createDefaultTables();


        splashScreen();

        Scanner scanner = new Scanner(System.in).useDelimiter(";");
        do {  // do-while !exit
            System.out.print(prompt);
            userCommand = scanner.next().trim();

            parse(userCommand);


        } while (!userCommand.equals("exit"));
        System.out.println("Exiting...");

    }

    private static String getWord(String temp, int n) {

        Pattern pattern = Pattern.compile("\\s([A-Za-z]+)");
        Matcher matcher = pattern.matcher(temp);
        for (int i = 0; i < n - 1; i++) {
            matcher.find();
        }
        return matcher.group(1);
    }


    private static void parse(String userCommand) {
        Scanner s1 = new Scanner(userCommand.toUpperCase());
        switch (s1.next()) {
            case "EXIT":
                break;
            case "SHOW":
                System.out.println("Show schema or show tables");
                switch (userCommand.toUpperCase()) {
                    case "SHOW SCHEMAS":
                        //select from schemata
                        break;
                    case "SHOW TABLES":
                        //select from tables where informationSchema = currDB
                }
                break;
            case "USE":
                String database = s1.next().toUpperCase();
                try {
                    RandomAccessFile schemata_schemaNameFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "SCHEMATA", "SCHEMA_NAME"), "rwd");
                    HashMap<String, List<Integer>> schemata_schemaNameHash = new HashMap<>();
                    getHashfromFile(schemata_schemaNameFile, schemata_schemaNameHash);
//                    if (!schemata_schemaNameHash.containsKey(database)) {
//                        System.out.println("Please create a schema first using create <schema name>");
//                    } else {
//                        currDatabase = database;
//                        System.out.printf("Database switched to %s\n", currDatabase);
//                    }
                    currDatabase = database;
                    System.out.printf("Database switched to %s\n", currDatabase);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                break;
            case "CREATE":
                System.out.println("create a schema or table");
                //call create string parser
                parseCreateString(userCommand);
                break;
            case "INSERT":
                System.out.println("insert into table");
                //call insert string parser
                break;
            case "DELETE":
                System.out.println("delete from table");
                //call delete string parser
                break;
            case "DROP":
                System.out.println("drop table");
                //delete the dat file
                //delete all indexes
                //delete all reference from information_schema database
                break;
            case "SELECT":
                System.out.println("select table");
                //call select command
                break;
            default:
                System.out.println("Unknown command");
                break;
        }
    }

    private static void parseCreateString(String userCommand) {
        switch (getWord(userCommand, 2).toUpperCase()) {
            case "TABLE":
                parseCreateTableString(userCommand);
                break;
            case "SCHEMA":
                parseCreateSchemaString(userCommand);
                break;
            default:
                System.out.println("Unknown Command");
                break;
        }


    }

    private static void parseCreateSchemaString(String userCommand) {
        //TODO
    }


    private static void parseCreateTableString(String userCommand) {
//        CREATE TABLE table_name (
//                column_name1 data_type(size) [primary key|not null],
//        column_name2 data_type(size) [primary key|not null],
//        column_name3 data_type(size) [primary key|not null],
//        ...
//        );

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

    private static void insertTable(String database, String tableName, List<String> cols) {
        try {
            RandomAccessFile tables_tableName = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "TABLES", "TABLE_NAME"), "rwd");
            HashMap<String, List<Integer>> tables_tableNameHash = new HashMap<>();
            getHashfromFile(tables_tableName, tables_tableNameHash);
            if (!tables_tableNameHash.containsKey(tableName)) {
                System.out.println("Please create a table first using create <table name> ( column1 datatype key, column2 datatype key...)");
                return;
            }

            RandomAccessFile tableFile = new RandomAccessFile(getDatName(database, tableName), "rwd");
            tableFile.seek(tableFile.length());

            int numOfValues = cols.size();

            //get complete table structure of this table
            LinkedHashMap<String, List<String>> columnStructure = getTableStructure(database, tableName);

            if (numOfValues > columnStructure.size()) {
                System.out.printf("The table %s contains only %d columns. Please enter values less than equal to %d", tableName, columnStructure.size(), columnStructure.size());
            } else if (numOfValues == columnStructure.size()) {
                //iterate through the cols
                //check if each input matches the columnStructure data type

                Iterator<String> inputIterator = cols.iterator();
                Iterator<String> targetIterator = columnStructure.keySet().iterator();
                boolean correctType = true;
                while (inputIterator.hasNext() && targetIterator.hasNext()){
                    String input = inputIterator.next();  //get input data
                    String targetColName = targetIterator.next(); //get output column name
                    List<String> targetColProp = columnStructure.get(targetColName); //get output column properties
                    String targetDataType = targetColProp.get(0); //get target column data type



                }

            }





            //check if it has not null and primary key data
            //add to main table

            //iterate through cols
            //keep adding to index


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static LinkedHashMap<String, List<String>> getTableStructure(String database, String tableName) throws IOException {
        RandomAccessFile columnsFile = new RandomAccessFile(getDatName("INFORMATION_SCHEMA", "COLUMNS"), "rwd");
        RandomAccessFile columns_table_schemaFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "COLUMNS", "TABLE_SCHEMA"), "rwd");
        HashMap<String, List<Integer>> schemaNameHash = new HashMap<>();
        getHashfromFile(columns_table_schemaFile, schemaNameHash);

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
            else if (isNullable.equals("YES"))      //if key is not there and constraint is not null then it is not null
                constraint = "NOT NULL";

            LinkedList<String> l1 = new LinkedList<>();
            l1.add(datatype);
            l1.add(constraint);
            columnStructure.put(colName, l1);

        }
        return columnStructure;
    }


    private static void createTable(String tableName, List<List<String>> colums) {
        if (currDatabase == null) {
            System.out.println("Please choose a schema first");
            return;
        }
        try {


            RandomAccessFile tablesFile = new RandomAccessFile(getDatName("INFORMATION_SCHEMA", "TABLES"), "rwd");
            RandomAccessFile columnsFile = new RandomAccessFile(getDatName("INFORMATION_SCHEMA", "COLUMNS"), "rwd");
            RandomAccessFile tables_table_schemaFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "TABLES", "TABLE_SCHEMA"), "rwd");
            RandomAccessFile tables_table_nameFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "TABLES", "TABLE_NAME"), "rwd");
            RandomAccessFile tables_table_rowFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "TABLES", "TABLE_ROW"), "rwd");

            RandomAccessFile columns_table_schemaFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "COLUMNS", "TABLE_SCHEMA"), "rwd");
            RandomAccessFile columns_table_nameFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "COLUMNS", "TABLE_NAME"), "rwd");
            RandomAccessFile columns_columnNameFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "COLUMNS", "COLUMN_NAME"), "rwd");
            RandomAccessFile columns_column_ordinalPosFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "COLUMNS", "ORDINAL_POSITION"), "rwd");
            RandomAccessFile columns_columnTypeFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "COLUMNS", "COLUMN_TYPE"), "rwd");
            RandomAccessFile columns_is_nullableFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "COLUMNS", "IS_NULLABLE"), "rwd");
            RandomAccessFile columns_column_keyFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "COLUMNS", "COLUMN_KEY"), "rwd");


            HashMap<String, List<Integer>> tables_table_schemaHash = new HashMap<>();
            HashMap<String, List<Integer>> tables_table_nameHash = new HashMap<>();
            HashMap<Long, List<Integer>> tables_table_rowHash = new HashMap<>();

            HashMap<String, List<Integer>> columns_table_schemaHash = new HashMap<>();
            HashMap<String, List<Integer>> columns_table_nameHash = new HashMap<>();
            HashMap<String, List<Integer>> columns_columnNameHash = new HashMap<>();
            HashMap<Integer, List<Integer>> columns_column_ordinalPosHash = new HashMap<>();
            HashMap<String, List<Integer>> columns_columnTypeHash = new HashMap<>();
            HashMap<String, List<Integer>> columns_is_nullableHash = new HashMap<>();
            HashMap<String, List<Integer>> columns_column_keyHash = new HashMap<>();

            getHashfromFile(tables_table_schemaFile, tables_table_schemaHash);
            getHashfromFile(tables_table_nameFile, tables_table_nameHash);
            getLongHashFromFile(tables_table_rowFile, tables_table_rowHash);

            getHashfromFile(columns_table_schemaFile, columns_table_schemaHash);
            getHashfromFile(columns_table_nameFile, columns_table_nameHash);
            getHashfromFile(columns_columnNameFile, columns_columnNameHash);
            getIntHashFromFile(columns_column_ordinalPosFile, columns_column_ordinalPosHash);
            getHashfromFile(columns_columnTypeFile, columns_columnTypeHash);
            getHashfromFile(columns_is_nullableFile, columns_is_nullableHash);
            getHashfromFile(columns_column_keyFile, columns_column_keyHash);


            RandomAccessFile newTableFile = new RandomAccessFile(getDatName(currDatabase, tableName), "rwd");
            List<String> l1 = new LinkedList<>();
            l1.add(currDatabase);
            l1.add(tableName);
            l1.add("0");
            insertTable("INFORMATION_SCHEMA", "TABLES", l1);
            //insert row into tables   (currDatabase, tableName, 0)
            int index = 1;
            for (List<String> list : colums) {
                RandomAccessFile temp = new RandomAccessFile(getNdxName(currDatabase, tableName, list.get(0)), "rwd");
                //schema_name = currDatabase
                //table_name = tableName
                //columnName = list.get(0)
                //ordinal position = index++
                //columnType = list.get(1)
                //isnullable = yes
//                if(list.size()>2){
//                    if(list.get(2).equals("NOT") && list.get(3).equals("NULL")){
//                        //isnullable = NO
//                    }
//                    else if (list.get(2).equals("PRIMARY") && list.get(3).equals("KEY")){
//                        //columnKey = PRI
//                    }
//                }

                //insert into column table

            }


            //read all 2 indexes into hashkey
            //create table dat file
            //create table column file

            //insert into columns table
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void getHashfromFile(RandomAccessFile tables_table_schemaFile, HashMap<String, List<Integer>> tables_table_schemaHash) throws IOException {
        int index = 0;
        while (index < tables_table_schemaFile.length()) {
            String key = readVarChar(tables_table_schemaFile);
            index = index + key.length() + 1;
            int n = readInt(tables_table_schemaFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(tables_table_schemaFile));
                index += 4;
            }
            tables_table_schemaHash.put(key, l1);
        }
    }

    private static void getLongHashFromFile(RandomAccessFile tables_table_schemaFile, HashMap<Long, List<Integer>> tables_table_schemaHash) throws IOException {
        int index = 0;
        while (index < tables_table_schemaFile.length()) {
            Long key = readLong(tables_table_schemaFile);
            index += 8;
            int n = readInt(tables_table_schemaFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(tables_table_schemaFile));
                index += 4;
            }
            tables_table_schemaHash.put(key, l1);
        }
    }

    private static void getIntHashFromFile(RandomAccessFile tables_table_schemaFile, HashMap<Integer, List<Integer>> tables_table_schemaHash) throws IOException {
        int index = 0;
        while (index < tables_table_schemaFile.length()) {
            Integer key = readInt(tables_table_schemaFile);
            index += 4;
            int n = readInt(tables_table_schemaFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(tables_table_schemaFile));
                index += 4;
            }
            tables_table_schemaHash.put(key, l1);
        }
    }


    private static boolean firstRun() {
        File schemata = new File("TableFiles/INFORMATION_SCHEMA/INFORMATION_SCHEMA.SCHEMATA.dat");
        if (!schemata.exists()) return true;
        File tables = new File("TableFiles/INFORMATION_SCHEMA/INFORMATION_SCHEMA.TABLES.dat");
        if (!tables.exists()) return true;
        File columns = new File("TableFiles/INFORMATION_SCHEMA/INFORMATION_SCHEMA.COLUMNS.dat");
        if (!columns.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/SCHEMATA/INFORMATION_SCHEMA.SCHEMATA.SCHEMA_NAME.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/TABLES/INFORMATION_SCHEMA.TABLES.TABLE_NAME.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/TABLES/INFORMATION_SCHEMA.TABLES.TABLE_ROW.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/TABLES/INFORMATION_SCHEMA.TABLES.TABLE_SCHEMA.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/COLUMNS/INFORMATION_SCHEMA.COLUMNS.COLUMN_KEY.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/COLUMNS/INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/COLUMNS/INFORMATION_SCHEMA.COLUMNS.COLUMN_TYPE.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/COLUMNS/INFORMATION_SCHEMA.COLUMNS.IS_NULLABLE.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/COLUMNS/INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/COLUMNS/INFORMATION_SCHEMA.COLUMNS.TABLE_NAME.ndx");
        if (!schemata.exists()) return true;
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/COLUMNS/INFORMATION_SCHEMA.COLUMNS.TABLE_SCHEMA.ndx");
        if (!schemata.exists()) return true;

        return false;

    }


    /**
     * Display the welcome splash screen
     */
    private static void splashScreen() {
        System.out.println(line("*", 80));
        System.out.print("Welcome to DavisBase "); // Display the string.
        version();
        System.out.println("Type \"help;\" to display supported commands.");
        System.out.println(line("*", 80));
    }

    private static void version() {
        System.out.println(version);
    }


    static private void writeInt(RandomAccessFile raf, int data) throws IOException {
        raf.writeInt(data);
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

    static private String readVarChar(RandomAccessFile raf) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte length = raf.readByte();
        for (int i = 0; i < length; i++) {
            sb.append((char) raf.readByte());
        }
        return sb.toString();
    }

    static private String readDateTime(RandomAccessFile raf) throws IOException {
        Long temp = raf.readLong();
        Date date = new Date(temp);
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD_hh:mm:ss");

        return dateFormat.format(date);

    }


    private static void saveHashtoFile(RandomAccessFile columnNameFile, HashMap<String, List<Integer>> columnNameHash) throws IOException {
        for (String key : columnNameHash.keySet()) {
            columnNameFile.writeByte(key.length());
            columnNameFile.writeBytes(key);
            columnNameFile.writeInt(columnNameHash.get(key).size());
            for (int i : columnNameHash.get(key)) {
                columnNameFile.writeInt(i);
            }
        }
    }

    private static void saveIntHashtoFile(RandomAccessFile columnNameFile, HashMap<Integer, List<Integer>> columnNameHash) throws IOException {
        for (Integer key : columnNameHash.keySet()) {
            columnNameFile.writeInt(key);
            columnNameFile.writeInt(columnNameHash.get(key).size());
            for (int i : columnNameHash.get(key)) {
                columnNameFile.writeInt(i);
            }
        }
    }


    private static void createDefaultTables() {
        try {
            /* FIXME: Put all binary data files in a separate subdirectory (subdirectory tree?) */
            /* FIXME: Should there not be separate Class static variables for the file names?
             *        and just hard code them here?
			 */
            /* TODO: Should there be separate methods to checkfor and subsequently create each file
             *       granularly, instead of a big bang all or nothing?
			 */
            createSchemataTable();
            createTablesTable();
            createColumnsTable();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void createColumnsTable() throws IOException {
        String database = "INFORMATION_SCHEMA";
        String table = "COLUMNS";
        RandomAccessFile columnsTableFile = new RandomAccessFile(getDatName(database, table), "rwd");

        RandomAccessFile tableSchemaFile = new RandomAccessFile(getNdxName(database, table, "TABLE_SCHEMA"), "rwd");
        RandomAccessFile tableNameFile = new RandomAccessFile(getNdxName(database, table, "TABLE_NAME"), "rwd");
        RandomAccessFile ColumnNameFile = new RandomAccessFile(getNdxName(database, table, "COLUMN_NAME"), "rwd");
        RandomAccessFile ColumnTypeFile = new RandomAccessFile(getNdxName(database, table, "COLUMN_TYPE"), "rwd");
        RandomAccessFile isNullableFile = new RandomAccessFile(getNdxName(database, table, "IS_NULLABLE"), "rwd");
        RandomAccessFile ColumnKeyFile = new RandomAccessFile(getNdxName(database, table, "COLUMN_KEY"), "rwd");
        RandomAccessFile ordinalPosFile = new RandomAccessFile(getNdxName(database, table, "ORDINAL_POSITION"), "rwd");

        List<Integer> schemaList = new LinkedList<>();
        HashMap<String, List<Integer>> tableNameHash = new HashMap<>();
        HashMap<String, List<Integer>> columnNameHash = new HashMap<>();
        HashMap<Integer, List<Integer>> ordinalPosHash = new HashMap<>();
        HashMap<String, List<Integer>> columnTypeHash = new HashMap<>();
        List<Integer> isNullableList = new LinkedList<>();
        List<Integer> columnKeyList = new LinkedList<>();
        int address = 0;

            /*
             *  Create the COLUMNS table file.
			 *  Initially it has 11 rows:
			 */
        // ROW 1: information_schema.columns.dat

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("SCHEMATA".length()); // TABLE_NAME
        columnsTableFile.writeBytes("SCHEMATA");
        List<Integer> l1 = new LinkedList<>();
        l1.add(address);
        tableNameHash.put("SCHEMATA", l1);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("SCHEMA_NAME".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("SCHEMA_NAME");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("SCHEMA_NAME", l1);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(1); // ORDINAL_POSITION
        l1 = new LinkedList<>();
        l1.add(address);
        ordinalPosHash.put(1, l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("varchar(64)");
        l1 = new LinkedList<>();
        l1.add(address);
        columnTypeHash.put("varchar(64)", l1);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 2: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("TABLES".length()); // TABLE_NAME
        columnsTableFile.writeBytes("TABLES");
        l1 = new LinkedList<>();
        l1.add(address);
        tableNameHash.put("TABLES", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("TABLE_SCHEMA".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("TABLE_SCHEMA");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("TABLE_SCHEMA", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(1); // ORDINAL_POSITION
        ordinalPosHash.get(1).add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("varchar(64)");
        columnTypeHash.get("varchar(64)").add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 3: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);
        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("TABLES".length()); // TABLE_NAME
        columnsTableFile.writeBytes("TABLES");
        tableNameHash.get("TABLES").add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("TABLE_NAME".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("TABLE_NAME");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("TABLE_NAME", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(2); // ORDINAL_POSITION
        l1 = new LinkedList<>();
        l1.add(address);
        ordinalPosHash.put(2, l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("varchar(64)");
        columnTypeHash.get("varchar(64)").add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 4: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("TABLES".length()); // TABLE_NAME
        columnsTableFile.writeBytes("TABLES");
        tableNameHash.get("TABLES").add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("TABLE_ROWS".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("TABLE_ROWS");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("TABLE_ROWS", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(3); // ORDINAL_POSITION
        l1 = new LinkedList<>();
        l1.add(address);
        ordinalPosHash.put(3, l1);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("long int".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("long int");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("long int", l1);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 5: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
        columnsTableFile.writeBytes("COLUMNS");
        l1 = new LinkedList<>();
        l1.add(address);
        tableNameHash.put("COLUMNS", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("TABLE_SCHEMA".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("TABLE_SCHEMA");
        columnNameHash.get("TABLE_SCHEMA").add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(1); // ORDINAL_POSITION
        ordinalPosHash.get(1).add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("varchar(64)");
        columnTypeHash.get("varchar(64)").add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 6: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
        columnsTableFile.writeBytes("COLUMNS");
        tableNameHash.get("COLUMNS").add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("TABLE_NAME".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("TABLE_NAME");
        columnNameHash.get("TABLE_NAME").add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(2); // ORDINAL_POSITION
        ordinalPosHash.get(2).add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("varchar(64)");
        columnTypeHash.get("varchar(64)").add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 7: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
        columnsTableFile.writeBytes("COLUMNS");
        tableNameHash.get("COLUMNS").add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("COLUMN_NAME".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("COLUMN_NAME");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("COLUMN_NAME", l1);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(3); // ORDINAL_POSITION
        ordinalPosHash.get(3).add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("varchar(64)");
        columnTypeHash.get("varchar(64)").add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 8: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
        columnsTableFile.writeBytes("COLUMNS");
        tableNameHash.get("COLUMNS").add(address);

        columnsTableFile.writeByte("ORDINAL_POSITION".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("ORDINAL_POSITION");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("ORDINAL_POSITION", l1);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(4); // ORDINAL_POSITION
        l1 = new LinkedList<>();
        l1.add(address);
        ordinalPosHash.put(4, l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("int".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("int");
        l1 = new LinkedList<>();
        l1.add(address);
        columnTypeHash.put("int", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 9: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
        columnsTableFile.writeBytes("COLUMNS");
        tableNameHash.get("COLUMNS").add(address);

        columnsTableFile.writeByte("COLUMN_TYPE".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("COLUMN_TYPE");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("COLUMN_TYPE", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(5); // ORDINAL_POSITION
        l1 = new LinkedList<>();
        l1.add(address);
        ordinalPosHash.put(5, l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("varchar(64)");
        columnTypeHash.get("varchar(64)").add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 10: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
        columnsTableFile.writeBytes("COLUMNS");
        tableNameHash.get("COLUMNS").add(address);

        columnsTableFile.writeByte("IS_NULLABLE".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("IS_NULLABLE");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("IS_NULLABLE", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(6); // ORDINAL_POSITION
        l1 = new LinkedList<>();
        l1.add(address);
        ordinalPosHash.put(6, l1);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("varchar(3)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("varchar(3)");
        l1 = new LinkedList<>();
        l1.add(address);
        columnTypeHash.put("varchar(3)", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);

        // ROW 11: information_schema.columns.tbl
        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte(database.length()); // TABLE_SCHEMA
        columnsTableFile.writeBytes(database);

        schemaList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
        columnsTableFile.writeBytes("COLUMNS");
        tableNameHash.get("COLUMNS").add(address);

        columnsTableFile.writeByte("COLUMN_KEY".length()); // COLUMN_NAME
        columnsTableFile.writeBytes("COLUMN_KEY");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("COLUMN_KEY", l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeInt(7); // ORDINAL_POSITION
        l1 = new LinkedList<>();
        l1.add(address);
        ordinalPosHash.put(7, l1);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("varchar(3)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("varchar(3)");
        columnTypeHash.get("varchar(3)").add(address);


        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
        columnsTableFile.writeBytes("NO");
        isNullableList.add(address);

        address = (int) columnsTableFile.getFilePointer();
        columnsTableFile.writeByte("".length()); // COLUMN_KEY
        columnsTableFile.writeBytes("");
        columnKeyList.add(address);


        tableSchemaFile.writeByte(database.length());
        tableSchemaFile.writeBytes(database);
        tableSchemaFile.writeInt(schemaList.size());
        for (int i : schemaList) {
            tableSchemaFile.writeInt(i);
        }

        saveHashtoFile(tableNameFile, tableNameHash);
        saveHashtoFile(ColumnNameFile, columnNameHash);
        saveHashtoFile(ColumnTypeFile, columnTypeHash);

        saveIntHashtoFile(ordinalPosFile, ordinalPosHash);

        isNullableFile.writeByte("NO".length());
        isNullableFile.writeBytes("NO");
        isNullableFile.writeInt(isNullableList.size());
        for (int i : isNullableList) {
            isNullableFile.writeInt(i);
        }


        ColumnKeyFile.writeByte("".length());
        ColumnKeyFile.writeBytes("");
        ColumnKeyFile.writeInt(columnKeyList.size());
        for (int i : columnKeyList) {
            ColumnKeyFile.writeInt(i);
        }


    }

    private static void createTablesTable() throws IOException {

        String database = "INFORMATION_SCHEMA";
        String tablename = "TABLES";
        RandomAccessFile tablesTableFile = new RandomAccessFile(getDatName(database, tablename), "rwd");

        RandomAccessFile table_schemaFile = new RandomAccessFile(getNdxName(database, tablename, "TABLE_SCHEMA"), "rwd");
        RandomAccessFile table_nameFile = new RandomAccessFile(getNdxName(database, tablename, "TABLE_NAME"), "rwd");
        RandomAccessFile table_rowFile = new RandomAccessFile(getNdxName(database, tablename, "TABLE_ROW"), "rwd");


        int address = 0;
            /*
             *  Create the TABLES table file.
			 *  Remember!!! Column names are not stored in the tables themselves
			 *              The column names (TABLE_SCHEMA, TABLE_NAME, TABLE_ROWS)
			 *              and their order (ORDINAL_POSITION) are encoded in the
			 *              COLUMNS table.
			 *  Initially it has three rows (each row may have a different length):
			 */
        // ROW 1: information_schema.tables.dat
        List<Integer> l1 = new LinkedList<>();

        address = (int) tablesTableFile.getFilePointer();
        tablesTableFile.writeByte("INFORMATION_SCHEMA".length()); // TABLE_SCHEMA
        tablesTableFile.writeBytes("INFORMATION_SCHEMA");
        l1.add(address);

        address = (int) tablesTableFile.getFilePointer();
        tablesTableFile.writeByte("SCHEMATA".length()); // TABLE_NAME
        tablesTableFile.writeBytes("SCHEMATA");

        table_nameFile.writeByte("SCHEMATA".length());
        table_nameFile.writeBytes("SCHEMATA");
        table_nameFile.writeInt(1);
        table_nameFile.writeInt(address);

        address = (int) tablesTableFile.getFilePointer();
        tablesTableFile.writeLong(1); // TABLE_ROWS

        table_rowFile.writeLong(1);
        table_rowFile.writeInt(1);
        table_rowFile.writeInt(address);

        // ROW 2: information_schema.tables.dat
        address = (int) tablesTableFile.getFilePointer();
        tablesTableFile.writeByte("INFORMATION_SCHEMA".length()); // TABLE_SCHEMA
        tablesTableFile.writeBytes("INFORMATION_SCHEMA");
        l1.add(address);

        address = (int) tablesTableFile.getFilePointer();
        tablesTableFile.writeByte("TABLES".length()); // TABLE_NAME
        tablesTableFile.writeBytes("TABLES");

        table_nameFile.writeByte("TABLES".length());
        table_nameFile.writeBytes("TABLES");
        table_nameFile.writeInt(1);
        table_nameFile.writeInt(address);

        address = (int) tablesTableFile.getFilePointer();
        tablesTableFile.writeLong(3); // TABLE_ROWS

        table_rowFile.writeLong(3);
        table_rowFile.writeInt(1);
        table_rowFile.writeInt(address);

        // ROW 3: information_schema.tables.dat
        address = (int) tablesTableFile.getFilePointer();
        tablesTableFile.writeByte("INFORMATION_SCHEMA".length()); // TABLE_SCHEMA
        tablesTableFile.writeBytes("INFORMATION_SCHEMA");
        l1.add(address);

        address = (int) tablesTableFile.getFilePointer();
        tablesTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
        tablesTableFile.writeBytes("COLUMNS");

        table_nameFile.writeByte("COLUMNS".length());
        table_nameFile.writeBytes("COLUMNS");
        table_nameFile.writeInt(1);
        table_nameFile.writeInt(address);

        address = (int) tablesTableFile.getFilePointer();
        tablesTableFile.writeLong(7); // TABLE_ROWS

        table_rowFile.writeLong(7);
        table_rowFile.writeInt(1);
        table_rowFile.writeInt(address);

        table_schemaFile.writeByte("INFORMATION_SCHEMA".length());
        table_schemaFile.writeBytes("INFORMATION_SCHEMA");
        table_schemaFile.writeInt(l1.size());
        for (int i : l1) {
            table_schemaFile.writeInt(i);
        }

    }

    private static void createSchemataTable() throws IOException {
        RandomAccessFile schemataTableFile = new RandomAccessFile(getDatName("INFORMATION_SCHEMA", "SCHEMATA"), "rwd");
        RandomAccessFile schemata_nameFile = new RandomAccessFile(getNdxName("INFORMATION_SCHEMA", "SCHEMATA", "SCHEMA_NAME"), "rwd");
        HashMap<String, List<Integer>> hash = new HashMap<>();
            /*
             *  Create the SCHEMATA table file.
			 *  Initially it has only one entry:
			 *      information_schema
			 */
        // ROW 1: information_schema.schemata.tbl

        int address = (int) schemataTableFile.getFilePointer();
        String data = "INFORMATION_SCHEMA";
        if (!hash.containsKey(data)) {
            List<Integer> l1 = new LinkedList<>();
            l1.add(address);
            hash.put(data, l1);
        }
        schemataTableFile.writeByte(data.length());
        schemataTableFile.writeBytes(data);
        for (String key : hash.keySet()) {
            schemata_nameFile.writeByte(key.length());
            schemata_nameFile.writeBytes(key);
            schemata_nameFile.writeInt(1);
            for (int i : hash.get(key)) {
                schemata_nameFile.writeInt(i);
            }
        }
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

    private static String getFirstWord(String text) {
        if (text.indexOf(' ') > -1) { // Check if there is more than one word.
            return text.substring(0, text.indexOf(' ')); // Extract first word.
        } else {
            return text; // Text is the first word itself.
        }
    }
}



