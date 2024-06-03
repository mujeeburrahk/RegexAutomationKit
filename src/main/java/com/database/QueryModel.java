package com.database;

import com.enums.QueryType;
import java.util.Map;

public class QueryModel {
    private final QueryType queryType;
    private Integer returnTop;
    private String selectColumns;
    private final String tableName;
    private String whereCondition;
    private String orderBy;
    private OrderType orderType;
    private Map<String, Object> insertColumnValues;
    private Map<String, Object> updateColumnValues;

    public QueryModel(QueryType queryType, String tableName) {
        this.queryType = queryType;
        this.tableName = tableName;
    }

    public QueryModel(QueryType queryType, String selectColumns, String tableName) {
        this(queryType, tableName);
        this.selectColumns = selectColumns;
    }

    public QueryModel(QueryType queryType, String tableName, Map<String, Object> insertColumnValues){
        this(queryType, tableName);
        this.insertColumnValues = insertColumnValues;
    }

    public QueryModel(QueryType queryType, Map<String, Object> updateColumnValues, String tableName){
        this(queryType, tableName);
        this.updateColumnValues = updateColumnValues;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setReturnTop(Integer returnTop) {
        if (returnTop != null && returnTop < 0) {
            throw new IllegalArgumentException("returnTop must be a non-negative value");
        }
        this.returnTop = returnTop;
    }

    public Integer getReturnTop() {
        return returnTop;
    }

    public void setWhereCondition(String whereCondition) {
        this.whereCondition = whereCondition;
    }

    public String getWhereCondition() {
        return whereCondition;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setSelectColumns(String selectColumns) {
        this.selectColumns = selectColumns;
    }

    public String getSelectColumns() {
        return selectColumns;
    }

    public void setInsertColumnValues(Map<String, Object> insertColumnValues) {
        this.insertColumnValues = insertColumnValues;
    }

    public Map<String, Object> getInsertColumnValues() {
        return insertColumnValues;
    }

    public void setUpdateColumnValues(Map<String, Object> updateColumnValues) {
        this.updateColumnValues = updateColumnValues;
    }

    public Map<String, Object> getUpdateColumnValues() {
        return updateColumnValues;
    }

    public enum OrderType {
        ASC, DESC
    }
}