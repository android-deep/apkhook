package ma.mhy.apkhook.kellinwood.security.zipsigner;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner
 * 作者 mahongyin
 * 时间 2019/4/8 11:52
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import java.util.ArrayList;

public class ProgressHelper {

    private int progressTotalItems = 0;
    private int progressCurrentItem = 0;
    private ProgressEvent progressEvent = new ProgressEvent();

    public void initProgress()
    {
        progressTotalItems = 10000;
        progressCurrentItem = 0;
    }

    public int getProgressTotalItems() {
        return progressTotalItems;
    }

    public void setProgressTotalItems(int progressTotalItems) {
        this.progressTotalItems = progressTotalItems;
    }

    public int getProgressCurrentItem() {
        return progressCurrentItem;
    }

    public void setProgressCurrentItem(int progressCurrentItem) {
        this.progressCurrentItem = progressCurrentItem;
    }

    public void progress( int priority, String message) {

        progressCurrentItem += 1;

        int percentDone;
        if (progressTotalItems == 0) percentDone = 0;
        else percentDone = (100 * progressCurrentItem) / progressTotalItems;

        // Notify listeners here
        for (ProgressListener listener : listeners) {
            progressEvent.setMessage(message);
            progressEvent.setPercentDone(percentDone);
            progressEvent.setPriority(priority);
            listener.onProgress( progressEvent);
        }
    }

    private ArrayList<ProgressListener> listeners = new ArrayList<ProgressListener>();

    @SuppressWarnings("unchecked")
    public synchronized void addProgressListener( ProgressListener l)
    {
        ArrayList<ProgressListener> list = (ArrayList<ProgressListener>)listeners.clone();
        list.add(l);
        listeners = list;
    }

    @SuppressWarnings("unchecked")
    public synchronized void removeProgressListener( ProgressListener l)
    {
        ArrayList<ProgressListener> list = (ArrayList<ProgressListener>)listeners.clone();
        list.remove(l);
        listeners = list;
    }
}
