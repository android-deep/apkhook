package ma.mhy.apkhook.axml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import ma.mhy.apkhook.axml.axmleditor.decode.AXMLDoc;
import ma.mhy.apkhook.axml.axmleditor.editor.PermissionEditor;
import ma.mhy.apkhook.axml.axmleditor.editor.PermissionEditor.EditorInfo;
import ma.mhy.apkhook.axml.axmleditor.editor.PermissionEditor.PermissionOpera;

public class AXMLEditor {
    private String origFile;

    public byte[] create()throws Exception {
        File file=(File)null;
        OutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        try {
           File file2=new File(this.origFile);
            AXMLDoc localAXMLDoc = new AXMLDoc();
            localAXMLDoc.parse(new FileInputStream(file2));
            PermissionEditor permissionEditor = new PermissionEditor(localAXMLDoc);
            permissionEditor.setEditorInfo(new EditorInfo()
                    .with(new PermissionOpera("android.permission.本工具由(HookAPK)提供").add())
                    .with(new PermissionOpera("android.permission.QQ.1976222027").add())
                    .with(new PermissionOpera("android.permission.Perfume").add())
                    .with(new PermissionOpera("android.permission.INTERNET").add())
                    .with(new PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add())
                    .with(new PermissionOpera("android.permission.ACCESS_WIFI_STATE").add())
                    .with(new PermissionOpera("android.permission.READ_PHONE_STATE").add()));
            permissionEditor.commit();
            localAXMLDoc.build(byteArrayOutputStream);
            localAXMLDoc.release();
            return ((ByteArrayOutputStream)byteArrayOutputStream).toByteArray();
        }
        catch (Exception localException)
        {
            throw localException;
        }
    }

    public void setOrigFile(String paramString)
    {
        this.origFile = paramString;
    }
}
