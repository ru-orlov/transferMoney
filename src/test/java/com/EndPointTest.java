package com;

import com.db.SQLiteJDBC;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import spark.Spark;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EndPointTest {

    SQLiteJDBC sqLiteJDBC = new SQLiteJDBC();
    EndPoint endPoint = new EndPoint(sqLiteJDBC);

    @BeforeAll
    public void startEndpoint() {
        endPoint.serverAPI();
        Spark.awaitInitialization();
    }

    @Test
    void testallAcountsControllerNotFound() throws Exception {
        HttpUriRequest request = new HttpGet( "http://localhost:4567/allAcounts"+"randomstring");
        CloseableHttpResponse res = HttpClientBuilder.create().build().execute( request );
        Assert.assertEquals(res.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void testallAcountsController() throws Exception {
        HttpUriRequest request = new HttpGet( "http://localhost:4567/allAcounts");
        HttpResponse response = HttpClientBuilder.create().build().execute( request );
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        Assert.assertNotNull(response);
    }

    @Test
    void testallTransfersControllerNotFound() throws Exception {
        HttpUriRequest request = new HttpGet( "http://localhost:4567/alltransfers"+"randomstring");
        CloseableHttpResponse res = HttpClientBuilder.create().build().execute( request );
        Assert.assertEquals(res.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void testallTransfersController() throws Exception {
        HttpUriRequest request = new HttpGet( "http://localhost:4567/alltransfers");
        HttpResponse response = HttpClientBuilder.create().build().execute( request );
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        Assert.assertNotNull(response);
    }


}