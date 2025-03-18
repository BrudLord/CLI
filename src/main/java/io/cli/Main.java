package io.cli;

import io.cli.parsers.MainParser;

public class Main {
    public static void main(String[] args) {
        MainParser parser = new MainParser();
        var v = parser.parse("Hello \"World\'!\" asd \' fff \" ggg \'");
        System.out.println();
    }
}