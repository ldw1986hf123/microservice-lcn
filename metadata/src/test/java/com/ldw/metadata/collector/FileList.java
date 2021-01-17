package com.ldw.metadata.collector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.Date;

/**
 * 遍历文件目录
 * 远程调用机器 需要 liunx 修改 /etc/hosts 添加 10.11.12.4 master
 * @author feng
 *
 */
public class FileList {

    public static void main(String[] args) throws IOException {
        //创建与HDFS连接
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://192.168.171.134:9000");

        //获得fileSystem
        FileSystem fileSystem = FileSystem.get(conf);

        //listStatus获取/test目录下信息
        FileStatus[] fileStatuses = fileSystem.listStatus(new Path("/ldw"));

        //遍历输出文件夹下文件
        for(FileStatus fileStatus :fileStatuses) {
            System.out.println(fileStatus.getPath() + "  " + new Date(fileStatus.getAccessTime()) + "  " +
                    fileStatus.getBlockSize() + "  " + fileStatus.getPermission());
        }
    }

    /**
     *
     * @param hdfs FileSystem 对象
     * @param path 文件路径
     */
    public static void iteratorShowFiles(FileSystem hdfs, Path path){
        try{
            if(hdfs == null || path == null){
                return;
            }
            //获取文件列表
            FileStatus[] files = hdfs.listStatus(path);

            //展示文件信息
            for (int i = 0; i < files.length; i++) {
                try{
                    if(files[i].isDirectory()){
                        System.out.println(">>>" + files[i].getPath()
                                + ", dir owner:" + files[i].getOwner());
                        //递归调用
                        iteratorShowFiles(hdfs, files[i].getPath());
                    }else if(files[i].isFile()){
                        System.out.println("   " + files[i].getPath()
                                + ", length:" + files[i].getLen()
                                + ", owner:" + files[i].getOwner());
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}