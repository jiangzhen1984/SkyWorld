package com.netease.nim.demo.file.browser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.demo.file.browser.FileBrowserAdapter.FileManagerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * �ļ�������
 * Created by hzxuwen on 2015/4/17.
 */
public class FileBrowserActivity extends TActionBarActivity implements TAdapterDelegate {
    // constant
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    public static final String EXTRA_DATA_PATH = "EXTRA_DATA_PATH";

    // data
    private ArrayList<String> names = null; //�洢�ļ�����
    private ArrayList<String> paths = null; //�洢�ļ�·��

    // view
    private ListView fileListView;
    private List<FileManagerItem> fileListItems = new ArrayList<>();

    public static void startActivityForResult(Activity activity, int reqCode) {
        Intent intent = new Intent();
        intent.setClass(activity, FileBrowserActivity.class);
        activity.startActivityForResult(intent, reqCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_browser_activity);
        findViews();
        showFileDir(ROOT_PATH);
    }

    private void findViews() {
        fileListView = (ListView) findViewById(R.id.file_list);
    }

    /**
     * ��ʾ�ļ��б�
     * @param path ��·��
     */
    private void showFileDir(String path){
        names = new ArrayList<>();
        paths = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();

        //�����ǰĿ¼���Ǹ�Ŀ¼
        if (ROOT_PATH.equals(path)) {
            names.add("@1");
            paths.add(ROOT_PATH);
        } else {
            names.add("@2");
            paths.add(file.getParent());
        }
        //��������ļ�
        for (File f : files) {
            names.add(f.getName());
            paths.add(f.getPath());
        }

        fileListItems.clear();
        for (int i = 0; i < names.size(); i++) {
            fileListItems.add(new FileManagerItem(names.get(i), paths.get(i)));
        }


        fileListView.setItemsCanFocus(true);
        fileListView.setAdapter(new FileBrowserAdapter(this, fileListItems, this));
        fileListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = fileListItems.get(position).getPath();

                File file = new File(path);
                // �ļ����ڲ��ɶ�
                if (file.exists() && file.canRead()) {
                    if (file.isDirectory()) {
                        //��ʾ��Ŀ¼���ļ�
                        showFileDir(path);
                    } else {
                        //�����ļ�
                        selectFile(path);
                    }
                } else {
                    //û��Ȩ��
                    Toast.makeText(FileBrowserActivity.this, R.string.no_permission, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectFile(String path) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA_PATH, path);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * *************** implements TAdapterDelegate ***************
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
        return FileBrowserViewHolder.class;
    }

    @Override
    public boolean enabled(int position) {
        return true;
    }
}
