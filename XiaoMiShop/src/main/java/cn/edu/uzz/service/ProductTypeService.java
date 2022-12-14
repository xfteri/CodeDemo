package cn.edu.uzz.service;

import cn.edu.uzz.pojo.ProductType;

import java.util.List;


public interface ProductTypeService {

    /**
     * 获取到数据框中所有的商品的类型
     *
     * @return {@code List<ProductType>}
     */
    List<ProductType> getAll();
}
