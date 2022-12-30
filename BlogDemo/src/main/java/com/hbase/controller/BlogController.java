package com.hbase.controller;

import com.hbase.service.BlogService;

import java.io.IOException;
import java.util.List;

public class BlogController {

    private BlogService service = new BlogService();

    public void init() throws IOException {

        service.init();
    }

    public void delete() throws IOException {
        service.delete();
    }


    // 发布内容
    public void publish(String star, String content) throws IOException {

        service.publish(star, content);

    }

    // 添加关注用户
    public void follow(String fans, String star) throws IOException {

        service.follow(fans, star);
    }


    // 移除（取关）用户
    public void unFollow(String fans, String star) throws IOException {

        service.unFollow(fans, star);
    }

    // 获取关注的人的blog内容
    //  获取某个明星的所有blog

    public List<String> getAllBlogsByUserID(String star) throws IOException {

        return service.getAllBlogByUserId(star);

    }

    // 8.2 获取关注的所有star的近期weibo
    public List<String> getAllRecentBlogs(String fans) throws IOException {

        return service.getAllRecentBlogs(fans);
    }

}