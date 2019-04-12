package ma.mhy.apkhook.axml;


public class AXMLEdirotMain
{
    public static byte[] AXML(String paramString)throws Exception {
        try {
            AXMLEditor localAXMLEditor = new AXMLEditor();
            localAXMLEditor.setOrigFile(paramString);
            return localAXMLEditor.create();
        }
        catch (Exception e)
        {
            throw e;
        }

    }
}
