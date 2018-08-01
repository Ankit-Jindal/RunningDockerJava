package dada;


import java.util.List;

public class HbaseRecord {
    String rowKey;
    List<ColumnValueDetails> columnValueDetails;

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public List<ColumnValueDetails> getColumnValueDetails() {
        return columnValueDetails;
    }

    public void setColumnValueDetails(List<ColumnValueDetails> columnValueDetails) {
        this.columnValueDetails = columnValueDetails;
    }
}