package com.lyl.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplateWebApplicationTests {

    @Test
    public void contextLoads() {
        System.out.println("123");
        File zipFile = new File("C:\\Users\\lyl\\Desktop\\image\\all.zip");
        // 调用压缩方法
        List<File> fileList = getFile();
        zipFiles(fileList, zipFile);
     }

     private List<File> getFile(){
         String filepath = "C:\\Users\\lyl\\Desktop\\image";//D盘下的file文件夹的目录
         File file = new File(filepath);//File类型可以是文件也可以是文件夹
         File[] fileList = file.listFiles();//将该目录下的所有文件放置在一个File类型的数组中
         List<File> wjList = new ArrayList<File>();//新建一个文件集合
         for (int i = 0; i < fileList.length; i++) {
             if (fileList[i].isFile()) {//判断是否为文件
                 wjList.add(fileList[i]);
             }
         }
         return wjList;
     }

     public static void zipFiles(List<File> srcFiles, File zipFile) {
        // 判断压缩后的文件存在不，不存在则创建
        if (!zipFile.exists()) {
           try {
                   zipFile.createNewFile();
               } catch (IOException e) {
                   e.printStackTrace();
               }
            }
        // 创建 FileOutputStream 对象
        FileOutputStream fileOutputStream = null;
        // 创建 ZipOutputStream
        ZipOutputStream zipOutputStream = null;
        // 创建 FileInputStream 对象
        FileInputStream fileInputStream = null;
        try {
                // 实例化 FileOutputStream 对象
                fileOutputStream = new FileOutputStream(zipFile);
                // 实例化 ZipOutputStream 对象
                zipOutputStream = new ZipOutputStream(fileOutputStream);
                // 创建 ZipEntry 对象
                ZipEntry zipEntry = null;
                // 遍历源文件数组
                for (int i = 0; i < srcFiles.size(); i++) {
                        // 将源文件数组中的当前文件读入 FileInputStream 流中
                        fileInputStream = new FileInputStream(srcFiles.get(i));
                        // 实例化 ZipEntry 对象，源文件数组中的当前文件
                        zipEntry = new ZipEntry(srcFiles.get(i).getName());
                       zipOutputStream.putNextEntry(zipEntry);
                       // 该变量记录每次真正读的字节个数
                       int len;
                       // 定义每次读取的字节数组
                       byte[] buffer = new byte[1024];
                       while ((len = fileInputStream.read(buffer)) > 0) {
                               zipOutputStream.write(buffer, 0, len);
                           }
                   }
               zipOutputStream.closeEntry();
               zipOutputStream.close();
               fileInputStream.close();
               fileOutputStream.close();
           } catch (IOException e) {
              e.printStackTrace();
           }


    }

}
