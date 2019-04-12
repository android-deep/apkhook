package ma.mhy.apkhook.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Lists;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;

import org.apache.commons.codec.binary.Base64;
import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.analysis.ClassPath;
import org.jf.dexlib2.analysis.DexClassProvider;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedMethod;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.MemoryDataStore;
import org.jf.smali.Smali;
import org.jf.smali.SmaliOptions;
import org.jf.util.IndentingWriter;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ma.mhy.apkhook.bin.xml.decode.AXmlDecoder;
import ma.mhy.apkhook.bin.xml.decode.AXmlResourceParser;
import ma.mhy.apkhook.bin.xml.decode.XmlPullParser;
import ma.mhy.apkhook.bin.zip.ZipEntry;
import ma.mhy.apkhook.bin.zip.ZipFile;
import ma.mhy.apkhook.bin.zip.ZipOutputStream;
import ma.mhy.apkhook.R;
import ma.mhy.apkhook.adapter.MethodAdapter;
import ma.mhy.apkhook.axml.axmleditor.decode.AXMLDoc;
import ma.mhy.apkhook.axml.axmleditor.editor.PermissionEditor;
import ma.mhy.apkhook.axml.axmleditor.editor.PermissionEditor.EditorInfo;
import ma.mhy.apkhook.axml.axmleditor.editor.PermissionEditor.PermissionOpera;
import ma.mhy.apkhook.kellinwood.security.zipsigner.ZipSigner;
import ma.mhy.apkhook.kellinwood.security.zipsigner.optional.CustomKeySigner;
import ma.mhy.apkhook.ui.ProgressDialog;
import ma.mhy.apkhook.ui.mToast;
import ma.mhy.apkhook.util.AESUtils;
import ma.mhy.apkhook.util.ApkSign;
import ma.mhy.apkhook.util.AssetsUtils;
import ma.mhy.apkhook.util.RSA;
import ma.mhy.apkhook.util.Shared;


public class MainFragment extends Fragment implements OnClickListener, DialogSelectionListener, Runnable, OnCheckedChangeListener {
    private AutoCompleteTextView Appid,Ver, GetExpired, SingleLogin, GetBulletin;
    private CheckBox Ad, JiaGu,Chou,New360;
    private RadioGroup mRadioGroup;
    private String path;
    private Shared shared;
    private ZipFile zip = null;
    private List<String> dex = new ArrayList();
    private boolean customApplication = false;
    private String customApplicationName;
    private String packageName;
    private boolean custom = true; //全局自动
    private String method;//函数
    private String mainactivity;//入口名字
    private String maindex;//入口所在dex
    private List<String> ChouClass = new ArrayList();
    private AutoCompleteTextView Key;
    private FilePickerDialog dialog;
    private FloatingActionButton fab;
    private List<ClassDef> mClassDef = new ArrayList();
    private View mView;
    private DialogProperties properties;
    private Button zhuru;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_main, container, false);
            initView();
        }
        return mView;
    }
    private void initView() {
        fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        //  创建一个DialogProperties的实例
        properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
//        properties.root = new File("sdcard");//不包括系统目录
        properties.root=new File(DialogConfigs.DEFAULT_DIR);//根目录
//        properties.root = new File("/mnt");//根目录
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"apk"};
        dialog = new FilePickerDialog(getActivity(), properties);
        dialog.setTitle("选择 *.APK 文件");
        // 为 FilePickerDialog 设置DialogSelectionListener
        dialog.setDialogSelectionListener(this);

        Appid = (AutoCompleteTextView) this.mView.findViewById(R.id.appid);
        Ver = (AutoCompleteTextView) this.mView.findViewById(R.id.Ver);
        Key = (AutoCompleteTextView) this.mView.findViewById(R.id.key);
        Ad = (CheckBox) mView.findViewById(R.id.Ad);

        try {
            Key.setText(AESUtils.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Key.setOnLongClickListener(new View.OnLongClickListener() {//长按换随即码
            @Override
            public boolean onLongClick(View v) {
                try {
                    Key.setText(AESUtils.getSecretKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        JiaGu = (CheckBox) mView.findViewById(R.id.JiaGu);//9
        JiaGu.setChecked(true);
        Chou = (CheckBox) mView.findViewById(R.id.chou);//8
        New360 = (CheckBox) mView.findViewById(R.id.New360);//10
        JiaGu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (JiaGu.isChecked()){
                    mToast.Show(getContext(),"抽代码，不支持开启跳加固");
                    Chou.setChecked(false);
                    New360.setChecked(false);
                    New360.setVisibility(View.GONE);
                }
                if (Chou.isChecked()){
                    New360.setChecked(false);
                    New360.setVisibility(View.VISIBLE);
                    return;
                }
                New360.setChecked(false);
                New360.setVisibility(View.GONE);
            }
        });
        Chou.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Chou.isChecked()){
                    mToast.Show(getContext(),"抽代码，不支持开启跳加固");
                    Chou.setChecked(false);
                }
                New360.setChecked(false);
                New360.setVisibility(View.GONE);
                if (JiaGu.isChecked()){

                    New360.setVisibility(View.VISIBLE);
                }else {
                    New360.setVisibility(View.GONE);
                }
            }
        });


        GetBulletin = (AutoCompleteTextView) mView.findViewById(R.id.Color);
        GetBulletin.setOnClickListener(this);
        SingleLogin = (AutoCompleteTextView) mView.findViewById(R.id.SingleLogin);
        GetExpired = (AutoCompleteTextView) mView.findViewById(R.id.GetExpired);

        mRadioGroup = (RadioGroup) mView.findViewById(R.id.mRadioGroup);
        mRadioGroup.setOnCheckedChangeListener(this);

        shared = new Shared(getActivity());

        ArrayList<String> mOriginalValues = new ArrayList<>();
        mOriginalValues.addAll(Arrays.asList(shared.getHistoryArray("GetBulletin")));
        GetBulletin.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mOriginalValues));


        ArrayList<String> mOriginalValues2 = new ArrayList<>();
        mOriginalValues2.addAll(Arrays.asList(shared.getHistoryArray("GetExpired")));
        GetExpired.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mOriginalValues2));


        ArrayList<String> mOriginalValues3 = new ArrayList<>();
        mOriginalValues3.addAll(Arrays.asList(shared.getHistoryArray("SingleLogin")));
        SingleLogin.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mOriginalValues3));


        ArrayList<String> mOriginalValues4 = new ArrayList<>();
        mOriginalValues4.addAll(Arrays.asList(shared.getHistoryArray("Appid")));
        Appid.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mOriginalValues4));


        ArrayList<String> mOriginalValues5 = new ArrayList<>();
        mOriginalValues5.addAll(Arrays.asList(shared.getHistoryArray("Ver")));
        Ver.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mOriginalValues5));

        zhuru = (Button) mView.findViewById(R.id.zhuru);
        zhuru.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    private void DlRun() {
        new Thread(this).start();
    }

    //修改配置信息
    private void EditConfig(DexBackedClassDef a2, DexBuilder builder) throws Exception {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter writer = new IndentingWriter(stringWriter);
        ClassDefinition classDefinition = new ClassDefinition(new BaksmaliOptions(), a2);
        classDefinition.writeTo(writer);
        writer.close();
        String code2 = stringWriter.toString();

        int keyLength = 512;
        KeyPair keyPair = RSA.generateRSAKeyPair(keyLength);
        RSAPublicKey Public = RSA.getPublicKey(keyPair);
        RSAPrivateKey Private = RSA.getPrivateKey(keyPair);
        byte[] PublicKey = Base64.encodeBase64(Public.getEncoded());
        byte[] PrivateKey = Base64.encodeBase64(Private.getEncoded());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Properties conf = new Properties();
        conf.setProperty("Appid", Appid.getText().toString().trim());
        conf.setProperty("Bt", SingleLogin.getText().toString().trim());
        conf.setProperty("Hint", GetExpired.getText().toString().trim());
        conf.setProperty("Color", GetBulletin.getText().toString().trim());
        conf.setProperty("ShowAd", Ad.isChecked() ? "true" : "false");
        conf.setProperty("Ver", Ver.getText().toString().trim());
        conf.save(out, "Perfume QQ1976222027");
        if (JiaGu.isChecked()) {
            if (this.New360.isChecked()) {
                conf.setProperty("New360", "true");
            }
            conf.setProperty("SignHook", "Hook");
            code2 = code2.replace("SignHookData", ApkSign.getApkSignInfo(path));
//            conf.setProperty("SignHook", "true");
//            conf.setProperty("SignData", ApkSign.getApkSignInfo(this.path));
//        } else {
//            conf.setProperty("New360", "false");
//            conf.setProperty("SignHook", "false");
        } else {
            conf.setProperty("New360", "false");
            conf.setProperty("SignHook", "No Hook");
        }
        byte[] Data = RSA.encryptWithPrivateKey(out.toByteArray(), RSA.generatePrivateKey(Base64.decodeBase64(PrivateKey)));
        out.close();
        StringBuffer a = new StringBuffer();
        for (int i = 0; i < PublicKey.length; i++) {
            a.append("0x" + Integer.toHexString(PublicKey[i]) + "t\n");
        }
        code2 = code2.replace("Config_Data", new String(Base64.encodeBase64(Data)));
        code2 = code2.replace("const/4 v0, 0x6", "const/16 v0,0x" + Integer.toHexString(PublicKey.length));
        code2 = code2.replace("-0x50t", a.toString());
        Smali.assembleSmaliFile(code2, builder, new SmaliOptions());
        //add
//        CharSequence str = new String(Base64.encodeBase64(((ByteArrayOutputStream) out).toByteArray()));
//        out.close();
//        ClassDef assembleSmaliFile = Smali.assembleSmaliFile(code2.replace("Config", str), builder, new SmaliOptions());
//        if (assembleSmaliFile == null) {
//            throw new Exception("Smali编译错误");
//        }
//        this.mClassDef.add(assembleSmaliFile);
    }

    //按钮事件
    @Override
    public void onClick(View p1) {
        switch (p1.getId()) {
            case R.id.fab:
                dialog.show();
                break;
            case R.id.zhuru:
                if (path == null) {
                    mToast.Show(getContext(),"请选择apk");
                    return;
                }
                shared.saveHistory(GetBulletin, "GetBulletin");
                shared.saveHistory(SingleLogin, "SingleLogin");
                shared.saveHistory(GetExpired, "GetExpired");
                shared.saveHistory(Ver, "Ver");
                shared.saveHistory(Appid, "Appid");
                if (!custom) {
                    try {
                        zip = new ZipFile(path);
                        if (mainactivity == null || method == null || maindex == null) {
                            SetActiity_Method("请选择入口Activity", true, parseManifestActivity(zip.getInputStream(zip.getEntry("AndroidManifest.xml"))));
//                            SetActiity("请选择入口Activity",  parseManifestActivity(zip.getInputStream(zip.getEntry("AndroidManifest.xml"))));
                        }
                    } catch (IOException e) {
                        mToast.Show(getContext(),e.getMessage());
                        clear();
                        return;
                    }
                } else if (this.Chou.isChecked()) {
                    showmsg("解析类名中.....");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final List arrayList = new ArrayList();
                                for (String entry : ChouClass) {//15
                                    for (DexBackedClassDef dexBackedClassDef : DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(zip.getInputStream(zip.getEntry(entry)))).getClasses()) {//14 zip
                                        if (!dexBackedClassDef.getType().contains("Landroid/")) {
                                            arrayList.add(dexBackedClassDef.getType().replaceFirst("L", "").replace(";", "").replace("/", "."));
                                        }
                                    }
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressDialog.hide();
                                        ProgressDialog.show(getActivity(), "请选择需要抽取代码的类", arrayList.size());
                                    }
                                });
                            } catch (Exception e) {
                                mToast.Show(getContext(),e.getMessage());
                                MainFragment.this.clear();//57
                            }

                        }
                    }).start();
                    return;
                } else {
                    new Thread(this).start();
                    return;
                }
                break;
            case R.id.Color:
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(false)
                        .setDialogId(0)
                        .setColor(Color.BLACK)
                        .setShowAlphaSlider(true)
                        .show(getActivity());
                break;
        }
    }

    private void JumpJiaGu(final DexBackedDexFile dexBackedDexFile, DexBuilder dexBuilder) throws IOException, Exception {
        int i = 0;
        showmsg("修改接口信息中.....");
        byte[] parseManifest = parseManifest(this.zip.getInputStream(this.zip.getEntry("AndroidManifest.xml")));
        if (this.customApplication) {
            DexBackedClassDef dexBackedClassDef = (DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(dexBackedDexFile)}), false, dexBackedDexFile.getClasses().size()).getClassDef("Lcom/App;");
