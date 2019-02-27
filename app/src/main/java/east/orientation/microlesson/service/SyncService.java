package east.orientation.microlesson.service;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import east.orientation.microlesson.app.MicroApp;
import east.orientation.microlesson.greendao.helper.DatabaseUtil;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.dao.Project;
import east.orientation.microlesson.local.model.FileInfo;
import east.orientation.microlesson.rxbus.RxBus;
import east.orientation.microlesson.rxbus.Subscribe;
import east.orientation.microlesson.rxbus.ThreadMode;
import east.orientation.microlesson.rxbus.event.ResponseMessage;
import east.orientation.microlesson.rxbus.event.SyncMessage;
import east.orientation.microlesson.rxbus.event.UpdateFilesMessage;
import east.orientation.microlesson.rxbus.event.UpdateUserMessage;
import east.orientation.microlesson.socket.SocketListener;
import east.orientation.microlesson.socket.SocketManager;
import east.orientation.microlesson.update.DownloadInfo;
import east.orientation.microlesson.update.UploadInfo;
import east.orientation.microlesson.utils.FileUtil;
import east.orientation.microlesson.utils.SharePreferenceUtil;

/**
 * Created by ljq on 2018/6/5.
 */

public class SyncService extends Service {
    private static final String TAG = "SyncService";
    private static final int DEFAULT_STREAM_PACKAGE = 1024 * 1024;
    private boolean isCheck;
    private String mAccount;
    private String mPassword;
    private String accountDir = FileUtil.getCacheFolder(Common.SAVE_DIR_NAME) + File.separator + mAccount;

    private ExecutorService mUpPoolExecutor;
    private ExecutorService mDownPoolExecutor;
    private WriteToFileThread mWriteToFileThread;
    private Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO
        RxBus.getDefault().register(this);
        //
        SocketManager.getInstance().registerSocketListener(new SocketListener() {
            @Override
            public void connected() {
                SocketManager.getInstance().login(mAccount, mPassword);
            }

            @Override
            public void connectFailed() {

            }

            @Override
            public void disconnected() {

            }
        });
        mHandler = new Handler();
        getProviderLogin();
        // 开启上传线程 Runtime.getRuntime().availableProcessors()
        mUpPoolExecutor = Executors.newFixedThreadPool(3);

