package com.drumbeat.zface.option;

import com.drumbeat.zface.permission.PermissionOption;
import com.drumbeat.zface.recognizer.RecognizerOption;
import com.drumbeat.zface.resource.ResourceOption;

/**
 * @author ZuoHailong
 * @date 2020/6/10
 */
public interface Option {

    /**
     * Request permissions.
     */
    PermissionOption permission();

    /**
     * Handle resource files.
     */
    ResourceOption resource();

    /**
     * Face recognizer.
     */
    RecognizerOption recognizer();

    /**
     * Living body recognizer.
     */
//    LivingOption living();
}
