package com.illdangag.iritube.core.data;

import com.illdangag.iritube.core.data.entity.FileMetadata;
import lombok.Builder;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Builder
public class IritubeFileInputStream extends InputStream {
    private final InputStream inputStream;

    @Getter
    private final FileMetadata fileMetadata;

    @Override
    public int read() throws IOException {
        return this.inputStream.read();
    }

    @Override
    public int available() throws IOException {
        return this.inputStream.available();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return this.inputStream.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return this.inputStream.transferTo(out);
    }
}
