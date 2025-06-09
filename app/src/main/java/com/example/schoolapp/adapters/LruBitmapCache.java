package com.example.schoolapp.adapters;

import android.graphics.Bitmap;
import androidx.collection.LruCache;
import com.android.volley.toolbox.ImageLoader;

public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
    public LruBitmapCache() {
        super((int) (Runtime.getRuntime().maxMemory() / 1024 / 8));
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
