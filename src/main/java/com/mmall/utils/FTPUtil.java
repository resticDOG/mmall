package com.mmall.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {
    //日志对象
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    //构造器
    public FTPUtil(String ip, int port, String user, String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    //开放方法
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 80, ftpUser, ftpPass);
        logger.info("开始连接FTP服务器");
        boolean result = ftpUtil.uploadFile("img", fileList);
        logger.info("结束上传，上传结果：{}",result);
        return result;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = true;     //默认值：true
        //声明文件io流
        FileInputStream fis = null;
        //连接ftp服务器
        if (connectServer(this.ip, this.user, this.pwd)){
            try {
                //更改工作目录
                ftpClient.changeWorkingDirectory(remotePath);
                //设置缓存
                ftpClient.setBufferSize(1024);
                //设置编码
                ftpClient.setControlEncoding("UTF-8");
                //设置文件类型：BINARY_FILE_TYPE —>二进制文件
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //打开本地被动模式
                ftpClient.enterLocalPassiveMode();
                //开始上传
                for (File fileItem : fileList) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                }
            } catch (IOException e) {
                uploaded = false;
                logger.error("上传文件异常", e);
            } finally {
                //关闭资源
                fis.close();
                ftpClient.disconnect();
            }
        } else {
            uploaded = false;
        }
        return uploaded;
    }

    /**
     * 连接ftp服务器
     */
    private boolean connectServer(String ip, String user, String pwd) throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(ip);
        //返回登录结果
        return ftpClient.login(user, pwd);
    }


    //字段
    private String ip;      //ip地址
    private String user;    //用户名
    private String pwd;     //密码
    private int port;       //端口
    private FTPClient ftpClient;    //客户端

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
