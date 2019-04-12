package ma.mhy.apkhook.kellinwood.security.zipsigner;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner
 * 作者 mahongyin
 * 时间 2019/4/8 11:53
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/** Produces the classic hex dump with an address column, hex data
 * section (16 bytes per row) and right-column printable character dislpay.
 */
public class HexDumpEncoder {

    static HexEncoder encoder = new HexEncoder();

    public static String encode( byte[] data) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            encoder.encode( data, 0, data.length, baos);
            byte[] hex = baos.toByteArray();

            StringBuilder hexDumpOut = new StringBuilder();
            for (int i = 0; i < hex.length; i += 32) {

                int max = Math.min(i+32, hex.length);

                StringBuilder hexOut = new StringBuilder();
                StringBuilder chrOut = new StringBuilder();

                hexOut.append( String.format("%08x: ", (i/2)));

                for (int j = i; j < max; j+= 2) {
                    hexOut.append( Character.valueOf( (char)hex[j]));
                    hexOut.append( Character.valueOf( (char)hex[j+1]));
                    if ((j+2) % 4 == 0) hexOut.append( ' ');

                    int dataChar = data[j/2];
                    if (dataChar >= 32 && dataChar < 127) chrOut.append( Character.valueOf( (char)dataChar));
                    else chrOut.append( '.');

                }

                hexDumpOut.append( hexOut.toString());
                for (int k = hexOut.length(); k < 50; k++) hexDumpOut.append(' ');
                hexDumpOut.append( "  ");
                hexDumpOut.append( chrOut);
                hexDumpOut.append("\n");
            }

            return hexDumpOut.toString();
        }
        catch (IOException x) {
            throw new IllegalStateException( x.getClass().getName() + ": " + x.getMessage());
        }
    }

}