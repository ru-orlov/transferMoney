package com;

import com.db.SQLiteJDBC;
import com.google.gson.*;
import com.model.Account;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.junit.jupiter.api.*;
import spark.Spark;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EndPointTest {

    SQLiteJDBC sqLiteJDBC = new SQLiteJDBC();
    EndPoint endPoint = new EndPoint(sqLiteJDBC);
    HttpClient httpClient = new HttpClient();

    @BeforeAll
    public void setUpAll() throws Exception {
        httpClient.start();
        endPoint.serverAPI();
        Spark.awaitInitialization();
        sqLiteJDBC.memoryRestore();
    }

    @AfterAll
    public void tearDownAll() throws Exception {
        httpClient.stop();
        Spark.stop();
        Thread.sleep(2000);
    }


    @Test
    void testallAcountsControllerNotFound() throws Exception {
        HttpUriRequest request = new HttpGet( "http://localhost:4567/allAcounts"+"randomstring");
        CloseableHttpResponse res = HttpClientBuilder.create().build().execute( request );
        assertEquals(res.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void testallAcountsController() throws Exception {
        HttpUriRequest request = new HttpGet( "http://localhost:4567/allAcounts");
        HttpResponse response = HttpClientBuilder.create().build().execute( request );
        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        assertNotNull(response);
    }

    @Test
    void testallTransfersControllerNotFound() throws Exception {
        HttpUriRequest request = new HttpGet( "http://localhost:4567/alltransfers"+"randomstring");
        CloseableHttpResponse res = HttpClientBuilder.create().build().execute( request );
        assertEquals(res.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void testallTransfersController() throws Exception {
        HttpUriRequest request = new HttpGet( "http://localhost:4567/alltransfers");
        HttpResponse response = HttpClientBuilder.create().build().execute( request );
        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        assertNotNull(response);
    }

    @Test
    void testallAccountsContentController() throws Exception {
        ContentResponse res = httpClient.GET("http://localhost:4567/allAcounts");
        assertEquals("[{\"id\":1,\"number\":555,\"balance\":100},{\"id\":2,\"number\":7777,\"balance\":130},{\"id\":3,\"number\":42,\"balance\":1000}]", res.getContentAsString());
        assertNotNull(res);
    }

    @Test
    void testallTransfersContentController() throws Exception {
        Request req = httpClient.POST("http://localhost:4567/transfer/dotransfer");
        req.content(new StringContentProvider("{\"fromAcc\":3,\"toAcc\":2,\"amount\":100}"));
        ContentResponse postRes = req.send();
        assertNotNull(postRes);

        String postResDetails = httpClient.GET("http://localhost:4567/alltransfers").getContentAsString();

        assertTrue(postResDetails.contains("id"));
        assertTrue(postRes.getContentAsString().contains("fromAcc"));
        assertTrue(postRes.getContentAsString().contains("toAcc"));
        assertTrue(postRes.getContentAsString().contains("transferDate"));
        assertTrue(postRes.getContentAsString().contains("amount"));
        System.out.println(postRes.getContentAsString());
    }

    @Test
    void testallTransferToContentController() throws Exception {
        Integer transferAmount = 100;
        String accountIdFrom = "3";
        String accountIdTo = "2";
        Account accountFromBefore = getAccountById(accountIdFrom);
        Account accountToBefore = getAccountById(accountIdTo);

        Request req = httpClient.POST("http://localhost:4567/transfer/dotransfer");
        req.content(new StringContentProvider("{\"fromAccId\":"+accountIdFrom+",\"toAccId\":"+accountIdTo+",\"amount\":"+transferAmount+"}"));
        req.send();

        Account accountFromAfter = getAccountById("3");
        Account accountToAfter = getAccountById("2");
        assertEquals(accountFromBefore.balance - accountFromAfter.balance, transferAmount);
        assertEquals(accountToAfter.balance - accountToBefore.balance, transferAmount);
    }

    private Account getAccountById(String id) throws InterruptedException, ExecutionException, TimeoutException {
        JsonObject jsonObject;
        String accountFromBeforeDetails = httpClient.GET("http://localhost:4567/account/id/"+id).getContentAsString();
        jsonObject = new Gson().fromJson(accountFromBeforeDetails, JsonObject.class);
        return new Gson().fromJson(jsonObject, Account.class);
    }

}