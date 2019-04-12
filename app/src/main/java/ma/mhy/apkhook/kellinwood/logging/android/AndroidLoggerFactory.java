package ma.mhy.apkhook.kellinwood.logging.android;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.logging.android
 * 作者 mahongyin
 * 时间 2019/4/8 12:06
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import ma.mhy.apkhook.kellinwood.logging.LoggerFactory;
import ma.mhy.apkhook.kellinwood.logging.LoggerInterface;

public class AndroidLoggerFactory implements LoggerFactory
{
    @Override
    public LoggerInterface getLogger(String category) {
        return new AndroidLogger(category);
    }
}

