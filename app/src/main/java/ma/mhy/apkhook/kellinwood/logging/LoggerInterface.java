package ma.mhy.apkhook.kellinwood.logging;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.logging
 * 作者 mahongyin
 * 时间 2019/4/8 11:42
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
public interface LoggerInterface {

    public static final String ERROR = "ERROR";
    public static final String WARNING = "WARNING";
    public static final String INFO = "INFO";
    public static final String DEBUG = "DEBUG";

    public boolean isErrorEnabled();
    public void error(String message);
    public void error(String message, Throwable t);


    public boolean isWarningEnabled();
    public void warning(String message);
    public void warning(String message, Throwable t);

    public boolean isInfoEnabled();
    public void info(String message);
    public void info(String message, Throwable t);

    public boolean isDebugEnabled();
    public void debug(String message);
    public void debug(String message, Throwable t);


}
