package com.hbase.service;

import com.hbase.constant.Names;
import com.hbase.dao.BlogDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlogService {
    private BlogDao dao = new BlogDao();

    public void init() throws IOException {

        //1) 创建命名空间以及表名的定义
        dao.createNamespace(Names.NAMESPACE_BLOG);

        //2) 创建微博内容表
        dao.createTable(Names.TABLE_BLOG, Names.BLOG_FAMILY_DATA);

        //3) 创建用户关系表
        dao.createTable(Names.TABLE_RELATION, Names.RELATION_FAMILY_DATA);

        //4) 创建用户微博内容接收邮件表
        dao.createTable(Names.TABLE_INBOX, Names.INBOX_DATA_VERSIONS, Names.INBOX_FAMILY_DATA);

    }

    public void delete() throws IOException {
        //删除blog表
        dao.dropTable(Names.NAMESPACE_BLOG,Names.DROP_TABLE_NAME1);
        //删除relation表
        dao.dropTable(Names.NAMESPACE_BLOG,Names.DROP_TABLE_NAME2);
        //删除inbox表
        dao.dropTable(Names.NAMESPACE_BLOG,Names.DROP_TABLE_NAME3);
    }

    public void publish(String star, String content) throws IOException {

        // 1. 在weibo表中插入一条数据
        String rowKey = star + "_" + System.currentTimeMillis();
        dao.putCell(Names.TABLE_BLOG, rowKey, Names.BLOG_FAMILY_DATA, Names.BLOG_COLUMN_CONTENT, content);

        // 2. 从relation表中获取star的所有fansID (默认有粉丝逻辑有些问题)
        String prefix = star + ":followedby:";
        List<String> list = dao.getRowKeysByPrefix(Names.TABLE_RELATION, prefix);

        if (list.size() <= 0) {
            return;
        }

        List<String> fansIds = new ArrayList<>();

        // 遍历
        for (String row : list) {
            String[] split = row.split(":");
            // 获取粉丝ID
            fansIds.add(split[2]);
        }


        // 3. 向所有fans的inbox中插入本条weibo的id
        // 循环调用 or 批量调用
        dao.putCells(Names.TABLE_INBOX, fansIds, Names.INBOX_FAMILY_DATA, star, rowKey);


    }

    public void follow(String fans, String star) throws IOException {

        // 1. 向relation表中插入两条数据
        String rowKey1 = fans + ":follow:" + star;
        String rowKey2 = star + ":followedby:" + fans;
        String time = System.currentTimeMillis() + "";
        dao.putCell(Names.TABLE_RELATION, rowKey1, Names.RELATION_FAMILY_DATA, Names.RELATION_COLUMN_TIME, time);
        dao.putCell(Names.TABLE_RELATION, rowKey2, Names.RELATION_FAMILY_DATA, Names.RELATION_COLUMN_TIME, time);


        // 2. 从weibo表中获取star的近期weibo

        // 拿取所有
        String startRow = star;
        String stopRow = star + "|";
        List<String> list = dao.getRowKeysByRange(Names.TABLE_BLOG, startRow, stopRow);

        // 判断
        if (list.size() <= 0) {
            return;
        }

        // 获取近期的weibo
        // 使用三元运算符进行判断
        int fromIndex = list.size() > Names.INBOX_DATA_VERSIONS ? list.size() - Names.INBOX_DATA_VERSIONS : 0;
        List<String> recentBlogIds = list.subList(fromIndex, list.size());


        // 3. 向fans的inbox表中插入star的近期weiboId
        for (String recentBlogId : recentBlogIds) {
            dao.putCell(Names.TABLE_INBOX, fans, Names.INBOX_FAMILY_DATA, star, recentBlogId);
        }


    }

    public void unFollow(String fans, String star) throws IOException {

        // 1. 删除relation表中的两条数据
        String rowKey1 = fans + ":follow:" + star;
        String rowKey2 = star + ":followedby:" + fans;
        dao.deleteRow(Names.TABLE_RELATION, rowKey1);
        dao.deleteRow(Names.TABLE_RELATION, rowKey2);


        // 2. 删除inbox表中的一列
        dao.deleteCells(Names.TABLE_INBOX, fans, Names.INBOX_FAMILY_DATA, star);


    }

    public List<String> getAllBlogByUserId(String star) throws IOException {

        return dao.getCellsByPrefix(Names.TABLE_BLOG, star, Names.BLOG_FAMILY_DATA, Names.BLOG_COLUMN_CONTENT);


    }

    public List<String> getAllRecentBlogs(String fans) throws IOException {

        // 1. 从inbox中获取fans的所有的star的近期weiboId
        List<String> list = dao.getFamilyByRowKey(Names.TABLE_INBOX, fans, Names.INBOX_FAMILY_DATA);


        // 2. 根据weiboID去weibo表中查询内容
        return dao.getCellsByRowKey(Names.TABLE_BLOG, list, Names.BLOG_FAMILY_DATA, Names.BLOG_COLUMN_CONTENT);


    }
}


