package ma.mhy.apkhook.axml.axmleditor.utils;


/**
 * @author Dmitry Skiba
 * 
 */
public class Cast {
        
        public static final CharSequence toCharSequence(String string) {
                if (string==null) {
                        return null;
                }
                return new CSString(string);
        }
}
