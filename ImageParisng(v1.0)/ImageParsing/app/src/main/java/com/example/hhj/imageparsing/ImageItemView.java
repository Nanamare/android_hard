package com.example.hhj.imageparsing;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URL;

/**
 * Created by hhj on 16. 7. 1..
 */
public class ImageItemView extends LinearLayout {

    ImageView ImageItem;

    public ImageItemView(Context context) {
        super(context);

        init(context);
    }

    public ImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    // 뷰그룹에 붙여주는 인플레이션 작업
    private void init(Context context){
        LayoutInflater  inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.image_item,this,true);

        ImageItem = (ImageView) findViewById(R.id.parsedImageView);

    }

    public void setImage(Bitmap bitmap) {

        ImageItem.setImageBitmap(bitmap);
    }
}