//            DexBackedDexFile dex = DexFileFactory.loadDexFile(dexBackedDexFile, 14, false);
//            Map<String, DexClassNode> map = readDex(dexFile);
            Writer stringWriter = new StringWriter();//2
            IndentingWriter indentingWriter = new IndentingWriter(stringWriter);//3
            new ClassDefinition(new BaksmaliOptions(), dexBackedClassDef).writeTo(indentingWriter);
            indentingWriter.close();
            String stringWriter2 = stringWriter.toString();
            if (this.customApplicationName.startsWith(".")) {
                if (this.packageName == null) {
                    throw new NullPointerException("Package name is null.");
                }
                this.customApplicationName = new StringBuffer().append(this.packageName).append(this.customApplicationName).toString();
            }
            this.customApplicationName = new StringBuffer().append(new StringBuffer().append("L").append(this.customApplicationName.replace('.', '/')).toString()).append(";").toString();
            ClassDef assembleSmaliFile = Smali.assembleSmaliFile(stringWriter2.replace("Landroid/app/Application;", this.customApplicationName), dexBuilder, new SmaliOptions());
            if (assembleSmaliFile == null) {
                throw new Exception("Smali编译错误");
            }
            mClassDef.add(assembleSmaliFile);
        }

        EditConfig((DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(dexBackedDexFile)}), false, dexBackedDexFile.getClasses().size()).getClassDef("Li/ﹳ;"), dexBuilder);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(getActivity(),"保存dex中.....",dexBackedDexFile.getClasses().size());
            }
        });
        int i2 = 0;
        for (DexBackedClassDef dexBackedClassDef2 : dexBackedDexFile.getClasses()) {
            for (ClassDef type : this.mClassDef) {
                if (type.getType().equals(dexBackedClassDef2.getType())) {
                    break;
                }
            }
            dexBuilder.internClassDef(dexBackedClassDef2);
            s(i2);
            i2 ++;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(getActivity(),"重组APK中......",zip.getEntrySize()-2);//9
            }
        });
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AXMLDoc aXMLDoc = new AXMLDoc();
        aXMLDoc.parse(new ByteArrayInputStream(parseManifest));
        PermissionEditor permissionEditor = new PermissionEditor(aXMLDoc);
        permissionEditor.setEditorInfo(new EditorInfo().with(new PermissionOpera("android.permission.本网络验证由战神网络提供").add()).with(new PermissionOpera("android.permission.QQ.1976222027").add()).with(new PermissionOpera("android.permission.Perfume").add()).with(new PermissionOpera("android.permission.INTERNET").add()).with(new PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add()).with(new PermissionOpera("android.permission.ACCESS_WIFI_STATE").add()).with(new PermissionOpera("android.permission.READ_PHONE_STATE").add()).with(new PermissionOpera("android.permission.WRITE_EXTERNAL_STORAGE").add()));
        permissionEditor.commit();
        aXMLDoc.build(byteArrayOutputStream);
        aXMLDoc.release();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new File("sdcard/战神网络验证.apk"));
        zipOutputStream.setLevel(1);
        zipOutputStream.putNextEntry("AndroidManifest.xml");
        zipOutputStream.write(((ByteArrayOutputStream) byteArrayOutputStream).toByteArray());
        zipOutputStream.closeEntry();
        MemoryDataStore memoryDataStore = new MemoryDataStore();
        dexBuilder.writeTo(memoryDataStore);
        zipOutputStream.putNextEntry(new StringBuffer().append(new StringBuffer().append("classes").append(this.dex.size() + 1).toString()).append(".dex").toString());
        zipOutputStream.write(Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize()));
        zipOutputStream.closeEntry();
        Enumeration entries = this.zip.getEntries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            if (!(zipEntry.getName().equals("AndroidManifest.xml") || zipEntry.getName().equals(new StringBuffer().append(new StringBuffer().append("classes").append(this.dex.size() + 1).toString()).append(".dex").toString()))) {
                zipOutputStream.copyZipEntry(zipEntry, this.zip);
                s(i);
                i++;
            }
        }
        zipOutputStream.close();
        this.zip.close();
    }
    private void JumpNew360(final DexBackedDexFile dexBackedDexFile, DexBuilder dexBuilder) throws IOException, Exception {
        int i = 0;
        showmsg("修改接口信息中.....");
        byte[] parseManifest = parseManifest(this.zip.getInputStream(this.zip.getEntry("AndroidManifest.xml")));
        EditConfig((DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(dexBackedDexFile)}), false, dexBackedDexFile.getClasses().size()).getClassDef("Li/ﹳ;"), dexBuilder);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(MainFragment.this.getActivity(), "保存dex中......", dexBackedDexFile.getClasses().size());
            }
        });
        int i2 = 0;
        for (DexBackedClassDef dexBackedClassDef : dexBackedDexFile.getClasses()) {
            for (ClassDef type : this.mClassDef) {
                if (type.getType().equals(dexBackedClassDef.getType())) {
                    break;
                }
            }
            dexBuilder.internClassDef(dexBackedClassDef);
            int i3 = i2 + 1;
            s(i2);
            i2 = i3;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(MainFragment.this.getActivity(), "重组APK中......", zip.getEntrySize() - 2);
            }
        });
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AXMLDoc aXMLDoc = new AXMLDoc();
        aXMLDoc.parse(new ByteArrayInputStream(parseManifest));
        PermissionEditor permissionEditor = new PermissionEditor(aXMLDoc);
        permissionEditor.setEditorInfo(new EditorInfo().with(new PermissionOpera("android.permission.本网络验证由战神网络提供").add())
                .with(new PermissionOpera("android.permission.QQ.526564445").add())
                .with(new PermissionOpera("android.permission.Perfume").add())
                .with(new PermissionOpera("android.permission.INTERNET").add())
                .with(new PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add())
                .with(new PermissionOpera("android.permission.ACCESS_WIFI_STATE").add())
                .with(new PermissionOpera("android.permission.READ_PHONE_STATE").add())
                .with(new PermissionOpera("android.permission.WRITE_EXTERNAL_STORAGE").add()));
        permissionEditor.commit();
        aXMLDoc.build(byteArrayOutputStream);
        aXMLDoc.release();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new File("sdcard/战神网络验证.apk"));
        zipOutputStream.setLevel(1);
        zipOutputStream.putNextEntry("AndroidManifest.xml");
        zipOutputStream.write(((ByteArrayOutputStream) byteArrayOutputStream).toByteArray());
        zipOutputStream.closeEntry();
        MemoryDataStore memoryDataStore = new MemoryDataStore();
        dexBuilder.writeTo(memoryDataStore);
        zipOutputStream.putNextEntry(new StringBuffer().append(new StringBuffer().append("classes").append(this.dex.size() + 1).toString()).append(".dex").toString());
        zipOutputStream.write(Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize()));
        zipOutputStream.closeEntry();
        Enumeration entries = this.zip.getEntries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            if (!(zipEntry.getName().equals("AndroidManifest.xml") || zipEntry.getName().equals(new StringBuffer().append(new StringBuffer().append("classes").append(this.dex.size() + 1).toString()).append(".dex").toString()))) {
                zipOutputStream.copyZipEntry(zipEntry, this.zip);
                s(i);
                i++;
                //i = i + 1;//
            }
        }
        zipOutputStream.close();
        this.zip.close();
    }

    private void Qj(final DexBackedDexFile dexBackedDexFile, DexBuilder dexBuilder, DexBuilder dexBuilder2) throws IOException, Exception {
        ClassDef assembleSmaliFile;
        String str2 = (String) null;
        DexBuilder dexBuilder3 = new DexBuilder(Opcodes.getDefault());
        dexBuilder3.setIgnoreMethodAndFieldError(true);
        if (this.Chou.isChecked()) {

            for (String str22 : this.dex) {
                for (DexBackedClassDef type : DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(new BufferedInputStream(this.zip.getInputStream(this.zip.getEntry(str22))))).getClasses()) {
                    if (type.getType().equals(new StringBuffer().append(new StringBuffer().append("L").append(((String) this.ChouClass.get(0)).replace(".", "/")).toString()).append(";").toString())) {
                        str2 = str22;
                        break;
                    }
                }
            }
        }
        showmsg("修改接口信息中.....");
        byte[] parseManifest = parseManifest(this.zip.getInputStream(this.zip.getEntry("AndroidManifest.xml")));
        if (this.customApplication) {
            DexBackedClassDef dexBackedClassDef = (DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(dexBackedDexFile)}), false, dexBackedDexFile.getClasses().size()).getClassDef("Lcom/App;");
            Writer stringWriter = new StringWriter();
            IndentingWriter indentingWriter = new IndentingWriter(stringWriter);
            new ClassDefinition(new BaksmaliOptions(), dexBackedClassDef).writeTo(indentingWriter);
            indentingWriter.close();
            str2 = stringWriter.toString();
            if (this.customApplicationName.startsWith(".")) {
                if (this.packageName == null) {
                    throw new NullPointerException("Package name is null.");
                }
                this.customApplicationName = new StringBuffer().append(this.packageName).append(this.customApplicationName).toString();
            }
            this.customApplicationName = new StringBuffer().append(new StringBuffer().append("L").append(this.customApplicationName.replace('.', '/')).toString()).append(";").toString();
            assembleSmaliFile = Smali.assembleSmaliFile(str2.replace("Landroid/app/Application;", this.customApplicationName), dexBuilder, new SmaliOptions());
            if (assembleSmaliFile == null) {
                throw new Exception("Smali编译错误");
            }
            this.mClassDef.add(assembleSmaliFile);
        }
        EditConfig((DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(dexBackedDexFile)}), false, dexBackedDexFile.getClasses().size()).getClassDef("Li/ﹳ;"), dexBuilder);
        if (this.Chou.isChecked()) {
            if (str2 == null) {
                throw new Exception("抽取类未找到");
            }
            DexBackedDexFile fromInputStream = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(new BufferedInputStream(this.zip.getInputStream(this.zip.getEntry(str2)))));
            showmsg("抽取代码中.....");
            DexBackedClassDef  dexBackedClassDef = (DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(fromInputStream)}), false, fromInputStream.getClasses().size()).getClassDef(new StringBuffer().append(new StringBuffer().append("L").append(((String) this.ChouClass.get(0)).replace(".", "/")).toString()).append(";").toString());
            Writer stringWriter2 = new StringWriter();
            IndentingWriter indentingWriter2 = new IndentingWriter(stringWriter2);
            new ClassDefinition(new BaksmaliOptions(), dexBackedClassDef).writeTo(indentingWriter2);
            indentingWriter2.close();
            str2 = stringWriter2.toString();
            ClassDef assembleSmaliFile2 = Smali.assembleSmaliFile(new StringBuffer().append(str2.split("\n")[0]).append(str2.split("\n")[1]).toString(), dexBuilder, new SmaliOptions());
            if (assembleSmaliFile2 == null) {
                throw new Exception("Smali编译错误");
            }
            this.mClassDef.add(assembleSmaliFile2);
            assembleSmaliFile = Smali.assembleSmaliFile(str2, dexBuilder2, new SmaliOptions());
            if (assembleSmaliFile == null) {
                throw new Exception("Smali编译错误");
            }
            this.mClassDef.add(assembleSmaliFile);
            for (DexBackedClassDef dexBackedClassDef2 : fromInputStream.getClasses()) {
                if (!dexBackedClassDef2.getType().equals(new StringBuffer().append(new StringBuffer().append("L").append(((String) this.ChouClass.get(0)).replace(".", "/")).toString()).append(";").toString())) {
                    dexBuilder3.internClassDef(dexBackedClassDef2);
                }
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(MainFragment.this.getActivity(), "保存dex中......", dexBackedDexFile.getClasses().size());

            }
        });
        int i = 0;
        for (DexBackedClassDef dexBackedClassDef22 : dexBackedDexFile.getClasses()) {
            for (ClassDef type2 : this.mClassDef) {
                if (type2.getType().equals(dexBackedClassDef22.getType())) {
                    break;
                }
            }
            dexBuilder.internClassDef(dexBackedClassDef22);
            int i2 = i + 1;
            s(i);
            i = i2;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(MainFragment.this.getActivity(), "重组APK中......", zip.getEntrySize() - 2);

            }
        });
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AXMLDoc aXMLDoc = new AXMLDoc();
        aXMLDoc.parse(new ByteArrayInputStream(parseManifest));
        PermissionEditor permissionEditor = new PermissionEditor(aXMLDoc);
        permissionEditor.setEditorInfo(new EditorInfo().with(new PermissionOpera("android.permission.本网络验证由战神网络提供").add()).with(new PermissionOpera("android.permission.QQ.526564445").add()).with(new PermissionOpera("android.permission.Perfume").add()).with(new PermissionOpera("android.permission.INTERNET").add()).with(new PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add()).with(new PermissionOpera("android.permission.ACCESS_WIFI_STATE").add()).with(new PermissionOpera("android.permission.READ_PHONE_STATE").add()).with(new PermissionOpera("android.permission.WRITE_EXTERNAL_STORAGE").add()));
        permissionEditor.commit();
        aXMLDoc.build(byteArrayOutputStream);
        aXMLDoc.release();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new File("sdcard/战神网络验证.apk"));
        zipOutputStream.setLevel(1);
        zipOutputStream.putNextEntry("AndroidManifest.xml");
        zipOutputStream.write(((ByteArrayOutputStream) byteArrayOutputStream).toByteArray());
        zipOutputStream.closeEntry();
        MemoryDataStore memoryDataStore = new MemoryDataStore();
        dexBuilder.writeTo(memoryDataStore);
        zipOutputStream.putNextEntry(new StringBuffer().append(new StringBuffer().append("classes").append(this.dex.size() + 1).toString()).append(".dex").toString());
        zipOutputStream.write(Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize()));
        zipOutputStream.closeEntry();
        if (this.Chou.isChecked()) {
            memoryDataStore = new MemoryDataStore();
            dexBuilder2.writeTo(memoryDataStore);
            zipOutputStream.putNextEntry("assets/Perfume.VMP");
            zipOutputStream.write(AESUtils.encrypt(Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize()), this.Key.getText().toString()));
            zipOutputStream.closeEntry();
            memoryDataStore = new MemoryDataStore();
            dexBuilder3.writeTo(memoryDataStore);
            zipOutputStream.putNextEntry(str2);
            zipOutputStream.write(Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize()));
            zipOutputStream.closeEntry();
        }
        Enumeration entries = this.zip.getEntries();
        int i3 = 0;
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            if (!(zipEntry.getName().equals("AndroidManifest.xml") || zipEntry.getName().equals(new StringBuffer().append(new StringBuffer().append("classes").append(this.dex.size() + 1).toString()).append(".dex").toString()))) {
                if (!this.Chou.isChecked() || !zipEntry.getName().equals(str2)) {
                    zipOutputStream.copyZipEntry(zipEntry, this.zip);
                    s(i3);
                    i3 ++;
                }
            }
        }
        zipOutputStream.close();
        this.zip.close();
    }

    private void Qjd(DexBackedDexFile dexBackedDexFile, DexBuilder dexBuilder, DexBuilder dexBuilder2) throws IOException, Exception {
        ClassDef assembleSmaliFile;
        int i = 0;
        DexBackedDexFile fromInputStream = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(this.zip.getInputStream(this.zip.getEntry("classes.dex"))));
        final Set<DexBackedClassDef> hashSet = new HashSet();
        hashSet.addAll(fromInputStream.getClasses());
        hashSet.addAll(dexBackedDexFile.getClasses());
        showmsg("修改接口信息中.....");
        byte[] parseManifest = parseManifest(this.zip.getInputStream(this.zip.getEntry("AndroidManifest.xml")));
        if (this.customApplication) {
            DexBackedClassDef dexBackedClassDef = (DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(dexBackedDexFile)}), false, dexBackedDexFile.getClasses().size()).getClassDef("Lcom/App;");
            Writer stringWriter = new StringWriter();
            IndentingWriter indentingWriter = new IndentingWriter(stringWriter);
            new ClassDefinition(new BaksmaliOptions(), dexBackedClassDef).writeTo(indentingWriter);
            indentingWriter.close();
            String stringWriter2 = stringWriter.toString();
            if (this.customApplicationName.startsWith(".")) {
                if (this.packageName == null) {
                    throw new NullPointerException("Package name is null.");
                }
                this.customApplicationName = new StringBuffer().append(this.packageName).append(this.customApplicationName).toString();
            }
            this.customApplicationName = new StringBuffer().append(new StringBuffer().append("L").append(this.customApplicationName.replace('.', '/')).toString()).append(";").toString();
            assembleSmaliFile = Smali.assembleSmaliFile(stringWriter2.replace("Landroid/app/Application;", this.customApplicationName), dexBuilder, new SmaliOptions());
            if (assembleSmaliFile == null) {
                throw new Exception("Smali编译错误");
            }
            this.mClassDef.add(assembleSmaliFile);
        }
        EditConfig((DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(dexBackedDexFile)}), false, dexBackedDexFile.getClasses().size()).getClassDef("Li/ﹳ;"), dexBuilder);
        if (this.Chou.isChecked()) {
            showmsg("抽取代码中.....");
            DexBackedClassDef  dexBackedClassDef = (DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(fromInputStream)}), false, fromInputStream.getClasses().size()).getClassDef(new StringBuffer().append(new StringBuffer().append("L").append(((String) this.ChouClass.get(0)).replace(".", "/")).toString()).append(";").toString());
            Writer stringWriter3 = new StringWriter();
            IndentingWriter indentingWriter2 = new IndentingWriter(stringWriter3);
            new ClassDefinition(new BaksmaliOptions(), dexBackedClassDef).writeTo(indentingWriter2);
            indentingWriter2.close();
           String stringWriter2 = stringWriter3.toString();
            ClassDef assembleSmaliFile2 = Smali.assembleSmaliFile(new StringBuffer().append(stringWriter2.split("\n")[0]).append(stringWriter2.split("\n")[1]).toString(), dexBuilder, new SmaliOptions());
            if (assembleSmaliFile2 == null) {
                throw new Exception("Smali编译错误");
            }
            this.mClassDef.add(assembleSmaliFile2);
            assembleSmaliFile = Smali.assembleSmaliFile(stringWriter2, dexBuilder2, new SmaliOptions());
            if (assembleSmaliFile == null) {
                throw new Exception("Smali编译错误");
            }
            this.mClassDef.add(assembleSmaliFile);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(MainFragment.this.getActivity(), "合并dex中......", hashSet.size());
            }
        });
        int i2 = 0;
        for (DexBackedClassDef dexBackedClassDef2 : hashSet) {
            for (ClassDef type : this.mClassDef) {
                if (type.getType().equals(dexBackedClassDef2.getType())) {
                    break;
                }
            }
            dexBuilder.internClassDef(dexBackedClassDef2);
            int i3 = i2 + 1;
            s(i2);
            i2 = i3;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(MainFragment.this.getActivity(), "重组APK中......", zip.getEntrySize() - 2);

            }
        });
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AXMLDoc aXMLDoc = new AXMLDoc();
        aXMLDoc.parse(new ByteArrayInputStream(parseManifest));
        PermissionEditor permissionEditor = new PermissionEditor(aXMLDoc);
        permissionEditor.setEditorInfo(new EditorInfo().with(new PermissionOpera("android.permission.本网络验证由战神网络提供").add()).with(new PermissionOpera("android.permission.QQ.526564445").add()).with(new PermissionOpera("android.permission.Perfume").add()).with(new PermissionOpera("android.permission.INTERNET").add()).with(new PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add()).with(new PermissionOpera("android.permission.ACCESS_WIFI_STATE").add()).with(new PermissionOpera("android.permission.READ_PHONE_STATE").add()).with(new PermissionOpera("android.permission.WRITE_EXTERNAL_STORAGE").add()));
        permissionEditor.commit();
        aXMLDoc.build(byteArrayOutputStream);
        aXMLDoc.release();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new File("sdcard/战神网络验证.apk"));
        zipOutputStream.putNextEntry("AndroidManifest.xml");
        zipOutputStream.write(((ByteArrayOutputStream) byteArrayOutputStream).toByteArray());
        zipOutputStream.closeEntry();
        MemoryDataStore memoryDataStore = new MemoryDataStore();
        dexBuilder.writeTo(memoryDataStore);
        zipOutputStream.putNextEntry("classes.dex");
        zipOutputStream.write(Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize()));
        zipOutputStream.closeEntry();
        if (this.Chou.isChecked()) {
            memoryDataStore = new MemoryDataStore();
            dexBuilder2.writeTo(memoryDataStore);
            zipOutputStream.putNextEntry("assets/Perfume.VMP");
            zipOutputStream.write(AESUtils.encrypt(Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize()), this.Key.getText().toString()));
            zipOutputStream.closeEntry();
        }
        Enumeration entries = this.zip.getEntries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            if (!(zipEntry.getName().equals("AndroidManifest.xml") || zipEntry.getName().equals("classes.dex"))) {
                zipOutputStream.copyZipEntry(zipEntry, this.zip);
                s(i);
                i ++;
            }
        }
        zipOutputStream.close();
        this.zip.close();
    }

    //插入代码
    private void a(DexBackedClassDef a1, DexBuilder builder) throws Exception {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter writer = new IndentingWriter(stringWriter);
        ClassDefinition classDefinition = new ClassDefinition(new BaksmaliOptions(), a1);
        classDefinition.writeTo(writer);
        writer.close();
        String code = stringWriter.toString();

        String rgex = ".method(.*)" + method + "\\(([\\s\\S]*?)end method{0,1}";
        Pattern p = Pattern.compile(rgex);
        Matcher m = p.matcher(code);
        String r = null;
        while (m.find()) {
            r = m.group();
        }
        if (r == null) {
            throw new Exception("没有找到函数头");
        }
        //把这里换成自定义文本
        String buff2 = r.replace("return", AssetsUtils.readAssetsTxt(getActivity(), "1.txt") + "\nreturn");
        code = code.replace(r, buff2);
        Smali.assembleSmaliFile(EditReg(code), builder, new SmaliOptions());
        //add
        ClassDef classDef = r.contains(".end annotation") ? Smali.assembleSmaliFile(code.replace(r, r.replaceFirst(".end annotation", new StringBuffer().append(".end annotation\n").append(AssetsUtils.readAssetsTxt(getActivity(), "1.txt")).toString())), builder, new SmaliOptions()) : Smali.assembleSmaliFile(code.replace(r, r.replaceFirst(mm(r), new StringBuffer().append(new StringBuffer().append(mm(r)).append("\n").toString()).append(AssetsUtils.readAssetsTxt(getActivity(), "1.txt")).toString())), builder, new SmaliOptions());
        if (classDef == null) {
            throw new Exception("Smali编译错误");
        }
        this.mClassDef.add(classDef);
    }



    private void dl(DexBackedDexFile dexBackedDexFile, DexBuilder dexBuilder) throws IOException, Exception {
        int i = 0;
        DexBackedDexFile fromInputStream = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(this.zip.getInputStream(this.zip.getEntry(this.maindex))));
        final Set hashSet = new HashSet();
        hashSet.addAll(fromInputStream.getClasses());
        hashSet.addAll(dexBackedDexFile.getClasses());
        List newArrayList = Lists.newArrayList(hashSet.iterator());
        showmsg("修改接口信息中.....");
        a((DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(fromInputStream)}), false, fromInputStream.getClasses().size()).getClassDef(new StringBuffer().append(new StringBuffer().append("L").append(this.mainactivity.replace(".", "/")).toString()).append(";").toString()), dexBuilder);
        EditConfig((DexBackedClassDef) new ClassPath(Lists.newArrayList(new DexClassProvider[]{new DexClassProvider(dexBackedDexFile)}), false, dexBackedDexFile.getClasses().size()).getClassDef("Li/ﹳ;"), dexBuilder);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(MainFragment.this.getActivity(), "合并dex中......", hashSet.size());

            }
        });
        for (int i2 = 0; i2 < newArrayList.size(); i2++) {
            DexBackedClassDef dexBackedClassDef = (DexBackedClassDef) newArrayList.get(i2);
            for (ClassDef type : this.mClassDef) {
                if (type.getType().equals(dexBackedClassDef.getType())) {
                    break;
                }
            }
            dexBuilder.internClassDef(dexBackedClassDef);
            s(i2);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(MainFragment.this.getActivity(), "重组APK中......", zip.getEntrySize() - 2);

            }
        });
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AXMLDoc aXMLDoc = new AXMLDoc();
        aXMLDoc.parse(new ByteArrayInputStream(toByteArray(this.zip.getInputStream(this.zip.getEntry("AndroidManifest.xml")))));
        PermissionEditor permissionEditor = new PermissionEditor(aXMLDoc);
        permissionEditor.setEditorInfo(new EditorInfo().with(new PermissionOpera("android.permission.本网络验证由战神网络提供").add()).with(new PermissionOpera("android.permission.QQ.526564445").add()).with(new PermissionOpera("android.permission.Perfume").add()).with(new PermissionOpera("android.permission.INTERNET").add()).with(new PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add()).with(new PermissionOpera("android.permission.ACCESS_WIFI_STATE").add()).with(new PermissionOpera("android.permission.READ_PHONE_STATE").add()).with(new PermissionOpera("android.permission.WRITE_EXTERNAL_STORAGE").add()));
        permissionEditor.commit();
        aXMLDoc.build(byteArrayOutputStream);
        aXMLDoc.release();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new File("sdcard/战神网络验证.apk"));
        zipOutputStream.setLevel(1);
        zipOutputStream.putNextEntry("AndroidManifest.xml");
        zipOutputStream.write(((ByteArrayOutputStream) byteArrayOutputStream).toByteArray());
        zipOutputStream.closeEntry();
        MemoryDataStore memoryDataStore = new MemoryDataStore();
        dexBuilder.writeTo(memoryDataStore);
        zipOutputStream.putNextEntry(this.maindex);
        zipOutputStream.write(Arrays.copyOf(memoryDataStore.getBufferData(), memoryDataStore.getSize()));
        zipOutputStream.closeEntry();
        Enumeration entries = this.zip.getEntries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            if (!(zipEntry.getName().equals("AndroidManifest.xml") || zipEntry.getName().equals(this.maindex))) {
                zipOutputStream.copyZipEntry(zipEntry, this.zip);
                int i3 = i + 1;
                s(i);
                i = i3;
            }
        }
        zipOutputStream.close();
        this.zip.close();
    }

    private String mm(String str) throws Exception {
        Matcher matcher = Pattern.compile(".registers [0-9]{1,2}").matcher(str);
        String str2 = (String) null;
        while (matcher.find()) {
            str2 = matcher.group();
        }
        if (str2 != null) {
            return str2;
        }
        throw new Exception("没有找到registers");
    }

    private static List<String> parseManifestActivity(InputStream is) throws IOException {
        String PackageName = null;
        List<String> list = new ArrayList();
        AXmlDecoder axml = AXmlDecoder.decode(is);
        AXmlResourceParser parser = new AXmlResourceParser();
        parser.open(new ByteArrayInputStream(axml.getData()), axml.mTableStrings);
        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG)
                continue;
            if (parser.getName().equals("manifest")) {
                int size = parser.getAttributeCount();
                for (int i = 0; i < size; ++i) {
                    if (parser.getAttributeName(i).equals("package")) {
                        PackageName = parser.getAttributeValue(i);
                    }
                }
            } else if (parser.getName().equals("activity")) {
                int size = parser.getAttributeCount();
                for (int i = 0; i < size; ++i) {
                    if (parser.getAttributeNameResource(i) == 0x01010003) {
                        String name = parser.getAttributeValue(i);
                        if (name.startsWith(".")) {
                            name = PackageName + name;
                        }
                        list.add(name);
                    }
                }
            }
        }
        return list;
    }
    private byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    //显示提示框
    private void showmsg(final String msg) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ProgressDialog.hide();
                ProgressDialog.show(getActivity(), msg);
            }
        });
    }

