package com.example.fileproviderdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends FragmentActivity {
    String key="D5E483D8B90C02BD4D470BA8049E1FA6";
    String iv ="1D64EB2BFA444CBF9853CDFB8B24DA7A";
    private Button openBtn;
    private Button encryptBtn;
    private Button decryptBtn;
    private String encryptStr = "";
    private Button encryptBtnAes;
    private Button decryptBtnAes;
    private String aesStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openBtn = (Button)findViewById(R.id.openBtn);
        encryptBtn = (Button)findViewById(R.id.encryptBtn);
        decryptBtn = (Button)findViewById(R.id.decryptBtn);
        encryptBtnAes = (Button)findViewById(R.id.encryptBtnAes);
        decryptBtnAes = (Button)findViewById(R.id.decryptBtnAes);
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //:/data/user/0/com.example.fileproviderdemo/cache
                Log.i("wanlijun","getCacheDir:"+getCacheDir().toString());
                //:/data/user/0/com.example.fileproviderdemo/files
                Log.i("wanlijun","getFilesDir:"+getFilesDir().toString());
                //:/storage/emulated/0/Android/data/com.example.fileproviderdemo/cache
                //手机上的/Android/data/com.example.fileproviderdemo/cache目录
                Log.i("wanlijun","getExternalCacheDir:"+getExternalCacheDir().toString());
                //:/storage/emulated/0/Android/data/com.example.fileproviderdemo/files/text/plain
                //手机上的/Android/data/com.example.fileproviderdemo/files/text/plain目录
                Log.i("wanlijun","getExternalFilesDir:"+getExternalFilesDir("text/plain").toString());
                //:/storage/emulated/0
                //手机上内部存储设备的根目录
                Log.i("wanlijun","getExternalStorageDirectory:"+ Environment.getExternalStorageDirectory().toString());
                //:/data
                Log.i("wanlijun","getDataDirectory:"+Environment.getDataDirectory().toString());
                //:/data/cache
                Log.i("wanlijun","getDownloadCacheDirectory:"+Environment.getDownloadCacheDirectory().toString());
                //:/storage/emulated/0/text/plain
                Log.i("wanlijun","getExternalStoragePublicDirectory:"+Environment.getExternalStoragePublicDirectory("text/plain").toString());
                File file = new File(getExternalFilesDir("text/plain"),"a.txt");

//                File file = new File(Environment.getExternalStorageDirectory()+"/zhihu","a.txt");
                Log.i("wanlijun",file.getAbsolutePath());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    Uri uri = FileProvider.getUriForFile(MainActivity.this,getPackageName()+".provider",file);
                    Log.i("wanlijun",uri.toString());
                    intent.setDataAndType(uri,"text/plain");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }else {
                    intent.setDataAndType(Uri.fromFile(file), "text/plain");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        encryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AES用seed加密
                String cleartext = "醉卧沙场君莫笑，古来征战几人回";
                String encrypt = AESUtils.encrypt(AESUtils.MY_SEED,cleartext);
                Log.i("wanlijun","encrypt="+encrypt);
                encryptStr = encrypt;
            }
        });
        decryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AES用seed解密
                Log.i("wanlijun","encryptStr="+encryptStr);
                String decrypt = AESUtils.decrypt(AESUtils.MY_SEED,encryptStr);
                Log.i("wanlijun","decrypt="+decrypt);
            }
        });
        encryptBtnAes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AES用对称秘钥（key,iv）加密
                String cleartext = "醉卧沙场君莫笑，古来征战几人回";
                String ciphertext = AESUtils.encrypt(cleartext,key,iv);
                aesStr = ciphertext;
                Log.i("wanlijun","ciphertext="+ciphertext);
            }
        });
        decryptBtnAes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AES用对称秘钥（key,iv）解密
                String cleartext = AESUtils.decrypt(aesStr,key,iv);
                Log.i("wanlijun","cleartext="+cleartext);
            }
        });
    }
}
