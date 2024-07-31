package com.mari05lim.tandera.model.sqlite;

/**
 * DEV Mari05liM
 */
public class Reference {

    private final String tableName;
    private final Column column;

    public Reference(String tableName, Column column) {
        this.tableName = tableName;
        this.column = column;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return the column
     */
    public Column getColumn() {
        return column;
    }

}