//ok
    private void writeInt(byte[] data, int off, int value) {
        data[off++] = (byte) (value & 0xFF);
        data[off++] = (byte) ((value >>> 8) & 0xFF);
        data[off++] = (byte) ((value >>> 16) & 0xFF);
        data[off] = (byte) ((value >>> 24) & 0xFF);
    }

    private int readInt(byte[] data, int off) {
        return data[off + 3] << 24 | (data[off + 2] & 0xFF) << 16 | (data[off + 1] & 0xFF) << 8
                | data[off] & 0xFF;
    }

    //解析apk的dex数量
    @Override
    public void onSelectedFilePaths(String[] p1) {
        path = p1[0];//选择文件路径
        clear();
        try {
            ZipFile z = new ZipFile(path);
            Enumeration<ZipEntry> e = z.getEntries();
            while (e.hasMoreElements()) {
                ZipEntry ze = e.nextElement();
                if (ze.isDirectory()) {
                    continue;
                } else if (ze.getName().startsWith("classes")
                        && ze.getName().endsWith("dex")) {
                    dex.add(ze.getName());
                }
            }
        } catch (IOException e) {
            mToast.Show(getContext(),e.getMessage());
        }
    }

    private void SetActiity(String str, final List<String> list) {
        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MethodAdapter methodAdapter = new MethodAdapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(methodAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(str);
        builder.setView(recyclerView);
        builder.setCancelable(false);
       final AlertDialog create = builder.create();
        create.show();
        methodAdapter.setOnItemClickListener(new MethodAdapter.OnItemClickListener( ) {
            @Override
            public void onItemClick(View view, int i) {
                dex.add(list.get(i).toString());//
                mToast.Show(getContext(),new StringBuffer().append("抽取类 -> ").append(list.get(i)).toString());
                create.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainFragment.this.DlRun();
                    }
                });
            }

        });


    }

