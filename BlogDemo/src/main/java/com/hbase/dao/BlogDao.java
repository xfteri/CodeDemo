package com.hbase.dao;

import com.hbase.constant.Names;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlogDao {
    public static Connection connection = null;

    static {
        try {
            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", "hadoop102,hadoop103,hadoop104");
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createNamespace(String namespace) throws IOException {

        Admin admin = connection.getAdmin();

        NamespaceDescriptor namespce = NamespaceDescriptor.create(namespace).build();

        admin.createNamespace(namespce);

        admin.close();

    }

    //判断表是否存在
    public static boolean existsTable(String nameSpace, String tableName) throws IOException {
        Admin admin = connection.getAdmin();
        return admin.tableExists(TableName.valueOf(nameSpace, tableName));
    }

    //删除表
    public void dropTable(String nameSpace, String tableName) throws IOException {
        //判断表格是否存在
        if (!existsTable(nameSpace, tableName)) {
            System.out.println(tableName + "不存在！");
            return;
        }
        Admin admin = connection.getAdmin();
        //字符串类型转换
        TableName tableName1 = TableName.valueOf(nameSpace, tableName);
        //禁用表格
        admin.disableTable(tableName1);
        //删除表格
        admin.deleteTable(tableName1);
    }

    //创建表
    public void createTable(String tableName, String... families) throws IOException {
        // 因为下面的存在，此处可以省略
        createTable(tableName, 1, families);
    }

    public void createTable(String tableName, Integer versions, String... families) throws IOException {
        Admin admin = connection.getAdmin();

        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));

        for (String family : families) {

            HColumnDescriptor familyDesc = new HColumnDescriptor(family);

            familyDesc.setMaxVersions(versions);

            table.addFamily(familyDesc);
        }

        admin.createTable(table);
        admin.close();
    }

    public void putCell(String tableName, String rowKey, String family, String column, String value) throws IOException {

        Table table = connection.getTable(TableName.valueOf(tableName));

        Put put = new Put(Bytes.toBytes(rowKey));

        put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));

        table.put(put);

        table.close();

    }


    public List<String> getRowKeysByPrefix(String tableName, String prefix) throws IOException {

        ArrayList<String> list = new ArrayList<>();

        Table table = connection.getTable(TableName.valueOf(tableName));

        Scan scan = new Scan();

        scan.setRowPrefixFilter(Bytes.toBytes(prefix));

        ResultScanner scanner = table.getScanner(scan);

        for (Result result : scanner) {

            byte[] row = result.getRow();
            String rowKey = Bytes.toString(row);
            list.add(rowKey);
        }

        scanner.close();
        table.close();

        return list;
    }

    public void putCells(String tableName, List<String> rowKeys, String family, String column, String value) throws IOException {

        Table table = connection.getTable(TableName.valueOf(tableName));

        ArrayList<Put> puts = new ArrayList<>();

        // 遍历RowKeys
        for (String rowKey : rowKeys) {
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
            puts.add(put);

        }

        table.put(puts);

        table.close();

    }

    public List<String> getRowKeysByRange(String tableName, String startRow, String stopRow) throws IOException {

        List<String> list = new ArrayList<>();

        Table table = connection.getTable(TableName.valueOf(tableName));

        Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(stopRow));

        ResultScanner scanner = table.getScanner(scan);

        for (Result result : scanner) {

            byte[] row = result.getRow();
            String rowKey = Bytes.toString(row);
            list.add(rowKey);
        }

        scanner.close();
        table.close();

        return list;
    }

    public void deleteRow(String tableName, String rowKey) throws IOException {

        Table table = connection.getTable(TableName.valueOf(tableName));

        Delete delete = new Delete(Bytes.toBytes(rowKey));

        table.delete(delete);

        table.close();
    }

    public void deleteCells(String tableName, String rowKey, String family, String column) throws IOException {

        Table table = connection.getTable(TableName.valueOf(tableName));

        Delete delete = new Delete(Bytes.toBytes(rowKey));

        delete.addColumns(Bytes.toBytes(family), Bytes.toBytes(column));

        table.delete(delete);

        table.close();

    }

    public List<String> getCellsByPrefix(String tableName, String prefix, String family, String column) throws IOException {

        List<String> list = new ArrayList<>();

        Table table = connection.getTable(TableName.valueOf(tableName));

        Scan scan = new Scan();

        scan.setRowPrefixFilter(Bytes.toBytes(prefix));

        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));

        ResultScanner scanner = table.getScanner(scan);

        for (Result result : scanner) {
            Cell[] cells = result.rawCells();
            list.add(Bytes.toString(CellUtil.cloneValue(cells[0])));
        }

        scanner.close();
        table.close();

        return list;
    }

    public List<String> getFamilyByRowKey(String tableName, String rowKey, String family) throws IOException {

        List<String> list = new ArrayList<>();

        Table table = connection.getTable(TableName.valueOf(tableName));

        Get get = new Get(Bytes.toBytes(rowKey));

        get.setMaxVersions(Names.INBOX_DATA_VERSIONS);

        get.addFamily(Bytes.toBytes(family));

        Result result = table.get(get);

        for (Cell cell : result.rawCells()) {
            list.add(Bytes.toString(CellUtil.cloneValue(cell)));
        }

        table.close();

        return list;
    }

    public List<String> getCellsByRowKey(String tableName, List<String> rowKeys, String family, String column) throws IOException {

        List<String> weibos = new ArrayList<>();

        Table table = connection.getTable(TableName.valueOf(tableName));

        List<Get> gets = new ArrayList<>();

        for (String rowKey : rowKeys) {
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));

            gets.add(get);
        }

        Result[] results = table.get(gets);

        for (Result result : results) {
            String weibo = Bytes.toString(CellUtil.cloneValue(result.rawCells()[0]));
            weibos.add(weibo);
        }

        table.close();

        return weibos;
    }
}