package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    //注入Dao
    @Autowired
    private CategoryMapper categoryMapper;

    //日志对象
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        if (StringUtils.isBlank(categoryName) || parentId == null){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);   //当前分类是可用的
        int resultCount = categoryMapper.insert(category);
        if (resultCount > 0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServerResponse<String> updateCategoryById(Integer categoryId, String categoryName) {
        if (StringUtils.isBlank(categoryName) || categoryId == null){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount > 0){
            return ServerResponse.createBySuccessMessage("更新品类成功");
        }
        return ServerResponse.createByErrorMessage("更新品类失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            //不需要给前端返回错误结果，直接打印日志
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    /**
     * 递归查询本节点的id及子节点的id
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        //递归算法初始化，调用时进行初始化
        Set<Category> categorySet = Sets.newHashSet();  //guava提供的方法，
        findChildCategory(categorySet, categoryId);
        //需要返回id的集合
        List<Integer> categoryList = Lists.newArrayList();
        if (categoryId != null){
            for (Category categoryItem : categorySet) {
                categoryList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归算法，算出子节点
     * 需要重写对象的hashcode和equals方法
     */
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        //查找当前id的Category
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            //若不为空，则将其添加进Set中,又将set作为参数传入自己，最后将此set返回
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有一个退出的条件，这里即是子节点下没有新的子节点了
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        /**如果没查到，myBatis不会返回一个null的List对象，而是返回0个元素的一个集合，所以不需要做null判断，
        如果是调用一些未知的方法，这里需要做一个空判断，否则用foreach循环的时候会报nullPoint异常*/
        for (Category categoryItem : categories) {
            /** 继续调用自己，查找当前节点的Category */
            findChildCategory(categorySet,categoryItem.getId());
        }
        //当查出的集合为空，即没有子节点的时候不会进入foreach，退出递归
        return categorySet;
    }

}
