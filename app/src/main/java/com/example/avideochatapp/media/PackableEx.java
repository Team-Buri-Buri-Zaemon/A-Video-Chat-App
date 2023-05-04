package com.example.avideochatapp.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
