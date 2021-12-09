package byu.cs240.familymapclient.net;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import byu.cs240.familymapclient.req.DataReq;
import byu.cs240.familymapclient.req.LoginReq;
import byu.cs240.familymapclient.req.RegisterReq;
import byu.cs240.familymapclient.res.DataRes;
import byu.cs240.familymapclient.res.LoginRes;
import byu.cs240.familymapclient.res.RegisterRes;

import static org.junit.Assert.*;

public class ServerProxyTest {
    static ServerProxy server;

    @BeforeClass
    public static void setUp() {
//        super.setUp()
        server = ServerProxy.getInstance();
        ServerProxy.setHostName("localhost");
        server.clear();
        RegisterReq regReq = new RegisterReq("Kris", "password", "email@gmail.com", "Kristin", "Hendricks", "f");
        server.register(regReq);
    }

    @Test
    public void testLogin_pass() {
        LoginReq loginReq = new LoginReq("Kris", "password");
        LoginRes actual = server.login(loginReq);

        assertEquals("Wrong UserName retrieved", "Kris", actual.getUserName());
        assertEquals("Bad Auth Token",16, actual.getAuthToken().length());
        assertEquals("Bad Person ID", 16, actual.getPersonID().length());
    }

    @Test
    public void testLogin_fail() {
        LoginReq logReq = new LoginReq("bad username", "doesn't exist");
        LoginRes actual = server.login(logReq);

        assertFalse("Successful login on invalid user", actual.isSuccess());
        assertEquals("Retrieved non existent user", "ERROR: bad request", actual.getMessage());
    }

    @Test
    public void testRegister_pass() {
        RegisterReq regReq = new RegisterReq("Good UserName", "password", "email@gmail.com", "Kristin", "Hendricks", "f");
        RegisterRes regRes = server.register(regReq);

        assertEquals("Retrieve wrong user", "Good UserName", regRes.getUserName());
        assertEquals("Bad AuthToken", 16, regRes.getAuthToken().length());
        assertEquals("Bad Person ID", 16, regRes.getPersonID().length());
    }

    @Test
    public void testRegister_fail() {
        RegisterReq regReq = new RegisterReq("Kris", "password", "email@gmail.com", "Kristin", "Hendricks", "f");
        RegisterRes regRes = server.register(regReq);

        assertFalse("Success true on invalid registration", regRes.isSuccess());
        assertEquals("Registered double user", "ERROR: Bad Request", regRes.getMessage());
    }

    @Test
    public void testDataRetrieval_pass() {
        LoginRes user = server.login(new LoginReq("Kris", "password"));

        DataRes actual = server.getData(new DataReq(user.getPersonID(), user.getAuthToken()));

        assertEquals("Incorrect number of people retrieved", 31, actual.getPersonData().length);
        assertEquals("Incorrect number of events retrieved", 91, actual.getEventData().length);
    }

    @Test
    public void testDataRetrieval_fail() {
        DataRes actual = server.getData(new DataReq("badID", "badToken"));

        assertFalse("successful retreival of data on invalid user", actual.isSuccess());
        assertEquals("Retrieved data for invalid user", "Data Retrieval Failed", actual.getMessage());
    }

}
