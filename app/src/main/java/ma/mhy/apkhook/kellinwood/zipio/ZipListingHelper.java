package ma.mhy.apkhook.kellinwood.zipio;

/**
 * 项目名 apkhook
 * 所在包 ma.mhy.apkhook.ma.mhy.apkhook.kellinwood.zipio
 * 作者 mahongyin
 * 时间 2019/4/8 11:46
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ma.mhy.apkhook.kellinwood.logging.LoggerInterface;

/**
 *
 */
public class ZipListingHelper
{

    static DateFormat dateFormat = new SimpleDateFormat("MM-dd-yy HH:mm");

    public static void listHeader(LoggerInterface log)
    {
        log.debug(" Length   Method    Size  Ratio   Date   Time   CRC-32    Name");
        log.debug("--------  ------  ------- -----   ----   ----   ------    ----");

    }

    public static void listEntry(LoggerInterface log, ZioEntry entry)
    {
        int ratio = 0;
        if (entry.getSize() > 0) ratio = (100 * (entry.getSize() - entry.getCompressedSize())) / entry.getSize();
        log.debug(String.format("%8d  %6s %8d %4d%% %s  %08x  %s",
                entry.getSize(),
                entry.getCompression() == 0 ? "Stored" : "Defl:N",
                entry.getCompressedSize(),
                ratio,
                dateFormat.format( new Date( entry.getTime())),
                entry.getCrc32(),
                entry.getName()));
    }
}

