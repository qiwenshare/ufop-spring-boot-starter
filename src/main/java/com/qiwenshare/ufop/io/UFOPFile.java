//package com.qiwenshare.ufop.io;
//
//import com.qiwenshare.ufop.util.UFOPUtils;
//import org.apache.commons.io.FilenameUtils;
//
///**
// * @author MAC
// * @version 1.0
// * @description: TODO
// * @date 2022/4/21 12:08
// */
//public class UFOPFile {
//
//    private final String path;
//    public static final String separator = "/";
//    private boolean isDirectory;
//
//    public UFOPFile(String pathname, boolean isDirectory) {
//        if (pathname == null) {
//            throw new NullPointerException();
//        }
//        this.path = formatPath(pathname);
//        this.isDirectory = isDirectory;
//    }
//
//    public UFOPFile(String parent, String child, boolean isDirectory) {
//        if (child == null) {
//            throw new NullPointerException();
//        }
//        if (parent != null) {
//            String parentPath = separator.equals(formatPath(parent)) ? "" : formatPath(parent);
//            this.path = parentPath + separator + formatPath(child);
//        } else {
//            this.path = formatPath(child);
//        }
//        this.isDirectory = isDirectory;
//    }
//
//
//    public String getParent() {
//        if (separator.equals(this.path)) {
//            return null;
//        }
//        if (!this.path.contains("/")) {
//            return null;
//        }
//        int index = path.lastIndexOf(separator);
//        if (index == 0) {
//            return separator;
//        }
//        return path.substring(0, index);
//    }
//
//    public UFOPFile getParentFile() {
//        String parentPath = this.getParent();
//        return new UFOPFile(parentPath, true);
//    }
//
//    public String getName() {
//        int index = path.lastIndexOf(separator);
//        if (!path.contains(separator)) {
//            return path;
//        }
//        return path.substring(index + 1);
//    }
//
//    public String getExtendName() {
//        return FilenameUtils.getExtension(getName());
//    }
//
//    public String getNameNotExtend() {
//        return FilenameUtils.removeExtension(getName());
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public boolean isDirectory() {
//       return isDirectory;
//    }
//
//    public boolean isFile() {
//        return !isDirectory;
//    }
//
//    public static void main(String[] args) {
//        int index = "/sdf".lastIndexOf(separator);
//        System.out.println(index);
//    }
//}
