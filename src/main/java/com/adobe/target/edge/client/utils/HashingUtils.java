/*
 * Copyright 2021 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.target.edge.client.utils;

/**
 * Hashing util that implements a MurmurHash3 algorithm. Based on the guava implementation found
 * here
 * https://github.com/google/guava/blob/392f6bf278d6baa907f3d737bb5a216f94ff0794/guava/src/com/google/common/hash/Murmur3_32HashFunction.java
 */
public class HashingUtils {
  private static final int DEFAULT_SEED_VALUE = 0;

  private static final int C1 = 0xcc9e2d51;
  private static final int C2 = 0x1b873593;

  public static final int CHARACTER_SIZE = 16;
  public static final int BYTE_SIZE = 8;

  public static final int CHARS_BYTES = CHARACTER_SIZE / BYTE_SIZE;

  private static int mixK1(int k1) {
    k1 *= C1;
    k1 = Integer.rotateLeft(k1, 15);
    k1 *= C2;
    return k1;
  }

  private static int mixH1(int h1, int k1) {
    h1 ^= k1;
    h1 = Integer.rotateLeft(h1, 13);
    h1 = h1 * 5 + 0xe6546b64;
    return h1;
  }

  private static int fmix(int h1, int length) {
    h1 ^= length;
    h1 ^= h1 >>> 16;
    h1 *= 0x85ebca6b;
    h1 ^= h1 >>> 13;
    h1 *= 0xc2b2ae35;
    h1 ^= h1 >>> 16;
    return h1;
  }

  public static int hashUnencodedChars(CharSequence input) {
    return hashUnencodedChars(input, DEFAULT_SEED_VALUE);
  }

  public static int hashUnencodedChars(CharSequence input, int seed) {
    int h1 = seed;

    // step through the CharSequence 2 chars at a time
    for (int i = 1; i < input.length(); i += 2) {
      int k1 = input.charAt(i - 1) | (input.charAt(i) << 16);
      k1 = mixK1(k1);
      h1 = mixH1(h1, k1);
    }

    // deal with any remaining characters
    if ((input.length() & 1) == 1) {
      int k1 = input.charAt(input.length() - 1);
      k1 = mixK1(k1);
      h1 ^= k1;
    }

    return fmix(h1, CHARS_BYTES * input.length());
  }
}
