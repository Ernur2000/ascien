package com.ascien.app.Models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Purchase implements Serializable {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public class Data{
        private String payment_url;

        public String getPayment_url() {
            return payment_url;
        }
        public void setPayment_url(String payment_url) {
            this.payment_url = payment_url;
        }
    }
}
