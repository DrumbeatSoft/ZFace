package com.drumbeat.zface;

import com.drumbeat.zface.option.Option;
import com.drumbeat.zface.permission.Permission;
import com.drumbeat.zface.permission.PermissionOption;
import com.drumbeat.zface.recognizer.Recognizer;
import com.drumbeat.zface.recognizer.RecognizerOption;
import com.drumbeat.zface.resource.Resource;
import com.drumbeat.zface.resource.ResourceOption;
import com.drumbeat.zface.target.Target;

/**
 * @author ZuoHailong
 * @date 2020/6/11
 */
public class Boot implements Option {

    private Target target;

    Boot(Target target) {
        this.target = target;
    }

    @Override
    public PermissionOption permission() {
        return Permission.getInstance().setTarget(target);
    }

    @Override
    public ResourceOption resource() {
        return Resource.getInstance().setTarget(target);
    }

    @Override
    public RecognizerOption recognizer() {
        return Recognizer.getInstance().setTarget(target);
    }

    /*@Override
    public LivingOption living() {
        return Living.getInstance().setTarget(target);
    }*/
}
