package org.example.sellingcourese.Request;

import lombok.Data;

@Data
public  class ImgurResponse {
    private ImgurData data;
    private boolean success;
    private int status;
}
