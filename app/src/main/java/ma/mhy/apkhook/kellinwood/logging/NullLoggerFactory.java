package ma.mhy.apkhook.kellinwood.logging;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.logging
 * 作者 mahongyin
 * 时间 2019/4/8 11:42
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
public class NullLoggerFactory implements LoggerFactory {

    static LoggerInterface logger = new LoggerInterface() {

        @Override
        public void debug(String message) {
        }

        @Override
        public void debug(String message, Throwable t) {
        }

        @Override
        public void error(String message) {
        }
        @Override
        public void error(String message, Throwable t) {
        }

        @Override
        public void info(String message) {
        }
        @Override
        public void info(String message, Throwable t) {
        }
        @Override
        public boolean isDebugEnabled() {
            return false;
        }
        @Override
        public boolean isErrorEnabled() {
            return false;
        }
        @Override
        public boolean isInfoEnabled() {
            return false;
        }
        @Override
        public boolean isWarningEnabled() {
            return false;
        }
        @Override
        public void warning(String message) {
        }
        @Override
        public void warning(String message, Throwable t) {
        }

    };

    @Override
    public LoggerInterface getLogger(String category) {
        return logger;
    }

}