package com.example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.example.R;

public class MaskedView extends View {

    public static final float IMAGE_BLEED_PERCENTAGE = .10f;
    protected Drawable maskDrawable;
    protected Bitmap image;
    protected Bitmap maskedImage;
    protected Bitmap overlay;

    
    protected Rect maskRect;
    private int maskResourceId;
    private int imageToMaskResourceId;
    private int overlayResourceId; 

    public MaskedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init(attrs);
    }

    public MaskedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskedView);
        maskResourceId = a.getResourceId(R.styleable.MaskedView_mask, 0);
        maskDrawable = getResources().getDrawable(maskResourceId);

        imageToMaskResourceId = a.getResourceId(R.styleable.MaskedView_imageToMask, 0);
        image = BitmapFactory.decodeResource(getResources(), imageToMaskResourceId);

        
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(maskDrawable == null && image == null) return;

        if(maskedImage == null) {
            createMaskedImage(getWidth(),  getHeight());
        }

        canvas.drawBitmap(maskedImage, 0, 0, null);

    }

    protected void createMaskedImage(int width, int height) {

        // 1. get width height of mask
        // 2. Scale image down to fit mask (at largest point via x or y)
        // 3. Center image over mask. (match centers) 
        // 3. Apply porterduffxfermod SRC_IN to get the masked image.
        createMaskRect();  

    }

    private void createMaskRect() {
        
        final Bitmap mask = BitmapFactory.decodeResource(getResources(), maskResourceId);
        
        int firstX = 0;
        int lastX = 0;

        int firstY = 0;
        int lastY = 0;

        int currentY = 0;
        int currentX = 0;

        int priorColor = Color.TRANSPARENT;
        int currentColor;

        int prevFirstX = 0;
        int prevLastX = 0;
        int prevFirstY = 0;
        int prevLastY = 0;
        
        int posY = 0; 
        int posX = 0; 
        
        // Find mask width and posY
        for(int y = 0; y < mask.getHeight(); y++) {
            currentY = y;

            firstX = 0;
            lastX = 0;
            for(int x = 0; x < mask.getWidth(); x++) {

                currentColor = mask.getPixel(x, y);

                if(currentColor != Color.TRANSPARENT && priorColor == Color.TRANSPARENT) {
                    firstX = x;
                    prevFirstX = firstX;
                } else if(priorColor != Color.TRANSPARENT) {
                    lastX = x;
                    prevLastX = lastX;
                }

                priorColor = currentColor;
            }
            
            if(firstX == prevFirstX && lastX == prevLastX && prevFirstX > 0 && prevLastX > 0) {
                firstX--;
                break;
            }

        }
        
        posY = currentY;
        int maskWidth = (lastX - firstX);

        //
        // Find mask height and posX
        //
        for(int x = 0; x < mask.getWidth(); x++) {
            currentX = x;

            for(int y = 0; y < mask.getHeight(); y++) {

                currentColor = mask.getPixel(x, y);

                if(currentColor != Color.TRANSPARENT && priorColor == Color.TRANSPARENT) {
                    firstY = y;
                    prevFirstY = firstY;
                } else if(priorColor != Color.TRANSPARENT) {
                    lastY = y;
                    prevLastY = lastY;
                }

                priorColor = currentColor;
            }

            if(firstY == prevFirstY && lastY == prevLastY && prevFirstY > 0 && prevLastY > 0) {
                break;
            }
        }

        posX = currentX;
        int maskHeight = lastY - firstY;

        final Rect maskRect = new Rect(posX, posY, posX + maskWidth, posY + maskHeight);
        Point centerOfMaskArea = new Point( maskRect.centerX(), maskRect.centerY() );

        //
        // Resize image to fit inside of mask.
        //
        int longerSideOfMask = maskRect.width() > maskRect.height() ? maskRect.width() : maskRect.height();
        float widthScale = ((float)longerSideOfMask / (float)image.getWidth()) + IMAGE_BLEED_PERCENTAGE;
        float heightScale = ((float)longerSideOfMask / (float)image.getHeight()) + IMAGE_BLEED_PERCENTAGE;
        int newWidth = (int) (image.getWidth() * widthScale);
        int newHeight = (int) (image.getHeight() * heightScale);

        image = Bitmap.createScaledBitmap(image, newWidth, newHeight, true);



        //
        // Center new scaled image over the mask and then porter duff mode to a new output.
        //
        Drawable imageDrawable = new BitmapDrawable(mask);
        Bitmap tempOutputMask = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempOutputMask);
        Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(mask, 0, 0, maskPaint);

        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        final Point centerOfImage = new Point(maskRect.left + (image.getWidth() / 2), maskRect.top + (image.getHeight() / 2));

        canvas.drawBitmap(image, maskRect.left - (centerOfImage.x - centerOfMaskArea.x ), maskRect.top - (centerOfImage.y - centerOfMaskArea.y), maskPaint);


        //
        // Merge overlay and new mask
        //
        overlay = BitmapFactory.decodeResource(getResources(), R.drawable.square_overlay);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(tempOutputMask, 0, 0, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

        canvas.drawBitmap(overlay, 0, 0, p);
        imageDrawable.draw(canvas);

        maskedImage = tempOutputMask;
    }
}
