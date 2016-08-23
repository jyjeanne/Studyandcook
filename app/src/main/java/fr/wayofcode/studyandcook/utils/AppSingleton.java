package fr.wayofcode.studyandcook.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by jerem_000 on 22/05/2016.
 */
public class AppSingleton {
    private static AppSingleton mInstance;
    private RequestQueue mRequestQueue;
    private final ImageLoader IMAGE_LOADER;
    private static Context mCtx;

    private AppSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();


        IMAGE_LOADER = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);
                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized AppSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return IMAGE_LOADER;
    }
}
