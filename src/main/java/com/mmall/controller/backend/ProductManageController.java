package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 后台商品管理控制器
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    //注入service
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * 保存产品方法
     */
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            return this.iProductService.saveOrUpdate(product);
        }
        return response;
    }

    /**
     * 产品上下架功能
     */
    @RequestMapping(value = "/set_sale_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            //开始设置商品上下架信息
            return this.iProductService.setSaleStatus(productId, status);
        }
        return response;
    }

    @RequestMapping(value = "/list.do")
    @ResponseBody
    public ServerResponse getProductList(HttpSession session,
                                  @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            //开始设置商品上下架信息
            return this.iProductService.getProductList(pageNum, pageSize);
        }
        return response;
    }

    /**
     * 获取产品详情
     */
    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            //开始设获取产品详情
            return this.iProductService.manageProductDetail(productId);
        }
        return response;
    }

    /**
     * 后台获取搜索且分页显示
     * 如果不传值，默认显示第一页，每页默认显示10条数据
     */
    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, String productName, Integer productId,
                                  @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            //开始获取商品list
            return this.iProductService.searchProduct(pageNum, pageSize, productName, productId);
        }
        return response;
    }

    /**
     * 文件上传控制器
     * @param session获取登录信息
     * @param file上传的文件，在前台的值是upload_file，非必须
     * @param request用于获取上下文对象
     * @return
     */
    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            //获取文件servlet真实路径
            String path = request.getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map<String, String> fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        }
        return response;
    }

    /**
     * 富文本图片上传
     * @param session
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false)
            MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        //声明map
        Map<String,Object> resultMap = Maps.newHashMap();
        //判断登录和管理员权限
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        /** 使用富文本上传需要遵守富文本上传的要求
         * simditor要求的返回JSON格式
         * {
         *     "success":true,
         *     "msg":"error message",
         *     "file_path":"[real file path]"
         * }
         * */
        if (user == null){
            resultMap.put("success",false);
            resultMap.put("msg", "未登录，请登录后再操作");
            return resultMap;
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success",false);
            resultMap.put("msg", "无权限，请登录管理员");
            return resultMap;
        }
    }
}
