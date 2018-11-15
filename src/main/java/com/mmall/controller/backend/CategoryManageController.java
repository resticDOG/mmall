package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 分类管理模块控制器
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    //注入userService
    @Autowired
    private IUserService iUserService;

    //注入categoryService
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加品类方法，通过参数控制默认值，让其默认值为0根节点品类
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") Integer parentId){
        //判断登录状态
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前未登录，需要登录之后操作");
        }
        //校验是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if (response.isSuccess()){
            //是管理员，开始添加分类
            return iCategoryService.addCategory(categoryName, parentId);
        }
        return response;
    }

    /**
     * 更新品类名称
     */
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session, Integer categoryId, String categoryName){
        //判断登录状态
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前未登录，需要登录之后操作");
        }
        //校验是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if (response.isSuccess()){
            //是管理员，开始更新分类
            return iCategoryService.updateCategoryById(categoryId, categoryName);
        }
        return response;
    }

    /**
     * 根据传入的id查询当前id下子节点的分类信息，平级且不递归
     * 如果id没有传入，默认查找id=0也就是根节点的信息
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        //判断登录状态
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前未登录，需要登录之后操作");
        }
        //校验是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if (response.isSuccess()){
            //查询子节点的category且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }
        return response;
    }

    /**
     * 获取当前分类的id并且递归查询其子节点的id的方法
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        //判断登录状态
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前未登录，需要登录之后操作");
        }
        //校验是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if (response.isSuccess()){
            //查询当前节点的id及递归子节点的id
            //0 -> 1000 -> 10000
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }
        return response;
    }


}
