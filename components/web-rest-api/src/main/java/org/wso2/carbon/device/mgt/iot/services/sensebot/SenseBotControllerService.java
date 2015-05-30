/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.device.mgt.iot.services.sensebot;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.wso2.carbon.device.mgt.iot.services.DeviceControllerService;
import org.wso2.carbon.device.mgt.iot.services.DeviceJSON;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

//@Path(value = "/FireAlarmController")
public class SenseBotControllerService {

    private static Logger log = Logger.getLogger(SenseBotControllerService.class);
    private static final Map<String, String> deviceIPList = new HashMap<String, String>();

    private static HttpURLConnection httpConn;
    private static final String URL_PREFIX = "http://";
    private static final String FORWARD_URL = "/move/F";
    private static final String BACKWARD_URL = "/move/B";
    private static final String LEFT_URL = "/move/L";
    private static final String RIGHT_URL = "/move/R";
    private static final String STOP_URL = "/move/S";

    /*    Service to switch "ON" and "OFF" the FireAlarm bulb
               Called by an external client intended to control the FireAlarm bulb */
    @Path("/forward") @POST public String moveForward(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {
        if (deviceServerPort == 0) {
            deviceServerPort = 80;
        }

        String result = null;
        String urlString = URL_PREFIX + deviceIp + ":" + deviceServerPort + FORWARD_URL;
        log.info(urlString);

        result = sendCommand(urlString);
        return result;
    }

    /*    Service to read the temperature from the FireAlarm temperature sensor
                   Called by an external client intended to get the current temperature */
    @Path("/backward") @POST public String moveBackward(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {
        if (deviceServerPort == 0) {
            deviceServerPort = 80;
        }

        String result = null;
        String urlString = URL_PREFIX + deviceIp + ":" + deviceServerPort + BACKWARD_URL;
        log.info(urlString);

        result = sendCommand(urlString);
        return result;
    }

    /*    Service to toggle the FireAlarm fan between "ON" and "OFF"
               Called by an external client intended to control the FireAlarm fan */
    @Path("/left") @POST public String turnLeft(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {
        if (deviceServerPort == 0) {
            deviceServerPort = 80;
        }

        String result = null;
        String urlString = URL_PREFIX + deviceIp + ":" + deviceServerPort + LEFT_URL;
        log.info(urlString);

        result = sendCommand(urlString);
        return result;
    }

    @Path("/right") @POST public String turnRight(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {
        if (deviceServerPort == 0) {
            deviceServerPort = 80;
        }

        String result = null;
        String urlString = URL_PREFIX + deviceIp + ":" + deviceServerPort + RIGHT_URL;
        log.info(urlString);

        result = sendCommand(urlString);
        return result;
    }

    @Path("/stop") @POST public String stop(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId, @FormParam("ip") String deviceIp,
            @FormParam("port") int deviceServerPort) {
        if (deviceServerPort == 0) {
            deviceServerPort = 80;
        }

        String result = null;
        String urlString = URL_PREFIX + deviceIp + ":" + deviceServerPort + STOP_URL;
        log.info(urlString);

        result = sendCommand(urlString);
        return result;
    }

    /*    Service to push all the sensor data collected by the FireAlarm
       Called by the FireAlarm device  */
    @Path("/pushalarmdata") @POST @Consumes(MediaType.APPLICATION_JSON) public String pushAlarmData(
            final DeviceJSON dataMsg, @Context HttpServletResponse response) {
        String result = null;

        result = DeviceControllerService
                .pushData(dataMsg.owner, "FireAlarm", dataMsg.deviceId, System.currentTimeMillis(), "DeviceData",
                        dataMsg.value, dataMsg.reply, response);
        return result;
    }


    private String sendCommand(String urlString) {
        String result = null;
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            log.error("Invalid URL: " + urlString);
        }
        try {
            httpConn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            log.error("Error Connectiong to HTTP Endpoint at: " + urlString);
        }

        try {
            httpConn.setRequestMethod(HttpMethod.GET);
            httpConn.setRequestProperty("User-Agent", "WSO2 Carbon Server");
            int responseCode = httpConn.getResponseCode();
            result = ""+responseCode + HttpStatus.getStatusText(responseCode) + "(No reply from Robot)";

            log.info("\nSending 'GET' request to URL : " + urlString);
            log.info("Response Code : " + responseCode);
        } catch (ProtocolException e) {
            log.error("Protocal mismatch exception ccured whilst trying to 'GET' resource");
        } catch (IOException e) {
            log.error("Error occured whilst reading return code from server");
        }

//        BufferedReader in = null;
//        StringBuffer response = new StringBuffer();
//        try {
//            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
//
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            result = response.toString();
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return result;
    }

}
