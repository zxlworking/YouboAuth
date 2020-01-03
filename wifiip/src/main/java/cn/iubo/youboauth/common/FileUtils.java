package cn.iubo.youboauth.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zxl on 2018/11/14.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static File createFileAndFolder(String fileName, String folderPath){
        File appDir = new File(folderPath);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        File destFile = new File(appDir, fileName);
        if(destFile.exists()){
            destFile.delete();
        }
        try {
            destFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destFile;
    }

    public static void appendToFile(File file, File destFile){
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int count = 0;
            while((count = fis.read(buffer)) != -1){
                fos.write(buffer,0,count);
            }
            fos.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendToFile(File file, String msg){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(msg.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
