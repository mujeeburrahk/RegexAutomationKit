package com.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.*;

public class DBHandler {
    private static final Logger log = LogManager.getLogger(DBHandler.class);

    public static Object executeQuery(QueryModel queryModel) {
        log.info("Executing {} query", queryModel.getQueryType());
        Object result = null;
        String query = buildQuery(queryModel);
        try (Connection connection = DBClientManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            setStatementParameters(statement, queryModel);
            String queryType = String.valueOf(queryModel.getQueryType());
            if (queryType.equals("INSERT") || queryType.equals("UPDATE") || queryType.equals("DELETE")) {
                result = statement.executeUpdate();
            } else {
                try (ResultSet resultSet = statement.executeQuery()) {
                    result = handleSelectQueryResult(resultSet);
                }
            }
            log.info("Query executed successfully: " + query);
        } catch (SQLException e) {
            log.error("Error executing query: " + e.getMessage(), e);
        }
        return result;
    }

    private static String buildQuery(QueryModel queryModel) {
        StringBuilder query = new StringBuilder();
        switch (queryModel.getQueryType()) {
            case SELECT:
                query.append("SELECT ");
                if (queryModel.getReturnTop() != null) {
                    query.append("TOP ").append(queryModel.getReturnTop()).append(" ");
                }
                query.append(queryModel.getSelectColumns() != null ? queryModel.getSelectColumns() : "*").append(" ");
                query.append("FROM ").append(queryModel.getTableName()).append(" ");
                break;
            case INSERT:
                query.append("INSERT INTO ").append(queryModel.getTableName()).append(" (");
                query.append(String.join(", ", queryModel.getInsertColumnValues().keySet())).append(") ");
                query.append("VALUES (");
                query.append("?, ".repeat(queryModel.getInsertColumnValues().size()));
                query.delete(query.length() - 2, query.length());
                query.append(")");
                break;
            case UPDATE:
                query.append("UPDATE ").append(queryModel.getTableName()).append(" SET ");
                queryModel.getUpdateColumnValues().forEach((column, value) -> query.append(column).append(" = ?, "));
                query.delete(query.length() - 2, query.length());
                if (queryModel.getWhereCondition() != null) {
                    query.append(" WHERE ").append(queryModel.getWhereCondition());
                }
                break;
            case DELETE:
                query.append("DELETE FROM ").append(queryModel.getTableName());
                if (queryModel.getWhereCondition() != null) {
                    query.append(" WHERE ").append(queryModel.getWhereCondition());
                }
                break;
        }
        if (queryModel.getOrderBy() != null) {
            query.append(" ORDER BY ").append(queryModel.getOrderBy());
            if (queryModel.getOrderType() != null) {
                query.append(" ").append(queryModel.getOrderType());
            }
        }
        log.info("Formatted query: {}", query);
        return query.toString();
    }

    private static void setStatementParameters(PreparedStatement statement, QueryModel queryModel) throws SQLException {
        int i = 1;
        switch (queryModel.getQueryType()) {
            case INSERT:
                for (Object value : queryModel.getInsertColumnValues().values())  {
                    statement.setObject(i++, value);
                }
                break;
            case UPDATE:
                for (Object value : queryModel.getUpdateColumnValues().values()) {
                    statement.setObject(i++, value);
                }
                break;
        }
    }

    public static Object handleSelectQueryResult(ResultSet resultSet) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
            if (results.size() == 1) {
                Map<String, Object> singleRow = results.get(0);
                return columnCount == 1 ? singleRow.values().iterator().next() : singleRow;
            }
        } catch (SQLException e) {
            log.error("Error processing result set: " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null && !resultSet.isClosed())
                    resultSet.close();
            } catch (SQLException e) {
                log.error("Error closing result set: " + e.getMessage(), e);
            }
        }
        return results;
    }
}