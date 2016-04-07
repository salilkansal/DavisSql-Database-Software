import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * On first run the methods of these classes will make the default 3 tables and fill
 * them with default information.
 *
 * @author Salil Kansal
 * @version 1.0
 * @since 2016-04-09
 */
public class FirstRun {
    static boolean firstRun() {
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
        schemata = new File("IndexFiles/INFORMATION_SCHEMA/TABLES/INFORMATION_SCHEMA.TABLES.TABLE_ROWS.ndx");
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

    static void createDefaultTables() {
        try {
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
        TreeMap<String, List<Integer>> tableNameHash = new TreeMap<>();
        TreeMap<String, List<Integer>> columnNameHash = new TreeMap<>();
        TreeMap<Integer, List<Integer>> ordinalPosHash = new TreeMap<>();
        TreeMap<String, List<Integer>> columnTypeHash = new TreeMap<>();
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
        columnsTableFile.writeByte("VARCHAR(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("VARCHAR(64)");
        l1 = new LinkedList<>();
        l1.add(address);
        columnTypeHash.put("VARCHAR(64)", l1);


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
        columnsTableFile.writeByte("VARCHAR(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("VARCHAR(64)");
        columnTypeHash.get("VARCHAR(64)").add(address);


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
        columnsTableFile.writeByte("VARCHAR(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("VARCHAR(64)");
        columnTypeHash.get("VARCHAR(64)").add(address);

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
        columnsTableFile.writeByte("LONG INT".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("LONG INT");
        l1 = new LinkedList<>();
        l1.add(address);
        columnNameHash.put("LONG INT", l1);


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
        columnsTableFile.writeByte("VARCHAR(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("VARCHAR(64)");
        columnTypeHash.get("VARCHAR(64)").add(address);

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
        columnsTableFile.writeByte("VARCHAR(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("VARCHAR(64)");
        columnTypeHash.get("VARCHAR(64)").add(address);

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
        columnsTableFile.writeByte("VARCHAR(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("VARCHAR(64)");
        columnTypeHash.get("VARCHAR(64)").add(address);

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
        columnsTableFile.writeByte("INT".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("INT");
        l1 = new LinkedList<>();
        l1.add(address);
        columnTypeHash.put("INT", l1);

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
        columnsTableFile.writeByte("VARCHAR(64)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("VARCHAR(64)");
        columnTypeHash.get("VARCHAR(64)").add(address);


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
        columnsTableFile.writeByte("VARCHAR(3)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("VARCHAR(3)");
        l1 = new LinkedList<>();
        l1.add(address);
        columnTypeHash.put("VARCHAR(3)", l1);

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
        columnsTableFile.writeByte("VARCHAR(3)".length()); // COLUMN_TYPE
        columnsTableFile.writeBytes("VARCHAR(3)");
        columnTypeHash.get("VARCHAR(3)").add(address);


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

        HashingFunctions.saveHashtoFile(tableNameFile, tableNameHash);
        HashingFunctions.saveHashtoFile(ColumnNameFile, columnNameHash);
        HashingFunctions.saveHashtoFile(ColumnTypeFile, columnTypeHash);
        HashingFunctions.saveIntHashtoFile(ordinalPosFile, ordinalPosHash);

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
        RandomAccessFile table_rowFile = new RandomAccessFile(getNdxName(database, tablename, "TABLE_ROWS"), "rwd");


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

}
