package org.smartregister.uniceftunisia.util;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ImageLoaderRequestTest {

    private ImageLoaderRequest imageLoaderRequest;

    @Before
    public void setUp() {
        Context context = Mockito.spy(Context.class);
        imageLoaderRequest = ImageLoaderRequest.getInstance(context);
    }

    @Test
    public void getImageLoader() {
        Assert.assertNotNull(imageLoaderRequest.getImageLoader());
    }
}