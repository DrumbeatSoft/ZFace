package com.drumbeat.zface.living;

import com.drumbeat.zface.target.Target;

/**
 * @author ZuoHailong
 * @date 2020/6/11
 */
public class Living implements LivingOption {

    private Target target;

    public static Living getInstance() {
        return InstanceHelper.instance;
    }

    @Override
    public void init() {
        
    }

    private static class InstanceHelper {
        private static Living instance = new Living();
    }

    private Living() {
    }

    public Living setTarget(Target target) {
        this.target = target;
        return this;
    }
}
