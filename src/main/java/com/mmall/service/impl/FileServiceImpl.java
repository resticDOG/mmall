package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file, String path){
        //获取原始名称
        String fileName = file.getOriginalFilename();
        //获取拓展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        //打印日志
        logger.info("开始上传文件，上传的文件名为：{}，上传的路径：{}，新文件名：{}",fileName, path, uploadFileName);
        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //文件已经上传至本地upload文件夹下
            //将targetFile上传到FTP服务器中
            boolean result = FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            if (result){
                //上传至ftp服务器成功则删除upload下的文件
                targetFile.delete();
            } else {
                logger.error("上传至FTP服务器失败");
                //上传失败返回null,不删除文件
                return null;
            }
        } catch (Exception e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }
}
