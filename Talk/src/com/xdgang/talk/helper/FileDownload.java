
package com.xdgang.talk.helper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileDownload {
    public interface IFileDownloadCallback {
        public static final int ERR_NET_CUT = 0;

        public static final int ERR_USER_CANCEL = 1;

        void onPercentage(int dID, long len, int curr);

        void onComplete(int dID, String url, String localFile);

        void onFaild(int dID, float percent, int errCode);
    }

    static FileDownload _inst = null;

    public static FileDownload instance() {
        if (_inst == null) {
            _inst = new FileDownload();
        }
        return _inst;
    }

    Map<String, DownloadThread> downloadThreads;

    int taskID;

    String downloadDir;

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String dir) {
        downloadDir = dir;
        File file = new File(downloadDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    FileDownload() {
        downloadDir = Environment.getExternalStorageDirectory() + "/contDownloadDir/";
        File file = new File(downloadDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        downloadThreads = new HashMap<String, DownloadThread>();
        taskID = 1;
    }

    public synchronized int Download(String url, IFileDownloadCallback callback, String fileName) {
        String strUrl = url;
//        strUrl = strUrl.toLowerCase();
        int retID = taskID;
        synchronized (downloadThreads) {
            DownloadThread t = downloadThreads.get(strUrl);
            if (t != null) {
                t.startDownload();
                return t.dID;
            }

            t = new DownloadThread();
            t.setParemet(strUrl, fileName, callback, retID);
            taskID++;
            downloadThreads.put(strUrl, t);
            t.startDownload();
        }
        return retID;
    }

    public boolean isFileExist(String fileName) {
        String path = downloadDir + fileName;
        File f = new File(path);
        return f.exists();
    }

    public void DeleteFile(String fileName) {
        String file = downloadDir + fileName;
        synchronized (downloadThreads) {
            Iterator it = downloadThreads.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                DownloadThread value = (DownloadThread) entry.getValue();
                if (value.fileName.endsWith(file))
                    return;
            }
            try {
                File f = new File(file);
                if (f.exists())
                    f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopDownload(String url) {
        synchronized (downloadThreads) {
            String strUrl = url;
            DownloadThread t = downloadThreads.get(strUrl);
            if (t == null)
                return;
            t.stopDownload();
        }
    }

    void clearTask(String url) {
        synchronized (downloadThreads) {
            String strUrl = url;
            DownloadThread t = downloadThreads.get(strUrl);
            if (t == null)
                return;
            downloadThreads.remove(strUrl);
        }
    }

    public float getPercentage(String url) {
        return 0;
    }

    public class DownloadThread extends Thread {
        public IFileDownloadCallback callback;

        public String urlStr;

        public int dID;

        public long fileSize;

        public String fileName;

        public String fileNameWithoutPath;

        public boolean bRun = false;

        public void startDownload() {
            if (bRun)
                return;
            bRun = true;
            this.start();
        }

        public void stopDownload() {
            if (!bRun)
                return;
            bRun = false;
            try {
                this.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setParemet(String url, String filename, IFileDownloadCallback iCallBack, int id) {
            urlStr = url;
            callback = iCallBack;
            dID = id;
            fileNameWithoutPath = filename;
            fileName = downloadDir + filename;
        }

        static final int BUFFER_SIZE = 10240;

        private File iconTmpFile = null;

        private File iconFile = null;

        public void run() {
            int errCode = IFileDownloadCallback.ERR_NET_CUT;
            HttpClient httpClient = null;
            int curPosition = 0;
            try {
                iconFile = new File(downloadDir, fileNameWithoutPath);
                iconTmpFile = new File(downloadDir, fileNameWithoutPath + "_tmp");
                iconTmpFile.createNewFile();

                HttpGet httpGet = new HttpGet(urlStr);
                httpGet.addHeader("Range", "bytes=" + iconTmpFile.length() + "-");
                httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                FileOutputStream fos = null;
                InputStream is = null;

                HttpResponse httpResponse = httpClient.execute(httpGet);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
                    httpEntity = httpResponse.getEntity();
                    fileSize = httpEntity.getContentLength();// 请求文件长度
                    if (httpEntity != null) {
                        fos = new FileOutputStream(iconTmpFile, true);
                        is = httpEntity.getContent();
                        byte[] buff = new byte[1024];
                        int recved = 0;
                        while ((recved = is.read(buff, 0, buff.length)) != -1 && bRun) {
                            fos.write(buff, 0, recved);
                            curPosition += recved;
                            float percent = (float) curPosition / (float) fileSize;
                            callback.onPercentage(dID, fileSize, curPosition);
                        }
                        fos.flush();
                        fos.close();
                        is.close();
                        iconTmpFile.renameTo(iconFile);
                    }
                    if (httpEntity != null) {
                        httpEntity.consumeContent();
                    }
                }

            } catch (Exception e) {
                errCode = IFileDownloadCallback.ERR_NET_CUT;
                e.printStackTrace();
            } finally {
                if (httpClient != null) {
                    httpClient.getConnectionManager().shutdown();
                }
            }

            if (!bRun)
                errCode = IFileDownloadCallback.ERR_USER_CANCEL;
            if (curPosition == fileSize) {
                callback.onComplete(dID, urlStr, fileName);
            } else {
                float percent = (float) curPosition / (float) fileSize;
                callback.onPercentage(dID, fileSize, curPosition);
                callback.onFaild(dID, percent, errCode);
            }
            clearTask(urlStr);
        }

        public float getPercentage() {
            return 0;
        }
    }

}
