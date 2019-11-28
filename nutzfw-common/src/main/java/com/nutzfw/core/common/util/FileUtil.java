/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util;

import com.nutzfw.modules.sys.dto.FileZipDTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.nutz.lang.Encoding;
import org.nutz.lang.random.R;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date 2015/5/1616:35
 */
@Slf4j
public class FileUtil {

    /**
     * 解压zip格式压缩包
     * 覆盖式解压缩
     * 对应的是ant.jar
     */
    public static void unzip(String sourceZip, String destDir, String[] excludes) throws Exception {
        try {
            Project p = new Project();
            Expand e = new Expand();
            e.setProject(p);
            e.setSrc(new File(sourceZip));
            e.setOverwrite(true);
            e.setDest(new File(destDir));
            if (null != excludes) {
                PatternSet patternSet = new PatternSet();
                for (String exclude : excludes) {
                    patternSet.setExcludes(exclude);
                }
                e.addPatternset(patternSet);
            }
            /*
             * ant下的zip工具默认压缩编码为UTF-8编码，
             * 而winRAR软件压缩是用的windows默认的GBK或者GB2312编码
             * 所以解压缩时要制定编码格式
             */
            e.setEncoding("GBK");
            e.execute();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 压缩
     *
     * @param sourceFile 压缩的源文件 如: c:/upload
     * @param targetZip  生成的目标文件 如：c:/upload.zip
     */
    public static void zip(String sourceFile, String targetZip, String[] excludes) {
        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(new File(targetZip));
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(new File(sourceFile));
        if (null != excludes) {
            for (String exclude : excludes) {
                fileSet.setExcludes(exclude);
            }
        }
        zip.addFileset(fileSet);
        zip.execute();
    }

    /**
     * 获得类的基路径，打成jar包也可以正确获得路径
     *
     * @return
     */
    public static String getBasePath() {
        String filePath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            try {
                //解决路径中有空格%20的问题
                filePath = URLDecoder.decode(filePath, Encoding.UTF8);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    /**
     * 取得文件后缀
     *
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }


    /**
     * 获得类的基路径，打成jar包也可以正确获得路径
     *
     * @return
     */
    public static String getClassPath() throws URISyntaxException {
        Path path = Paths.get(FileUtil.class.getClassLoader().getResource("/").toURI());
        return path.toAbsolutePath().toString();
    }


    /**
     * 获取安全路径
     *
     * @param filePath
     * @return
     */
    public static String getSafePath(String filePath) {
        filePath = filePath.replace("../", "");
        return filePath;
    }


    /**
     * 根据文件名创建唯一文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Path createFilePath(String savePath, String fileName, String userId) throws IOException {
        Date date = new Date();
        Path newFile = Paths.get(savePath,
                "web",
                userId, DateUtil.getFullYear(date),
                DateUtil.getMonth(date) + DateUtil.getDay(date),
                R.UU16() + "_" + fileName);
        //判断文件夹是否存在
        if (!newFile.getParent().toFile().exists()) {
            Files.createDirectories(newFile.getParent());
        }
        do {
            //随机一个新文件名字
            newFile = Paths.get(newFile.getParent().toString() + File.separator + R.UU16() + "_" + fileName);
        } while (newFile.toFile().exists());
        //如果文件存在
        Files.createFile(newFile);
        return newFile;
    }


    /**
     * i/o进行读取文件
     *
     * @return fileContent读出的内容
     */
    public static String readFile(String filePath) {
        String fileContent = "";
        try {
            File f = new File(filePath);
            if (f.isFile() && f.exists()) {
                try (InputStreamReader read = new InputStreamReader(new FileInputStream(f), Encoding.UTF8);
                     BufferedReader reader = new BufferedReader(read)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContent += line + "\n";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    /**
     * i/o写入文件
     *
     * @param content   写入文件内容
     * @param writePath 要写入的文件名路径
     */
    public static void writeFile(String content, String writePath,
                                 String charCoder) {
        try {
            File file = new File(writePath);
            try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), charCoder);
                 BufferedWriter reader = new BufferedWriter(osw)) {
                reader.write(content);
                osw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * i/o写入文件
     *
     * @param content 写入文件内容
     * @param path    要写入的文件
     */
    public static void writeFile(String content, Path path, String charCoder) {
        try {
            try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(path.toFile()), charCoder);
                 BufferedWriter reader = new BufferedWriter(osw)) {
                reader.write(content);
                osw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建文件夹
     *
     * @param path 　路径
     */
    public static void createFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 删除指定文件夹 如果文件夹里面存在文件夹就进行递归，删除规则是从里面开始先删除
     *
     * @param folderPath 文件夹路径
     */
    public static void delFolders(String folderPath) {
        // 删除完里面所有内容
        File file = new File(folderPath);
        // 如果路径本身就是一个文件就直接删除
        if (!file.isDirectory()) {
            file.delete();
            return;
        }
        // 检查文件夹里面是否存在文件夹
        File[] tempList = file.listFiles();
        if (tempList != null && tempList.length > 0) {
            for (File tmpFile : tempList) {
                if (tmpFile.isDirectory()) {
                    // 递归删除
                    delFolders(tmpFile.getPath());
                } else {
                    tmpFile.delete();
                }
            }
        } else {
            file.delete();
        }
        delFolders(file.getPath());
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     * @return
     */
    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 以当前程序目录为根目录写临时文件
     *
     * @param content
     */
    public static Path writeTmpFile(String content) throws IOException {
        Path path = createTempFile();
        FileUtil.writeFile(content, path, Encoding.UTF8);
        return path;
    }


    /**
     * 以当前程序目录为根目录写临时文件
     *
     * @param bytes
     */
    public static Path writeTmpFile(byte[] bytes) throws IOException {
        Path path = createTempFile();
        FileUtil.writeFile(bytes, path);
        return path;
    }

    private static void writeFile(byte[] bytes, Path path) {
        try (FileOutputStream out = new FileOutputStream(path.toFile())) {
            out.write(bytes);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 以当前程序目录为根目录写临时文件
     */
    public static Path createTempFile(String fileName) throws IOException {
        Path newFile;
        int max = 20;
        do {
            if (max > 0) {
                max--;
            } else {
                throw new RuntimeException("文件创建失败！！！");
            }
            newFile = Paths.get(System.getProperty("java.io.tmpdir"), "NutzFw", R.UU16(), fileName);
        } while (newFile.toFile().exists());
        if (!newFile.getParent().toFile().exists()) {
            Files.createDirectory(newFile.getParent());
        }
        Files.createFile(newFile);
        return newFile;
    }

    /**
     * 以当前程序目录为根目录写临时文件
     */
    public static Path createTempFile() throws IOException {
        return createTempFile(R.UU16() + ".tmp");
    }

    /**
     * 创建临时目录
     *
     * @param folderName
     */
    public static String createTempFolder(String folderName) throws IOException {
        Path newFile = null;
        do {
            //如果文件存在
            newFile = Paths.get(System.getProperty("java.io.tmpdir"), R.UU16(), folderName);
        } while (newFile.toFile().exists());
        Files.createDirectories(newFile);
        return newFile.toFile().toString();
    }


    /**
     * 以当前程序目录为根目录读取临时文件
     *
     * @param fileName
     * @return
     */
    public static String readTmpFile(String fileName) {
        // 获取程序当前路径
        String strDir = System.getProperty("java.io.tmpdir");
        // 将路径分隔符更换
        String filepath = strDir + File.separatorChar + fileName + ".tmp";
        return FileUtil.readFile(filepath);
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return
     */
    public static String getFilePrefix(String fileName) {
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return prefix;
    }


    public static void createNewFile(File file) {
        try {
            if (!file.exists()) {
                org.nutz.lang.Files.createNewFile(file);
            }
        } catch (IOException e) {
            log.error("创建文件失败", e);
        }
    }

    public static void createNewFile(String path) {
        createNewFile(new File(path));
    }

    /**
     * 子节数组 写入文件
     *
     * @param
     * @throws Exception
     */
    public static boolean writeFileToByte(String path, byte[] bytes) {
        File file = new File(path);
        createNewFile(file);
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将这个 filePath 路径的文件 写入文件
     *
     * @param file
     * @param filePath
     * @return
     */
    public static boolean writeFileToByte(File file, String filePath) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            byte[] bytes = fileToByte(filePath);
            out.write(bytes);
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读文件到字节数组中
     *
     * @param filePath
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) {
        FileInputStream is = null;
        try {
            File file = new File(filePath);
            byte[] dist = null;
            if (file.exists()) {
                is = new FileInputStream(file);
                dist = new byte[is.available()];
                is.read(dist);
            }
            return dist;
        } catch (Exception e) {
            log.error("IO异常", e);
            return new byte[0];
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }

    }

    public static boolean zipFiles(List<FileZipDTO> srcfile, OutputStream outputStream) {
        byte[] buf = new byte[1024];
        try (ZipOutputStream zipStream = new ZipOutputStream(outputStream)) {
            // 支持中文
            zipStream.setEncoding("GBK");
            for (FileZipDTO file : srcfile) {
                try (FileInputStream in = new FileInputStream(file.getPath().toFile())) {
                    // 压缩条目不是具体独立的文件，而是压缩包文件列表中的列表项，称为条目，就像索引一样
                    ZipEntry zipEntry = new ZipEntry(file.getFileName());
                    // 定位到该压缩条目位置，开始写入文件到压缩包中
                    zipStream.putNextEntry(zipEntry);
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        zipStream.write(buf, 0, len);
                    }
                    zipStream.closeEntry();
                }
            }
            zipStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 压缩文件
     *
     * @param srcfile File[] 需要压缩的文件列表
     * @param zipfile File 压缩后的文件
     * @author
     */
    public static boolean zipFiles(List<File> srcfile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipfile));
            // 支持中文
            zipStream.setEncoding("GBK");
            for (File file : srcfile) {
                try (FileInputStream in = new FileInputStream(file)) {
                    // 压缩条目不是具体独立的文件，而是压缩包文件列表中的列表项，称为条目，就像索引一样
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    // 定位到该压缩条目位置，开始写入文件到压缩包中
                    zipStream.putNextEntry(zipEntry);
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        zipStream.write(buf, 0, len);
                    }
                    zipStream.closeEntry();
                }
            }
            zipStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据输入的文件与输出流对文件进行打包
     *
     * @param inputFile
     * @param ouputStream
     */
    private static void zipFile(File inputFile, ZipOutputStream ouputStream) {
        if (inputFile.exists()) {
            //如果是目录的话这里是不采取操作的， 至于目录的打包正在研究中
            if (inputFile.isFile()) {
                try (FileInputStream in = new FileInputStream(inputFile);
                     BufferedInputStream bins = new BufferedInputStream(in, 512)) {
                    ZipEntry entry = new ZipEntry(inputFile.getName());
                    ouputStream.putNextEntry(entry);
                    // 向压缩文件中输出数据
                    int nNumber;
                    byte[] buffer = new byte[1024];
                    while ((nNumber = bins.read(buffer)) != -1) {
                        ouputStream.write(buffer, 0, nNumber);
                    }
                } catch (IOException e) {
                    log.error("IO异常", e);
                }
            } else {
                File[] files = inputFile.listFiles();
                for (int i = 0; i < files.length; i++) {
                    zipFile(files[i], ouputStream);
                }
            }
        }
    }

    /**
     * 清空文件和文件目录
     *
     * @param f
     */
    public static void clean(File f) throws Exception {
        String[] cs = f.list();
        if (null == cs || cs.length <= 0) {
            System.out.println("delFile:[ " + f + " ]");
            boolean isDelete = f.delete();
            if (!isDelete) {
                System.out.println("delFile:[ " + f.getName() + "文件删除失败！" + " ]");
                throw new Exception(f.getName() + "文件删除失败！");
            }
        } else {
            for (String cn : cs) {
                String cp = f.getPath() + File.separator + cn;
                File f2 = new File(cp);
                if (f2.exists() && f2.isFile()) {
                    System.out.println("delFile:[ " + f2 + " ]");
                    boolean isDelete = f2.delete();
                    if (!isDelete) {
                        System.out.println("delFile:[ " + f2.getName() + "文件删除失败！" + " ]");
                        throw new Exception(f2.getName() + "文件删除失败！");
                    }
                } else if (f2.exists() && f2.isDirectory()) {
                    clean(f2);
                }
            }
            System.out.println("delFile:[ " + f + " ]");
            boolean isDelete = f.delete();
            if (!isDelete) {
                System.out.println("delFile:[ " + f.getName() + "文件删除失败！" + " ]");
                throw new Exception(f.getName() + "文件删除失败！");
            }
        }
    }

    /**
     * 文件打包
     *
     * @param path       原文件路径
     * @param returnPath 压缩后文件路径
     * @param isDrop     是否删除原文件:true删除、false不删除
     * @return
     * @throws Exception
     */
    public static String generateZip(String path, String returnPath, String zipName, Boolean isDrop) throws
            Exception {
        List<File> files = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("原因文件：[ " + file.getPath() + " ]不存在，打包失败！");
        }
        if (file.exists() && file.isFile()) {
            files.add(file);
        } else if (file.exists() && file.isDirectory()) {
            File[] fil = new File(path).listFiles();
            for (int i = 0; i < fil.length; i++) {
                files.add(fil[i]);
            }
        }
        File outFile = new File(returnPath);
        File fileMidir = new File(outFile.getParent());
        if (!fileMidir.exists()) {
            boolean isMkDirs = fileMidir.mkdirs();
            if (!isMkDirs) {
                throw new Exception("存放压缩文件目录：[ " + outFile.getName() + " ]，创建目录失败！");
            }
        }
        if (!outFile.exists() || !outFile.isFile()) {
            if (!outFile.isFile()) {
                outFile = new File(outFile.getPath() + File.separator + zipName + ".zip");
            }
            boolean isMkDirs = outFile.createNewFile();
            if (!isMkDirs) {
                throw new Exception("压缩文件目录：[ " + outFile.getName() + " ]，创建压缩文件失败！");
            }
        }
        // 创建文件输出流
        FileOutputStream fous = new FileOutputStream(outFile);
        /**
         * 打包的方法我们会用到ZipOutputStream这样一个输出流, 所以这里我们把输出流转换一下
         */
        ZipOutputStream zipOut = new ZipOutputStream(fous);
        try {
            /**
             * 这个方法接受的就是一个所要打包文件的集合， 还有一个ZipOutputStream
             */
            if (files != null && files.size() > 0) {
                zipFile(files, zipOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            zipOut.close();
            fous.close();
            if (isDrop) {
                clean(new File(path));
            }
        }
        return outFile.getPath();
    }

    /**
     * 把接受的全部文件打成压缩包
     *
     * @param files
     * @param outputStream
     */
    private static void zipFile(List<File> files, ZipOutputStream outputStream) {
        for (File file : files) {
            zipFile(file, outputStream);
        }
    }

    /**
     * 关闭流
     */
    public static void closeStream(InputStream is, OutputStream os) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * inputStream转outputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static ByteArrayOutputStream parse(InputStream in) throws Exception {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            swapStream.write(ch);
        }
        return swapStream;
    }

    /**
     * outputStream转inputStream
     *
     * @param out
     * @return
     * @throws Exception
     */
    public static ByteArrayInputStream parse(OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }

    /**
     * 清空文件夹
     */
    public static void clearFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }
        String[] filePathes = folder.list();
        if (filePathes == null || filePathes.length == 0) {
            return;
        }
        for (String filePath : filePathes) {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    clearFolder(filePath);
                } else {
                    file.delete();
                }
            }
        }
    }

    /**
     * inputStream转String
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static String parseString(InputStream in) throws Exception {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            swapStream.write(ch);
        }
        return swapStream.toString();
    }

    /**
     * OutputStream 转String
     *
     * @param out
     * @return
     * @throws Exception
     */
    public static String parseString(OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream.toString();
    }

    /**
     * String转inputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static ByteArrayInputStream parseInputStream(String in) throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream(in.getBytes());
        return input;
    }

    /**
     * String 转outputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static ByteArrayOutputStream parseOutputStream(String in) throws Exception {
        return parse(parseInputStream(in));
    }

    /**
     * 字符串转文件
     *
     * @param fileContent
     * @param path
     * @throws IOException
     */
    public static void strToFile(String fileContent, String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        try (
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, Encoding.UTF8);
        ) {
            osw.write(fileContent);
        } finally {

        }
    }

    /**
     * 拷贝文件
     *
     * @throws IOException
     */
    public static void copyFile(File formFile, File toPathFile) throws IOException {
        try (FileInputStream fi = new FileInputStream(formFile);
             FileOutputStream fo = new FileOutputStream(toPathFile);
             FileChannel in = fi.getChannel();
             FileChannel out = fo.getChannel()
        ) {
            //连接两个通道，并且从in通道读取，然后写入out通道
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileType(String fileName) {
        if (fileName == null) {
            fileName = "文件名为空！";
            return fileName;
        } else {
            // 获取文件后缀名并转化为写，用于后续比较
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
            // 创建图片类型数组
            String[] img = {"bmp", "jpg", "jpeg", "png", "tiff", "gif", "pcx", "tga", "exif", "fpx", "svg", "psd",
                    "cdr", "pcd", "dxf", "ufo", "eps", "ai", "raw", "wmf"};
            for (int i = 0; i < img.length; i++) {
                if (img[i].equals(fileType)) {
                    return "pic";
                }
            }
            // 创建文档类型数组
            String[] document = {"txt", "doc", "docx", "xls", "htm", "html", "jsp", "rtf", "wpd", "pdf", "ppt"};
            for (int i = 0; i < document.length; i++) {
                if (document[i].equals(fileType)) {
                    return "doc";
                }
            }
            // 创建视频类型数组
            String[] video = {"mp4", "avi", "mov", "wmv", "asf", "navi", "3gp", "mkv", "f4v", "rmvb", "webm"};
            for (int i = 0; i < video.length; i++) {
                if (video[i].equals(fileType)) {
                    return "video";
                }
            }
            // 创建音乐类型数组
            String[] music = {"mp3", "wma", "wav", "mod", "ra", "cd", "md", "asf", "aac", "vqf", "ape", "mid", "ogg",
                    "m4a", "vqf"};
            for (int i = 0; i < music.length; i++) {
                if (music[i].equals(fileType)) {
                    return "music";
                }
            }
        }
        return "else";
    }

    /**
     * 输入流转换为文件
     *
     * @param ins  输入流
     * @param file 文件
     * @return
     */
    public static File inputstreamtofile(@NonNull InputStream ins, File file) {
        try (OutputStream os = new FileOutputStream(file)) {
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

        } catch (Exception e) {
            log.error("IO异常", e);
        } finally {
            try {
                ins.close();
            } catch (IOException e) {

            }
        }
        return file;
    }

    public static String getMD5(File file) {
        try {
            return DigestUtils.md5Hex(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 从输入流中读取内容
     *
     * @param is InputStream 输入流对象
     * @return String
     * @throws Exception
     */
    public String readFromIS(InputStream is) throws Exception {
        try {
            String strRtn = "";
            int length = is.available();
            byte[] buf = new byte[length];
            while ((is.read(buf, 0, length)) != -1) {
                strRtn = strRtn + new String(buf, 0, length, Encoding.UTF8);
            }
            return strRtn;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            is.close();
        }
    }

}
