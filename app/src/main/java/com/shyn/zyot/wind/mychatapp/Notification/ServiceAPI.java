package com.shyn.zyot.wind.mychatapp.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ServiceAPI {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA6pQqSmo:APA91bFTnA5NVGhI3KBblYAH8E3bXqySv9Ch50u3C2RFuferQYwmh4kfXN0-QIETBogEKSP_LnrIUsHAfr9rT0N5gVL1oLcUWE8LNLJSH6XrPKa99Wr4CGiPMsgk-LHT1mAO5sdRC6UR"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
