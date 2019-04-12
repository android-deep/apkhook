package ma.mhy.apkhook.kellinwood.logging;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.logging
 * 作者 mahongyin
 * 时间 2019/4/8 11:38
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import java.util.Map;
import java.util.TreeMap;

public class LoggerManager {

    static LoggerFactory factory = new NullLoggerFactory();

    static Map<String,LoggerInterface> loggers = new TreeMap<String,LoggerInterface>();

    public static void setLoggerFactory( LoggerFactory f) {
        factory = f;
    }

    public static LoggerInterface getLogger(String category) {

        LoggerInterface logger = loggers.get( category);
        if (logger == null) {
            logger = factory.getLogger(category);
            loggers.put( category, logger);
        }
        return logger;
    }
}