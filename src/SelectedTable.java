

import java.util.LinkedList;
import java.util.List;

/**
 * A table class which has all the rows of the table queried.
 * @author Salil Kansal
 * @version 1.0
 * @since 2016-04-09
 */
public class SelectedTable {
    Row headerRow = new Row();
    List<String> colStructure = new LinkedList<>();
    List<Row> data = new LinkedList<>();

    void printTable() {
        if (data.size() == 0) {
            System.out.println("0 rows selected");
            return;

        }

        MultiColumnPrinter mp = new MultiColumnPrinter(headerRow.row.size(), 10, "-", 0,false) {
            @Override
            public void doPrint(String str) {
                System.out.print(str);
            }

            @Override
            public void doPrintln(String str) {
                System.out.println();
            }
        };

        String[] d = new String[headerRow.row.size()];

        for (Row temp : data) {
            int j = 0;
            for (String data : temp.row) {
                d[j++] = data;
            }
            mp.add(d);

        }
        String[] header = new String[headerRow.row.size()];
        int index = 0;
        for (String t : headerRow.row) {
            header[index++] = t;
        }




        mp.addTitle(header);

        mp.print(true);


    }
}

class Row {
    List<String> row = new LinkedList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String aRow : row) {
            sb.append(aRow);
            sb.append("\t");
        }
        return sb.toString();
    }
}
