package com.qiwenshare.ufo.operation.write;

import com.qiwenshare.ufo.operation.write.domain.WriteFile;

import java.io.InputStream;

public abstract class Writer {
    public abstract void write(InputStream inputStream, WriteFile writeFile);
}
