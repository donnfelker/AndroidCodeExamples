package com.example.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.example.R;
import roboguice.util.Ln;


public class RoundedCornerImage extends View {

    private Bitmap image;
    private Drawable placeholder;
    private Bitmap framedPhoto;
    private float cornerRadius;


    public RoundedCornerImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init(attrs);
    }

    public RoundedCornerImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs);

    }

    private void init(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedCornerImage);
        placeholder = getResources().getDrawable(a.getResourceId(R.styleable.RoundedCornerImage_placeHolder, 0));
        image = BitmapFactory.decodeResource(getResources(), a.getResourceId( R.styleable.RoundedCornerImage_image, 0));
        cornerRadius =  a.getFloat(R.styleable.RoundedCornerImage_cornerRadius, 18); // Why default to 18? It just looks good and will actually show rounded corners.
        Ln.d("ROUNDED CORNER: cornerRadius:" + cornerRadius);
    }

    // Uncomment if you want to make sure that the result is always square, otherwise comment this stuff out.
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int measuredHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        // Ensure this view is always square.
        int min = Math.min(measuredHeight, measuredWidth);
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(placeholder == null && image == null) return;

        if(framedPhoto == null) {
            createFramedPhoto( Math.min(getWidth(), getHeight()) );
        }

       canvas.drawBitmap(framedPhoto,0, 0, null);

    }

    private void createFramedPhoto(int size) {

        // Start with either the placeholder or the image. This is useful if you want to download the image and
        // have something showing before hand.
        Drawable imageDrawable = (image != null) ? new BitmapDrawable(image) : placeholder;

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        RectF outerRect = new RectF(0, 0, size, size);

        /*
        *  How To keep aspect ratio:
        *  To base it upon the size of the image/density use the following. This is so that when the size of the image
        *  increases, so does the size of the rounded corner. A 500 x 500 image would have a larger radius than a
        *  25 x 25 image would have.
        */
        //float outerRadius = size / cornerRadius;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        
        canvas.drawRoundRect(outerRect, cornerRadius, cornerRadius, paint);

        // Compose image with red rectangle
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, size, size);

        // Save the layer to apply the paint
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.restore();

        framedPhoto = output;

    }


}