//ok
    private void SetActiity_Method(String Msg, final boolean Type, final List<String> list) {
	/*
	 true activity
	 false method
	 */
        RecyclerView item = new RecyclerView(getActivity());
        item.setLayoutManager(new LinearLayoutManager(getActivity()));
        MethodAdapter methodadapter = new MethodAdapter(list);
        item.setHasFixedSize(true);
        item.setAdapter(methodadapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(Msg);
        builder.setView(item);
        builder.setCancelable(false);
        final AlertDialog dialog;
        dialog = builder.create();
        dialog.show();
        methodadapter.setOnItemClickListener(new MethodAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (Type) {
                    mainactivity = list.get(position).toString();
                    mToast.Show(getContext(),"Activity接口 -> " + mainactivity);
                    dialog.dismiss();
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            showmsg("解析Method......");
                            ParserMethod(zip);
                        }
                    });
                } else {
                    method = list.get(position).toString();
                    mToast.Show(getContext(),"Method接口 -> " + method);
                    dialog.dismiss();
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            DlRun();
                        }
                    });
                }
            }
        });
    }
//    private void ParserMethod(final ZipFile zipFile) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ProgressDialog.hide();
//                ProgressDialog.show(getActivity(),"保存dex中.....",zipFile.getEntrySize());
//            }
//        }).start();
//    }
//
    //ok
    private void ParserMethod(final ZipFile zip) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (dex.size() == 0) {
                        ZipFile z = new ZipFile(path);
                        Enumeration<ZipEntry> e = z.getEntries();
                        while (e.hasMoreElements()) {
                            ZipEntry ze = e.nextElement();
                            if (ze.isDirectory()) {
                                continue;
                            } else if (ze.getName().startsWith("classes")
                                    && ze.getName().endsWith("dex")) {
                                dex.add(ze.getName());
                            }
                        }
                    }
                    DexBackedDexFile cl;
                    final List<String> methodlist = new ArrayList();
                    for (final String dexfile : dex) {
                        if (maindex != null) break;
                        cl = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(new BufferedInputStream(zip.getInputStream(zip.getEntry(dexfile)))));
                        if (cl.getClasses() == null) {
                            throw new Exception("Dex读取失败可能已经加密");
                        }
                        for (DexBackedClassDef h : cl.getClasses()) {
                            if (h.getType().equals("L" + mainactivity.replace(".", "/") + ";")) {
                                maindex = dexfile;
                                for (DexBackedMethod m : h.getMethods()) {
                                    methodlist.add(m.getName());
                                }
                                break;
                            }
                        }
                    }
                    if (methodlist.size() == 0) {
                        throw new Exception("MetHod解析失败可能已经加密");
                    }
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ProgressDialog.hide();
                            SetActiity_Method("请选择注入Method", false, methodlist);
