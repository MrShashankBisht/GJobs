//
// Created by Shashank_singh_bisht on 01-05-2021.
//

#include <jni.h>
#include <string>
#include <iostream>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_iab_GJobs_util_NativeClass_getDecodedString(JNIEnv *env, jclass clazz) {
    std::string str = "jvvru<11yyy0uctmctktguwnv0eqo1Crk0rjr";
    int len = str.length();
    char charArray[len];
    strcpy(charArray, str.c_str());

    for(int i = 0; (i < 100 && charArray[i] != '\0'); i++)
        charArray[i] = charArray[i] - 2;

    return env->NewStringUTF(charArray);
}