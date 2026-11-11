package com.gamechief.aifunnyringtone.models;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {
    private static final String[] DEFAULT_API_KEYS = {};
    private static final String[] REMOTE_KEY_URLS = {
            "https://funnyringtoneapp.s3.us-east-1.amazonaws.com/modelslab_key.txt",
            "https://funnyringtoneapp.s3.us-east-1.amazonaws.com/modelslab_api_key.txt"
    };
    private static final String BASE_URL = "https://modelslab.com/api/v6/voice/";
    private static final int MAX_RETRIES = 20;
    private static final int RETRY_DELAY_MS = 5000;
    private static final int GENERATE_TIMEOUT_MS = 30000;
    private static final int FETCH_TIMEOUT_MS = 15000;
    private static final String TAG = "ApiClient";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final RequestQueue queue;
    private final List<String> fetchedOnlineKeys = new ArrayList<>();
    private String workingApiKey = null;
    private boolean hasFetchedOnline = false;

    public interface SfxCallback {
        void onError(VolleyError volleyError);

        void onProcessing(String str);

        void onProgress(int i, int i2);

        void onSuccess(String str);
    }

    public ApiClient(Context ctx) {
        this.queue = Volley.newRequestQueue(ctx);
    }

    private List<String> getApiKeys() {
        List<String> keys = new ArrayList<>();
        if (isValidKeyFormat(workingApiKey)) {
            String sanitized = sanitizeKey(workingApiKey);
            if (isValidKeyFormat(sanitized)) {
                keys.add(sanitized);
            }
        }

        try {
            FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
            String[] paramNames = {"apiKey", "api_key", "modelslab_key", "modelslab_api_key", "modelsLabApiKey", "sfx_api_key", "modelslab_keys", "models_lab_key", "voice_api_key"};
            for (String param : paramNames) {
                String val = remoteConfig.getString(param);
                if (!val.trim().isEmpty()) {
                    String cleanVal = val.trim().replaceAll("[\"'\\[\\]{}]", "");
                    String[] split = cleanVal.split("[,;\\s]+");
                    for (String k : split) {
                        String sanitized = sanitizeKey(k);
                        if (isValidKeyFormat(sanitized) && !keys.contains(sanitized)) {
                            keys.add(sanitized);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch apiKey from FirebaseRemoteConfig", e);
        }

        for (String onlineKey : fetchedOnlineKeys) {
            String sanitized = sanitizeKey(onlineKey);
            if (isValidKeyFormat(sanitized) && !keys.contains(sanitized)) {
                keys.add(sanitized);
            }
        }

        for (String defaultKey : DEFAULT_API_KEYS) {
            String sanitized = sanitizeKey(defaultKey);
            if (isValidKeyFormat(sanitized) && !keys.contains(sanitized)) {
                keys.add(sanitized);
            }
        }

        return keys;
    }

    private String sanitizeKey(String key) {
        if (key == null) return "";
        return key.trim().replaceAll("[\"'\\[\\]{}]", "");
    }

    private void fetchRemoteKeysOnline(Runnable onComplete) {
        hasFetchedOnline = true;
        try {
            FirebaseRemoteConfig.getInstance().fetchAndActivate().addOnCompleteListener(task ->
                    Log.d(TAG, "FirebaseRemoteConfig fetchAndActivate result: " + task.isSuccessful()));
        } catch (Exception e) {
            Log.e(TAG, "Failed to trigger FirebaseRemoteConfig fetchAndActivate", e);
        }

        fetchKeysFromUrls(0, onComplete);
    }

    private void fetchKeysFromUrls(int urlIndex, Runnable onComplete) {
        if (urlIndex >= REMOTE_KEY_URLS.length) {
            if (onComplete != null) onComplete.run();
            return;
        }

        String url = REMOTE_KEY_URLS[urlIndex];
        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    if (response != null && !response.trim().isEmpty()) {
                        String clean = response.trim().replaceAll("[\"'\\[\\]{}]", "");
                        String[] split = clean.split("[,;\\s]+");
                        for (String k : split) {
                            String sanitized = sanitizeKey(k);
                            if (isValidKeyFormat(sanitized) && !fetchedOnlineKeys.contains(sanitized)) {
                                fetchedOnlineKeys.add(sanitized);
                            }
                        }
                    }
                    fetchKeysFromUrls(urlIndex + 1, onComplete);
                },
                error -> {
                    Log.e(TAG, "Failed to fetch online key from " + url + ": " + error);
                    fetchKeysFromUrls(urlIndex + 1, onComplete);
                });
        req.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
        queue.add(req);
    }

    private boolean isValidKeyFormat(String key) {
        if (key == null) return false;
        String sanitized = sanitizeKey(key);
        return sanitized.length() >= 10;
    }

    private String maskKey(String key) {
        if (key == null || key.length() < 8) return "***";
        return key.substring(0, 4) + "***" + key.substring(key.length() - 4);
    }

    private boolean isInvalidApiKeyError(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) return false;
        String lower = errorMessage.toLowerCase();
        return lower.contains("invalid api key")
                || lower.contains("invalid_api_key")
                || lower.contains("invalid key")
                || lower.contains("api-keys")
                || lower.contains("api_key")
                || lower.contains("unauthorized")
                || lower.contains("key not found")
                || lower.contains("key is invalid")
                || lower.contains("access denied")
                || lower.contains("key expired")
                || lower.contains("quota")
                || lower.contains("credit")
                || lower.contains("plan limit")
                || lower.contains("insufficient")
                || lower.contains("payment required")
                || lower.contains("401")
                || lower.contains("403");
    }

    private String extractErrorMessageFromVolleyError(VolleyError err) {
        if (err == null) return "Unknown error";
        if (err.networkResponse != null && err.networkResponse.data != null) {
            try {
                String body = new String(err.networkResponse.data, StandardCharsets.UTF_8).trim();
                if (!body.isEmpty()) {
                    if (body.startsWith("{")) {
                        JSONObject obj = new JSONObject(body);
                        return extractErrorMessage(obj);
                    }
                    return body;
                }
            } catch (Exception ignored) {}
        }
        return err.getMessage() != null && !err.getMessage().isEmpty() ? err.getMessage() : err.toString();
    }

    public void generateSfx(String prompt, SfxCallback cb) {
        List<String> keys = getApiKeys();
        if (keys.isEmpty()) {
            fetchRemoteKeysOnline(() -> {
                List<String> updatedKeys = getApiKeys();
                if (updatedKeys.isEmpty()) {
                    String errorMsg = "Invalid API Key. Please get a valid API key from https://modelslab.com/dashboard/api-keys and update it in Firebase Remote Config key 'apiKey'.";
                    Log.e(TAG, errorMsg);
                    cb.onError(new VolleyError(errorMsg));
                } else {
                    generateSfxWithKeyIndex(prompt, cb, 0);
                }
            });
        } else {
            generateSfxWithKeyIndex(prompt, cb, 0);
        }
    }

    private void generateSfxWithKeyIndex(String prompt, SfxCallback cb, int keyIndex) {
        List<String> keys = getApiKeys();
        if (keyIndex >= keys.size()) {
            if (!hasFetchedOnline) {
                fetchRemoteKeysOnline(() -> {
                    List<String> updatedKeys = getApiKeys();
                    if (keyIndex < updatedKeys.size()) {
                        generateSfxWithKeyIndex(prompt, cb, keyIndex);
                    } else {
                        String errorMsg = "Invalid API Key. Please get a valid API key from https://modelslab.com/dashboard/api-keys and update it in Firebase Remote Config key 'apiKey'.";
                        Log.e(TAG, errorMsg);
                        cb.onError(new VolleyError(errorMsg));
                    }
                });
                return;
            }
            String errorMsg = "Invalid API Key. Please get a valid API key from https://modelslab.com/dashboard/api-keys and update it in Firebase Remote Config key 'apiKey'.";
            Log.e(TAG, errorMsg);
            cb.onError(new VolleyError(errorMsg));
            return;
        }

        String apiKey = keys.get(keyIndex);
        try {
            JSONObject obj = new JSONObject();
            obj.put("key", apiKey);
            obj.put("prompt", prompt);
            obj.put("duration", 10);
            obj.put("temp", false);
            Log.d(TAG, "POST https://modelslab.com/api/v6/voice/sfx prompt=" + prompt + " key=" + maskKey(apiKey) + " (keyIndex=" + keyIndex + ")");

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://modelslab.com/api/v6/voice/sfx", obj,
                    res -> {
                        Log.d(TAG, "Generate response: " + res);
                        String status = res.optString("status", "");
                        if ("error".equalsIgnoreCase(status) || "failed".equalsIgnoreCase(status)) {
                            String errorMessage = extractErrorMessage(res);
                            if (isInvalidApiKeyError(errorMessage)) {
                                if (apiKey.equals(workingApiKey)) {
                                    workingApiKey = null;
                                }
                                if (keyIndex + 1 < keys.size()) {
                                    Log.w(TAG, "API key " + maskKey(apiKey) + " returned key error: " + errorMessage + ". Retrying with key index " + (keyIndex + 1));
                                    generateSfxWithKeyIndex(prompt, cb, keyIndex + 1);
                                    return;
                                } else if (!hasFetchedOnline) {
                                    Log.w(TAG, "All local keys failed. Fetching online keys...");
                                    fetchRemoteKeysOnline(() -> {
                                        List<String> updatedKeys = getApiKeys();
                                        if (keyIndex + 1 < updatedKeys.size()) {
                                            generateSfxWithKeyIndex(prompt, cb, keyIndex + 1);
                                        } else {
                                            handleGenerateResponse(res, cb);
                                        }
                                    });
                                    return;
                                }
                            }
                            handleGenerateResponse(res, cb);
                        } else {
                            workingApiKey = apiKey;
                            handleGenerateResponse(res, cb);
                        }
                    },
                    err -> {
                        String errMsg = extractErrorMessageFromVolleyError(err);
                        Log.e(TAG, "Generate request Volley error with key " + maskKey(apiKey) + ": " + errMsg);
                        if (isInvalidApiKeyError(errMsg)) {
                            if (apiKey.equals(workingApiKey)) {
                                workingApiKey = null;
                            }
                            if (keyIndex + 1 < keys.size()) {
                                Log.w(TAG, "Retrying generateSfx with next key index " + (keyIndex + 1));
                                generateSfxWithKeyIndex(prompt, cb, keyIndex + 1);
                                return;
                            } else if (!hasFetchedOnline) {
                                Log.w(TAG, "All local keys failed with Volley error. Fetching online keys...");
                                fetchRemoteKeysOnline(() -> {
                                    List<String> updatedKeys = getApiKeys();
                                    if (keyIndex + 1 < updatedKeys.size()) {
                                        generateSfxWithKeyIndex(prompt, cb, keyIndex + 1);
                                    } else {
                                        cb.onError(new VolleyError(errMsg, err));
                                    }
                                });
                                return;
                            }
                        }
                        cb.onError(new VolleyError(errMsg, err));
                    });
            req.setRetryPolicy(new DefaultRetryPolicy(GENERATE_TIMEOUT_MS, 1, 1.0f));
            this.queue.add(req);
        } catch (JSONException e) {
            cb.onError(new VolleyError(e));
        }
    }

    // --- STEP 2: Poll fetch API until success ---
    public void fetchSfx(String id, SfxCallback cb) {
        fetchSfx(id, cb, 0, 0);
    }

    private void fetchSfx(String id, SfxCallback cb, int attempt, int keyIndex) {
        if (attempt > MAX_RETRIES) {
            cb.onError(new VolleyError("Max retries reached"));
            return;
        }

        try {
            String url = BASE_URL + "fetch/" + id;
            Log.d(TAG, "POST " + url + " (attempt " + (attempt + 1) + ", keyIndex " + keyIndex + ")");

            List<String> keys = getApiKeys();
            String apiKey;
            if (isValidKeyFormat(workingApiKey)) {
                apiKey = workingApiKey;
            } else if (!keys.isEmpty() && keyIndex < keys.size()) {
                apiKey = keys.get(keyIndex);
            } else {
                apiKey = keys.isEmpty() ? "" : keys.get(0);
            }

            JSONObject obj = new JSONObject();
            obj.put("key", apiKey);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, obj,
                    res -> {
                        Log.d(TAG, "Fetch response: " + res);
                        String status = res.optString("status", "");

                        switch (status.toLowerCase()) {
                            case "success":
                                String audioUrl = extractAudioUrl(res);
                                if (audioUrl != null) {
                                    cb.onSuccess(audioUrl);
                                } else {
                                    cb.onError(new VolleyError("No valid audio URL found"));
                                }
                                break;

                            case "processing":
                            case "queued":
                            case "pending":
                            case "waiting":
                                cb.onProgress(attempt + 1, MAX_RETRIES);
                                handler.postDelayed(() -> fetchSfx(id, cb, attempt + 1, keyIndex), RETRY_DELAY_MS);
                                break;

                            case "error":
                            case "failed":
                                String errorMessage = extractErrorMessage(res);
                                Log.e(TAG, "Fetch API returned error: " + errorMessage);
                                if (isInvalidApiKeyError(errorMessage)) {
                                    if (apiKey.equals(workingApiKey)) {
                                        workingApiKey = null;
                                    }
                                    if (keyIndex + 1 < keys.size()) {
                                        Log.w(TAG, "Retrying fetchSfx with keyIndex " + (keyIndex + 1));
                                        fetchSfx(id, cb, attempt, keyIndex + 1);
                                        return;
                                    }
                                }
                                cb.onError(new VolleyError(errorMessage));
                                break;

                            default:
                                String fallbackUrl = extractAudioUrl(res);
                                if (fallbackUrl != null) {
                                    cb.onSuccess(fallbackUrl);
                                } else {
                                    String err = extractErrorMessage(res);
                                    if (isInvalidApiKeyError(err) && apiKey.equals(workingApiKey)) {
                                        workingApiKey = null;
                                    }
                                    cb.onError(new VolleyError(err));
                                }
                                break;
                        }
                    },
                    err -> {
                        String errMsg = extractErrorMessageFromVolleyError(err);
                        Log.e(TAG, "Fetch error: " + errMsg);
                        if (isInvalidApiKeyError(errMsg)) {
                            if (apiKey.equals(workingApiKey)) {
                                workingApiKey = null;
                            }
                            if (keyIndex + 1 < keys.size()) {
                                fetchSfx(id, cb, attempt, keyIndex + 1);
                                return;
                            }
                        }
                        if (attempt < MAX_RETRIES) {
                            cb.onProgress(attempt + 1, MAX_RETRIES);
                            handler.postDelayed(() -> fetchSfx(id, cb, attempt + 1, keyIndex), RETRY_DELAY_MS);
                        } else {
                            cb.onError(new VolleyError(errMsg, err));
                        }
                    });
            req.setRetryPolicy(new DefaultRetryPolicy(FETCH_TIMEOUT_MS, 1, 1.0f));
            queue.add(req);
        } catch (JSONException e) {
            cb.onError(new VolleyError(e));
        }
    }

    private void handleGenerateResponse(JSONObject res, SfxCallback cb) {
        Log.d(TAG, "Response: " + res);
        try {
            String status = res.optString("status", "");
            switch (status.toLowerCase()) {
                case "success":
                    String audioUrl = extractAudioUrl(res);
                    if (audioUrl != null) {
                        cb.onSuccess(audioUrl);
                    } else {
                        cb.onError(new VolleyError("No valid audio URL found"));
                    }
                    break;

                case "processing":
                case "queued":
                case "pending":
                    String trackId = extractTrackId(res);
                    if (trackId != null && !trackId.isEmpty()) {
                        Log.d(TAG, "Processing started. Track ID: " + trackId);
                        cb.onProcessing(trackId);
                    } else {
                        cb.onError(new VolleyError("Processing started but no track ID was provided"));
                    }
                    break;

                case "error":
                case "failed":
                    String errorMessage = extractErrorMessage(res);
                    Log.e(TAG, "API returned error: " + errorMessage);
                    cb.onError(new VolleyError(errorMessage));
                    break;

                default:
                    String fallbackUrl = extractAudioUrl(res);
                    if (fallbackUrl != null) {
                        cb.onSuccess(fallbackUrl);
                        break;
                    }
                    String id = extractTrackId(res);
                    if (id != null && !id.isEmpty()) {
                        cb.onProcessing(id);
                        break;
                    }
                    String err = extractErrorMessage(res);
                    cb.onError(new VolleyError(err));
                    break;
            }
        } catch (Exception e) {
            cb.onError(new VolleyError(e));
        }
    }

    private String extractTrackId(JSONObject res) {
        if (res == null) return null;
        if (res.has("id") && !res.isNull("id")) {
            String id = res.optString("id", "").trim();
            if (!id.isEmpty() && !"null".equalsIgnoreCase(id)) {
                return id;
            }
        }
        if (res.has("job_id") && !res.isNull("job_id")) {
            String jobId = res.optString("job_id", "").trim();
            if (!jobId.isEmpty() && !"null".equalsIgnoreCase(jobId)) {
                return jobId;
            }
        }
        if (res.has("task_id") && !res.isNull("task_id")) {
            String taskId = res.optString("task_id", "").trim();
            if (!taskId.isEmpty() && !"null".equalsIgnoreCase(taskId)) {
                return taskId;
            }
        }
        return null;
    }

    private String extractErrorMessage(JSONObject res) {
        if (res == null) return "Unknown error";

        if (res.has("message")) {
            Object msgObj = res.opt("message");
            if (msgObj instanceof String) {
                String msg = (String) msgObj;
                if (!msg.isEmpty()) return msg;
            } else if (msgObj instanceof JSONArray) {
                JSONArray arr = (JSONArray) msgObj;
                if (arr.length() > 0) {
                    return arr.optString(0, "");
                }
            }
        }

        if (res.has("error")) {
            Object errObj = res.opt("error");
            if (errObj instanceof String) {
                String err = (String) errObj;
                if (!err.isEmpty()) return err;
            } else if (errObj instanceof JSONObject) {
                String msg = ((JSONObject) errObj).optString("message", "");
                if (!msg.isEmpty()) {
                    return msg;
                }
            }
        }

        String details = res.optString("details", "");
        if (!details.isEmpty()) {
            return details;
        }

        String status = res.optString("status", "");
        return status.isEmpty() ? "API Error" : "API returned status: " + status;
    }

    private String extractAudioUrl(JSONObject res) {
        try {
            JSONArray output = res.optJSONArray("output");
            if (output != null && output.length() > 0) {
                Object first = output.get(0);
                if (first instanceof JSONObject) {
                    JSONObject firstObj = (JSONObject) first;
                    String contractUrl = firstObj.optString(ImagesContract.URL, "");
                    if (!contractUrl.isEmpty()) {
                        return contractUrl;
                    }
                    String genericUrl = firstObj.optString("url", "");
                    if (!genericUrl.isEmpty()) {
                        return genericUrl;
                    }
                } else if (first instanceof String) {
                    String strUrl = (String) first;
                    if (!strUrl.isEmpty()) {
                        return strUrl;
                    }
                }
            }
            JSONArray proxyLinks = res.optJSONArray("proxy_links");
            if (proxyLinks != null && proxyLinks.length() > 0) {
                String link = proxyLinks.optString(0, "");
                if (!link.isEmpty()) {
                    return link;
                }
            }
            JSONArray links = res.optJSONArray("links");
            if (links != null && links.length() > 0) {
                String link = links.optString(0, "");
                if (!link.isEmpty()) {
                    return link;
                }
            }
            String singleOutput = res.optString("output", "");
            if (singleOutput.startsWith("http")) {
                return singleOutput;
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing audio URL: " + e.getMessage());
        }
        return null;
    }
}