//                            SetActiity("请选择注入Method",  methodlist);
                        }
                    });
                } catch (Exception e) {
                    mToast.Show(getContext(),e.getMessage());
                    clear();
                    ProgressDialog.hide();
                }
            }
        }).start();
    }


    //修改reg
    private String EditReg(String buff) throws Exception {
        String rgex = ".registers [0-9]{1,2}";
        Pattern p = Pattern.compile(rgex);
        Matcher m = p.matcher(buff);
        String r = null;
        while (m.find()) {
            r = m.group();
        }
        if (r == null) {
            throw new Exception("没有找到registers");
        }
        int reg = Integer.parseInt(r.replace(".registers ", ""));
        while (true) {
            if (reg < 12) {
                reg++;
            } else {
                break;
            }
        }
        buff = buff.replace(r, ".registers " + reg);
        return buff;
    }

    //清理数据57
    private void clear() {
        customApplication = false;
        dex.clear();
        zip = null;
        mainactivity = null;
        maindex = null;
        method = null;
        ChouClass.clear();
        mClassDef.clear();
        ProgressDialog.hide();
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {//getId()
            case R.id.qj:
                this.custom = true;
                mToast.Show(getContext(),"已经切换为全局注册机模式");
                return;
            case R.id.dl:
                this.custom = false;
                mToast.Show(getContext(), "已经切换为单例注册机模式");
                return;
            default:
                return;
        }
    }
    //设置颜色
    public void SetColor(String color) {
        GetBulletin.setText(color);
    }

    //更新进度
    private void s(final int i) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ma.mhy.apkhook.ui.ProgressDialog.setProgress(i);
            }
        });
    }
    //Axml编辑
    private byte[] parseManifest(InputStream is) throws IOException {
        AXmlDecoder axml = AXmlDecoder.decode(is);
        AXmlResourceParser parser = new AXmlResourceParser();
        parser.open(new ByteArrayInputStream(axml.getData()), axml.mTableStrings);
        boolean success = false;

        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG)
                continue;
            if (parser.getName().equals("manifest")) {
                int size = parser.getAttributeCount();
                for (int i = 0; i < size; ++i) {
                    if (parser.getAttributeName(i).equals("package")) {
                        packageName = parser.getAttributeValue(i);
                    }
                }
            } else if (parser.getName().equals("application")) {
                int size = parser.getAttributeCount();
                for (int i = 0; i < size; ++i) {
                    if (parser.getAttributeNameResource(i) == 0x01010003) {
                        customApplication = true;
                        customApplicationName = parser.getAttributeValue(i);
                        int index = axml.mTableStrings.getSize();
                        byte[] data = axml.getData();
                        int off = parser.currentAttributeStart + 20 * i;
                        off += 8;
                        writeInt(data, off, index);
                        off += 8;
                        writeInt(data, off, index);
                    }
                }
                if (!customApplication) {
                    int off = parser.currentAttributeStart;
                    byte[] data = axml.getData();
                    byte[] newData = new byte[data.length + 20];
                    System.arraycopy(data, 0, newData, 0, off);
                    System.arraycopy(data, off, newData, off + 20, data.length - off);

                    // chunkSize
                    int chunkSize = readInt(newData, off - 32);
                    writeInt(newData, off - 32, chunkSize + 20);
                    // attributeCount
                    writeInt(newData, off - 8, size + 1);

                    int idIndex = parser.findResourceID(0x01010003);
                    if (idIndex == -1)
                        throw new IOException("idIndex == -1");

                    boolean isMax = true;
                    for (int i = 0; i < size; ++i) {
                        int id = parser.getAttributeNameResource(i);
                        if (id > 0x01010003) {
                            isMax = false;
                            if (i != 0) {
                                System.arraycopy(newData, off + 20, newData, off, 20 * i);
                                off += 20 * i;
                            }
                            break;
                        }
                    }
                    if (isMax) {
                        System.arraycopy(newData, off + 20, newData, off, 20 * size);
                        off += 20 * size;
                    }

                    writeInt(newData, off, axml.mTableStrings.find("http://schemas.android.com/apk/res/android"));
                    writeInt(newData, off + 4, idIndex);
                    writeInt(newData, off + 8, axml.mTableStrings.getSize());
                    writeInt(newData, off + 12, 0x03000008);
                    writeInt(newData, off + 16, axml.mTableStrings.getSize());
                    axml.setData(newData);
                }
                success = true;
                break;
            }
        }
        if (!success)
            throw new IOException();
        ArrayList<String> list = new ArrayList<>(axml.mTableStrings.getSize());
        axml.mTableStrings.getStrings(list);
        list.add("com.App");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        axml.write(list, baos);
        return baos.toByteArray();
    }


    //ok run
    @Override
    public void run() {
        FileOutputStream error = null;
        try {
            zip = new ZipFile(path);
            error = new FileOutputStream(new File("sdcard/apkhook错误日志记录.txt"));
            AssetsUtils.copy(getActivity().getAssets(), "key.jks", "key.jks");//找到文件>命名为文件
            DexBuilder builder = new DexBuilder(Opcodes.getDefault());
            builder.setIgnoreMethodAndFieldError(true);
            showmsg("读取dex.....");
            if (!custom) {
                //单例模式
                DexBackedDexFile classes = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(new BufferedInputStream(zip.getInputStream(zip.getEntry(maindex)))));
                DexBackedDexFile data = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(getActivity().getAssets().open("data.dat")));
                final Set<? extends DexBackedClassDef> l = new HashSet<>();
                l.addAll((HashSet)classes.getClasses());
                l.addAll((HashSet)data.getClasses());
                List<DexBackedClassDef> c = Lists.newArrayList(l.iterator());
                showmsg("修改接口信息中.....");
                ClassPath classpath1 = new ClassPath(Lists.newArrayList(new DexClassProvider(classes)), false, classes.getClasses().size());
                DexBackedClassDef a1 = (DexBackedClassDef) classpath1.getClassDef("L" + mainactivity.replace(".", "/") + ";");
                a(a1, builder);

                //注入的dex
                ClassPath classpath2 = new ClassPath(Lists.newArrayList(new DexClassProvider(data)), false, data.getClasses().size());
                DexBackedClassDef a2 = (DexBackedClassDef) classpath2.getClassDef("Lcom/App;");
                EditConfig(a2, builder);

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ProgressDialog.hide();
                        ProgressDialog.show(getActivity(), "合并dex中......", l.size());
                    }
                });
                for (int i = 0; i < c.size(); i++) {
                    DexBackedClassDef cl = c.get(i);
                    if (!cl.getType().equals("L" + mainactivity.replace(".", "/") + ";")
                            && !cl.getType().equals("Lcom/App;")) {
                        builder.internClassDef(cl);
                    }
                    s(i);
                }
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ProgressDialog.hide();
                        ProgressDialog.show(getActivity(), "重组APK中......", zip.getEntrySize() - 2);
                    }
                });
                ByteArrayOutputStream a = new ByteArrayOutputStream();
                AXMLDoc doc = new AXMLDoc();
                doc.parse(new ByteArrayInputStream(toByteArray(zip.getInputStream(zip.getEntry("AndroidManifest.xml")))));
                PermissionEditor permissionEditor = new PermissionEditor(doc);
                permissionEditor.setEditorInfo(new PermissionEditor.EditorInfo()
                        .with(new PermissionEditor.PermissionOpera("android.permission.本网络验证由缥缈轩(天狼星)提供").add())
                        .with(new PermissionEditor.PermissionOpera("android.permission.QQ.1976222027").add())
                        .with(new PermissionEditor.PermissionOpera("android.permission.Perfume").add())
                        .with(new PermissionEditor.PermissionOpera("android.permission.INTERNET").add())
                        .with(new PermissionEditor.PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add())
                        .with(new PermissionEditor.PermissionOpera("android.permission.ACCESS_WIFI_STATE").add())
                        .with(new PermissionEditor.PermissionOpera("android.permission.READ_PHONE_STATE").add())
                );
                permissionEditor.commit();
                doc.build(a);
                doc.release();
                ZipOutputStream zos = new ZipOutputStream(new File("sdcard/a.apk"));
                zos.setLevel(1);
                zos.putNextEntry("AndroidManifest.xml");
                zos.write(a.toByteArray());
                zos.closeEntry();

                MemoryDataStore store = new MemoryDataStore();
                builder.writeTo(store);
                zos.putNextEntry(maindex);
                zos.write(Arrays.copyOf(store.getBufferData(), store.getSize()));
                zos.closeEntry();
                Enumeration<ZipEntry> enumeration = zip.getEntries();
                int i = 0;
                while (enumeration.hasMoreElements()) {
                    ZipEntry ze = enumeration.nextElement();
                    if (ze.getName().equals("AndroidManifest.xml")
                            || ze.getName().equals(maindex))
                        continue;
                    zos.copyZipEntry(ze, zip);
                    s(i++);
                }
                zos.close();
                zip.close();
               ZipSigner z = new ZipSigner();
                showmsg("签名中.....");
              CustomKeySigner.signZip(z,
                        "sdcard/key.jks",
                        "123456789".toCharArray(),
                        "缥缈轩",
                        "123456789".toCharArray(),
                        "SHA1withRSA",
                        "sdcard/a.apk",
                        "sdcard/缥缈轩网络验证.apk");
                new File("sdcard/a.apk").delete();
            } else {
                if (dex.size() == 0) {
                    ZipFile z = new ZipFile(path);
                    Enumeration<ZipEntry> e = z.getEntries();
                    while (e.hasMoreElements()) {
                        ZipEntry ze = e.nextElement();
                        if (ze.isDirectory()) {
                            continue;
                        } else if (ze.getName().startsWith("classes")
                                && ze.getName().endsWith("dex")) {
                            dex.add(ze.getName());
                        }
                    }
                }
                //全局模式
                if (JiaGu.isChecked()) {
                    //跳加固
                    final DexBackedDexFile data = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(new BufferedInputStream(getActivity().getAssets().open("data.dat"))));
                    showmsg("修改接口信息中.....");
                    ZipEntry XmlEntry = zip.getEntry("AndroidManifest.xml");
                    byte[] xml = parseManifest(zip.getInputStream(XmlEntry));
                    if (customApplication) {
                        ClassPath classpath3 = new ClassPath(Lists.newArrayList(new DexClassProvider(data)), false, data.getClasses().size());
                        DexBackedClassDef a3 = (DexBackedClassDef) classpath3.getClassDef("Lcom/App;");
                        StringWriter stringWriter = new StringWriter();
                        IndentingWriter writer = new IndentingWriter(stringWriter);
                        ClassDefinition classDefinition = new ClassDefinition(new BaksmaliOptions(), a3);
                        classDefinition.writeTo(writer);
                        writer.close();
                        String code2 = stringWriter.toString();
                        if (customApplicationName.startsWith(".")) {
                            if (packageName == null)
                                throw new NullPointerException("Package name is null.");
                            customApplicationName = packageName + customApplicationName;
                        }
                        customApplicationName = "L" + customApplicationName.replace('.', '/') + ";";
                        code2 = code2.replace("Landroid/app/Application;", customApplicationName);
                        int keyLength = 512;
                        KeyPair keyPair = RSA.generateRSAKeyPair(keyLength);
                        RSAPublicKey Public = RSA.getPublicKey(keyPair);
                        RSAPrivateKey Private = RSA.getPrivateKey(keyPair);
                        byte[] PublicKey = Base64.encodeBase64(Public.getEncoded());
                        byte[] PrivateKey = Base64.encodeBase64(Private.getEncoded());
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        Properties conf = new Properties();
                        conf.setProperty("Appid", Appid.getText().toString().trim());
                        conf.setProperty("Bt", SingleLogin.getText().toString().trim());
                        conf.setProperty("Hint", GetExpired.getText().toString().trim());
                        conf.setProperty("Color", GetBulletin.getText().toString().trim());
                        conf.setProperty("ShowAd", Ad.isChecked() ? "true" : "false");
                        conf.setProperty("Ver", Ver.getText().toString().trim());

                        if (JiaGu.isChecked()) {
                            if (this.New360.isChecked()) {//add
                                JumpNew360((DexBackedDexFile)data, (DexBuilder)builder);
                            }//
                            conf.setProperty("SignHook", "Hook");
                            code2 = code2.replace("SignHookData", ApkSign.getApkSignInfo(path));
                            JumpJiaGu((DexBackedDexFile)data, builder);// ADD
                    }
                    //add
                        if (this.dex.size() != 1) {
                            Qj((DexBackedDexFile)data, builder, builder);
                        } else if (this.dex.size() == 1) {
                            Qjd((DexBackedDexFile)data, builder, builder);
                        }//add

                        conf.save(out, "Perfume QQ1834661238");
                        byte[] Data = RSA.encryptWithPrivateKey(out.toByteArray(), RSA.generatePrivateKey(Base64.decodeBase64(PrivateKey)));
                        out.close();
                        StringBuffer a = new StringBuffer();
                        for (int i = 0; i < PublicKey.length; i++) {
                            a.append("0x" + Integer.toHexString(PublicKey[i]) + "t\n");
                        }
                        code2 = code2.replace("Config_Data", new String(Base64.encodeBase64(Data)));
                        code2 = code2.replace("const/4 v0, 0x6", "const/16 v0,0x" + Integer.toHexString(PublicKey.length));
                        code2 = code2.replace("-0x50t", a.toString());

                        Smali.assembleSmaliFile(code2, builder, new SmaliOptions());
                    } else {
                        ClassPath classpath2 = new ClassPath(Lists.newArrayList(new DexClassProvider(data)), false, data.getClasses().size());
                        DexBackedClassDef a2 = (DexBackedClassDef) classpath2.getClassDef("Lcom/App;");
                        EditConfig(a2, builder);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressDialog.hide();
                            ProgressDialog.show(getActivity(), "保存dex中......", data.getClasses().size());
                        }
                    });
                    int i = 0;
                    for (DexBackedClassDef cl : data.getClasses()) {
                        if (!cl.getType().equals("Lcom/App;")) {
                            if (customApplication && cl.getType().equals("Lcom/App;")) {
                                continue;
                            } else {
                                builder.internClassDef(cl);
                            }
                        }
                        s(i++);
                    }
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                           ProgressDialog.hide();
                            ProgressDialog.show(getActivity(), "重组APK中......", zip.getEntrySize() - 2);
                        }
                    });
                    ByteArrayOutputStream a = new ByteArrayOutputStream();
                    AXMLDoc doc = new AXMLDoc();
                    doc.parse(new ByteArrayInputStream(xml));
                    PermissionEditor permissionEditor = new PermissionEditor(doc);
                    permissionEditor.setEditorInfo(new PermissionEditor.EditorInfo()
                            .with(new PermissionEditor.PermissionOpera("android.permission.本网络验证由缥缈轩(天狼星)提供").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.QQ.1834661238").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.Perfume").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.INTERNET").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.ACCESS_WIFI_STATE").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.READ_PHONE_STATE").add())
                    );
                    permissionEditor.commit();
                    doc.build(a);
                    doc.release();
                    ZipOutputStream zos = new ZipOutputStream(new File("sdcard/a.apk"));
                    zos.setLevel(1);
                    zos.putNextEntry("AndroidManifest.xml");
                    zos.write(a.toByteArray());
                    zos.closeEntry();

                    MemoryDataStore store = new MemoryDataStore();
                    builder.writeTo(store);
                    zos.putNextEntry("classes" + (dex.size() + 1) + ".dex");
                    zos.write(Arrays.copyOf(store.getBufferData(), store.getSize()));
                    zos.closeEntry();
                    Enumeration<ZipEntry> enumeration = zip.getEntries();
                    i = 0;
                    while (enumeration.hasMoreElements()) {
                        ZipEntry ze = enumeration.nextElement();
                        if (ze.getName().equals("AndroidManifest.xml")
                                || ze.getName().equals("classes" + (dex.size() + 1) + ".dex"))
                            continue;
                        zos.copyZipEntry(ze, zip);
                        s(i++);
                    }
                    zos.close();
                    zip.close();
                    ZipSigner z = new ZipSigner();
                    showmsg("签名中.....");
                    CustomKeySigner.signZip(z,
                            "sdcard/key.jks",
                            "123456789".toCharArray(),
                            "缥缈轩",
                            "123456789".toCharArray(),
                            "SHA1withRSA",
                            "sdcard/a.apk",
                            "sdcard/缥缈轩网络验证.apk");
                    new File("sdcard/a.apk").delete();
                } else if (dex.size() != 1) {
                    //多dex
                    final DexBackedDexFile data = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(new BufferedInputStream(getActivity().getAssets().open("data.dat"))));
                    showmsg("修改接口信息中.....");
                    ZipEntry XmlEntry = zip.getEntry("AndroidManifest.xml");
                    byte[] xml = parseManifest(zip.getInputStream(XmlEntry));
                    if (customApplication) {
                        ClassPath classpath3 = new ClassPath(Lists.newArrayList(new DexClassProvider(data)), false, data.getClasses().size());
                        DexBackedClassDef a3 = (DexBackedClassDef) classpath3.getClassDef("Lcom/App;");
                        StringWriter stringWriter = new StringWriter();
                        IndentingWriter writer = new IndentingWriter(stringWriter);
                        ClassDefinition classDefinition = new ClassDefinition(new BaksmaliOptions(), a3);
                        classDefinition.writeTo(writer);
                        writer.close();
                        String code2 = stringWriter.toString();
                        if (customApplicationName.startsWith(".")) {
                            if (packageName == null)
                                throw new NullPointerException("Package name is null.");
                            customApplicationName = packageName + customApplicationName;
                        }
                        customApplicationName = "L" + customApplicationName.replace('.', '/') + ";";
                        code2 = code2.replace("Landroid/app/Application;", customApplicationName);

                        int keyLength = 512;
                        KeyPair keyPair = RSA.generateRSAKeyPair(keyLength);
                        RSAPublicKey Public = RSA.getPublicKey(keyPair);
                        RSAPrivateKey Private = RSA.getPrivateKey(keyPair);
                        byte[] PublicKey = Base64.encodeBase64(Public.getEncoded());
                        byte[] PrivateKey = Base64.encodeBase64(Private.getEncoded());
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        Properties conf = new Properties();
                        conf.setProperty("Appid", Appid.getText().toString().trim());
                        conf.setProperty("Bt", SingleLogin.getText().toString().trim());
                        conf.setProperty("Hint", GetExpired.getText().toString().trim());
                        conf.setProperty("Color", GetBulletin.getText().toString().trim());
                        conf.setProperty("ShowAd", Ad.isChecked() ? "true" : "false");
                        conf.setProperty("Ver", Ver.getText().toString().trim());
                        if (JiaGu.isChecked()) {
                            conf.setProperty("SignHook", "Hook");
                            code2 = code2.replace("SignHookData", ApkSign.getApkSignInfo(path));
                        }
                        conf.save(out, "Perfume QQ1834661238");
                        byte[] Data = RSA.encryptWithPrivateKey(out.toByteArray(), RSA.generatePrivateKey(Base64.decodeBase64(PrivateKey)));
                        out.close();
                        StringBuffer a = new StringBuffer();
                        for (int i = 0; i < PublicKey.length; i++) {
                            a.append("0x" + Integer.toHexString(PublicKey[i]) + "t\n");
                        }
                        code2 = code2.replace("Config_Data", new String(Base64.encodeBase64(Data)));
                        code2 = code2.replace("const/4 v0, 0x6", "const/16 v0,0x" + Integer.toHexString(PublicKey.length));
                        code2 = code2.replace("-0x50t", a.toString());


                        Smali.assembleSmaliFile(code2, builder, new SmaliOptions());
                    } else {
                        ClassPath classpath2 = new ClassPath(Lists.newArrayList(new DexClassProvider(data)), false, data.getClasses().size());
                        DexBackedClassDef a2 = (DexBackedClassDef) classpath2.getClassDef("Lcom/App;");
                        EditConfig(a2, builder);
                    }
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ma.mhy.apkhook.ui.ProgressDialog.hide();
                            ma.mhy.apkhook.ui.ProgressDialog.show(getActivity(), "保存dex中......", data.getClasses().size());
                        }
                    });
                    int i = 0;
                    for (DexBackedClassDef cl : data.getClasses()) {
                        if (!cl.getType().equals("Lcom/App;")) {
                            if (customApplication && cl.getType().equals("Lcom/App;")) {
                                continue;
                            } else {
                                builder.internClassDef(cl);
                            }
                        }
                        s(i++);
                    }
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ma.mhy.apkhook.ui.ProgressDialog.hide();
                            ma.mhy.apkhook.ui.ProgressDialog.show(getActivity(), "重组APK中......", zip.getEntrySize() - 2);
                        }
                    });
                    ByteArrayOutputStream a = new ByteArrayOutputStream();
                    AXMLDoc doc = new AXMLDoc();
                    doc.parse(new ByteArrayInputStream(xml));
                    PermissionEditor permissionEditor = new PermissionEditor(doc);
                    permissionEditor.setEditorInfo(new PermissionEditor.EditorInfo()
                            .with(new PermissionEditor.PermissionOpera("android.permission.本网络验证由缥缈轩(天狼星)提供").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.QQ.1834661238").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.Perfume").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.INTERNET").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.ACCESS_WIFI_STATE").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.READ_PHONE_STATE").add())
                    );
                    permissionEditor.commit();
                    doc.build(a);
                    doc.release();
                    ZipOutputStream zos = new ZipOutputStream(new File("sdcard/a.apk"));
                    zos.setLevel(1);
                    zos.putNextEntry("AndroidManifest.xml");
                    zos.write(a.toByteArray());
                    zos.closeEntry();

                    MemoryDataStore store = new MemoryDataStore();
                    builder.writeTo(store);
                    zos.putNextEntry("classes" + (dex.size() + 1) + ".dex");
                    zos.write(Arrays.copyOf(store.getBufferData(), store.getSize()));
                    zos.closeEntry();
                    Enumeration<ZipEntry> enumeration = zip.getEntries();
                    i = 0;
                    while (enumeration.hasMoreElements()) {
                        ZipEntry ze = enumeration.nextElement();
                        if (ze.getName().equals("AndroidManifest.xml")
                                || ze.getName().equals("classes" + (dex.size() + 1) + ".dex"))
                            continue;
                        zos.copyZipEntry(ze, zip);
                        s(i++);
                    }
                    zos.close();
                    zip.close();
                    ZipSigner z = new ZipSigner();
                    showmsg("签名中.....");
                    CustomKeySigner.signZip(z,
                            "sdcard/key.jks",
                            "123456789".toCharArray(),
                            "缥缈轩",
                            "123456789".toCharArray(),
                            "SHA1withRSA",
                            "sdcard/a.apk",
                            "sdcard/缥缈轩网络验证.apk");
                    new File("sdcard/a.apk").delete();
                } else if (dex.size() == 1) {
                    //单dex
                    ZipEntry dexEntry = zip.getEntry("classes.dex");
                    DexBackedDexFile classes = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(new BufferedInputStream(zip.getInputStream(dexEntry))));
                    DexBackedDexFile data = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new BufferedInputStream(new BufferedInputStream(getActivity().getAssets().open("data.dat"))));
                    final Set<? extends DexBackedClassDef> l = new HashSet<>();
                    l.addAll((HashSet)classes.getClasses());
                    l.addAll((HashSet)data.getClasses());
                //    addAll(java.util.Collection<? extends capture<? extends org>>)
                 // addAll(java.util.Collection<>  in Set cannot be applied to (java.util.Set<capture<? extends org>>)

                    List<DexBackedClassDef> c = Lists.newArrayList(l.iterator());
                    showmsg("修改接口信息中.....");
                    ZipEntry XmlEntry = zip.getEntry("AndroidManifest.xml");
                    byte[] xml = parseManifest(zip.getInputStream(XmlEntry));
                    if (customApplication) {
                        ClassPath classpath3 = new ClassPath(Lists.newArrayList(new DexClassProvider(data)), false, data.getClasses().size());
                        DexBackedClassDef a3 = (DexBackedClassDef) classpath3.getClassDef("Lcom/App;");
                        StringWriter stringWriter = new StringWriter();
                        IndentingWriter writer = new IndentingWriter(stringWriter);
                        ClassDefinition classDefinition = new ClassDefinition(new BaksmaliOptions(), a3);
                        classDefinition.writeTo(writer);
                        writer.close();
                        String code2 = stringWriter.toString();
                        if (customApplicationName.startsWith(".")) {
                            if (packageName == null)
                                throw new NullPointerException("Package name is null.");
                            customApplicationName = packageName + customApplicationName;
                        }
                        customApplicationName = "L" + customApplicationName.replace('.', '/') + ";";
                        code2 = code2.replace("Landroid/app/Application;", customApplicationName);

                        int keyLength = 512;
                        KeyPair keyPair = RSA.generateRSAKeyPair(keyLength);
                        RSAPublicKey Public = RSA.getPublicKey(keyPair);
                        RSAPrivateKey Private = RSA.getPrivateKey(keyPair);
                        byte[] PublicKey = Base64.encodeBase64(Public.getEncoded());
                        byte[] PrivateKey = Base64.encodeBase64(Private.getEncoded());
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        Properties conf = new Properties();
                        conf.setProperty("Appid", Appid.getText().toString().trim());
                        conf.setProperty("Bt", SingleLogin.getText().toString().trim());
                        conf.setProperty("Hint", GetExpired.getText().toString().trim());
                        conf.setProperty("Color", GetBulletin.getText().toString().trim());
                        conf.setProperty("ShowAd", Ad.isChecked() ? "true" : "false");
                        conf.setProperty("Ver", Ver.getText().toString().trim());
                        if (JiaGu.isChecked()) {
                            conf.setProperty("SignHook", "Hook");
                            code2 = code2.replace("SignHookData", ApkSign.getApkSignInfo(path));
                        }
                        conf.save(out, "Perfume QQ1834661238");
                        byte[] Data = RSA.encryptWithPrivateKey(out.toByteArray(), RSA.generatePrivateKey(Base64.decodeBase64(PrivateKey)));
                        out.close();
                        StringBuffer a = new StringBuffer();
                        for (int i = 0; i < PublicKey.length; i++) {
                            a.append("0x" + Integer.toHexString(PublicKey[i]) + "t\n");
                        }
                        code2 = code2.replace("Config_Data", new String(Base64.encodeBase64(Data)));
                        code2 = code2.replace("const/4 v0, 0x6", "const/16 v0,0x" + Integer.toHexString(PublicKey.length));
                        code2 = code2.replace("-0x50t", a.toString());


                        Smali.assembleSmaliFile(code2, builder, new SmaliOptions());
                    } else {
                        ClassPath classpath2 = new ClassPath(Lists.newArrayList(new DexClassProvider(data)), false, data.getClasses().size());
                        DexBackedClassDef a2 = (DexBackedClassDef) classpath2.getClassDef("Lcom/App;");
                        EditConfig(a2, builder);
                    }
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ma.mhy.apkhook.ui.ProgressDialog.hide();
                            ma.mhy.apkhook.ui.ProgressDialog.show(getActivity(), "合并dex中......", l.size());
                        }
                    });
                    for (int i = 0; i < c.size(); i++) {
                        DexBackedClassDef cl = c.get(i);
                        if (!cl.getType().equals("Lcom/App;")) {
                            if (customApplication && cl.getType().equals("Lcom/App;")) {
                                continue;
                            } else {
                                builder.internClassDef(cl);
                            }
                        }
                        s(i);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ma.mhy.apkhook.ui.ProgressDialog.hide();
                            ma.mhy.apkhook.ui.ProgressDialog.show(getActivity(), "重组APK中......", zip.getEntrySize() - 2);
                        }
                    });
                    ByteArrayOutputStream a = new ByteArrayOutputStream();
                    AXMLDoc doc = new AXMLDoc();
                    doc.parse(new ByteArrayInputStream(xml));
                    PermissionEditor permissionEditor = new PermissionEditor(doc);
                    permissionEditor.setEditorInfo(new PermissionEditor.EditorInfo()
                            .with(new PermissionEditor.PermissionOpera("android.permission.本工具由MHY提供").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.QQ.1976222027").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.Perfume").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.INTERNET").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.ACCESS_NETWORK_STATE").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.ACCESS_WIFI_STATE").add())
                            .with(new PermissionEditor.PermissionOpera("android.permission.READ_PHONE_STATE").add())
                    );
                    permissionEditor.commit();
                    doc.build(a);
                    doc.release();
                    ZipOutputStream zos = new ZipOutputStream(new File("sdcard/a.apk"));
                    zos.setLevel(1);
                    zos.putNextEntry("AndroidManifest.xml");
                    zos.write(a.toByteArray());
                    zos.closeEntry();

                    MemoryDataStore store = new MemoryDataStore();
                    builder.writeTo(store);
                    zos.putNextEntry("classes.dex");
                    zos.write(Arrays.copyOf(store.getBufferData(), store.getSize()));
                    zos.closeEntry();
                    Enumeration<ZipEntry> enumeration = zip.getEntries();
                    int i = 0;
                    while (enumeration.hasMoreElements()) {
                        ZipEntry ze = enumeration.nextElement();
                        if (ze.getName().equals("AndroidManifest.xml")
                                || ze.getName().equals("classes.dex"))
                            continue;
                        zos.copyZipEntry(ze, zip);
                        s(i++);
                    }
                    zos.close();
                    zip.close();
                   ZipSigner z = new ZipSigner();
                    showmsg("签名中.....");
                    CustomKeySigner.signZip(z,
                            "sdcard/key.jks",
                            "123456789".toCharArray(),
                            "缥缈轩",
                            "123456789".toCharArray(),
                            "SHA1withRSA",
                            "sdcard/a.apk",
                            "sdcard/缥缈轩网络验证.apk");
                    new File("sdcard/a.apk").delete();
                }
            }
            mToast.Show(getContext(),"注入完成 APK路径->" + "sdcard/缥缈轩网络验证.apk");
        } catch (final Exception e) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream pout = new PrintStream(out);
            e.printStackTrace(pout);
            String ret = new String(out.toByteArray());
            pout.close();
            try {
                out.close();
                error.write(ret.getBytes());
                error.close();
            } catch (Exception me) {
            }
            mToast.ShowDialog(getActivity(), ret);
            if (ret.contains("Unsigned short value out of range:")) {
                mToast.Show(getContext(),"Dex合并输出失败 类过多 超出65535");
            }
        } finally {
            if (new File("sdcard/key.jks").exists())
                new File("sdcard/key.jks").delete();
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ProgressDialog.hide();
                    clear();
                }
            });
        }
    }

}