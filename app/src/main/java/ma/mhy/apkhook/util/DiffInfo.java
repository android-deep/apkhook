package ma.mhy.apkhook.util;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.util
 * 作者 mahongyin
 * 时间 2019/4/8 21:29
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
public class DiffInfo{
    public static DiffInfo getInstance(){
      return new DiffInfo();
    }
        //两个dex遍历做diff的过程.
//        public DiffInfo diff(File newFile, File oldFile) throws IOException {
//            DexBackedDexFile newDexFile = DexFileFactory.loadDexFile(newFile, 19, true);
//            DexBackedDexFile oldDexFile = DexFileFactory.loadDexFile(oldFile, 19, true);
//            DiffInfo info = DiffInfo.getInstance();
//            boolean contains = false;
//            for(Iterator iterator = newDexFile.getClasses().iterator(); iterator.hasNext();) {
//                DexBackedClassDef newClazz = (DexBackedClassDef)iterator.next();
//                Set oldclasses = oldDexFile.getClasses();
//                for(Iterator iterator1 = oldclasses.iterator(); iterator1.hasNext();)
//                {
//                    DexBackedClassDef oldClazz = (DexBackedClassDef)iterator1.next();
//                    if(newClazz.equals(oldClazz)) {
//                        compareField(newClazz, oldClazz, info);
//                        compareMethod(newClazz, oldClazz, info);
//                        contains = true;
//                        break;
//                    }
//                }
//
//                if(!contains)
//                    info.addAddedClasses(newClazz);
//            }
//
//            return info;
//        }

}
