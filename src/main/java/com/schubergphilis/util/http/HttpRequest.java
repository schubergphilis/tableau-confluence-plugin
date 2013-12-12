/**
 * Developer: Roel Gerrits
 * Company: Schuberg Philis
 * Date: 7/14/11
 * Time: 10:20 AM
 *
 *      Licensed to the Apache Software Foundation (ASF) under one
 *      or more contributor license agreements.  See the NOTICE file
 *      distributed with this work for additional information
 *      regarding copyright ownership.  The ASF licenses this file
 *      to you under the Apache License, Version 2.0 (the
 *      "License"); you may not use this file except in compliance
 *      with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing,
 *      software distributed under the License is distributed on an
 *      "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *      KIND, either express or implied.  See the License for the
 *      specific language governing permissions and limitations
 *      under the License.
 **/

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

    public HttpRequest withUrl(String url)
    {
        _url = url;
        return this;
    }

    public HttpRequest withPostParam(String name, String value)
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

    public String post() throws IOException, NoSuchAlgorithmException, KeyManagementException {

        // Create the request
        OutputStreamWriter writer = null;
        URLConnection conn = null;

        URL url = new URL(_url);
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