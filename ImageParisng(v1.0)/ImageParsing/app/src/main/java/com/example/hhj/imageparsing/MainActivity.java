package com.example.hhj.imageparsing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    ImageAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);


        adapter = new ImageAdapter(this);

        // 웹 크롤링으로 페이지 소스 읽어와 src 부분만을 adapter에 addItem 시킨다.
        getImageSrc thread = new getImageSrc();
        thread.start();

        try{ thread.join();}
        catch (Exception e){ e.printStackTrace();}

        gridView.setAdapter(adapter);

    }


    class ImageAdapter extends BaseAdapter{

        Context context = null;

        ImageAdapter(Context context)
        {
            super();
            this.context=context;

        }

        // 넣을 각각 아이템을 SingerItem으로 지정
        ArrayList<DataList> items = new ArrayList<DataList>();

        // 그리드 뷰가 어댑터에게 데이터 몇 개 가지고 있는지 물어보는 함수
        public int getCount() { return items.size();}

        // 넘겨 받은 인덱스의 값을 리턴하는 함수
        public Object getItem(int position) { return items.get(position); }

        public long getItemId(int position) { return position; }

        // ParsingItem 에 값을 넣어주는 메소드
        public void addItem(String src)
        {
            DataList data = new DataList();
            data.parsedItem = src;

            // 추출된 src가 들어가있는 DataList 객체를 items에 추가한다
            items.add(data);
        }


        /*
         * 각각의 아이템을 위한 뷰를 생성한다
         *
         * @param position
         * @param convertView : 리스트 뷰 용량 너무 많아지면 폭발하므로 아이템 재사용을 위해 사용
         * @param parent
         * @return
         */

        public View getView(int position, View convertView, ViewGroup parent) {

            // convertview 로 이전 값 기억은 하나 전 사진이 저장되었다가 바뀌는 것에 대한 해결법
            ViewHolder holder;

            ImageItemView view = null;
            if(convertView == null)
            {
                view = new ImageItemView(getApplicationContext());
            }
            else
            {
                view = (ImageItemView) convertView;
            }
            // convertView 가 null 이면 다시 만들어주고 아니면 그대로 써라 -> 메모리 효율적 사용
            /*if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.image_item,null);
                holder = new ViewHolder();
                holder.hImage = (ImageView) findViewById(R.id.parsedImageView);
                convertView.setTag(holder);
            }
            */

            // 인덱스 position에 들어있는 DataList를 culItem 에 저장
            DataList culItem = items.get(position);


            // 스레드에서 리턴 받은 비트맵을 뷰에 붙여준다
            getImage asynctask = new getImage(culItem.parsedItem,view);
            asynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);



            // 각각의 아이템이 되는 뷰를 리턴
            return view;
        }
    }



    // 데이터 저장할 클래스
    public class DataList {

        public String parsedItem;

        public DataList(){}

        public DataList(String src){
            this.parsedItem = src;
        }

        public void setParsedItem(String parsedItem) {
            this.parsedItem = parsedItem;
        }
    }

    static class ViewHolder {
        ImageView hImage;
        int hposition;
    }

    // 웹 페이지에서 img 태그의 src 부분만 파싱해오는 스레드
    class getImageSrc extends Thread
    {
        public void run()
        {
            DataList dataList = new DataList();

            // 이미지 태그 앞에 붙는 기본 주소
            String baseAddress = "http://www.gettyimagesgallery.com";

            try {

                URL url = new URL("http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx");
                URLConnection conn = url.openConnection();
                int a;
                // 소스코드 가져오기 위해 스트림 선언
                BufferedReader br;
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;

                while((line=br.readLine())!=null)
                {
                    if(line.contains("<img"))
                    {
                        String str[]=line.split("<img src=\"");
                        String path[]=str[1].split("\"");
                        if(path[0].contains("Thumbnails"))
                        {
                            //dataList.setParsedItem(baseAddress + path[0]);
                            //Log.i("dataList 변수값 :", dataList.parsedItem);
                            adapter.addItem(baseAddress + path[0]);
                            Log.d("adress",baseAddress + path[0]);
                        }
                    }
                }
            } catch (Exception e) {
                // TODO 자동 생성된 catch 블록
                e.printStackTrace();
            }
        }
    }

    public class getImage extends AsyncTask<String, Void, Bitmap>
    {
        String srcAddress;
        Bitmap bm;
        ViewHolder holder;
        ImageItemView view;

        public getImage(String src,ImageItemView view) {
            this.srcAddress = src;
            this.holder = holder;
            this.view = view;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try{
                URL url = new URL(srcAddress);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bm = BitmapFactory.decodeStream(is);
            }catch (Exception e) {

            }

            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
                view.setImage(bm);
        }
    }


}
