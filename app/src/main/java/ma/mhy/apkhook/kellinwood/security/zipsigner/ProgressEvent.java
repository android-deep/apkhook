package ma.mhy.apkhook.kellinwood.security.zipsigner;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner
 * 作者 mahongyin
 * 时间 2019/4/8 11:52
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
public class ProgressEvent {

    public static final int PRORITY_NORMAL = 0;
    public static final int PRORITY_IMPORTANT = 1;

    private String message;
    private int percentDone;
    private int priority;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getPercentDone() {
        return percentDone;
    }
    public void setPercentDone(int percentDone) {
        this.percentDone = percentDone;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

}