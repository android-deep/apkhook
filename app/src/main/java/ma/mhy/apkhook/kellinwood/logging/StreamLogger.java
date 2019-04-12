package ma.mhy.apkhook.kellinwood.logging;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.logging
 * 作者 mahongyin
 * 时间 2019/4/8 11:39
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import java.io.PrintStream;

public class StreamLogger extends AbstractLogger {

    PrintStream out;

    public StreamLogger( String category, PrintStream out)
    {
        super( category);
        this.out = out;
    }

    @Override
    protected void write(String level, String message, Throwable t) {
        out.print( format( level, message));
        if (t != null) t.printStackTrace(out);
    }

}
