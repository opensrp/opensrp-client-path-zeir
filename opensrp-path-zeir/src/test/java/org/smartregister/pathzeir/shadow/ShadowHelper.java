package org.smartregister.pathzeir.shadow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 05-03-2020.
 */
public class ShadowHelper {

    private static HashMap<String, HashMap<Integer, ArrayList<Object>>> methodCalls = new HashMap<>();

    public void addMethodCall(@NonNull String methodSignature, Object... params) {
        int count = 0;

        HashMap<Integer, ArrayList<Object>> methodCall = methodCalls.get(methodSignature);
        if (methodCall != null) {
            count = methodCall.size();
        } else {
            methodCall = new HashMap<>();
        }
        ArrayList<Object> methodParams = new ArrayList<>();

        Collections.addAll(methodParams, params);

        methodCall.put(count, methodParams);
        methodCalls.put(methodSignature, methodCall);
    }

    public HashMap<String, HashMap<Integer, ArrayList<Object>>> getMethodCalls() {
        return methodCalls;
    }

    public HashMap<Integer, ArrayList<Object>> getMethodCalls(@NonNull String methodWithShortSignature) {
        return methodCalls.get(methodWithShortSignature);
    }

    public boolean isCalled(@NonNull String methodWithShortSignature) {
        return methodCalls.containsKey(methodWithShortSignature);
    }

    @Nullable
    public ArrayList<Object> getMethodParams(@NonNull String methodWithShortSignature, int methodHitIndex) {
        return isCalled(methodWithShortSignature) ? methodCalls.get(methodWithShortSignature).get(methodHitIndex) : null;
    }

}
