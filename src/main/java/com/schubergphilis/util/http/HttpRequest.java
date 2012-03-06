package com.schubergphilis.util.http;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpRequest
{
    private String _url = "";
    private Map<String, String> _postParameters = new HashMap<String,String>() {};


    public HttpRequest()
    {

    }

    public HttpRequest WithUrl(String url)
    {
        _url = url;
        return this;
    }

    public HttpRequest WithPostParam(String name, String value)
    {
        _postParameters.put(name, value);
        return this;
    }

    private String getData() throws UnsupportedEncodingException {
        StringBuilder data = new StringBuilder();

        Iterator<Map.Entry<String,String>> it = _postParameters.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, String> pairs = it.next();
            data.append(URLEncoder.encode(pairs.getKey(), "UTF-8"))
                .append("=")
                .append(URLEncoder.encode(pairs.getValue(), "UTF-8"));

            if(it.hasNext())
                data.append("&");
        }

        return data.toString();
    }

    public String Post() throws IOException, NoSuchAlgorithmException, KeyManagementException {

        // Create the request
        OutputStreamWriter writer = null;
        URLConnection conn = null;

        String _trustedUrl = _url + "/trusted";

        URL url = new URL(_trustedUrl);
        conn = url.openConnection();

        conn.setDoOutput(true);

        writer = new OutputStreamWriter(conn.getOutputStream());

        //write parameters
        writer.write(getData());
        writer.flush();

        // Get the response
        StringBuffer answer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
        {
            answer.append(line);
        }
        writer.close();
        reader.close();

        //Return the response
        return answer.toString();
    }
}