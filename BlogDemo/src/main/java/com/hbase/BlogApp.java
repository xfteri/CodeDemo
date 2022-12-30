package com.hbase;

import com.hbase.controller.BlogController;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class BlogApp {

    private static BlogController controller = new BlogController();


    @Test
    public void init() throws IOException {
        // 1. 创建表的初始化
        controller.init();
    }

    @Test
    public void publish() throws IOException {
        // 2. 发blog
        controller.publish("zhangsan","happy");
        controller.publish("zhangsan","sad");
        controller.publish("zhangsan","normal");
        controller.publish("zhangsan","angry");
        controller.publish("zhangsan","123456");
        controller.publish("wangwu","456789");
    }

    @Test
    public void follow() throws IOException {
        // 3. 关注
        controller.follow("1002","zhangsan");
        controller.follow("1003","zhangsan");
        controller.follow("1002","wangwu");
        controller.follow("1003","wangwu");
    }

    @Test
    public void showMsg() throws IOException {
        // 4. 获取内容
        // 最新的消息(获取)
        List<String> allRecentBlogs = controller.getAllRecentBlogs("1002");
        // 查看数据
        for (String allRecentBlog : allRecentBlogs) {
            System.out.println(allRecentBlog);
        }
    }

    @Test
    public void unFollow() throws IOException {
        List<String> allRecentBlogs = controller.getAllRecentBlogs("1002");
        // 5. 取关微博
        controller.unFollow("1002","wangwu");
        // 查看数据
        for (String allRecentBlog : allRecentBlogs) {
            System.out.println(allRecentBlog);
        }
    }

    @Test
    public void showAll() throws IOException {
        // 6. 获取某一个人的所有微博
        List<String> allBlogsByUserID = controller.getAllBlogsByUserID("zhangsan");

        for (String s : allBlogsByUserID) {
            System.out.println(s);
        }
    }

    @Test
    public void deleteAll() throws IOException {
        //删除表
        controller.delete();
    }
}

