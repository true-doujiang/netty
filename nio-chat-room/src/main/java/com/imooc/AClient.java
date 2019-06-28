package com.imooc;

import com.imooc.client.NioClient;

import java.io.IOException;

public class AClient {

    public static void main(String[] args) throws IOException {
        new NioClient().start("AClient");
    }

}
