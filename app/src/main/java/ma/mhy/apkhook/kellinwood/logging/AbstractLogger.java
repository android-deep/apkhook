package ma.mhy.apkhook.kellinwood.logging;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.kellinwood.logging
 * 作者 mahongyin
 * 时间 2019/4/8 11:39
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractLogger implements LoggerInterface
{

    protected String category;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public AbstractLogger(String category) {
        this.category = category;
    }

    protected String format( String level, String message) {
        return String.format( "%s %s %s: %s\n", dateFormat.format(new Date()), level, category, message);
    }

    protected abstract void write( String level, String message, Throwable t);

    protected void writeFixNullMessage( String level, String message, Throwable t) {
        if (message == null) {
            if (t != null) message = t.getClass().getName();
            else message = "null";
        }
        write( level, message, t);
    }
    @Override
    public void debug(String message, Throwable t) {
        writeFixNullMessage( DEBUG, message, t);
    }
    @Override
    public void debug(String message) {
        writeFixNullMessage( DEBUG, message, null);
    }
    @Override
    public void error(String message, Throwable t) {
        writeFixNullMessage( ERROR, message, t);
    }
    @Override
    public void error(String message) {
        writeFixNullMessage( ERROR, message, null);
    }
    @Override
    public void info(String message, Throwable t) {
        writeFixNullMessage( INFO, message, t);
    }
    @Override
    public void info(String message) {
        writeFixNullMessage( INFO, message, null);
    }
    @Override
    public void warning(String message, Throwable t) {
        writeFixNullMessage( WARNING, message, t);
    }
    @Override
    public void warning(String message) {
        writeFixNullMessage( WARNING, message, null);
    }
    @Override
    public boolean isDebugEnabled() {
        return true;
    }
    @Override
    public boolean isErrorEnabled() {
        return true;
    }
    @Override
    public boolean isInfoEnabled() {
        return true;
    }
    @Override
    public boolean isWarningEnabled() {
        return true;
    }



}
