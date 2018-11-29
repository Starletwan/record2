package com.example.notewithhand;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //使用的数据库se
    private MYSQLiteOpenHelper dbHelper;
    //用于显示列表的ListView
    private ListView listView = null;
    //用于存方列表的信息
    List<String> list= new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MYSQLiteOpenHelper(this,"Record.db",null,1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("record",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                String note = cursor.getString(cursor.getColumnIndex("note"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                if(note.length() > 36 ){
                    note = note.substring(0,36);
                }
                list.add(note+"\n"+time);
            }while(cursor.moveToNext());
        }
        cursor.close();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this,android.R.layout.simple_list_item_1,list);
        listView = (ListView) findViewById(R.id.list_item);
        listView.setAdapter(adapter);
        //点击事件-更改
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i=new Intent(MainActivity.this,UpdateActivity.class);
                i.putExtra("id",String.valueOf(id));
                startActivity(i);//启动第二个activity并把i传递过去
            }
        });
        //长点击事件-删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                //弹出对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("删除");
                dialog.setMessage("你确定你要删除？");
                dialog.setCancelable(false);
                //确定按键
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        Cursor cursor = db.query("record",null,null,null,null,null,null);
                        int i=0;
                        boolean realy=false;//realy 判断是否删除数据成功
                        //删除选中数据
                        if (cursor.moveToFirst()){
                            do{
                                //删除数据
                                if(i == position){
                                    String time = cursor.getString(cursor.getColumnIndex("time"));
                                    db.delete("record","time = ?",new String[]{time});
                                    realy=true;
                                    break;
                                }
                                i++;
                            }while(cursor.moveToNext());
                        }
                        //更新列表数据
                        if(realy){
                            //移除已删除的信息
                            list.remove(list.get(position));
                            //刷新
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this,"删除成功",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"删除失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                    }
                });
                //取消按键
                dialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,"没有删除操作",
                            Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                return true;
            }
        });
    }
    @Override
    //菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item:
                Intent intent1=new Intent(MainActivity.this,CreateActivity.class);
                startActivity(intent1);
                break;
            case R.id.find_item:
                Intent intent2=new Intent(MainActivity.this,RetrieveActivity.class);
                startActivity(intent2);
                break;
            default:
        }
        return true;
    }
}
