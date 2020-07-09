package com.gb.utils;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class InvalidStringGenerator extends Generator<String> {

    public InvalidStringGenerator() {
        super(String.class);
    }

    @Override
    public String generate(SourceOfRandomness sor, GenerationStatus gs) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 150; i++) {
            sb.append(sor.nextChar('A', 'z'));
        }

        return sb.toString();
    }

}