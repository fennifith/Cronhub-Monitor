package me.jfenn.cronhubclient.data.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Request {

    private String url;
    private RequestThread thread;
    private Gson gson;
    private boolean isInitialized;

    private List<OnInitListener> listeners;
    private List<String> tags;

    Request(String url) {
        this.url = url;
        listeners = new ArrayList<>();
        tags = new ArrayList<>();

        gson = new GsonBuilder()
                .registerTypeAdapter(getClass(), new MootInstanceCreator(this))
                .create();
    }

    /**
     * Called once the request to github servers has been successfully completed.
     *
     * @param json the json response
     */
    private void init(String json) {
        if (initJson(gson, json)) {
            onInit();
            isInitialized = true;
            for (OnInitListener listener : listeners) {
                listener.onInit(this);
            }
        }
    }

    /**
     * Called when there is a failure.
     */
    private void failure(String message) {
        for (OnInitListener listener : listeners) {
            listener.onFailure(this, message);
        }
    }

    /**
     * Initializes the values in the class from the json string. Exists only to be
     * overridden if necessary.
     * @param gson the gson object
     * @param json the json string
     */
    protected boolean initJson(Gson gson, String json) {
        try {
            gson.fromJson(json, getClass());
            return true;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Log.e("Attribouter", "Error parsing JSON from " + url);
        }

        failure("Broken formatting");
        return false;
    }

    /**
     * Called once the object has finished being initialized. Exists only to be overriden
     * if necessary.
     */
    protected void onInit() {
    }

    /**
     * Starts the network request thread, should only be called once.
     */
    public final void startInit(Context context, String token) {
        if (token != null) {
            thread = new RequestThread(context, token, this, url);
            thread.start();
        } else failure("Invalid API Key");
    }

    public final boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Merge this data's listeners with another. Should only be called if the
     * two are of the exact same class.
     *
     * @param data the data to merge with
     * @return a somewhat pointless "this", only to make it blatantly obvious which GitHubData actually contains the end result
     */
    public final Request merge(Request data) {
        for (OnInitListener listener : data.listeners) {
            if (!listeners.contains(listener))
                listeners.add(listener);
        }

        for (String tag : data.tags)
            addTag(tag);

        if (isInitialized()) {
            for (OnInitListener listener : data.listeners)
                listener.onInit(this);
        }

        return this;
    }

    public final void addTag(String tag) {
        if (!tags.contains(tag))
            tags.add(tag);
    }

    public final List<String> getTags() {
        return tags;
    }

    public final void addOnInitListener(OnInitListener listener) {
        listeners.add(listener);
    }

    public final void removeOnInitListener(OnInitListener listener) {
        listeners.remove(listener);
    }

    public final void interruptThread() {
        if (thread != null && thread.isAlive() && !thread.isInterrupted())
            thread.interrupt();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Request && ((Request) obj).url.equals(url);
    }

    private static class MootInstanceCreator implements InstanceCreator<Request> {

        private Request instance;

        public MootInstanceCreator(Request instance) {
            this.instance = instance;
        }

        @Override
        public Request createInstance(Type type) {
            return instance;
        }
    }

    private static class RequestThread extends Thread {

        private File cacheFile;
        private Request data;
        private String url;
        private String token;

        private RequestThread(Context context, String token, Request data, String url) {
            this.data = data;
            this.url = url;
            this.token = token;
            File dir = new File(context.getCacheDir() + "/cronhub");
            if (!dir.exists())
                dir.mkdirs();

            cacheFile = new File(dir, url.replace("/", ".") + ".json");
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            BufferedReader jsonReader = null;
            StringBuilder jsonBuilder = new StringBuilder();
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                if (token != null)
                    connection.setRequestProperty("X-Api-Key", token);

                jsonReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                if (connection.getResponseCode() == 403) {
                    jsonReader.close();
                    return;
                }

                String line;
                while ((line = jsonReader.readLine()) != null)
                    jsonBuilder.append(line);
            } catch (IOException e) {
                e.printStackTrace();
                jsonBuilder = null;
            }

            if (connection != null) {
                connection.disconnect();
            }

            if (jsonReader != null) {
                try {
                    jsonReader.close();
                } catch (IOException ignored) {
                }
            }

            if (jsonBuilder != null) {
                String json = jsonBuilder.toString();
                callInit(json);

                PrintWriter cacheWriter = null;
                try {
                    cacheWriter = new PrintWriter(cacheFile);
                    cacheWriter.println(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (cacheWriter != null)
                    cacheWriter.close();

                return;
            } else {
                String cache = null;

                StringBuilder cacheBuilder = new StringBuilder();
                Scanner cacheScanner = null;
                try {
                    cacheScanner = new Scanner(cacheFile);
                    while (cacheScanner.hasNext()) {
                        cacheBuilder.append(cacheScanner.nextLine());
                    }

                    cache = cacheBuilder.toString();
                } catch (IOException ignored) {
                } catch (Exception e) {
                    cacheFile.delete(); //probably a formatting error
                }

                if (cacheScanner != null)
                    cacheScanner.close();

                if (cache != null) {
                    callInit(cache);
                    return;
                }
            }

            callFailure("Bad request");
        }

        private void callInit(final String json) {
            new Handler(Looper.getMainLooper()).post(() -> data.init(json));
        }

        private void callFailure(final String message) {
            new Handler(Looper.getMainLooper()).post(() -> data.failure(message));
        }
    }

    public interface OnInitListener {
        void onInit(Request data);

        void onFailure(Request data, String message); //TODO: actually calling this method when something fails might be nice
    }

}