package io.cli.command.util;

import java.io.*;

/**
 * Utility class for file processing operations and stream wrappers.
 */
public final class FileProcessor {
    private FileProcessor() {
    }

    /**
     * Returns a non-closeable InputStream wrapper that does not close the underlying stream.
     *
     * @param in The original InputStream.
     * @return A non-closeable InputStream wrapper.
     */
    public static InputStream nonCloseable(InputStream in) {
        return new NonCloseableInputStream(in);
    }

    /**
     * Returns a non-closeable OutputStream wrapper that flushes on close without closing the underlying stream.
     *
     * @param out The original OutputStream.
     * @return A non-closeable OutputStream wrapper.
     */
    public static OutputStream nonCloseable(OutputStream out) {
        return new NonCloseableOutputStream(out);
    }

    /**
     * NonCloseableInputStream is a wrapper around an InputStream that ignores calls to close().
     */
    public static class NonCloseableInputStream extends FilterInputStream {
        /**
         * Constructs a new NonCloseableInputStream.
         *
         * @param in the underlying input stream.
         */
        protected NonCloseableInputStream(InputStream in) {
            super(in);
        }

        /**
         * Overrides close to do nothing.
         */
        @Override
        public void close() throws IOException {
            // Do nothing to keep the underlying stream open.
        }
    }

    /**
     * NonCloseableOutputStream is a wrapper around an OutputStream that flushes on close
     * but does not close the underlying stream.
     */
    public static class NonCloseableOutputStream extends FilterOutputStream {
        /**
         * Constructs a new NonCloseableOutputStream.
         *
         * @param out the underlying output stream.
         */
        protected NonCloseableOutputStream(OutputStream out) {
            super(out);
        }

        /**
         * Overrides close to flush the stream but not close it.
         */
        @Override
        public void close() throws IOException {
            out.flush();
        }
    }
}
