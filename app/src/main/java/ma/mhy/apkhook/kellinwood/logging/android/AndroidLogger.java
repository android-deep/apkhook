package ma.mhy.apkhook.kellinwood.logging.android;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.logging.android
 * 作者 mahongyin
 * 时间 2019/4/8 12:08
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import ma.mhy.apkhook.kellinwood.logging.AbstractLogger;


public class AndroidLogger extends AbstractLogger
{
    Context toastContext;

    public AndroidLogger(String c) {
        super(c);
        int pos = this.category.lastIndexOf('.');
        if (pos > 0) {
            this.category = this.category.substring(pos + 1);
        }
    }

    boolean isErrorToastEnabled = true;
    boolean isWarningToastEnabled = true;
    boolean isInfoToastEnabled = false;
    boolean isDebugToastEnabled = false;

    public Context getToastContext()
    {
        return this.toastContext;
    }

    public void setToastContext(Context toastContext)
    {
        this.toastContext = toastContext;
    }

    public boolean isErrorToastEnabled()
    {
        return this.isErrorToastEnabled;
    }

    public void setErrorToastEnabled(boolean isErrorToastEnabled)
    {
        this.isErrorToastEnabled = isErrorToastEnabled;
    }

    public boolean isWarningToastEnabled()
    {
        return this.isWarningToastEnabled;
    }

    public void setWarningToastEnabled(boolean isWarningToastEnabled)
    {
        this.isWarningToastEnabled = isWarningToastEnabled;
    }

    public boolean isInfoToastEnabled()
    {
        return this.isInfoToastEnabled;
    }

    public void setInfoToastEnabled(boolean isInfoToastEnabled)
    {
        this.isInfoToastEnabled = isInfoToastEnabled;
    }

    public boolean isDebugToastEnabled()
    {
        return this.isDebugToastEnabled;
    }

    public void setDebugToastEnabled(boolean isDebugToastEnabled)
    {
        this.isDebugToastEnabled = isDebugToastEnabled;
    }

    public void errorLO(String message, Throwable t)
    {
        boolean toastState = this.isErrorToastEnabled;
        this.isErrorToastEnabled = false;
        write("ERROR", message, t);
        this.isErrorToastEnabled = toastState;
    }

    public void warningLO(String message, Throwable t)
    {
        boolean toastState = this.isWarningToastEnabled;
        this.isWarningToastEnabled = false;
        write("WARNING", message, t);
        this.isWarningToastEnabled = toastState;
    }

    public void infoLO(String message, Throwable t)
    {
        boolean toastState = this.isInfoToastEnabled;
        this.isInfoToastEnabled = false;
        write("INFO", message, t);
        this.isInfoToastEnabled = toastState;
    }

    public void debugLO(String message, Throwable t)
    {
        boolean toastState = this.isDebugToastEnabled;
        this.isDebugToastEnabled = false;
        write("DEBUG", message, t);
        this.isDebugToastEnabled = toastState;
    }

    protected void toast(String message)
    {
        try
        {
            if (this.toastContext != null) {
                Toast.makeText(this.toastContext, message, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Throwable t)
        {
            Log.e(this.category, message, t);
        }
    }
    @Override
    public void write(String level, String message, Throwable t)
    {
        if ("ERROR".equals(level))
        {
            if (t != null) {
                Log.e(this.category, message, t);
            } else {
                Log.e(this.category, message);
            }
            if (this.isErrorToastEnabled) {
                toast(message);
            }
        }
        else if ("DEBUG".equals(level))
        {
            if (t != null) {
                Log.d(this.category, message, t);
            } else {
                Log.d(this.category, message);
            }
            if (this.isDebugToastEnabled) {
                toast(message);
            }
        }
        else if ("WARNING".equals(level))
        {
            if (t != null) {
                Log.w(this.category, message, t);
            } else {
                Log.w(this.category, message);
            }
            if (this.isWarningToastEnabled) {
                toast(message);
            }
        }
        else if ("INFO".equals(level))
        {
            if (t != null) {
                Log.i(this.category, message, t);
            } else {
                Log.i(this.category, message);
            }
            if (this.isInfoToastEnabled) {
                toast(message);
            }
        }
    }
    @Override
    public boolean isDebugEnabled()
    {
        boolean enabled = Log.isLoggable(this.category, 3);
        return enabled;
    }
    @Override
    public boolean isErrorEnabled()
    {
        return Log.isLoggable(this.category, 6);
    }
    @Override
    public boolean isInfoEnabled()
    {
        return Log.isLoggable(this.category, 4);
    }

    @Override
    public boolean isWarningEnabled()
    {
        return Log.isLoggable(this.category, 5);
    }
}

