package com.example.user.siolSupervise.utility

import android.util.Log
import cz.msebera.android.httpclient.NameValuePair
import cz.msebera.android.httpclient.client.config.RequestConfig
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity
import cz.msebera.android.httpclient.client.methods.HttpGet
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.conn.ConnectTimeoutException
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder
import cz.msebera.android.httpclient.message.BasicNameValuePair
import cz.msebera.android.httpclient.util.EntityUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.SocketTimeoutException
import java.net.URI
import java.util.ArrayList

class HttpRequest {
    companion object {
        fun SendToggleRequest(parameterValue: String, ipAddress: String, portNumber: String, parameterName: String): String? {
            var serverResponse: String?

            val getRequest = HttpGet() // create an HTTP GET object
            val requestConfig = RequestConfig.custom()
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000).build()
            val httpclient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build() // create an HTTP client
            try {
                val website = URI("http://$ipAddress:$portNumber/?$parameterName=$parameterValue")
                getRequest.uri = website // set the URL of the GET request
                val response = httpclient.execute(getRequest) // execute the request
                // get the ip address server's reply
                val httpEntity = response.entity
                val inputStream = httpEntity.content
                val input = BufferedReader(InputStreamReader(inputStream))
                serverResponse = input.readLine()
                // Close the connection
                EntityUtils.consume(httpEntity)
                inputStream.close()
            }
            catch (e: SocketTimeoutException) {
                Log.e("log_tag_sock", e.toString())
                serverResponse = e.message
            }
            catch (e: ConnectTimeoutException) {
                Log.e("log_tag_conn", e.toString())
                serverResponse = e.message
            }
            catch (e: IOException) {
                Log.e("log_tag_IO", e.toString())
                serverResponse = e.message
            }
            catch (e: NullPointerException) {
                Log.e("log_tag_null", e.toString())
                serverResponse = e.message
            }
            catch (e: Exception) {
                Log.e("log_tag_pin", e.toString())
                serverResponse = e.message
            }
            finally {
                try {
                    httpclient.close()
                    getRequest.releaseConnection()
                }
                catch (e: Exception) {
                    Log.e("log_tag_close", e.toString())
                }
            }
            // return the server's reply/response text
            return serverResponse
        }

        fun DownloadFromMySQL(query: String, phpAddress: String?): String? {
            var result: String?
            val post = HttpPost(phpAddress)
            val requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(5000).build()
            val httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build()
            try {
                val nameValuePairs = ArrayList<NameValuePair>()
                nameValuePairs.add(BasicNameValuePair("category", query))
                post.entity = UrlEncodedFormEntity(nameValuePairs)
                val httpResponse = httpClient.execute(post)
                val httpEntity = httpResponse.entity
                val inputStream = httpEntity.content
                val bufReader = BufferedReader(InputStreamReader(inputStream, "utf-8"), 8)
                val builder = StringBuilder()
                var line: String? = null
                while ({ line = bufReader.readLine(); line }() != null) {
                    builder.append(line)
                    builder.append(System.lineSeparator())
                }
                EntityUtils.consume(httpEntity)
                inputStream.close()
                result = builder.toString()
            }
            catch (e: SocketTimeoutException) {
                Log.e("log_tag_sock", e.toString())
                result = e.message
            }
            catch (e: ConnectTimeoutException) {
                Log.e("log_tag_conn", e.toString())
                result = e.message
            }
            catch (e: IOException) {
                Log.e("log_tag_IO", e.toString())
                result = e.message
            }
            catch (e: Exception) {
                Log.e("log_tag", e.toString())
                result = e.message
            }
            finally {
                try {
                    httpClient.close()
                    post.releaseConnection()
                }
                catch (e: Exception) {
                    Log.e("log_tag_close", e.toString())
                }
            }
            return result
        }
    }
}