package com.hbase.constant;


public class Names {

    public final static String NAMESPACE_BLOG = "blog";

    public final static String TABLE_BLOG = "blog:message";
    public final static String TABLE_RELATION = "blog:relation";
    public final static String TABLE_INBOX = "blog:inbox";

    public final static String BLOG_FAMILY_DATA = "data";
    public final static String RELATION_FAMILY_DATA = "data";
    public final static String INBOX_FAMILY_DATA = "data";

    public final static String BLOG_COLUMN_CONTENT = "content";
    public final static String RELATION_COLUMN_TIME = "time";

    public final static Integer INBOX_DATA_VERSIONS = 3;

    public final static String DROP_TABLE_NAME1 = TABLE_BLOG.split(":")[1];
    public final static String DROP_TABLE_NAME2 = TABLE_RELATION.split(":")[1];
    public final static String DROP_TABLE_NAME3 = TABLE_INBOX.split(":")[1];

}