        // 开启下载线程
        mWriteToFileThread = new WriteToFileThread();
        mWriteToFileThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e(TAG," - SyncService onStartCommand");
        return START_NOT_STICKY;
    }

    private void getProviderLogin() {
        Uri user_uri = Uri.parse("content://east.orientation.newlanucher/user");
        // 查询
        queryUser(user_uri);
        // 为uri的数据改变注册监听器
        getContentResolver().registerContentObserver(
                user_uri, true,
                new ContentObserver(mHandler) {
                    @Override
                    public boolean deliverSelfNotifications() {

                        return super.deliverSelfNotifications();
                    }

                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);

                    }

                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        super.onChange(selfChange, uri);
                        // 查询
                        queryUser(uri);
                        // 账号变化 断开重连
                        SocketManager.getInstance().disConnect();
                    }
                });
        SocketManager.getInstance().login(mAccount, mPassword);
    }

    private void queryUser(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, new String[]{"_id,account,password,name,userId"}, null, null, null);

        if (cursor == null) return;

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String account = cursor.getString(1);
            String password = cursor.getString(2);
            String name = cursor.getString(3);
            String userId = cursor.getString(4);
            mAccount = account;
            mPassword = password;
            // 全局变量
            MicroApp.sUser.account = account;
            MicroApp.sUser.password = password;
            MicroApp.sUser.name = name;
            MicroApp.sUser.userId = userId;
            // 账户存储路径
            getAccountDir(account);
            // 存储用户信息
            SharePreferenceUtil.put(getApplicationContext(),Common.KEY_USER,MicroApp.sUser);
            //SharePreferenceUtil.put(getApplicationContext(), Common.KEY_ACCOUNT, mAccount, Common.KEY_PASSWD, mPassword);
            // TODO
            RxBus.getDefault().post(new UpdateUserMessage(name));
        }
        cursor.close();
    }

    public String getAccountDir(String account) {
        accountDir = FileUtil.getCacheFolder(Common.SAVE_DIR_NAME) + File.separator + account;
        return accountDir;
    }

    public void handleLogin(boolean isSuccess) {
        if (isSuccess) {
//            // 登录成功 查询一次所需要更新的文件
//            if (mWriteToFileThread != null && !mWriteToFileThread.isDownloading) {
//                // 查询所有文件列表
//                SocketManager.getInstance().filequery(Common.TYPE_MICRO_LESSONS, "", "");
//            }
            // 存储账号密码
            SharePreferenceUtil.put(getApplicationContext(), Common.KEY_ACCOUNT, mAccount, Common.KEY_PASSWD, mPassword);
        }
    }

    public void handleGetIdEntity() {

    }

    public void handleFileQuery(String response) {
//        List<FileInfo> files = new ArrayList<>();
//
//        // 第3个逗号的位置
//        int indexT = response.indexOf(",", response.indexOf(",", response.indexOf(",") + 1) + 1);
//        if (indexT == response.length() - 1) {// 判断是否有列表
//            return;
//        }
//        String listStr = response.substring(indexT + 1, response.length() - 1);
//        String[] array = listStr.split(";");
//        FileInfo fileInfo;
//        for (int i = 0; i < array.length; i++) {
//            String[] arrayContent = array[i].split(",");
//            fileInfo = new FileInfo();
//            fileInfo.fileName = arrayContent[0];
//            fileInfo.fileSize = Long.valueOf(arrayContent[1]);
//            if (fileInfo.fileName != null && !TextUtils.isEmpty(fileInfo.fileName)) {
//                files.add(fileInfo);
//            }
//        }
//
//        if (!isCheck) {
//            // 查询账户文件夹中文件
//            //List<File> dirList = FileUtil.listFilesInDir(accountDir);
//            List<Project> projects = DatabaseUtil.getProjects(MicroApp.sUser.userId);
//            // 检查本地不包含文件
//            for (int i = 0; i < files.size(); i++) {
//                File file = new File(accountDir + File.separator + files.get(i).fileName);
//
//                if (DatabaseUtil.getProject(MicroApp.sUser.userId,file.getName()) == null) {
//                    // 如果本地不包含 则下载
//                    SocketManager.getInstance().filedown(Common.TYPE_MICRO_LESSONS, files.get(i).fileName, 0, DEFAULT_STREAM_PACKAGE,MicroApp.sUser.userId);
//                }
//
//            }
//
//            FileInfo info;
//            for (int i = 0; i < projects.size(); i++) {
//                info = new FileInfo();
//                info.fileName = projects.get(i).getFileName();
//                info.fileSize = new File(projects.get(i).getFilePath()).length();
//                if (!files.contains(info)) {
//                    addToUploadQueue(projects.get(i));
//                }
//            }
//            isCheck = true;
//        }
    }

    public void handleFileDown(boolean isSuccess, String response, byte[] bytes) {
        if (isSuccess) {
            int indexS = response.indexOf(",", response.indexOf(",") + 1); // 第二个逗号的位置
            if (indexS < 0 || indexS == response.length() - 1) {// 判断是否有列表

                Log.i(TAG, "no download ");
            } else {
                try {
                    int a0 = response.indexOf(",");
                    int a1 = response.indexOf(",", a0 + 1);
                    int a2 = response.indexOf(",", a1 + 1);
                    int a3 = response.indexOf(",", a2 + 1);
                    int a4 = response.indexOf(",", a3 + 1);
                    int a5 = response.indexOf(",", a4 + 1);
                    int a6 = response.indexOf(",", a5 + 1);

                    DownloadInfo downloadInfo = new DownloadInfo();
                    downloadInfo.type = response.substring(a1 + 1, a2);
                    downloadInfo.fileName = response.substring(a2 + 1, a3);
                    downloadInfo.size = Long.valueOf(response.substring(a3 + 1, a4));
                    downloadInfo.offset = Long.valueOf(response.substring(a4 + 1, a5));
                    downloadInfo.length = Integer.valueOf(response.substring(a5 + 1, a6));

                    String head = response.substring(0, a6 + 1);
                    Log.i(TAG, head);
                    int len = head.getBytes("gbk").length;

                    int cLen = bytes.length - len;
                    byte[] content = new byte[cLen];
                    System.arraycopy(bytes, bytes.length - cLen, content, 0, cLen);

                    downloadInfo.content = content;
                    downloadInfo.length = downloadInfo.content.length;
                    downloadInfo.path = accountDir + File.separator + downloadInfo.fileName;

                    if (mWriteToFileThread != null) mWriteToFileThread.offer(downloadInfo);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e(TAG, "download receive not success");
        }
    }

    public void handleFileUp() {

    }

    public void handleFileDel(boolean isSuccess) {
        if (isSuccess) {
            Log.i(TAG, "删除成功");
        } else {
            Log.i(TAG, "删除失败");
        }
    }

    public void handleFileRename() {

    }

    /**
     * 上传线程
     **/
    public static class FileToServerThread implements Runnable {
        private UploadInfo mUploadInfo;
        private RandomAccessFile mRandomAccessFile;

        public FileToServerThread(UploadInfo uploadInfo) {
            this.mUploadInfo = uploadInfo;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                // 如果文件上传信息不为空 并且文件路径不为空则下一步
                if (mUploadInfo != null && !TextUtils.isEmpty(mUploadInfo.path)) {
                    File file = new File(mUploadInfo.path);
                    // 如果文件不存在 则跳过
                    if (!file.exists()) {
                        Log.d(TAG, mUploadInfo.path + "文件不存在");
                        return;
                    }
                    mRandomAccessFile = new RandomAccessFile(file, "r");

                    // 每次默认读取文件字节流包
                    byte[] buf = new byte[DEFAULT_STREAM_PACKAGE];
                    // 实际读取的字节流长度
                    int count;
                    // 移动到offset位置读取
                    mRandomAccessFile.seek(mUploadInfo.offset);

                    while ((count = mRandomAccessFile.read(buf)) != -1) {
                        ByteBuffer bb;
                        // 如果实际读取长度等于默认读取长度 则直接发送buf
                        if (count == DEFAULT_STREAM_PACKAGE) {
                            bb = ByteBuffer.allocate(buf.length);
                            bb.put(buf);
                            // 发送文件字节流
                            SocketManager.getInstance().fileup(mUploadInfo.type, mUploadInfo.fileName, mUploadInfo.size, mUploadInfo.offset, buf.length, bb);
                            // 偏移量增加发送长度
                            mUploadInfo.offset += buf.length;

                        } else if (count < DEFAULT_STREAM_PACKAGE) {
                            // 如果实际读取长度小于默认读取长度 则舍弃buf多余部分
                            byte[] content = new byte[count];

                            System.arraycopy(buf, 0, content, 0, count);
                            bb = ByteBuffer.allocate(content.length);
                            bb.put(content);
                            // 发送文件字节流
                            SocketManager.getInstance().fileup(mUploadInfo.type, mUploadInfo.fileName, mUploadInfo.size, mUploadInfo.offset, content.length, bb);
                            // 偏移量增加发送长度
                            mUploadInfo.offset += content.length;

                        }
                    }
                    // 发布
                    SocketManager.getInstance().publishFile(mUploadInfo.clazz,Common.TYPE_MICRO_LESSONS,mUploadInfo.fileName,mUploadInfo.free);
                    Log.d(TAG, mUploadInfo.fileName + "上传完成");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "error " + e);
            } finally {
                try {
                    if (mRandomAccessFile != null)
                        mRandomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 下载线程
     **/
    public static class WriteToFileThread implements Runnable {
        private Thread mThread;
        private RandomAccessFile mRandomAccessFile;
        private boolean isStop;
        private boolean isDownloading;
        private LinkedBlockingQueue<DownloadInfo> mQueue = new LinkedBlockingQueue<>();

        public List<Project> mProjects = new ArrayList<>();
        public Project mProject;

        public WriteToFileThread() {
            isStop = true;
        }

        public synchronized void start() {
            if (isStop) {
                try {
                    mThread = new Thread(this);
                    isStop = false;
                    mThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                while (!isStop) {
                    DownloadInfo downloadInfo = mQueue.take();
                    if (downloadInfo != null && !TextUtils.isEmpty(downloadInfo.path)) {
                        for (int i = 0; i < mProjects.size(); i++) {
                            if (downloadInfo.fileName.equals(mProjects.get(i).getFileName()) &&
                                    downloadInfo.size == (mProjects.get(i).getSize())) {
                                downloadInfo.userId = mProjects.get(i).getUserId();
                            }
                        }

                        File file = new File(downloadInfo.path);
                        // 如果文件不存在 则创建文件
                        if (!file.exists()) {
                            // 创建失败则跳过
                            if (!createFileByDeleteOldFile(file) || file.isDirectory()) {
                                return;
                            }
                        }
                        // 正在下载
                        isDownloading = true;

                        mRandomAccessFile = new RandomAccessFile(file, "rw");
                        // 移动到offset位置读写
                        mRandomAccessFile.seek(downloadInfo.offset);
                        // 写入文件
                        mRandomAccessFile.write(downloadInfo.content, 0, downloadInfo.content.length);
                        // 偏移增加本次获取长度
                        downloadInfo.offset += downloadInfo.length;
                        // 通知下载进度
                        if (downloadInfo.offset < downloadInfo.size) {
                            // offset 不等于总长则还没下载完成
                            int packageSize = DEFAULT_STREAM_PACKAGE;
                            if (downloadInfo.size - downloadInfo.offset < DEFAULT_STREAM_PACKAGE) packageSize = (int) (downloadInfo.size - downloadInfo.offset);
                            SocketManager.getInstance().filedown(Common.TYPE_MICRO_LESSONS, downloadInfo.fileName, downloadInfo.offset, packageSize,downloadInfo.userId);
                        } else if (downloadInfo.offset == downloadInfo.size) {
                            // offset 等于总长则下载完成
                            Project project;
                            for (int i = 0; i < mProjects.size(); i++) {
                                if (downloadInfo.fileName.equals(mProjects.get(i).getFileName())) {
                                    project = mProjects.get(i);
                                    mProjects.remove(i);
                                    project.setOwnerId(MicroApp.sUser.userId);
                                    project.setThumbnail(downloadInfo.path);
                                    project.setFilePath(downloadInfo.path);

                                    DatabaseUtil.insertProject(project);
                                    break;
                                }
                            }
                            Log.i(TAG, downloadInfo.fileName + " 下载完成");

                            // TODO 执行下载操作 完成后 更新页面
                            RxBus.getDefault().post(new UpdateFilesMessage());
                            isDownloading = false;
                        }
                        Log.i(TAG, downloadInfo.fileName + " Down offset :" + downloadInfo.offset + "/" + downloadInfo.size);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "error " + e);
            } finally {
                try {
                    if (mRandomAccessFile != null)
                        mRandomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean offer(DownloadInfo downloadInfo) {
            return mQueue.offer(downloadInfo);
        }

        public synchronized void shutdown() {
            if (mThread != null && !isStop) {
                isStop = true;
                mThread.interrupt();
                mThread = null;
                mQueue.clear();

                mProjects.clear();
            }
        }

        public boolean isShutdown() {
            return isStop;
        }

        public boolean isDownloading() {
            return isDownloading;
        }

        /**
         * 判断文件是否存在，存在则在创建之前删除
         *
         * @param file 文件
         * @return {@code true}: 创建成功<br>{@code false}: 创建失败
         */
        public static boolean createFileByDeleteOldFile(File file) {
            if (file == null) {
                return false;
            }
            // 文件存在并且删除失败返回false
            if (file.exists() && file.isFile() && !file.delete()) {
                return false;
            }
            // 创建目录失败返回false
            if (!createOrExistsDir(file.getParentFile())) {
                return false;
            }
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * 判断目录是否存在，不存在则判断是否创建成功
         *
         * @param file 文件
         * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
         */
        public static boolean createOrExistsDir(File file) {
            // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
            return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
        }
    }

    /**
     * 添加到上传队列
     *
     * @param project 上传路径
     */
    private void addToUploadQueue(Project project) {
        File file = new File(project.getFilePath());
        if (file.exists()) {
            UploadInfo uploadInfo;
            if (file.isDirectory()) {
                List<File> list = FileUtil.listFilesInDir(project.getFilePath());
                for (File f : list) {
                    uploadInfo = new UploadInfo();
                    uploadInfo.type = Common.TYPE_MICRO_LESSONS;
                    uploadInfo.clazz = project.getClazz();
                    uploadInfo.free = project.getFree();
                    uploadInfo.path = f.getAbsolutePath();
                    uploadInfo.fileName = f.getName();
                    uploadInfo.size = f.length();
                    uploadInfo.offset = 0;
                    if (mUpPoolExecutor != null)
                        mUpPoolExecutor.execute(new FileToServerThread(uploadInfo));
                }
            } else {
                uploadInfo = new UploadInfo();
                uploadInfo.type = Common.TYPE_MICRO_LESSONS;
                uploadInfo.clazz = project.getClazz();
                uploadInfo.free = project.getFree();
                uploadInfo.path = file.getAbsolutePath();
                uploadInfo.fileName = file.getName();
                uploadInfo.size = file.length();
                uploadInfo.offset = 0;
                if (mUpPoolExecutor != null)
                    mUpPoolExecutor.execute(new FileToServerThread(uploadInfo));
            }
        }
    }

    private void handlePublishQuery() {

    }

    private void handlePublishFile() {

    }

    @Subscribe(threadMode = ThreadMode.NEW_THREAD)
    public void OnMessageEvent(SyncMessage syncMessage) {
        switch (syncMessage.getAction()) {
            case Common.CMD_FILE_UP:
                Project project = (Project) syncMessage.getObject();
                // 上传
                Log.d(TAG, "upload path " + project.getFilePath());
                if (!TextUtils.isEmpty(project.getFilePath()))
                    addToUploadQueue(project);
                break;
            case Common.CMD_FILE_DEL:
                Object object = syncMessage.getObject();
                // 删除
                Log.d(TAG, "delete name " + (String) object);
                SocketManager.getInstance().filedel(Common.TYPE_MICRO_LESSONS, (String) object);
                break;
            case Common.CMD_FILE_DOWN_TYPE:
                if (mWriteToFileThread != null) {
                    Project downProject = (Project) syncMessage.getObject();
                    if (!mWriteToFileThread.mProjects.contains(downProject)) {
                        mWriteToFileThread.mProjects.add(downProject);
                        Log.e(TAG,"下载: "+downProject.getFileName()+" userId: "+downProject.getUserId()+" size : "+downProject.getSize());
                        int packageSize = DEFAULT_STREAM_PACKAGE;
                        if (downProject.getSize() < DEFAULT_STREAM_PACKAGE)
                            packageSize = (int) downProject.getSize();
                        SocketManager.getInstance().filedown(Common.TYPE_MICRO_LESSONS, downProject.getFileName(), 0, packageSize,downProject.getUserId());
                    }
                }
                break;
            default:
                break;
        }
    }

    @Subscribe()
    public void onEvent(ResponseMessage message) {
        switch (message.getCmd()) {
            case Common.CMD_LOGIN:
                // 登录
                handleLogin(message.isSuccess());
                break;
            case Common.CMD_GETIDENTITY:
                // 获取用户信息
                handleGetIdEntity();
                break;
            case Common.CMD_FILE_UP_TYPE:
                // 上传
                handleFileUp();
                break;
            case Common.CMD_FILE_QUERY_TYPE:
                // 查询
                handleFileQuery(message.getResponse());
                break;
            case Common.CMD_FILE_DEL_TYPE:
                // 删除
                handleFileDel(message.isSuccess());
                break;
            case Common.CMD_FILE_DOWN_TYPE:
                // 下载
                handleFileDown(message.isSuccess(), message.getResponse(), message.getBytes());
                break;
            case Common.CMD_FILE_RENAME_TYPE:
                // 重命名
                handleFileRename();
                break;
            case Common.CMD_PUBLISH_FILE:
                // 发布
                handlePublishFile();
                break;
            case Common.CMD_PUBLISH_QUERY:
                // 查询发布文件
                handlePublishQuery();
                break;
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        if (mWriteToFileThread != null && !mWriteToFileThread.isShutdown())
            mWriteToFileThread.shutdown();
        // TODO
        RxBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
