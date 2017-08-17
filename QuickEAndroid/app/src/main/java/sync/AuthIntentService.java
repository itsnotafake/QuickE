package sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import nippledefensecommittee.quicke.R;
import utility.AuthToken;

/**
 * Created by Devin on 8/14/2017.
 */

/**
 * IntentService for getting Yelp's Auth Token associated with our Client ID and client
 * Secret. The token does expire after 180 days
 */
public class AuthIntentService extends IntentService {
    private static final String TAG = AuthIntentService.class.getName();

    public AuthIntentService() {
        super("AuthIntentService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.auth_URI);

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, response);
                        VolleyLog.e(TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", AuthToken.getGrantType());
                params.put("client_id", AuthToken.getClientId());
                params.put("client_secret", AuthToken.getClientSecret());
                return params;
            }
        };
        queue.add(jsonObjRequest);
    }
}
