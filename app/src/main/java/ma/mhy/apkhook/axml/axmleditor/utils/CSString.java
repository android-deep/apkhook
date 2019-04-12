package ma.mhy.apkhook.axml.axmleditor.utils;

/**
 * @author Dmitry Skiba
 *
 * Helper class, used in Cast.toCharSequence.
 */
public class CSString implements CharSequence {
        
        public CSString(String string) {
                if (string==null) {
                        string="";
                }
                m_string=string;
        }

        @Override
        public int length() {
                return m_string.length();
        }
        
        @Override
        public char charAt(int index) {
                return m_string.charAt(index);
        }
        
        @Override
        public CharSequence subSequence(int start, int end) {
                return new CSString(m_string.substring(start,end));
        }
        
        @Override
        public String toString() {
                return m_string;
        }
        
        private String m_string;
}