package com.salesforce.graphql.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesforce.graphql.OauthObject;
import com.salesforce.graphql.SessionRequest;
import com.salesforce.graphql.entities.ManagedContentChannel;
import com.salesforce.graphql.entities.ManagedContentItem;
import com.salesforce.graphql.entities.ManagedContentSearchItem;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class HttpUtility {
    private static final int PORT = 443;
    private static  String CHANNEL_ENDPOINT = "/services/data/v50.0/connect/cms/delivery/channels";
    private static  String QUERY_ENDPOINT = "/services/data/v50.0/connect/cms/delivery/channels/%s/contents/query";
    private static  String SEARCH_ENDPOINT = "/services/data/v50.0/connect/cms/delivery/channels/%s/contents/search?queryTerm=%s";
    private static String ACCESS_TOKEN = "access_token";


    private static OauthObject oAuthSessionProvider(SessionRequest sessionRequest) throws IOException, JSONException {
        String baseUrl = sessionRequest.getBaseUrl() + "/services/oauth2/token";
        CloseableHttpClient client =  HttpClientBuilder.create().build();
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setConnectionRequestTimeout(30000).setCookieSpec(CookieSpecs.DEFAULT);

        // Set the SID.
        System.out.println("Logging in as " + sessionRequest.getUsername() + " in environment " + sessionRequest.getBaseUrl());



        // Send a post request to the OAuth URL.
        HttpPost oauthPost = new HttpPost(baseUrl);
        // The request body must contain these 5 values.
        List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
        parametersBody.add(new BasicNameValuePair("grant_type", "password"));
        parametersBody.add(new BasicNameValuePair("username", sessionRequest.getUsername()));
        parametersBody.add(new BasicNameValuePair("password", sessionRequest.getPassword()));
        parametersBody.add(new BasicNameValuePair("client_id", sessionRequest.getClientId()));
        parametersBody.add(new BasicNameValuePair("client_secret", sessionRequest.getSecret()));
        oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));

        // Execute the request.
        System.out.println("POST " + baseUrl + "...\n");
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response = client.execute(oauthPost, responseHandler);
        JSONObject oauthLoginResponse = new JSONObject(response);
        OauthObject oauthObject = getOauthObject(oauthLoginResponse);

        return oauthObject;
    }

    /**
     * Get Oauth response object
     * @param oauthResponse
     * @return
     * @throws JSONException
     */
    private static OauthObject getOauthObject(JSONObject oauthResponse) throws JSONException {
        OauthObject oauthObject = new OauthObject();
        if(oauthResponse.has("id")){
            oauthObject.setId(oauthResponse.getString("id"));
        }
        if(oauthResponse.has(ACCESS_TOKEN)){
            oauthObject.setAccessToken(oauthResponse.getString(ACCESS_TOKEN));
        }

        return oauthObject;
    }

    public static List<ManagedContentChannel> getManagedContentChannelObject(SessionRequest sessionRequest,
                                                                             DataFetchingEnvironment environment,
                                                                             DataFetchingFieldSelectionSet selectionSet) throws IOException, URISyntaxException, JSONException {
        final String CONTENT_NODE_NAME= "items";

        List<ManagedContentChannel> channels = new ArrayList<>();
        CloseableHttpClient client = HttpClientBuilder.create().build();

        //Step 1: Get the oauth access token
        OauthObject oauthObject = oAuthSessionProvider(sessionRequest);

        //Step 2: Http get delivery channels
        HttpGet userInfoRequest = new HttpGet(sessionRequest.getBaseUrl()+CHANNEL_ENDPOINT);
        userInfoRequest.setHeader("Authorization", "Bearer "+oauthObject.getAccessToken());

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response = client.execute(userInfoRequest, responseHandler);

        JSONObject channelResponse = new JSONObject(response);
        if(channelResponse != null) {

            JSONArray jsonArray = channelResponse.getJSONArray("channels");
            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ManagedContentChannel managedContentChannel = new ObjectMapper().readValue(jsonObject.toString(), ManagedContentChannel.class);
                if (managedContentChannel != null) {
                    if (selectionSet.contains(CONTENT_NODE_NAME)) {
                        List<SelectedField> contentNodeFields = selectionSet.getFields(CONTENT_NODE_NAME);
                        contentNodeFields.forEach(selectedField -> {
                            Object filterManagedContentId = selectedField.getArguments().get("managedContentId");
                            if (filterManagedContentId != null) {

                                managedContentChannel.setItems(
                                        HttpUtility.getManagedContent(sessionRequest, managedContentChannel.getChannelId())
                                                .stream()
                                                .filter(item -> item.getManagedContentId().equals(filterManagedContentId))
                                                .collect(Collectors.toList())
                                );

                            } else {
                                managedContentChannel.setItems(
                                        HttpUtility.getManagedContent(sessionRequest, managedContentChannel.getChannelId())
                                );
                            }
                        });
                    }
                    channels.add(managedContentChannel);
                }
            }
        }
        return channels;
    }

    public static List<ManagedContentItem> getManagedContent(SessionRequest sessionRequest, String channelId){
        List<ManagedContentItem> contents = new ArrayList<>();

        CloseableHttpClient client = HttpClientBuilder.create().build();

        //Step 1: Get the oauth access token
        OauthObject oauthObject = null;
        try {
            oauthObject = oAuthSessionProvider(sessionRequest);


            //Step 2: Http get delivery channels
            HttpGet userInfoRequest = new HttpGet(sessionRequest.getBaseUrl() + String.format(QUERY_ENDPOINT, channelId));
            userInfoRequest.setHeader("Authorization", "Bearer " + oauthObject.getAccessToken());

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = client.execute(userInfoRequest, responseHandler);

            JSONObject contentResponse = new JSONObject(response);
            if (contentResponse != null) {
                JSONArray jsonArray = contentResponse.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ManagedContentItem managedContentItem = new ObjectMapper().readValue(jsonObject.toString(), ManagedContentItem.class);
                    if (managedContentItem != null) {
                        contents.add(managedContentItem);
                    }
                }

            }
        } catch(Exception ex){

        }
        return contents;
    }

    public static List<ManagedContentSearchItem> searchContent(SessionRequest sessionRequest, String channelId, String searchPhrase) throws IOException, URISyntaxException, JSONException {
        List<ManagedContentSearchItem> searchItems = new ArrayList<>();
        CloseableHttpClient client = HttpClientBuilder.create().build();

        //Step 1: Get the oauth access token
        OauthObject oauthObject = oAuthSessionProvider(sessionRequest);

        //Step 2: Http get delivery channels
        HttpGet userInfoRequest = new HttpGet(sessionRequest.getBaseUrl()+String.format(SEARCH_ENDPOINT, channelId, searchPhrase));
        userInfoRequest.setHeader("Authorization", "Bearer "+oauthObject.getAccessToken());

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response = client.execute(userInfoRequest, responseHandler);

        JSONObject contentResponse = new JSONObject(response);
        if(contentResponse != null) {
            JSONArray jsonArray = contentResponse.getJSONArray("items");
            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ManagedContentSearchItem managedContentSearchItem = new ObjectMapper().readValue(jsonObject.toString(), ManagedContentSearchItem.class);
                if(managedContentSearchItem != null) {
                    searchItems.add(managedContentSearchItem);
                }
            }

        }
        return searchItems;
    }
}
