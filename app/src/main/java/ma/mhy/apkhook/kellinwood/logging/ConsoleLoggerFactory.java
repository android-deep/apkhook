package ma.mhy.apkhook.kellinwood.logging;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.logging
 * 作者 mahongyin
 * 时间 2019/4/8 11:40
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

public class ConsoleLoggerFactory implements LoggerFactory {
    @Override
    public LoggerInterface getLogger(String category) {
        return new StreamLogger( category, System.out);
    }
}