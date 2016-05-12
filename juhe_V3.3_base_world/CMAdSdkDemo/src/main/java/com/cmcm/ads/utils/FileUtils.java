
package com.cmcm.ads.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.channels.FileLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    private static final String TAG = "FileUtils";
    
    static class FileObject {
        boolean mRead;
        boolean mInited = false;
        File mFile;
        FileOutputStream mOut;
        
        public FileObject(String path, boolean readOrWrite) {
            mRead = readOrWrite;
            mFile = new File(path);
        }
        
        private boolean checkOpen() {
            if (mInited) 
                return true;
            
            mInited = true;
            if (mRead) {
            } else {
                try {
                    mOut = new FileOutputStream(mFile, true);
                } catch (Exception e) {
                    Log.e(TAG, "open fail " + mFile.getAbsolutePath() + " err: " + e.getMessage() );
                }
            }
            return true;
        }
        
        public int write(String str) {
            checkOpen();
            FileLock lock = null;
            
            int count = -1;
            try {
                lock = mOut.getChannel().lock();
                mOut.write(str.getBytes());
                count = str.length();
            } catch (Exception e) {
                Log.e(TAG, "open fail " + mFile.getAbsolutePath() + " err: " + e.getMessage() );
            } finally {
                if (lock != null) {
                    try {
                        lock.release();
                    } catch (IOException e) {
                    }
                }
            }
            return count;
        }
        
        public String read() {
            if(!mRead)
                return null;
            return null;
        }
        
        public void close() {
            if (mOut != null) {
                try {
                    mOut.close();
                    mOut = null;
                } catch (IOException e) {
                }
            }
        }
    }
    
    
    public static String getPath(String uri)
    {
      if (TextUtils.isEmpty(uri))
        return null;
      if ((uri.startsWith("file://")) && (uri.length() > 7))
        return Uri.decode(uri.substring(7));
      return Uri.decode(uri);
    }

    /**
     * 删除文件夹
     * @param f
     */
    public static void deleteDir(File f) {
        if (f != null && f.exists() && f.isDirectory()) {
            File[] files = null;
            try {
                files = f.listFiles();
            } catch (Throwable e) {
                Log.w(TAG, "listFiles Exception: " + e);
            }
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory())
                        deleteDir(file);
                    file.delete();
                }
            }
            f.delete();
        }
    }

    /**
     * 删除文件夹中指定日期之前的所有文件
     * @param folder 目标文件夹
     * @param millionSecondsOfDays 指定天数的毫秒值（最后修改时间差值大于该值的文件将被删除）
     */
    public static void deleteFilesBeforeDays(File folder, long millionSecondsOfDays) {
        if (folder != null && folder.exists() && folder.isDirectory()) {
            File[] files = null;
            try {
                files = folder.listFiles();
            } catch (Throwable e) {
                Log.w(TAG, "listFiles Exception: " + e);
            }
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.equals(folder) || file.equals(folder.getParentFile())) { 
                    continue;
                }
                
                if (file.isDirectory()) {
                    deleteFilesBeforeDays(file, millionSecondsOfDays);
                    
                    if (file.list().length == 0) {
                        file.delete();
                    }
                } else if (file.isFile()) {
                    if (file.lastModified() + millionSecondsOfDays < System.currentTimeMillis()) {
                        file.delete();
                    }
                }
            }
        }
    }

    public static String readStringFromFile(String filename, String encoding)
            throws IOException {
        File file = new File(filename);
        return readStringFromFile(file, encoding);
    }
    
    public static long getFileLastModifiedTime(String strFilename) {
        File file = new File(strFilename);
        return file.lastModified();
    }
    
    public static String readStringFromFile(File file, String encoding)
            throws IOException {
        FileInputStream in = null;
        FileLock lock = null;
        try {
            in = new FileInputStream(file);

            lock = in.getChannel().lock(0, Integer.MAX_VALUE, true);
            return readStringFromStream(in, encoding);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file not found: path=" + file.getAbsolutePath());
            return null;
        } finally {
        	if (lock != null) {
        		try {
        			lock.release();
        		} catch (IOException e) {
        		}
        	}
            if (in != null)
                in.close();
        }
    }

    public static String readStringFromZip(String zipfile, String name, String encoding) throws IOException {
        ZipFile zip = null;
        String result = null;
        try {
            zip = new ZipFile(zipfile);

            ZipEntry entry = zip.getEntry(name);
            if (entry != null && !entry.isDirectory()) {
                InputStream is = null;
                try {
                    is = zip.getInputStream(entry);
                    result = readStringFromStream(is, encoding);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }

            }
        } finally {
            if (zip != null) {
                zip.close();
            }
        }
        return result;
    }

    public static String readStringFromStream(InputStream in, String encoding) throws IOException {
        Reader reader = null;
        StringBuilder builder = null;
        try {
            reader = new InputStreamReader(in, encoding);
            builder = new StringBuilder();
            char[] buf = new char[512];
            int len;
            while ((len = reader.read(buf)) > 0) {
                builder.append(buf, 0, len);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return builder.toString();
    }
    
//    public static InputStream openFileStream(String filepath) {
//        try {
//            File file = new File(filepath);
//            if (file.exists() && file.isFile() && file.canRead()) {
//                return new FileInputStream(file);
//            }
//        } catch (Exception e) {
//            KLog.e(TAG, "file not found: path=" + filepath, e);
//            return null;
//        } 
//        return null;
//    }
    
    public static Object deserializeFromFile(File file) {
        if (file == null || !file.exists())
            return null;

        Object o = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        FileLock lock = null;

        try {
            fis = new FileInputStream(file);
            lock = fis.getChannel().lock(0L, Long.MAX_VALUE, true);
            ois = new ObjectInputStream(fis);
            o = ois.readUnshared();
            return o;
        } catch (FileNotFoundException e) {
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
        } catch (Exception e) {
            file.delete();
        } catch (OutOfMemoryError oom) {
        } catch (Throwable e) {
            // java.lang.NoSuchMethodError: create
            // at java.io.ObjectInputStream.<clinit>(ObjectInputStream.java:2034)
        } finally {
            if (lock != null)
                try {
                    lock.release();
                } catch (IOException e) {
                }
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
            }
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static boolean serializeToFile(Serializable o, File file) {
        if (o == null || file == null)
            return false;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        FileLock lock = null;

        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            lock = fos.getChannel().lock();

            oos.writeUnshared(o);
            oos.flush();
            fos.flush();
            return true;
        } catch (Exception e) {
        } catch (OutOfMemoryError oom) {
        } finally {
            if (oos != null)
                try {
                    oos.reset();
                } catch (IOException e) {
                }
            if (lock != null)
                try {
                    lock.release();
                } catch (IOException e) {
                }
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }
        return false;
    }
    
    public static void writeStringToFile(File target, String in, String encoding) throws IOException {
        if (in == null) {
            return;
        }
        writeBytesToFile(target, in.getBytes(encoding));
    }
    
    public static void writeBytesToFile(File target, byte[] bytes) throws IOException {
        if (bytes == null) {
            return;
        }
        ByteArrayInputStream bais = null;
        FileOutputStream fos = null;
        FileLock lock = null;
        
        byte[] buffer = new byte[4096];
        int count = -1;
        try {
            bais = new ByteArrayInputStream(bytes);
            fos = new FileOutputStream(target);
            
            lock = fos.getChannel().lock();
            while ((count = bais.read(buffer)) != -1) {
            	fos.write(buffer, 0, count);
            }
            fos.flush();
        } finally {
            if (lock != null) {
            	try {
            		lock.release();
            	} catch (IOException e) {
            	}
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (bais != null) {
                try {
                	bais.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {

        }

        return bitmap;
    }

    public static String readStringFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        String str = null;
        try {
            istr = assetManager.open(strName);
            str = readStringFromStream(istr, "utf-8");
        } catch (IOException e) {

        }

        return str;
    }



    public static boolean pathFileExists(String filepath) {
		File file = new File(filepath);
		return file.exists();
	}

    /**
     * 根据文件路径获取文件头信息
     *
     * @param filePath
     *            文件路径
     * @return 文件头信息
     */
    public static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[12];
        /*
         * int read() 从此输入流中读取一个数据字节。 int read(byte[] b) 从此输入流中将最多 b.length
         * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
         * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
         */
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    /**
     * 将要读取文件头信息的文件的byte数组转换成string类型表示
     *
     * @param src
     *            要读取文件头信息的文件的byte数组
     * @return 文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        System.out.println(builder.toString());
        return builder.toString();
    }
}
